package com.expensemanagement.expense_api.DTO;

import java.math.BigDecimal;

public record MonthlyReportResponse(

        Integer month,
        Integer year,
        BigDecimal totalAmount,
        Long totalTransactions

) {}
