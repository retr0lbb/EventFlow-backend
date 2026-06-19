package spring.infra.api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final StringRedisTemplate redisTemplate;
    private static final int MAX_REQUESTS_PER_MINUTE = 10;

    public RateLimitingFilter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String path = request.getRequestURI();

        if (path.contains("/api/v1/auth")) {
            String clientIp = getClientIp(request);
            String redisKey = "rate_limit:auth:" + clientIp;

            Long currentCount = redisTemplate.opsForValue().increment(redisKey);
            System.out.println("RateLimitingFilter -> IP: " + clientIp + " | Requisições no minuto: " + currentCount);

            if (currentCount != null && currentCount == 1) {
                redisTemplate.expire(redisKey, Duration.ofMinutes(1));
            }

            if (currentCount != null && currentCount > MAX_REQUESTS_PER_MINUTE) {
                System.out.println("RateLimitingFilter -> BLOQUEADO IP: " + clientIp);
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write("{\"error\": \"Too many requests. Please try again in a minute.\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}
