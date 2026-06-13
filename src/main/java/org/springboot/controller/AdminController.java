package org.springboot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springboot.entity.Admin;
import org.springboot.entity.dto.JwtUser;
import org.springboot.entity.dto.RefreshDTO;
import org.springboot.entity.dto.UserLoginDTO;
import org.springboot.entity.request.AdminRequest;
import org.springboot.entity.request.AdminLoginRequest;
import org.springboot.common.Result;
import org.springboot.service.impl.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    @Autowired
    AdminServiceImpl adminServiceImpl;

    @GetMapping("/")
    public Result allAdmins() {
        List<Admin> admins = adminServiceImpl.allAdmins();
        return Result.success(admins);
    }

    // 获取所有管理员数据
    @GetMapping("/page")
    public Result pageAdmins(AdminRequest adminRequest) {
        return Result.success(adminServiceImpl.getAdmins(adminRequest));
    }

    // 获取当前登录管理员数据
    @GetMapping("/info")
    public Result infoAdmin() {
        JwtUser jwtUser = (JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Result.success(jwtUser);
    }

    // 新增管理员
    @PostMapping("/")
    public Result addAdmin(@RequestBody Admin admin) {
        return Result.success(adminServiceImpl.addAdmin(admin));
    }

    // 编辑当前管理员
    @PutMapping("/")
    public Result updateAdmin(@RequestBody Admin admin) {
        return Result.success(adminServiceImpl.updateAdmin(admin));
    }

    // 删除管理员
    @DeleteMapping("/{id}")
    public Result deleteAdmin(@PathVariable Integer id) {
        return Result.success(adminServiceImpl.deleteAdmin(id));
    }

    // 密码登录
    @PostMapping("/login/password")
    public Result login(@RequestBody AdminLoginRequest adminLoginRequest) {
        return adminServiceImpl.login(adminLoginRequest);
    }

    // 发送验证码
    @PostMapping("/login/code/send")
    public Result sendCode(@RequestBody String phone) {
        return adminServiceImpl.sendCode(phone);
    }

    // 验证码登录
    @PostMapping("/login/code")
    public Result loginByCode(@RequestBody UserLoginDTO userLoginDTO) {
        return adminServiceImpl.loginByCode(userLoginDTO);
    }

    // 刷新短期令牌
    @PostMapping("/refresh")
    public Result refreshAdmin(@RequestBody RefreshDTO refreshDTO) {
        String oldRefreshToken = refreshDTO.getRefreshToken();
        log.info("Received Token: [{}]", oldRefreshToken);
        return Result.success(adminServiceImpl.refresh(oldRefreshToken));
    }

    // 退出登录并清除令牌
    @PostMapping("/deleteToken")
    public Result deleteToken(@RequestBody RefreshDTO refreshDTO) {
        String refreshToken = refreshDTO.getRefreshToken();
        log.info("Received Token: [{}]", refreshToken);
        adminServiceImpl.deleteRefreshToken(refreshToken);
        return Result.success();
    }
}
