package org.springboot.config;

import org.springboot.utils.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> {
                cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.addAllowedOrigin("http://localhost:5173");
                    config.addAllowedOrigin("https://shuangyuhub.com");
                    config.addAllowedOrigin("https://www.shuangyuhub.com");
                    config.addAllowedOrigin("https://admin.shuangyuhub.com");
                    config.addAllowedOrigin("https://h5.shuangyuhub.com");
                    config.addAllowedHeader("Authorization");
                    config.addAllowedHeader("Content-Type");
                    config.addAllowedHeader("*");
                    config.addAllowedMethod("*");
                    config.setAllowCredentials(true);
                    return config;
                });
            })
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers(
                            "/admin/login/**",
                            "/admin/refresh",
                            "/user/login/**",
                            "/user/refresh",
                            "/book/swiper",
                            "/book/newBooks",
                            "/book/home",
                            "/book/page"
                    ).permitAll()
                    .requestMatchers("/book/external/**", "/book/import/**").hasRole("ADMIN")
                    .requestMatchers("/dashboard/**").hasRole("ADMIN")
                    .requestMatchers("/book/**", "/user/info", "/user/deleteToken", "/user/favorites/**").hasAnyRole("USER", "ADMIN")
                    .requestMatchers("/admin/**", "/user/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
