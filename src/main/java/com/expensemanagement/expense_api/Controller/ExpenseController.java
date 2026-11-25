package com.expensemanagement.expense_api.Controller;

import com.expensemanagement.expense_api.DTO.*;
import com.expensemanagement.expense_api.Entity.Expense;
import com.expensemanagement.expense_api.Service.ExpenseService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expense")
@Tag(
        name = "Expense Management",
        description = "APIs for managing personal expenses"
)
@SecurityRequirement(name = "bearerAuth")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService){
        this.expenseService = expenseService ;
    }

    private Integer getLoggedUserId(Authentication authentication){
         return (Integer) authentication.getPrincipal() ;
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse createExpense(@Valid @RequestBody CreateExpenseRequest request, Authentication authentication){
        Integer userId = getLoggedUserId(authentication) ;
        return expenseService.createExpense(request, userId) ;
    }

    @GetMapping()
    public Page<ExpenseResponse> getAllExpense(Authentication authentication,
        @PageableDefault(size = 10,sort = "expenseDate",direction = Sort.Direction.DESC)Pageable pageable){

        Integer userId = getLoggedUserId(authentication) ;
        return expenseService.getAllExpenses(userId ,pageable) ;
    }

    @GetMapping("/{id}")
    public ExpenseResponse getExpenseById(@PathVariable Integer id, Authentication authentication){
        Integer userId = getLoggedUserId(authentication) ;
        return expenseService.getExpenseById(id ,userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteExpense(@PathVariable Integer id, Authentication authentication){
        Integer userId = getLoggedUserId(authentication) ;
        expenseService.deleteExpense(id, userId);
    }

    @PutMapping("{id}")
    public ResponseEntity<ExpenseResponse> updateExpense
            (@PathVariable Integer id, @Valid @RequestBody UpdateExpenseRequest request, Authentication authentication)
    {
        Integer userId = (Integer) authentication.getPrincipal() ;
        ExpenseResponse response = expenseService.updateExpense(id, userId, request) ;

        return ResponseEntity.ok(response) ;
    }

    @GetMapping("/reports/monthly")
    public ResponseEntity<MonthlyReportResponse> getMonthlyReport
            (@RequestParam Integer month, @RequestParam Integer year, Authentication authentication)
    {
        Integer userId = (Integer) authentication.getPrincipal() ;
        MonthlyReportResponse reportResponse = expenseService.getMonthlyReport(userId,month, year) ;

        return ResponseEntity.ok(reportResponse) ;
    }

    @GetMapping("/reports/category")
    public ResponseEntity<List<CategoryReportResponse>> getCategoryReport(
            @RequestParam LocalDate startDate,@RequestParam LocalDate endDate, Authentication authentication)
    {
        Integer userId = (Integer) authentication.getPrincipal() ;
        List<CategoryReportResponse> response = expenseService.getCategoryReport(userId,startDate,endDate) ;

        return ResponseEntity.ok(response) ;
    }

    @GetMapping("/reports/date-range")
    public ResponseEntity<DateRangeReportResponse> getDateRangeReport(
            @RequestParam LocalDate startDate,@RequestParam LocalDate endDate, Authentication authentication)
    {
        Integer userId = (Integer) authentication.getPrincipal() ;
        DateRangeReportResponse response = expenseService.getDateRangeReport(userId,startDate,endDate) ;

        return ResponseEntity.ok(response) ;
    }
}
