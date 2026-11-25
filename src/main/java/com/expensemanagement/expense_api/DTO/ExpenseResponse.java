package com.expensemanagement.expense_api.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseResponse(
        Integer id,
        BigDecimal amount,
        String category,
        String description,
        LocalDate expenseDate
) {}
