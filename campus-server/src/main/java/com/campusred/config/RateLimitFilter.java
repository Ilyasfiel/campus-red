package com.campusred.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter implements Filter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String path = request.getRequestURI();
        String clientIp = request.getRemoteAddr();
        String key = clientIp + ":" + path;

        Bucket bucket = buckets.computeIfAbsent(key, k -> createBucket(path));

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            response.setStatus(429);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":429,\"msg\":\"请求过于频繁，请稍后再试\"}");
        }
    }

    private Bucket createBucket(String path) {
        Bandwidth limit;
        if (path.contains("/login")) {
            limit = Bandwidth.simple(10, Duration.ofMinutes(1));
        } else if (path.contains("/upload")) {
            limit = Bandwidth.simple(10, Duration.ofMinutes(1));
        } else if (path.contains("/send")) {
            limit = Bandwidth.simple(30, Duration.ofMinutes(1));
        } else {
            limit = Bandwidth.simple(60, Duration.ofMinutes(1));
        }
        return Bucket.builder().addLimit(limit).build();
    }
}
