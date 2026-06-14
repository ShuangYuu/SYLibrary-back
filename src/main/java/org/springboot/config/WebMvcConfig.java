package org.springboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final UploadProperties uploadProperties;

    public WebMvcConfig(UploadProperties uploadProperties) {
        this.uploadProperties = uploadProperties;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String publicPrefix = normalizePrefix(uploadProperties.getPublicPrefix());
        String avatarDir = Path.of(uploadProperties.getAvatarDir()).toAbsolutePath().normalize().toUri().toString();
        registry.addResourceHandler(publicPrefix + "**")
                .addResourceLocations(avatarDir);
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
}
