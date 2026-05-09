package com.example.hngpractise;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitFilter implements Filter {
    private final Map<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
    private long lastResetTime = System.currentTimeMillis();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Reset the counts every 60 seconds
        if (System.currentTimeMillis() - lastResetTime > 60000) {
            requestCounts.clear();
            lastResetTime = System.currentTimeMillis();
        }

        String clientIp = httpRequest.getRemoteAddr();
        String path = httpRequest.getRequestURI();

        int limit = path.startsWith("/auth") ? 200 : 500;

        requestCounts.putIfAbsent(clientIp, new AtomicInteger(0));
        int currentCount = requestCounts.get(clientIp).incrementAndGet();

        if (currentCount > limit) {
            httpResponse.setStatus(429); // Error 429: Too Many Requests
            httpResponse.getWriter().write("{\"status\": \"error\", \"message\": \"Rate limit exceeded\"}");
            return;
        }

        chain.doFilter(request, response);
    }
}
