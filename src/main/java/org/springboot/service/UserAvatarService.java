package org.springboot.service;

import org.springframework.web.multipart.MultipartFile;

public interface UserAvatarService {
    String uploadAvatar(Integer userId, MultipartFile file);
}
