package com.expensemanagement.expense_api.Repository;

import com.expensemanagement.expense_api.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {

    Optional<User> findByEmail(String email) ;

    Boolean existsByEmail(String email) ;


}
