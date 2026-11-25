package com.expensemanagement.expense_api.Service;

import com.expensemanagement.expense_api.DTO.*;
import com.expensemanagement.expense_api.Entity.Expense;
import com.expensemanagement.expense_api.Repository.ExpenseRepository;
import com.expensemanagement.expense_api.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    private static final Logger logger = LoggerFactory.getLogger(ExpenseService.class) ;

    public ExpenseService(ExpenseRepository expenseRepository){
        this.expenseRepository = expenseRepository;
    }

    public List<ExpenseResponse> getAllExpense(Integer userId){

        return expenseRepository.findAllByUserIdOrderByExpenseDateDesc(userId)
                                .stream()
                                .map(this::toResponse)
                                .toList() ;
    }

    public ExpenseResponse getExpenseById(Integer id, Integer userId){
        logger.info("Fetching expenses for user ID: "+userId);
        Expense expense = expenseRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found: "+id)) ;
        return toResponse(expense) ;
    }

    // Add expense
    @Transactional
    public ExpenseResponse createExpense(CreateExpenseRequest request, Integer userId){

        logger.info("Creating expense for user ID: {}", userId);
        Expense expense = new Expense() ;
        expense.setUserId(userId);
        expense.setAmount(request.amount());
        expense.setCategory(request.category());
        expense.setDescription(request.description());
        expense.setExpenseDate(request.expenseDate());

        Expense saved = expenseRepository.save(expense) ;
        logger.info("Expense created successfully with ID: "+saved.getId());
        return toResponse(expense) ;
    }

    // Delete Expense
    @Transactional
    public void deleteExpense(Integer id, Integer userId){

        logger.info("Deleting expense ID: {} for user ID:"+id,userId);
        Expense expense = expenseRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("There is no Expense with id: "+ id)) ;

        expenseRepository.deleteById(id);
    }

    private ExpenseResponse toResponse(Expense expense){

        return new ExpenseResponse(
                expense.getId(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getDescription(),
                expense.getExpenseDate()
        ) ;
    }

    public ExpenseResponse updateExpense(Integer id, Integer userId, UpdateExpenseRequest request){

        Expense expense = expenseRepository.findByIdAndUserId(id, userId)
                                            .orElseThrow(() -> new ResourceNotFoundException("Expense not found: "+ id)) ;
        expense.setAmount(request.amount());
        expense.setCategory(request.category());
        expense.setDescription(request.description());
        expense.setExpenseDate(request.expenseDate());

        Expense updatedExpense = expenseRepository.save(expense) ;
        return toResponse(updatedExpense) ;
    }


    // Monthly expense Report
    public MonthlyReportResponse getMonthlyReport(Integer userId, Integer month, Integer year){

        if(month < 1 || month >12){
            throw new IllegalArgumentException("Month must be between 1 and 12") ;
        }

        if(year < 2000 || year > 2100){
            throw new IllegalArgumentException("Invalid year") ;
        }

        BigDecimal totalAmount = expenseRepository.getMonthlyTotal(userId,month,year) ;

        Long totalTransactions = expenseRepository.countMonthlyExpense(userId, month, year) ;

        return new MonthlyReportResponse(month,year,totalAmount,totalTransactions) ;
    }


    // Category wise expense Report
    public List<CategoryReportResponse> getCategoryReport(Integer userId, LocalDate startDate,LocalDate endDate){
        if(startDate == null || endDate == null || !startDate.isBefore(endDate)){
            throw new IllegalArgumentException("Start date must be before end date") ;
        }

        return expenseRepository.getCategoryReport(userId,startDate, endDate) ;
    }

    // Expenses by Date range
    public DateRangeReportResponse getDateRangeReport(Integer userId, LocalDate startDate, LocalDate endDate){
        if(startDate == null ||endDate == null){
            throw new IllegalArgumentException("Start date and end date are required") ;
        }

        if(startDate.isAfter(endDate)){
            throw new IllegalArgumentException("Start date can not be after end date") ;
        }

        BigDecimal totalAmount = expenseRepository.getTotalByDateRange(userId,startDate,endDate) ;

        Long totalTransactions = expenseRepository.countByDateRange(userId,startDate,endDate) ;

        return new DateRangeReportResponse(startDate,endDate,totalAmount,totalTransactions) ;
    }

    // Pagination
    public Page<ExpenseResponse> getAllExpenses(Integer userId, Pageable pageable){
        return expenseRepository.findAllByUserId(userId, pageable)
                .map(this::toResponse) ;
    }
}
