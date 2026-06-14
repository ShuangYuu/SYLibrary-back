package org.springboot.service.impl;

import org.springboot.config.UploadProperties;
import org.springboot.exception.InvalidRequestException;
import org.springboot.exception.ServiceException;
import org.springboot.mapper.UserMapper;
import org.springboot.service.UserAvatarService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class UserAvatarServiceImpl implements UserAvatarService {

    private static final long MAX_AVATAR_SIZE = 2 * 1024 * 1024;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");

    private final UploadProperties uploadProperties;
    private final UserMapper userMapper;

    public UserAvatarServiceImpl(UploadProperties uploadProperties, UserMapper userMapper) {
        this.uploadProperties = uploadProperties;
        this.userMapper = userMapper;
    }

    @Override
    public String uploadAvatar(Integer userId, MultipartFile file) {
        validateFile(file);

        String extension = getExtension(file.getOriginalFilename());
        String fileName = "u_" + userId + "_" + UUID.randomUUID() + "." + extension;
        Path avatarDir = Path.of(uploadProperties.getAvatarDir()).toAbsolutePath().normalize();
        Path target = avatarDir.resolve(fileName).normalize();

        if (!target.startsWith(avatarDir)) {
            throw new InvalidRequestException("文件路径非法");
        }

        try {
            Files.createDirectories(avatarDir);
            file.transferTo(target);
        } catch (IOException e) {
            throw new ServiceException("头像保存失败", e);
        }

        String avatarUrl = normalizeBaseUrl(uploadProperties.getPublicBaseUrl())
                + normalizePrefix(uploadProperties.getPublicPrefix())
                + fileName;
        userMapper.updateUserImage(userId, avatarUrl);
        return avatarUrl;
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidRequestException("请选择头像图片");
        }
        if (file.getSize() > MAX_AVATAR_SIZE) {
            throw new InvalidRequestException("头像不能超过2MB");
        }
        String extension = getExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new InvalidRequestException("仅支持jpg、jpeg、png、webp格式");
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new InvalidRequestException("文件格式不正确");
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }

    private String normalizePrefix(String prefix) {
        String value = prefix == null || prefix.isBlank() ? "/uploads/avatar/" : prefix.trim();
        if (!value.startsWith("/")) {
            value = "/" + value;
        }
        if (!value.endsWith("/")) {
            value = value + "/";
        }
        return value;
    }

    private String normalizeBaseUrl(String baseUrl) {
        if (baseUrl == null || baseUrl.isBlank()) {
            return "";
        }
        String value = baseUrl.trim();
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
