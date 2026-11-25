package com.expensemanagement.expense_api.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DateRangeReportResponse(
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal totalAmount,
        Long totalTransactions
) {}
