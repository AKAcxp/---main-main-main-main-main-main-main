package org.example.knowmateadmin.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.knowmateadmin.utils.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;
import java.util.ArrayList;

@Component
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Autowired
    public JwtAuthFilter(JwtUtil jwtUtil, @Lazy UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        String role = null; // 用于存储从 token 中提取的角色

        log.debug("JwtAuthFilter: Processing request to {}", request.getRequestURI());
        log.debug("JwtAuthFilter: Authorization Header: {}", authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                username = jwtUtil.getUsername(token);
                role = jwtUtil.getRole(token); // 从 token 中提取角色
                log.debug("JwtAuthFilter: Extracted token: {}", token);
                log.debug("JwtAuthFilter: Extracted username from token: '{}'".concat(username));
                log.debug("JwtAuthFilter: Extracted role from token: '{}'".concat(role));
            } catch (Exception e) {
                log.warn("JwtAuthFilter: Error extracting username or role from token: {}", e.getMessage());
            }
        }

        // 如果 token 中有用户名且当前没有认证信息
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 从 UserDetailsService 加载用户详情
            UserDetails userDetails = null;
            try {
                userDetails = this.userDetailsService.loadUserByUsername(username);
                log.debug("JwtAuthFilter: UserDetails loaded for '{}'. Authorities: {}", username,
                        userDetails.getAuthorities());
            } catch (Exception e) {
                log.warn("JwtAuthFilter: Error loading UserDetails for username '{}': {}", username, e.getMessage());
            }

            if (userDetails != null && jwtUtil.isTokenValid(token)) {
                log.debug("JwtAuthFilter: Token for '{}' is valid.", username);
                // 创建认证信息并设置到 SecurityContextHolder
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("JwtAuthFilter: User '{}' authenticated successfully with roles: {}", username,
                        userDetails.getAuthorities());
            } else if (userDetails == null) {
                log.warn("JwtAuthFilter: UserDetails is null for username: {}", username);
            } else {
                log.warn("JwtAuthFilter: Token validation failed for user: {}. Is token valid: {}", username,
                        jwtUtil.isTokenValid(token));
            }
        } else if (username == null && authHeader != null && authHeader.startsWith("Bearer ")) {
            log.warn("JwtAuthFilter: Token present but username could not be extracted or token is invalid.");
        } else if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // 如果没有Bearer token，或者Authorization header为空，则不进行token认证，交给后续的过滤器处理
            log.debug(
                    "JwtAuthFilter: No Bearer token found in Authorization header or header is null. Proceeding without token authentication.");
        } else {
            // 用户名不为空，但 SecurityContextHolder 已经有认证信息，跳过
            log.debug(
                    "JwtAuthFilter: SecurityContextHolder already has an authentication for user: {}. Skipping token authentication.",
                    username);
        }

        filterChain.doFilter(request, response);
    }
}