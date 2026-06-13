package org.springboot.utils;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springboot.entity.dto.JwtUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        System.out.println("header: " + header);
        if (header != null && header.startsWith("Bearer ")) {
            try {
                String token = header.substring(7);

                Claims claims = JwtUtil.validateToken(token);
                JwtUser jwtUser = new JwtUser(claims);

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        jwtUser,
                        null,
                        jwtUser.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
                System.out.println(auth.getPrincipal());
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                response.getWriter().write("{\"error\":\"invalid_token\"}");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
