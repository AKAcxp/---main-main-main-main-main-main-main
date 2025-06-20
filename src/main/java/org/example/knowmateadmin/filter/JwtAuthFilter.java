package org.example.knowmateadmin.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.knowmateadmin.utils.JwtUtil;
import org.example.knowmateadmin.service.CustomUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.info("JwtAuthFilter: Processing request for URI: {}", request.getRequestURI());

        // 如果是 /api/recommendation 路径，并且 SecurityConfig 中已设置为 permitAll，
        // 我们可以测试直接放行，看看是否能解决问题。
        // 这意味着对于这个特定路径，我们暂时不通过此 Filter 进行 Token 认证。
        if ("/api/recommendation".equals(request.getRequestURI())) {
            log.info(
                    "JwtAuthFilter: Path is /api/recommendation, bypassing token auth in this filter (assuming permitAll in SecurityConfig).");
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        log.info("JwtAuthFilter: Authorization header: {}", authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            log.info("JwtAuthFilter: Extracted token: {}", token);
            try {
                username = jwtUtil.getUsername(token);
                String roleFromToken = jwtUtil.getRole(token);
                log.info("JwtAuthFilter: Username from token: {}, Role from token: {}", username, roleFromToken);
            } catch (Exception e) {
                log.error("JwtAuthFilter: Error extracting username/role from token: {} - Exception: {}", token,
                        e.getMessage());
            }
        } else {
            log.info("JwtAuthFilter: No Bearer token found in Authorization header or header is null.");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            boolean isTokenValid = jwtUtil.isTokenValid(token);
            log.info("JwtAuthFilter: Is token valid? {}", isTokenValid);

            if (isTokenValid) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                        null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("JwtAuthFilter: User '{}' authenticated successfully with authorities: {}", username,
                        userDetails.getAuthorities());
            } else {
                log.warn("JwtAuthFilter: Token validation failed for token: {}", token);
            }
        } else {
            if (username == null) {
                log.info("JwtAuthFilter: Username is null, cannot proceed with authentication via token.");
            }
            // If username is not null but SecurityContextHolder already has an
            // authentication,
            // it means it was likely set by a previous filter or mechanism.
            // For stateless, this usually means it's the first time for this request.
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                log.info("JwtAuthFilter: SecurityContextHolder already has an authentication for user: {}",
                        SecurityContextHolder.getContext().getAuthentication().getName());
            }
        }
        filterChain.doFilter(request, response);
    }
}