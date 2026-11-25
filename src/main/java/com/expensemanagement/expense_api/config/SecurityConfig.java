package com.expensemanagement.expense_api.config;

import com.expensemanagement.expense_api.Service.JwtService;
import com.expensemanagement.expense_api.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService){
        return new JwtAuthenticationFilter(jwtService) ;
    }

    @Bean
    public SecurityFilterChain securityFilterChain
            (HttpSecurity httpSecurity,JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {

        httpSecurity.csrf(csrf -> csrf.disable())
//                .httpBasic(basic -> basic.disable())
//                .formLogin(form -> form.disable())

                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        auth -> auth

                                // Swagger API
                                .requestMatchers(
                                        "/api/auth/register",
                                        "/api/auth/login",
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/v3/api-docs/**"
                                ).permitAll()

                                //  Authentication API
                                .requestMatchers(HttpMethod.POST, "/api/auth/register","/api/auth/login")
                                .permitAll()

                                // Actuator endpoints
                                .requestMatchers("/actuator/health","/actuator/info").permitAll()

                                // Expense endpoints(Authentication Required)
                                .requestMatchers("/api/expense/**").authenticated()


                                .anyRequest().denyAll()

                ).exceptionHandling(exception -> exception.authenticationEntryPoint(
                        (request, response, authException) -> response.sendError(401,"Unauthorized"))
                ).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) ;

        return httpSecurity.build() ;
    }
}
