package com.community.system.config;

import com.community.system.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;

    public JwtInterceptor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        // 放行 OPTIONS 请求
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtUtils.validateToken(token)) {
                Claims claims = jwtUtils.getClaimsFromToken(token);
                // 存入请求作用域
                request.setAttribute("userId", claims.get("userId"));
                request.setAttribute("role", claims.get("role"));
                return true;
            }
        }
        
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write("{\"code\": 401, \"msg\": \"未授权或Token已失效，请重新登录\"}");
        return false;
    }
}
