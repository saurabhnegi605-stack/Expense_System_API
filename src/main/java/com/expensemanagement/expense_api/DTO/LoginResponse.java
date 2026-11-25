package com.expensemanagement.expense_api.DTO;

public record LoginResponse(

        String accessToken,
        String tokenType
) {}
