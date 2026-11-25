# Expense_System_API

A RESTful Expense Management System built using Java and Spring Boot. The application allows users to register, log in, and manage their personal expenses securely.

Features

- User registration and login
- JWT-based authentication
- Secure password storage
- User-specific expense management
- Create, read, update, and delete expenses
- Expense pagination
- Input validation
- Global exception handling
- API documentation using Swagger / OpenAPI
- Logging and application monitoring
- Database schema management using Flyway
- Unit and integration testing

Technologies Used

- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- JWT
- MySQL
- Flyway
- Maven
- Swagger / OpenAPI
- JUnit
- Mockito
- Git and GitHub

Database

The application uses MySQL.

Main tables:

- "users" — stores user account information
- "expense" — stores expense records associated with users

Getting Started

Prerequisites

Make sure you have installed:

- Java JDK
- Maven
- MySQL
- Git

Clone the Repository

git clone https://github.com/saurabhnegi605-stack/Expense_System_API.git

Move into the project directory:

cd expense-api

Configure the Database

Create a MySQL database for the application.

Configure your database connection in "application.properties" or through environment variables.

Example:

spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

jwt.secret=${JWT_SECRET}

Set the corresponding environment variables before starting the application.

Do not commit database passwords or JWT secrets to GitHub.

Run the Application

Using Maven:

mvn spring-boot:run

Or build the application:

mvn clean package

API Documentation

After starting the application, access the Swagger UI using the Swagger URL configured in the project.

Use the authentication endpoint to obtain a JWT token, then authorize requests in Swagger using the Bearer token.

Testing

Run the tests using:

mvn test

The project includes tests for authentication and expense-related API functionality.

Security

- Authentication is handled using JWT.
- Passwords are stored securely using password hashing.
- Users can access only their own expense records.
- Protected endpoints require authentication.

Author

Saurabh Kumar
