package com.expensemanagement.expense_api.Controller;

import com.expensemanagement.expense_api.DTO.LoginRequest;
import com.expensemanagement.expense_api.DTO.LoginResponse;
import com.expensemanagement.expense_api.DTO.RegisterRequest;
import com.expensemanagement.expense_api.Service.UserService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService){
        this.userService = userService ;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String,String> register(@Valid @RequestBody RegisterRequest request){
        userService.register(request);

        return Map.of("message","Registration successful") ;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request){
        return userService.login(request) ;
    }
}
