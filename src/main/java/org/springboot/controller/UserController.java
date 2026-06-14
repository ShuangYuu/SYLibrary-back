package org.springboot.controller;

import org.springboot.common.Result;
import org.springboot.entity.User;
import org.springboot.entity.dto.JwtUser;
import org.springboot.entity.dto.RefreshDTO;
import org.springboot.entity.dto.UserLoginDTO;
import org.springboot.entity.request.UserRequest;
import org.springboot.service.UserFavoriteBookService;
import org.springboot.service.UserService;
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
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    UserFavoriteBookService userFavoriteBookService;

    @GetMapping("/")
    public Result allUsers() {
        List<User> users = userService.allUsers();
        return Result.success(users);
    }

    @GetMapping("/page")
    public Result pageUsers(UserRequest userRequest) {
        return Result.success(userService.getUsers(userRequest));
    }

    @PostMapping("/")
    public Result addUser(@RequestBody User user) {
        return Result.success(userService.addUser(user));
    }

    @PutMapping("/")
    public Result updateUser(@RequestBody User user) {
        return Result.success(userService.updateUser(user));
    }

    @DeleteMapping("/{id}")
    public Result deleteUser(@PathVariable Integer id) {
        return Result.success(userService.deleteUser(id));
    }

    @GetMapping("/info")
    public Result getUserInfo() {
        JwtUser jwtUser = (JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Result.success(jwtUser);
    }

    @GetMapping("/favorites")
    public Result getFavorites() {
        JwtUser jwtUser = (JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Result.success(userFavoriteBookService.getFavorites(jwtUser.getId()));
    }

    @GetMapping("/favorites/{bookId}")
    public Result isFavorite(@PathVariable Integer bookId) {
        JwtUser jwtUser = (JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Result.success(userFavoriteBookService.isFavorite(jwtUser.getId(), bookId));
    }

    @PostMapping("/favorites/{bookId}")
    public Result addFavorite(@PathVariable Integer bookId) {
        JwtUser jwtUser = (JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userFavoriteBookService.addFavorite(jwtUser.getId(), bookId);
        return Result.success();
    }

    @DeleteMapping("/favorites/{bookId}")
    public Result deleteFavorite(@PathVariable Integer bookId) {
        JwtUser jwtUser = (JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userFavoriteBookService.deleteFavorite(jwtUser.getId(), bookId);
        return Result.success();
    }

    @PostMapping("/login/password")
    public Result login(@RequestBody UserLoginDTO userLoginDTO) {
        return Result.success(userService.login(userLoginDTO));
    }

    @PostMapping("/refresh")
    public Result refreshUser(@RequestBody RefreshDTO refreshDTO) {
        return Result.success(userService.refresh(refreshDTO.getRefreshToken()));
    }

    @PostMapping("/deleteToken")
    public Result deleteToken(@RequestBody RefreshDTO refreshDTO) {
        userService.deleteRefreshToken(refreshDTO.getRefreshToken());
        return Result.success();
    }
}
