package org.springboot.entity.dto;

import io.jsonwebtoken.Claims;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
public class JwtUser implements UserDetails {
    private Integer id;
    private String username;
    private String userImage;
    private String phone; //user
    private String email; //admin
    private String role;

    private List<GrantedAuthority> authorities;

    public JwtUser(Claims claims) {
        this.id = claims.get("id", Integer.class);
        this.username = claims.get("username", String.class);
        this.userImage = claims.get("userImage", String.class);
        this.phone = claims.get("phone", String.class);
        this.email = claims.get("email", String.class);
        String roleClaim = claims.get("role", String.class);

        if (roleClaim != null && !roleClaim.isEmpty()) {
            this.role = roleClaim;
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + roleClaim.toUpperCase());
            this.authorities = List.of(authority);
        } else {
            // 如果 role 为空，设置一个默认值，防止授权逻辑失败
            this.role = "UNKNOWN";
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_UNKNOWN");
            this.authorities = List.of(authority);
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }
}
