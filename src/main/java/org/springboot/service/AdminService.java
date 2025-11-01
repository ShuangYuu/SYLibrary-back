package org.springboot.service;

import org.springboot.bean.Admin;
import org.springboot.bean.request.AdminRequest;
import org.springboot.bean.request.AdminLoginRequest;

import java.util.List;
import java.util.Map;

public interface AdminService {

    List<Admin> allAdmins();
    
    Object getAdmins(AdminRequest adminRequest);

    int addAdmin(Admin admin);

    int updateAdmin(Admin admin);

    int deleteAdmin(Integer id);

    Map<String, String> login(AdminLoginRequest adminLoginRequest);

    Object refresh(String refreshToken);

    void deleteRefreshToken(String refreshToken_s);
}
