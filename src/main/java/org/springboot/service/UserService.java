package org.springboot.service;

import org.springboot.common.Result;
import org.springboot.entity.User;
import org.springboot.entity.dto.JwtUser;
import org.springboot.entity.dto.UserLoginDTO;
import org.springboot.entity.request.UserRequest;

import java.util.List;
import java.util.Map;

public interface UserService {

    List<User> allUsers();

    Object getUsers(UserRequest userRequest);

    JwtUser getUserInfo(Integer userId);

    int addUser(User user);

    int updateUser(User user);

    int deleteUser(Integer id);

    Map<String, String> login(UserLoginDTO userLoginDTO);

    Map<String, String> refresh(String oldRefreshToken);

    void deleteRefreshToken(String refreshToken_s);

    Result sendCode(String phone);

    Result loginByCode(UserLoginDTO userLoginDTO);
}
