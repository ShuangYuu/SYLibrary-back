package org.springboot.config;

import org.springboot.utils.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    // 构造函数注入
    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            //解决跨域
            .cors(cors -> {
                cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.addAllowedOrigin("http://localhost:5173");
                    config.addAllowedHeader("Authorization");
                    config.addAllowedHeader("Content-Type");
                    config.addAllowedHeader("*");
                    config.addAllowedMethod("*");
                    config.setAllowCredentials(true);
                    return config;
                });
            })

            // 关闭 CSRF 防护（POST 请求不再检查 CSRF token）
            .csrf(AbstractHttpConfigurer::disable)

            // 放行（不需要认证），其他请求必须登录
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // 允许所有 OPTIONS 请求
                    .requestMatchers(
                            "/admin/login",
                            "/admin/refresh",
                            "/user/login",
                            "/user/refresh",
                            "/book/swiper",
                            "/book/newBooks"
                    ).permitAll()
                    .requestMatchers("/book/**", "/user/info", "/user/deleteToken").hasAnyRole("USER", "ADMIN")
                    .requestMatchers("/admin/**", "/user/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
            )

            // 添加 JWT 过滤器
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
