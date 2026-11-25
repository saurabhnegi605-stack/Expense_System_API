package com.expensemanagement.expense_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

import java.security.SecureRandom;
import java.util.Base64;

@SpringBootApplication
public class ExpenseApiApplication{

	public static void main(String[] args) {

		SpringApplication.run(ExpenseApiApplication.class, args);
//		byte[] secret = new byte[32] ;
//		new SecureRandom().nextBytes(secret);
//
//		System.out.println(Base64.getEncoder().encodeToString(secret));
	}

}
