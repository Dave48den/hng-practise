package com.example.hngpractise;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ApiVersionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String apiVersion = request.getHeader("X-API-Version");

        // We only care about /api/ endpoints for now
        if (request.getRequestURI().startsWith("/api/")) {
            if (apiVersion == null || !apiVersion.equals("1")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                response.getWriter().write("{\"status\": \"error\", \"message\": \"API version header required\"}");
                return false; // Stop the request here
            }
        }
        return true; // Carry on to the controller
    }
}
