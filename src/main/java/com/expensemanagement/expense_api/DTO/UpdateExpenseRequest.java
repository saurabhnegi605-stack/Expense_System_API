package com.expensemanagement.expense_api.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateExpenseRequest (

        @NotNull
        @Positive
        BigDecimal amount,

        @NotBlank
        @Size(max =100)
        String category,

        @Size(max = 255)
        String description,

        @NotNull
        LocalDate expenseDate
){
}
