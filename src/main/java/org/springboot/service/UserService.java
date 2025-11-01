package org.springboot.service;

import org.springboot.bean.User;
import org.springboot.bean.request.UserLoginRequest;
import org.springboot.bean.request.UserRequest;

import java.util.List;
import java.util.Map;

public interface UserService {

    List<User> allUsers();
    Object getUsers(UserRequest userRequest);

    int addUser(User user);

    int updateUser(User user);

    int deleteUser(Integer id);

    Map<String, String> login(UserLoginRequest userLoginRequest);

    Map<String, String> refresh(String oldRefreshToken);

    void deleteRefreshToken(String refreshToken_s);
}
