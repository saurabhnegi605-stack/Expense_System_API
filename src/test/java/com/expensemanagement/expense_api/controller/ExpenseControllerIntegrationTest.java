package com.expensemanagement.expense_api.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.metamodel.model.domain.ManagedDomainType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ExpenseControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper ;

    @Test
    void getExpenses_withoutToken_shouldReturnUnauthorized() throws Exception{
        mockMvc.perform(get("/api/expense")).andExpect(status().isUnauthorized()) ;
    }

    @Test
    void registerUser_shouldRegisterSuccessfully() throws Exception{
        String requestBody = """
                {
                "name" : "Saurabh",
                "email" : "test@example.com",
                "password" : "Test@123"
                }
                """ ;
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)).andExpect(status().is2xxSuccessful()) ;
    }

    @Test
    void loginUser_shouldReturnJwtToken() throws Exception{
        // For registration
        String requestBody = """
                {
                "name" : "Saurabh",
                "email" : "test@example.com",
                "password" : "Test@123"
                }
                """ ;

        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)).andExpect(status().is2xxSuccessful()) ;

        // For login
        String loginRequest = """
                {
                "email" : "test@example.com",
                "password" : "Test@123"
                }
                """ ;
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest)).andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.accessToken").isNotEmpty()) ;
    }

    @Test
    void createAndGetExpense_withJwtToken_shouldWork() throws Exception{
        // For registration
        String requestBody = """
                {
                "name" : "Saurabh",
                "email" : "test@example.com",
                "password" : "Test@123"
                }
                """ ;

        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)).andExpect(status().is2xxSuccessful()) ;

        // For login
        String loginRequest = """
                {
                "email" : "test@example.com",
                "password" : "Test@123"
                }
                """ ;
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                                .content(loginRequest)).andExpect(status().isOk())
                                .andExpect(jsonPath("$.accessToken").exists())
                                .andReturn() ;

        String response = loginResult.getResponse().getContentAsString();
        String token = objectMapper.readTree(response).get("accessToken").asText() ;

        // Create expense using JWT
        String expenseRequest = """
        {
            "amount": 500,
            "category": "food",
            "description": "lunch",
            "expenseDate": "2026-11-12"
        }
        """;
        mockMvc.perform(post("/api/expense").header("Authorization","Bearer "+token)
                .contentType(MediaType.APPLICATION_JSON).content(expenseRequest))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.amount").value(500))
                .andExpect(jsonPath("$.category").value("food")) ;

        // Retrieve expense using JWT
        mockMvc.perform(get("/api/expense").header("Authorization", "Bearer "+token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].category").value("food")) ;
    }

    private String registerAndLogin(String name, String email) throws Exception{
        String registerRequest = """
                {
                "name" : "%s",
                "email" : "%s",
                "password" : "Test@123"
                }
                """.formatted(name,email) ;

        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                                        .content(registerRequest))
                                        .andExpect(status().is2xxSuccessful()) ;

        String loginRequest = """
                {
                "email" : "%s",
                "password" : "Test@123"
                }
                """.formatted(email) ;

        MvcResult result = mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                                        .content(loginRequest))
                                        .andExpect(status().isOk())
                                        .andReturn() ;

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("accessToken").asText() ;
    }

    @Test
    void userShouldNotAccessAnotherUserExpense() throws Exception{

        // Register and login User A
        String tokenA = registerAndLogin("User A","usera@example.com") ;

        // Register and login User B
        String tokenB = registerAndLogin("User B" ,"userb@example.com") ;

        // User A creates an expense
        String expenseRequest = """
                {
                "amount" : 500,
                "category" : "Lunch",
                "description": "food",
                "expenseDate" : "2026-11-14"
                }
                """ ;

        MvcResult createResult = mockMvc.perform(post("/api/expense")
                    .header("Authorization", "Bearer "+ tokenA)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(expenseRequest)).andExpect(status().is2xxSuccessful())
                    .andReturn() ;

        // Extract expense ID
        Integer expenseId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                            .get("id").asInt();

        // User B retrieves their expenses
        mockMvc.perform(get("/api/expense")
                            .header("Authorization", "Bearer "+ tokenB))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.content").isEmpty()) ;

        // User B retrieve expense by ID
        mockMvc.perform(get("/api/expense/"+ expenseId)
                        .header("Authorization", "Bearer "+ tokenB))
                .andExpect(status().isNotFound()) ;

        // User B tries to update user A's expense
        mockMvc.perform(put("/api/expense/"+ expenseId)
                        .header("Authorization", "Bearer "+ tokenB)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(expenseRequest))
                        .andExpect(status().isNotFound()) ;

        // User B tries to delete user A's expense
        mockMvc.perform(delete("/api/expense/"+ expenseId)
                .header("Authorization", "Bearer "+ tokenB))
                .andExpect(status().isNotFound()) ;
    }

    @Test
    void shouldRejectNegativeExpenseAmount() throws Exception{
        String token = registerAndLogin("Validation User","negative@example.com") ;

        String request = """
                {
                "amount" : -100,
                "category" : "Food",
                "description" : "Invalid expense",
                "expenseDate" : "2026-11-14"
                }
                """ ;
        mockMvc.perform(post("/api/expense")
                .header("Authorization", "Bearer "+ token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isBadRequest()) ;
    }

    @Test
    void shouldRejectBlankExpenseCategory() throws Exception{
        String token = registerAndLogin("Validation User","negative@example.com") ;

        String request = """
                {
                "amount" : 100,
                "category" : " ",
                "description" : "Invalid expense",
                "expenseDate" : "2026-11-14"
                }
                """ ;
        mockMvc.perform(post("/api/expense")
                        .header("Authorization", "Bearer "+ token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest()) ;
    }

    @Test
    void shouldRejectMissingExpenseDate() throws Exception{
        String token = registerAndLogin("Validation User","negative@example.com") ;

        String request = """
                {
                "amount" : 100,
                "category" : "Food",
                "description" : "Invalid expense"
                }
                """ ;
        mockMvc.perform(post("/api/expense")
                        .header("Authorization", "Bearer "+ token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest()) ;
    }
}
