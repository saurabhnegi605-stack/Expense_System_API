package com.expensemanagement.expense_api.DTO;

import java.math.BigDecimal;

public record CategoryReportResponse(
        String category,
        BigDecimal totalAmount,
        Long totalTransactions
) {}
