package com.expensemanagement.expense_api.security;

import com.expensemanagement.expense_api.Service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService ;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization") ;
        
        // No Token : Spring Security decide whether access is allowed or not
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")){

            filterChain.doFilter(request,response);
            return;
        }

        String token = authorizationHeader.substring(7) ;

        try {

            //Extracting userId from token
            Integer userId = jwtService.extractUserId(token);

            // Authentication object
            UsernamePasswordAuthenticationToken authentication
                    = new UsernamePasswordAuthenticationToken(userId,null, Collections.emptyList()) ;

            SecurityContextHolder.getContext().setAuthentication(authentication);

        }catch (JwtException | IllegalArgumentException exception){

            System.out.println("JWT ERROR: "+ exception.getClass().getSimpleName()); // Debug
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Invalid or expired token");
            return;
        }
        System.out.println("5. Continuing security filter chain");
        filterChain.doFilter(request,response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath() ;

        return path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs") ;
    }
}
