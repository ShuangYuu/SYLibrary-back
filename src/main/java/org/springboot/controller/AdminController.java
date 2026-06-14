package org.springboot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springboot.common.Result;
import org.springboot.entity.Admin;
import org.springboot.entity.dto.JwtUser;
import org.springboot.entity.dto.RefreshDTO;
import org.springboot.entity.request.AdminLoginRequest;
import org.springboot.entity.request.AdminRequest;
import org.springboot.service.impl.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping("/page")
    public Result pageAdmins(AdminRequest adminRequest) {
        return Result.success(adminServiceImpl.getAdmins(adminRequest));
    }

    @GetMapping("/info")
    public Result infoAdmin() {
        JwtUser jwtUser = (JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Result.success(jwtUser);
    }

    @PostMapping("/")
    public Result addAdmin(@RequestBody Admin admin) {
        return Result.success(adminServiceImpl.addAdmin(admin));
    }

    @PutMapping("/")
    public Result updateAdmin(@RequestBody Admin admin) {
        return Result.success(adminServiceImpl.updateAdmin(admin));
    }

    @DeleteMapping("/{id}")
    public Result deleteAdmin(@PathVariable Integer id) {
        return Result.success(adminServiceImpl.deleteAdmin(id));
    }

    @PostMapping("/login/password")
    public Result login(@RequestBody AdminLoginRequest adminLoginRequest) {
        return adminServiceImpl.login(adminLoginRequest);
    }

    @PostMapping("/refresh")
    public Result refreshAdmin(@RequestBody RefreshDTO refreshDTO) {
        return Result.success(adminServiceImpl.refresh(refreshDTO.getRefreshToken()));
    }

    @PostMapping("/deleteToken")
    public Result deleteToken(@RequestBody RefreshDTO refreshDTO) {
        adminServiceImpl.deleteRefreshToken(refreshDTO.getRefreshToken());
        return Result.success();
    }
}
