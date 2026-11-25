package com.expensemanagement.expense_api.Repository;

import com.expensemanagement.expense_api.DTO.CategoryReportResponse;
import com.expensemanagement.expense_api.Entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Integer> {

    List<Expense> findAllByUserIdOrderByExpenseDateDesc(Integer userID);

    Optional<Expense> findByIdAndUserId(Integer id, Integer userId) ;

    Page<Expense> findAllByUserId(Integer userId, Pageable pageable) ;

    // Monthly expenses total
    @Query("""
            SELECT COALESCE(SUM(e.amount), 0) FROM Expense e
            WHERE e.userId = :userId
            AND MONTH(e.expenseDate) = :month
            AND YEAR(e.expenseDate) = :year
            """)
    BigDecimal getMonthlyTotal(@Param("userId") Integer userId,
                               @Param("month") Integer month,
                               @Param("year") Integer year
    ) ;

    // Count total number of transactions in a month
    @Query("""
            SELECT COUNT(e) FROM Expense e
            WHERE e.userId = :userId
            AND MONTH(e.expenseDate) = :month
            AND YEAR(e.expenseDate) = :year
            """)
    Long countMonthlyExpense( @Param("userId") Integer userId,
                                    @Param("month") Integer month,
                                    @Param("year") Integer year
    ) ;

    @Query("""
           SELECT new com.expensemanagement.expense_api.DTO.CategoryReportResponse(
                        e.category, SUM(e.amount), COUNT(e)
                        )
           FROM Expense e
           WHERE e.userId = :userId
           AND e.expenseDate >= :startDate
           AND e.expenseDate < :endDate
           GROUP BY e.category
           ORDER BY SUM(e.amount) DESC
            """)
    List<CategoryReportResponse> getCategoryReport(
            @Param("userId") Integer userId,
            @Param("startDate")LocalDate startDate,
            @Param("endDate")LocalDate endDate
            ) ;

    @Query("""
            SELECT COALESCE(SUM(e.amount), 0) FROM Expense e
            WHERE e.userId = :userId
            AND e.expenseDate >= :startDate
            AND e.expenseDate <= :endDate
            """)
    BigDecimal getTotalByDateRange(
            @Param("userId") Integer userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    ) ;

    @Query("""
            SELECT COUNT(e) FROM Expense e
            WHERE e.userId = :userId
            AND e.expenseDate >= :startDate
            AND e.expenseDate <= :endDate
            """)
    Long countByDateRange( @Param("userId") Integer userId,
                           @Param("startDate") LocalDate startDate,
                           @Param("endDate") LocalDate endDate
    ) ;
}
