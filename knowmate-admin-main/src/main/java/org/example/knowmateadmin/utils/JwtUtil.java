package org.example.knowmateadmin.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKeyString;
    private Key signingKey;

    @Value("${jwt.expiration}")
    private long expiration;

    @PostConstruct
    public void init() {
        this.signingKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
        log.info("JwtUtil initialized with secret length: {} bytes and expiration: {} ms", secretKeyString.length(),
                expiration);
    }

    private Key getSigningKey() {
        return signingKey;
    }

    public String generateToken(Long userId, String username, String role) {
        log.info("JwtUtil: Generating token for userId: {}, username: '{}', role: '{}'", userId, username, role);
        String token = Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
        log.info("JwtUtil: Generated token: {}", token);
        return token;
    }

    public String getUsername(String token) {
        log.info("JwtUtil: Attempting to get username from token: {}", token);
        try {
            String username = parseToken(token).getBody().getSubject();
            log.info("JwtUtil: Successfully extracted username '{}' from token.", username);
            return username;
        } catch (ExpiredJwtException e) {
            log.warn("JwtUtil: Failed to get username. Token expired: {}. Token: {}", e.getMessage(), token);
            throw e;
        } catch (UnsupportedJwtException e) {
            log.warn("JwtUtil: Failed to get username. Token unsupported: {}. Token: {}", e.getMessage(), token);
            throw e;
        } catch (MalformedJwtException e) {
            log.warn("JwtUtil: Failed to get username. Token malformed: {}. Token: {}", e.getMessage(), token);
            throw e;
        } catch (SecurityException e) {
            log.warn("JwtUtil: Failed to get username. Token signature invalid: {}. Token: {}", e.getMessage(), token);
            throw e;
        } catch (IllegalArgumentException e) {
            log.warn("JwtUtil: Failed to get username. Token claims string is empty or null: {}. Token: {}",
                    e.getMessage(), token);
            throw e;
        } catch (JwtException e) {
            log.warn("JwtUtil: Failed to get username. Generic JwtException: {}. Token: {}", e.getMessage(), token);
            throw e;
        }
    }

    public String getRole(String token) {
        log.info("JwtUtil: Attempting to get role from token: {}", token);
        try {
            String role = parseToken(token).getBody().get("role", String.class);
            log.info("JwtUtil: Successfully extracted role '{}' from token.", role);
            return role;
        } catch (Exception e) {
            log.warn("JwtUtil: Failed to get role from token: {}. Exception: {}", token, e.getMessage());
            throw e;
        }
    }

    /**
     * 从token中获取用户ID
     * 
     * @param token JWT令牌
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        log.info("JwtUtil: Attempting to get userId from token: {}", token);
        try {
            Long userId = parseToken(token).getBody().get("userId", Long.class);
            log.info("JwtUtil: Successfully extracted userId '{}' from token.", userId);
            return userId;
        } catch (Exception e) {
            log.warn("JwtUtil: Failed to get userId from token: {}. Exception: {}", token, e.getMessage());
            throw e;
        }
    }

    /**
     * 从token中获取用户角色
     * 
     * @param token JWT令牌
     * @return 用户角色
     */
    public String getRoleFromToken(String token) {
        return getRole(token);
    }

    /**
     * 从HttpServletRequest中提取JWT token并解析出用户ID
     * 
     * @param request HTTP请求
     * @return 用户ID
     */
    public static Long getUserIdFromRequest(HttpServletRequest request) {
        log.info("getUserIdFromRequest: 开始从请求中提取用户ID");
        String authHeader = request.getHeader("Authorization");
        log.info("getUserIdFromRequest: Authorization头: {}", authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            log.info("getUserIdFromRequest: 提取到token: {}", token);
            try {
                // 使用配置的密钥，而不是硬编码的密钥
                // 注意：由于这是静态方法，我们无法直接访问实例变量signingKey
                // 作为临时解决方案，我们将使用与application.yml中相同的密钥
                Key tempSigningKey = Keys.hmacShaKeyFor(
                        "smart_diet_secret_123456789012345678901234567890".getBytes(StandardCharsets.UTF_8));

                log.info("getUserIdFromRequest: 尝试从token中提取userId");

                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(tempSigningKey)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                log.info("getUserIdFromRequest: 成功解析token，claims: {}", claims);

                Long userId = claims.get("userId", Long.class);
                log.info("getUserIdFromRequest: 成功提取userId: {}", userId);
                return userId;
            } catch (ExpiredJwtException e) {
                log.error("getUserIdFromRequest: Token已过期: {}", e.getMessage());
                return null;
            } catch (Exception e) {
                log.error("getUserIdFromRequest: 从token提取userId失败: {}, 异常类型: {}", e.getMessage(),
                        e.getClass().getName());
                e.printStackTrace(); // 打印完整堆栈跟踪以便调试
                return null;
            }
        }
        log.warn("getUserIdFromRequest: 没有Authorization头或不是Bearer token");
        return null;
    }

    public boolean isTokenValid(String token) {
        log.info("JwtUtil: Validating token: {}", token);
        try {
            parseToken(token);
            log.info("JwtUtil: Token is valid: {}", token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JwtUtil: Token validation failed. Token expired: {}. Token: {}", e.getMessage(), token);
        } catch (UnsupportedJwtException e) {
            log.warn("JwtUtil: Token validation failed. Token unsupported: {}. Token: {}", e.getMessage(), token);
        } catch (MalformedJwtException e) {
            log.warn("JwtUtil: Token validation failed. Token malformed: {}. Token: {}", e.getMessage(), token);
        } catch (SecurityException e) {
            log.warn("JwtUtil: Token validation failed. Token signature invalid: {}. Token: {}", e.getMessage(), token);
        } catch (IllegalArgumentException e) {
            log.warn("JwtUtil: Token validation failed. Token claims string is empty or null: {}. Token: {}",
                    e.getMessage(), token);
        } catch (JwtException e) {
            log.warn("JwtUtil: Token validation failed. Generic JwtException: {}. Token: {}", e.getMessage(), token);
        }
        log.info("JwtUtil: Token is NOT valid: {}", token);
        return false;
    }

    /**
     * 从HttpServletRequest中提取JWT token并解析出用户名
     */
    public String getUsernameFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return getUsername(token);
        }
        return null;
    }

    public static Long extractUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                // 使用配置的密钥，而不是硬编码的密钥
                Key tempSigningKey = Keys.hmacShaKeyFor(
                        "smart_diet_secret_123456789012345678901234567890".getBytes(StandardCharsets.UTF_8));

                log.debug("extractUserId: Attempting to extract userId from token");

                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(tempSigningKey)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                Long userId = claims.get("userId", Long.class);
                log.debug("extractUserId: Successfully extracted userId: {}", userId);
                return userId;
            } catch (ExpiredJwtException e) {
                log.warn("extractUserId: Token expired for user ID extraction. Token: {}", token, e);
                return null;
            } catch (JwtException e) { // Catch broader JwtException for other parsing issues
                log.warn("extractUserId: Invalid token for user ID extraction. Token: {}", token, e);
                return null;
            }
        }
        log.warn("extractUserId: No Authorization header or not a Bearer token");
        return null;
    }

    private Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
    }
}