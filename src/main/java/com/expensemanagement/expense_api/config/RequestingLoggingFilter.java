package com.expensemanagement.expense_api.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RequestingLoggingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(request,response);
        }finally {
            long duration = System.currentTimeMillis() - startTime;

            logger.info("HTTP {"+ request.getMethod()+"}  " +
                    "{"+ request.getRequestURI()+"}\n" +
                    "Status : {"+response.getStatus()+"}\n"+
                    "Duration: {"+duration+"} ms");
        }
    }
}
