package com.expensemanagement.expense_api.Service;

import com.expensemanagement.expense_api.DTO.LoginRequest;
import com.expensemanagement.expense_api.DTO.LoginResponse;
import com.expensemanagement.expense_api.DTO.RegisterRequest;
import com.expensemanagement.expense_api.Entity.User;
import com.expensemanagement.expense_api.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class UserService {

    private JwtService jwtService;

    private final UserRepository userRepository ;

    private final PasswordEncoder passwordEncoder ;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService ;
    }

    // Register
    @Transactional
    public void register(RegisterRequest request){
        String email = request.email().trim().toLowerCase() ;

        if (!email.matches("^[A-Za-z0-9._%+-]+@gmail\\.com$")) {
            throw new IllegalArgumentException(
                    "email must contain @gmail.com at end"
            );
        }

        if(userRepository.existsByEmail(email)){
            throw new IllegalArgumentException("An account with this email already exists");
        }

        User user = new User() ;

        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        userRepository.save(user) ;
    }

    // Login
    public LoginResponse login(LoginRequest request){

        String email = request.email().trim().toLowerCase() ;

        User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password")) ;

        if(!passwordEncoder.matches(request.password(), user.getPassword())){
            throw new IllegalArgumentException("Invalid password or email") ;
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail()) ;

        return new LoginResponse(token, "Bearer") ;
    }
}
