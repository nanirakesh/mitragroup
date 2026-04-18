package com.mitra.config;

import com.mitra.service.JwtService;
import com.mitra.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final ApplicationContext applicationContext;
    
    public JwtAuthenticationFilter(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        final String authorizationHeader = request.getHeader("Authorization");
        
        String email = null;
        String jwt = null;
        
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                JwtService jwtService = applicationContext.getBean(JwtService.class);
                email = jwtService.extractEmail(jwt);
            } catch (Exception e) {
                // Invalid token
            }
        }
        
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            JwtService jwtService = applicationContext.getBean(JwtService.class);
            UserService userService = applicationContext.getBean(UserService.class);
            
            UserDetails userDetails = userService.loadUserByUsername(email);
            
            if (jwtService.validateToken(jwt, email)) {
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}