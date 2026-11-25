package com.expensemanagement.expense_api.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateExpenseRequest(
        @NotNull(message = "Amount cannot be null/Empty or blank")
        @Positive(message = "Amount should be greater than 0")
        BigDecimal amount,

        @NotNull(message = "category cannot be null")
        @NotBlank(message = "category cannot be Empty or blank")
        @Size(max = 100)
        String category,

        @Size(max = 255)
        String description,

        @NotNull(message = "expense date cannot be null")
        LocalDate expenseDate
) {}
