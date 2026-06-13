package org.springboot.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springboot.common.Result;
import org.springboot.entity.RefreshToken;
import org.springboot.entity.User;
import org.springboot.entity.dto.JwtUser;
import org.springboot.entity.dto.UserLoginDTO;
import org.springboot.entity.request.UserRequest;
import org.springboot.exception.ForbiddenException;
import org.springboot.exception.InvalidRequestException;
import org.springboot.exception.ServiceException;
import org.springboot.exception.UnauthorizedException;
import org.springboot.mapper.RefreshTokenMapper;
import org.springboot.mapper.UserMapper;
import org.springboot.service.UserService;
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
import java.util.UUID;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    RefreshTokenMapper refreshTokenMapper;

    @Override
    public List<User> allUsers() {
        return userMapper.allUsers();
    }

    @Override
    public Object getUsers(UserRequest userRequest) {
        PageHelper.startPage(userRequest.getPageNum(), userRequest.getPageSize());
        List<User> users = userMapper.getUsers(userRequest);
        return new PageInfo<>(users);
    }

    @Override
    public int addUser(User user) {
        user.setCardID(UUID.randomUUID().toString());
        user.setPassword(PasswordUtil.encode(user.getPassword()));
        return userMapper.addUser(user);
    }

    @Override
    public int updateUser(User user) {
        user.setPassword(PasswordUtil.encode(user.getPassword()));
        return userMapper.updateUser(user);
    }

    @Override
    public int deleteUser(Integer id) {
        return userMapper.deleteUser(id);
    }

    @Override
    public Result sendCode(String phone) {
        return null;
    }

    @Override
    public Result loginByCode(UserLoginDTO userLoginDTO) {
        return null;
    }

    @Override
    public Map<String, String> login(UserLoginDTO userLoginDTO) {
        List<User> users = userMapper.findByPhone(userLoginDTO);
        if (users == null || users.isEmpty()) {
            throw new ServiceException("用户名错误");
        }

        User login = users.stream()
                .filter(user -> PasswordUtil.checkPassword(userLoginDTO.getPassword(), user.getPassword()))
                .findFirst()
                .orElse(null);

        if (login == null) {
            throw new ServiceException("密码错误");
        }

        JwtUser jwtUser = new JwtUser();
        BeanUtils.copyProperties(login, jwtUser);

        String accessToken = JwtUtil.createAccessToken(jwtUser);
        String refreshToken = JwtUtil.createRefreshToken(jwtUser);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        Claims claims = JwtUtil.validateToken(refreshToken);
        System.out.println("当前权限: " + claims.get("role", String.class));
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

        if (!"refresh_token".equals(claims.get("type", String.class))) {
            throw new InvalidRequestException("这不是一个正确的刷新令牌");
        }

        refreshToken.setId(claims.get("id", Integer.class));
        refreshToken.setType("refresh_token");
        refreshToken.setJti(claims.get("jti", String.class));

        System.out.println("尝试查询 Refresh Token:");
        System.out.println("  JTI (来自 JWT): " + refreshToken.getJti());

        Optional<RefreshToken> rfTOptional = refreshTokenMapper.findByJtiAndId(refreshToken);

        if (rfTOptional.isEmpty()) {
            throw new ForbiddenException("无效的令牌");
        }

        refreshTokenMapper.deleteRefreshToken(rfTOptional.get());

        JwtUser jwtUser = new JwtUser(claims);
        JwtUser jwtUser2 = userMapper.searchById(jwtUser);
        jwtUser.setUsername(jwtUser2.getUsername());
        jwtUser.setUserImage(jwtUser2.getUserImage());
        jwtUser.setPhone(jwtUser2.getPhone());
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
