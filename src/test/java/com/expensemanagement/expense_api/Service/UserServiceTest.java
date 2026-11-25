package com.expensemanagement.expense_api.Service;

import com.expensemanagement.expense_api.DTO.RegisterRequest;
import com.expensemanagement.expense_api.Entity.Expense;
import com.expensemanagement.expense_api.Entity.User;
import com.expensemanagement.expense_api.Repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository ;
    @InjectMocks
    private UserService userService;


    @Test
    void emailValidation_shouldThrowIllegalArgumentException() {

        RegisterRequest request =
                new RegisterRequest("test", "test@example.com", "1234");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.register(request)
        );

        assertEquals(
                "email must contain @gmail.com at end",
                exception.getMessage()
        );

        // Verify database save is not called
        verify(userRepository, never()).save(any(User.class));
    }

}
