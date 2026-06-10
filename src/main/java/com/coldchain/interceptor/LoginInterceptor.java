package com.coldchain.interceptor;

import com.coldchain.common.Result;
import com.coldchain.common.ResultCode;
import com.coldchain.entity.SysUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        if (uri.startsWith("/api/user/login") || 
            uri.startsWith("/api/user/logout") ||
            uri.startsWith("/doc.html") || 
            uri.startsWith("/v2/api-docs") ||
            uri.startsWith("/swagger") ||
            uri.startsWith("/webjars") ||
            uri.endsWith(".html") || 
            uri.endsWith(".css") || 
            uri.endsWith(".js") || 
            uri.endsWith(".png") || 
            uri.endsWith(".jpg") || 
            uri.endsWith(".gif") || 
            uri.endsWith(".ico") ||
            uri.endsWith(".svg") ||
            uri.endsWith(".woff2") ||
            uri.endsWith(".ttf") ||
            uri.equals("/") ||
            uri.equals("/login.html")) {
            return true;
        }

        if (uri.startsWith("/api/")) {
            HttpSession session = request.getSession();
            SysUser user = (SysUser) session.getAttribute("currentUser");
            if (user == null) {
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                Result<Void> result = Result.error(ResultCode.UNAUTHORIZED.getCode(), "未登录或登录已过期，请重新登录");
                response.getWriter().write(objectMapper.writeValueAsString(result));
                return false;
            }
        }

        return true;
    }
}
