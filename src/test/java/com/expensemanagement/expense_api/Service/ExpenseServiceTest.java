package com.expensemanagement.expense_api.Service;

import com.expensemanagement.expense_api.DTO.*;
import com.expensemanagement.expense_api.Entity.Expense;
import com.expensemanagement.expense_api.Repository.ExpenseRepository;

import com.expensemanagement.expense_api.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)       // ExtendWith : enables Mockito in JUnit5
public class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository ;

    @InjectMocks
    private ExpenseService expenseService;

    @Test
    void getAllExpenses_shouldReturnUserExpenses(){
        Integer userId = 1;

        Expense expense = new Expense() ;
        expense.setId(10);
        expense.setAmount(new BigDecimal("500"));
        expense.setCategory("Food");
        expense.setDescription("Lunch");
        expense.setExpenseDate(LocalDate.now());

        expense.setUserId(userId);
        Pageable pageable = PageRequest.of(0, 10) ;

        Page<Expense> expensePage = new PageImpl<>(List.of(expense), pageable, 1) ;

        when(expenseRepository.findAllByUserId(userId, pageable)).thenReturn(expensePage) ;

        Page<ExpenseResponse> result = expenseService.getAllExpenses(userId, pageable) ;
        assertEquals(1, result.getTotalElements()) ;
        assertEquals(1, result.getTotalPages()) ;
        assertEquals("Food",result.getContent().get(0).category());
        assertEquals(new BigDecimal("500"), result.getContent().get(0).amount()) ;

        verify(expenseRepository).findAllByUserId(userId, pageable) ;
    }

    @Test
    void createExpense_shouldSaveExpenseForAuthenticatedUser(){
        Integer userId = 1 ;

        CreateExpenseRequest request = new CreateExpenseRequest(
                        new BigDecimal("1000"),
                        "Food",
                        "Lunch",
                        LocalDate.now()
        ) ;

        when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> invocation.getArgument(0)) ;

        ExpenseResponse result = expenseService.createExpense(request,userId) ;

        assertEquals(new BigDecimal("1000"), result.amount());
        assertEquals("Food", result.category());
        assertEquals("Lunch", result.description());

        ArgumentCaptor<Expense> captor = ArgumentCaptor.forClass(Expense.class) ;

        verify(expenseRepository).save(captor.capture()) ;
        Expense savedExpense = captor.getValue() ;
        assertEquals(userId, savedExpense.getUserId());
    }

    @Test
    void getExpenseById_shouldReturnExpenseForOwner(){
        Integer userId = 1 ;
        Integer expenseId = 10 ;

        Expense expense = new Expense() ;
        expense.setId(expenseId);
        expense.setAmount(new BigDecimal("500"));
        expense.setCategory("Food");
        expense.setDescription("Lunch");

        expense.setExpenseDate(LocalDate.now());
        expense.setUserId(userId);

        when(expenseRepository.findByIdAndUserId(expenseId,userId)).thenReturn(Optional.of(expense)) ;

        ExpenseResponse result = expenseService.getExpenseById(expenseId, userId) ;
        assertEquals(expenseId, result.id());
        assertEquals("Food", result.category());
    }

    @Test
    void getExpenseById_shouldThrowExceptionWhenExpenseNotFound(){
        Integer userId = 1 ;
        Integer expenseId = 10 ;

        when(expenseRepository.findByIdAndUserId(expenseId,userId)).thenReturn(Optional.empty()) ;

        assertThrows(ResourceNotFoundException.class, () -> expenseService.getExpenseById(expenseId,userId)) ;
    }

    @Test
    void updateExpense_shouldUpdateExpenseForOwner(){
        Integer userId = 1 ;
        Integer expenseId = 1 ;

        Expense expense = new Expense();
        expense.setId(expenseId) ;
        expense.setAmount(new BigDecimal("500"));
        expense.setCategory("Food");
        expense.setCategory("Lunch");

        expense.setExpenseDate(LocalDate.now());
        expense.setUserId(userId);

        UpdateExpenseRequest request = new UpdateExpenseRequest(
                new BigDecimal("800"),
                "Travel",
                "Bus Ticket",
                LocalDate.now()
        ) ;

        when(expenseRepository.findByIdAndUserId(expenseId, userId)).thenReturn(Optional.of(expense)) ;
        when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> invocation.getArgument(0)) ;

        ExpenseResponse result = expenseService.updateExpense(expenseId,userId, request) ;
        assertEquals(new BigDecimal("800"), result.amount());
        assertEquals("Travel", result.category());
        assertEquals("Bus Ticket", result.description());

        verify(expenseRepository).save(expense) ;
    }

    @Test
    void updateExpense_shouldThrowExceptionWhenExpenseNotOwnedByUser(){
         Integer userId = 1 ;
         Integer expenseId = 10 ;

        UpdateExpenseRequest request = new UpdateExpenseRequest(
                new BigDecimal("800"),
                "Travel",
                "Bus Ticket",
                LocalDate.now()
        ) ;

        when(expenseRepository.findByIdAndUserId(expenseId, userId)).thenReturn(Optional.empty()) ;
        assertThrows(ResourceNotFoundException.class, () -> expenseService.updateExpense(expenseId, userId,request)) ;

        verify(expenseRepository, never()).save((any(Expense.class))) ;
    }

    @Test
    void deleteExpense_shouldDeleteExpenseForOwner(){
        Integer userId = 1 ;
        Integer expenseId = 10 ;

        Expense expense = new Expense() ;
        expense.setId(expenseId);
        expense.setUserId(userId);

        when(expenseRepository.findByIdAndUserId(expenseId,userId)).thenReturn(Optional.of(expense)) ;
        expenseService.deleteExpense(expenseId, userId);

        verify(expenseRepository).deleteById(expenseId);
    }

    @Test
    void deleteExpense_shouldThrowExceptionWhenExpenseNotOwnedByUser(){
        Integer userId = 1 ;
        Integer expenseId = 10 ;

        when(expenseRepository.findByIdAndUserId(expenseId,userId)).thenReturn(Optional.empty()) ;

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> expenseService.deleteExpense(expenseId, userId)
        );

        assertEquals(
                "There is no Expense with id: 10",
                exception.getMessage()
        );
        verify(expenseRepository, never()).delete(any(Expense.class));
    }

    @Test
    void getMonthlyReport_shouldReturnTotalExpenseAndCountForMonth() {

        Integer userId = 1;
        Integer month = 10;
        Integer year = 2026;

        BigDecimal expectedTotal = new BigDecimal("15000.00");
        Long expectedCount = 50l;

        when(expenseRepository.getMonthlyTotal(userId, month, year))
                .thenReturn(expectedTotal);

        when(expenseRepository.countMonthlyExpense(userId, month, year))
                .thenReturn(expectedCount);

        MonthlyReportResponse response =
        expenseService.getMonthlyReport(userId, month, year);

        assertEquals(expectedTotal, response.totalAmount());
        assertEquals(expectedCount, response.totalTransactions());

        verify(expenseRepository, times(1))
                .getMonthlyTotal(userId, month, year);
        verify(expenseRepository, times(1))
                .countMonthlyExpense(userId, month, year);
    }


    @Test
    void getMonthlyReport_shouldReturnZeroWhenNoExpensesExist() {

        when(expenseRepository.getMonthlyTotal(1, 10, 2026))
                .thenReturn(BigDecimal.ZERO);

        when(expenseRepository.countMonthlyExpense(1, 10, 2026))
                .thenReturn(0L);

        MonthlyReportResponse response =
                expenseService.getMonthlyReport(1, 10, 2026);

        assertEquals(BigDecimal.ZERO, response.totalAmount());
        assertEquals(0L, response.totalTransactions());
    }

    // Category-wise test
    @Test
    void getCategoryReport_shouldReturnCategoryWiseExpenses() {

        Integer userId = 1;

        LocalDate startDate = LocalDate.of(2026, 10, 1);
        LocalDate endDate = LocalDate.of(2026, 11, 1);

        List<CategoryReportResponse> expectedReport = List.of(

                new CategoryReportResponse(
                        "Food",
                        new BigDecimal("5000.00"),
                        10L
                ),

                new CategoryReportResponse(
                        "Travel",
                        new BigDecimal("3000.00"),
                        5L
                ),

                new CategoryReportResponse(
                        "Shopping",
                        new BigDecimal("2000.00"),
                        3L
                )
        );

        when(expenseRepository.getCategoryReport(
                userId, startDate, endDate))
                .thenReturn(expectedReport);

        List<CategoryReportResponse> actualReport =
                expenseService.getCategoryReport(
                        userId, startDate, endDate);

        assertNotNull(actualReport);

        assertEquals(3, actualReport.size());

        assertEquals("Food", actualReport.get(0).category());

        assertEquals(
                new BigDecimal("5000.00"),
                actualReport.get(0).totalAmount()
        );

        assertEquals(
                10L,
                actualReport.get(0).totalTransactions()
        );

        assertEquals("Travel", actualReport.get(1).category());

        assertEquals("Shopping", actualReport.get(2).category());

        verify(expenseRepository, times(1))
                .getCategoryReport(userId, startDate, endDate);
    }

    @Test
    void getCategoryReport_shouldReturnEmptyListWhenNoExpensesExist() {

        Integer userId = 1;

        LocalDate startDate = LocalDate.of(2026, 10, 1);
        LocalDate endDate = LocalDate.of(2026, 11, 1);

        when(expenseRepository.getCategoryReport(
                userId, startDate, endDate))
                .thenReturn(Collections.emptyList());

        List<CategoryReportResponse> actual =
                expenseService.getCategoryReport(
                        userId, startDate, endDate);

        assertNotNull(actual);
        assertTrue(actual.isEmpty());

        verify(expenseRepository)
                .getCategoryReport(userId, startDate, endDate);
    }

    // Date range test cases
    @Test
    void getReportByDateRange_shouldReturnTotalExpense() {

        Integer userId = 1;

        LocalDate startDate = LocalDate.of(2026, 10, 1);
        LocalDate endDate = LocalDate.of(2026, 10, 31);

        BigDecimal expectedTotal = new BigDecimal("25000.00");
        Long expectedCount = 20L ;

        when(expenseRepository.getTotalByDateRange(userId, startDate, endDate))
                .thenReturn(expectedTotal);

        when(expenseRepository.countByDateRange(userId, startDate, endDate))
                .thenReturn(expectedCount);

        DateRangeReportResponse response = expenseService.getDateRangeReport(userId, startDate, endDate);

        assertEquals(expectedTotal, response.totalAmount());
        assertEquals(expectedCount, response.totalTransactions());

        verify(expenseRepository, times(1))
                .getTotalByDateRange(userId, startDate, endDate);

        verify(expenseRepository, times(1))
                .countByDateRange(userId, startDate, endDate);
    }

    @Test
    void getReportByDateRange_shouldReturnZeroWhenNoExpensesExist() {

        Integer userId = 1;

        LocalDate startDate = LocalDate.of(2026, 10, 1);
        LocalDate endDate = LocalDate.of(2026, 10, 31);

        when(expenseRepository.getTotalByDateRange(userId, startDate, endDate))
                .thenReturn(BigDecimal.ZERO);

        when(expenseRepository.countByDateRange(userId, startDate, endDate))
                .thenReturn(0L);

        DateRangeReportResponse  response = expenseService.getDateRangeReport(userId, startDate, endDate);

        assertEquals(BigDecimal.ZERO, response.totalAmount());
        assertEquals(0L , response.totalTransactions());
    }

}
