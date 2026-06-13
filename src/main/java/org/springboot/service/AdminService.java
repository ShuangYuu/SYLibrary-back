package org.springboot.service;

import org.springboot.common.Result;
import org.springboot.entity.Admin;
import org.springboot.entity.request.AdminRequest;
import org.springboot.entity.request.AdminLoginRequest;

import java.util.List;

public interface AdminService {

    List<Admin> allAdmins();
    
    Object getAdmins(AdminRequest adminRequest);

    int addAdmin(Admin admin);

    int updateAdmin(Admin admin);

    int deleteAdmin(Integer id);

    Result login(AdminLoginRequest adminLoginRequest);

    Object refresh(String refreshToken);

    void deleteRefreshToken(String refreshToken_s);
}
