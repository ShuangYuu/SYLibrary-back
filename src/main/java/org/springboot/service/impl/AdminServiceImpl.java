package org.springboot.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springboot.common.Result;
import org.springboot.entity.Admin;
import org.springboot.entity.RefreshToken;
import org.springboot.entity.dto.JwtUser;
import org.springboot.entity.dto.UserLoginDTO;
import org.springboot.entity.request.AdminLoginRequest;
import org.springboot.entity.request.AdminRequest;
import org.springboot.exception.ForbiddenException;
import org.springboot.exception.InvalidRequestException;
import org.springboot.exception.ServiceException;
import org.springboot.exception.UnauthorizedException;
import org.springboot.mapper.AdminMapper;
import org.springboot.mapper.RefreshTokenMapper;
import org.springboot.service.AdminService;
import org.springboot.utils.JwtUtil;
import org.springboot.utils.PasswordUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.springboot.utils.redisConstants.*;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    AdminMapper adminMapper;

    @Autowired
    RefreshTokenMapper refreshTokenMapper;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public List<Admin> allAdmins() {
        return adminMapper.allAdmins();
    }

    @Override
    public Object getAdmins(AdminRequest adminRequest) {
        PageHelper.startPage(adminRequest.getPageNum(), adminRequest.getPageSize());
        List<Admin> admins = adminMapper.getAdmins(adminRequest);
        return new PageInfo<>(admins);
    }

    @Override
    public int addAdmin(Admin admin) {
        if (admin.getPassword().isEmpty()) {
            admin.setPassword("123456");
        }
        admin.setPassword(PasswordUtil.encode(admin.getPassword()));
        return adminMapper.addAdmin(admin);
    }

    @Override
    public int updateAdmin(Admin admin) {
        return adminMapper.updateAdmin(admin);
    }

    @Override
    public int deleteAdmin(Integer id) {
        return adminMapper.deleteAdmin(id);
    }

    public Result sendCode(String phone) {
        if (phone == null || !phone.matches("^1[3-9]\\d{9}$")) {
            throw new InvalidRequestException("手机号格式不正确");
        }

        String lockKey = LOGIN_LOCK_KEY + phone;
        if (redisTemplate.hasKey(lockKey)) {
            throw new InvalidRequestException("发送过于频繁，请稍后重试");
        }

        String code = RandomUtil.randomNumbers(6);
        redisTemplate.opsForValue().set(LOGIN_CODE_KEY + phone, code, LOGIN_CODE_TTL, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(lockKey, "1", LOGIN_LOCK_TTL, TimeUnit.SECONDS);

        log.debug("验证码发送成功 -> {} : {}", phone, code);
        return Result.success();
    }

    public Result loginByCode(UserLoginDTO userLoginDTO) {
        if (userLoginDTO.getPhone() == null || !userLoginDTO.getPhone().matches("^1[3-9]\\d{9}$")) {
            throw new InvalidRequestException("手机号格式不正确");
        }

        String cacheCode = redisTemplate.opsForValue().get(LOGIN_CODE_KEY + userLoginDTO.getPhone());
        if (cacheCode == null) {
            throw new InvalidRequestException("验证码已过期");
        }

        if (!cacheCode.equals(userLoginDTO.getCode())) {
            throw new InvalidRequestException("验证码错误");
        }

        Admin admin = adminMapper.getAdminByPhone(userLoginDTO.getPhone());
        if (admin == null) {
            admin = new Admin();
            admin.setUsername("admin_" + UUID.randomUUID());
            admin.setPhone(userLoginDTO.getPhone());
            adminMapper.addAdminByLogin(admin);
            admin = adminMapper.getAdminByPhone(userLoginDTO.getPhone());
        }

        return Result.success(takeToken(admin));
    }

    @Override
    public Result login(AdminLoginRequest adminLoginRequest) {
        Admin admin = adminMapper.login(adminLoginRequest);

        if (admin == null) {
            throw new ServiceException("用户名错误");
        } else if (!PasswordUtil.checkPassword(adminLoginRequest.getPassword(), admin.getPassword())) {
            throw new ServiceException("密码错误");
        }

        return Result.success(takeToken(admin));
    }

    public Map<String, String> takeToken(Admin admin) {
        JwtUser jwtUser = new JwtUser();
        BeanUtils.copyProperties(admin, jwtUser);
        System.out.println("当前权限: " + jwtUser.getRole());

        String accessToken = JwtUtil.createAccessToken(jwtUser);
        String refreshToken = JwtUtil.createRefreshToken(jwtUser);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        Claims claims = JwtUtil.validateToken(refreshToken);
        RefreshToken refreshTokenObj = new RefreshToken();
        refreshTokenObj.setId(claims.get("id", Integer.class));
        refreshTokenObj.setType(claims.get("type", String.class));
        refreshTokenObj.setJti(claims.get("jti", String.class));
        refreshTokenObj.setRole(claims.get("role", String.class));
        refreshTokenMapper.addRefreshToken(refreshTokenObj);

        return tokens;
    }

    @Override
    public Map<String, String> refresh(String oldRefreshToken) {
        System.out.println("DEBUG: 收到刷新请求，Token 内容为 [" + oldRefreshToken + "]");

        Claims claims;
        RefreshToken refreshToken = new RefreshToken();

        try {
            claims = JwtUtil.validateToken(oldRefreshToken);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            throw new UnauthorizedException("刷新令牌已过期，请重新登录");
        } catch (io.jsonwebtoken.security.SignatureException e) {
            throw new UnauthorizedException("令牌签名无效");
        } catch (Exception e) {
            e.printStackTrace();
            throw new UnauthorizedException("刷新令牌解析失败: " + e.getMessage());
        }

        System.out.println("已解析 claims，内容如下:");
        System.out.println(claims);

        if (!"refresh_token".equals(claims.get("type", String.class))) {
            throw new InvalidRequestException("这不是一个正确的刷新令牌");
        }

        refreshToken.setId(claims.get("id", Integer.class));
        refreshToken.setType("refresh_token");
        refreshToken.setJti(claims.get("jti", String.class));
        refreshToken.setRole(claims.get("role", String.class));

        Optional<RefreshToken> rfTOptional = refreshTokenMapper.findByJtiAndId(refreshToken);

        if (rfTOptional.isEmpty()) {
            throw new ForbiddenException("无效的令牌");
        }

        refreshTokenMapper.deleteRefreshToken(rfTOptional.get());

        JwtUser jwtUser = new JwtUser(claims);
        JwtUser jwtUser2 = adminMapper.searchById(jwtUser);
        jwtUser.setUsername(jwtUser2.getUsername());
        jwtUser.setPhone(jwtUser2.getPhone());
        jwtUser.setEmail(jwtUser2.getEmail());
        System.out.println(jwtUser);

        String newAccessToken = JwtUtil.createAccessToken(jwtUser);
        String newRefreshToken = JwtUtil.createRefreshToken(jwtUser);
        claims = JwtUtil.validateToken(newRefreshToken);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                jwtUser,
                null,
                jwtUser.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        RefreshToken refreshTokenObj = new RefreshToken();
        refreshTokenObj.setId(claims.get("id", Integer.class));
        refreshTokenObj.setType(claims.get("type", String.class));
        refreshTokenObj.setJti(claims.get("jti", String.class));
        refreshTokenObj.setRole(claims.get("role", String.class));
        refreshTokenMapper.addRefreshToken(refreshTokenObj);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        tokens.put("refreshToken", newRefreshToken);

        return tokens;
    }

    @Override
    public void deleteRefreshToken(String refreshToken_s) {
        Claims claims = JwtUtil.validateToken(refreshToken_s);
        RefreshToken refreshToken = new RefreshToken(claims.get("id", Integer.class));
        refreshTokenMapper.deleteRefreshToken(refreshToken);
        log.info("删除 Token 成功");
    }
}
