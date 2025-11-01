package org.springboot.controller;

import org.springboot.bean.User;
import org.springboot.bean.dto.JwtUser;
import org.springboot.bean.dto.RefreshDTO;
import org.springboot.bean.request.UserLoginRequest;
import org.springboot.common.Result;
import org.springboot.bean.request.UserRequest;
import org.springboot.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserServiceImpl userServiceImpl;

    @GetMapping("/")
    public Result allUsers() {
        List<User> users = userServiceImpl.allUsers();
        return Result.success(users);
    }

    @GetMapping("/page")
    public Result pageUsers(UserRequest userRequest) {
        return Result.success(userServiceImpl.getUsers(userRequest));
    }

    @PostMapping("/")
    public Result addUser(@RequestBody User user) {
        return Result.success(userServiceImpl.addUser(user));
    }

    @PutMapping("/")
    public Result updateUser(@RequestBody User user) {
        return Result.success(userServiceImpl.updateUser(user));
    }

    @DeleteMapping("/{id}")
    public Result deleteUser(@PathVariable Integer id) {
        return Result.success(userServiceImpl.deleteUser(id));
    }

    @GetMapping("/info")
    public Result getUserInfo() {
        JwtUser jwtUser = (JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Result.success(jwtUser);
    }

    @PostMapping("/login")
    public Result login(@RequestBody UserLoginRequest userLoginRequest) {
        return Result.success(userServiceImpl.login(userLoginRequest));
    }

    @PostMapping("/refresh")
    public Result refreshUser(@RequestBody RefreshDTO refreshDTO) {
        String refreshToken = refreshDTO.getRefreshToken();
        return Result.success(userServiceImpl.refresh(refreshToken));
    }

    @PostMapping("/deleteToken")
    public Result deleteToken(@RequestBody RefreshDTO refreshDTO) {
        String refreshToken = refreshDTO.getRefreshToken();
        userServiceImpl.deleteRefreshToken(refreshToken);
        return Result.success();
    }

}
