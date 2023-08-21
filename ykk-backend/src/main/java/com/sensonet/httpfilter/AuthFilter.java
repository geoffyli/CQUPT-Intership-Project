package com.sensonet.httpfilter;

import com.google.common.base.Strings;
import com.sensonet.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@WebFilter(urlPatterns = "/*",filterName = "authFilter")
public class AuthFilter implements Filter{

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)servletRequest;
        HttpServletResponse resp = (HttpServletResponse)servletResponse;
        // Get the path of the request
        String path = ((HttpServletRequest) servletRequest).getServletPath();
        // If the path is /login, skip the filter and continue the request.
        if(path.equals("/login")){
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        if(path.equals("/signup")){
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        if(path.equals("/logout")){
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        // If the path is /device/tags, skip the filter and continue the request.
        if(path.contains("/device/tags")){
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        //断连监控
        if(path.contains("/device/clientAction")){
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        // Check if the request header contains the Authorization field
        String authToken = ((HttpServletRequest) servletRequest).getHeader("Authorization");
        if(Strings.isNullOrEmpty(authToken)){
            ((HttpServletResponse) servletResponse).setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }
        // Check if the token is valid
        try {
            JwtUtil.parseJWT(authToken);
            // If the token is in the blacklist, return 401
            if(JwtUtil.inBlacklist(authToken)){
                ((HttpServletResponse) servletResponse).setStatus(HttpStatus.UNAUTHORIZED.value());
                return;
            }
        } catch (Exception e) {
            ((HttpServletResponse) servletResponse).setStatus(HttpStatus.UNAUTHORIZED.value());
        }

        // If the token is valid, continue the request.
        filterChain.doFilter(servletRequest, servletResponse);
    }

}
