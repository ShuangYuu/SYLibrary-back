package org.springboot.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springboot.bean.Admin;
import org.springboot.bean.RefreshToken;
import org.springboot.bean.dto.JwtUser;
import org.springboot.bean.request.AdminRequest;
import org.springboot.bean.request.AdminLoginRequest;
import org.springboot.exception.ForbiddenException;
import org.springboot.exception.InvalidRequestException;
import org.springboot.exception.ServiceException;
import org.springboot.exception.UnauthorizedException;
import org.springboot.mapper.AdminMapper;
import org.springboot.mapper.RefreshTokenMapper;
import org.springboot.utils.JwtUtil;
import org.springboot.utils.PasswordUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    AdminMapper adminMapper;

    @Autowired
    RefreshTokenMapper refreshTokenMapper;

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
        if(admin.getPassword().isEmpty()){
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

    @Override
    public Map<String, String> login(AdminLoginRequest adminLoginRequest) {

        Admin login = adminMapper.login(adminLoginRequest);

        if(login == null) {
            throw new ServiceException("用户名错误！");
        }
        else if(!PasswordUtil.checkPassword(adminLoginRequest.getPassword(), login.getPassword())) {
            throw new ServiceException("密码错误！");
        }
        JwtUser jwtUser = new JwtUser();
        BeanUtils.copyProperties(login, jwtUser);
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

        Claims claims;
        RefreshToken refreshToken = new RefreshToken();

        try {
            claims = JwtUtil.validateToken(oldRefreshToken);
        } catch (Exception e) {
            throw new UnauthorizedException("刷新令牌已失效");
        }

        System.out.println("✅ 已解析 claims，打印内容:");
        System.out.println(claims.toString());

        if(!"refresh_token".equals(claims.get("type", String.class))) {
            throw new InvalidRequestException("这不是一个正确的刷新令牌");
        }

        refreshToken.setId(claims.get("id", Integer.class));
        refreshToken.setType("refresh_token");
        refreshToken.setJti(claims.get("jti", String.class));
        refreshToken.setRole(claims.get("role", String.class));

        Optional<RefreshToken> rfTOptional = refreshTokenMapper.findByJtiAndId(refreshToken);

        if(rfTOptional.isEmpty()) {
            throw new ForbiddenException("无效的令牌");
        }

        refreshTokenMapper.deleteRefreshToken(rfTOptional.get());

        JwtUser jwtUser = new JwtUser(claims);
        System.out.println(jwtUser);
        JwtUser jwtUser2 = adminMapper.searchById(jwtUser);
        jwtUser.setUsername(jwtUser2.getUsername());
        jwtUser.setPhone(jwtUser2.getPhone());
        jwtUser.setEmail(jwtUser2.getEmail());
        System.out.println(jwtUser);

        String newAccessToken = JwtUtil.createAccessToken(jwtUser);
        String newRefreshToken = JwtUtil.createRefreshToken(jwtUser);
        claims = JwtUtil.validateToken(newRefreshToken);

        UsernamePasswordAuthenticationToken auth =  new UsernamePasswordAuthenticationToken(
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
        log.info("删除Token成功!");
    }
}
