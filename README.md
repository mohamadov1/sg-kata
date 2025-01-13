
# Bank Account Kata

## Description

Bank Account Kata is a Java project based on Spring Boot (version 3.1.4) that simulates basic banking operations. It provides a REST API to manage a bank account, perform deposits, withdrawals, and check the balance.

## Features

- **Create bank accounts**
- **Deposit money into an account**
- **Withdraw money from an account**
- **Check account balance**
- **Data validation**
- **Automatic API documentation with Swagger**

## Prerequisites

- **Java 21** (Ensure you have version 21 installed)
- **Maven 3.8.1** or higher
- **IDE** (IntelliJ IDEA, Eclipse, or any Maven-compatible IDE)

## Installation and Execution

### Step 1: Clone the repository

```bash
git clone https://github.com/mohamadov1/sg-kata.git
cd sg-kata
```

### Step 2: Build the project

Run the following command to download dependencies and compile the project:

```bash
mvn clean install
```

### Step 3: Run the application

Start the Spring Boot application using:

```bash
mvn spring-boot:run
```

The application will be available at [http://localhost:8080](http://localhost:8080).

## API Documentation

Swagger API documentation is available once the application is running at:

[http://localhost:8080/swagger-ui/](http://localhost:8080/swagger-ui/)

## Key Dependencies

- **Spring Boot Starter Data JPA**: Data management and ORM
- **Spring Boot Starter Web**: REST API
- **Spring Boot Starter Validation**: Data validation
- **H2 Database**: Embedded database for testing
- **Lombok**: Reduces boilerplate code
- **Springfox Boot Starter**: Swagger documentation
- **JUnit 5**: Testing framework
- **AssertJ**: Advanced testing assertions

## Testing

To run the unit tests, use:

```bash
mvn test
```

## Configuration

The main configuration file is located at `src/main/resources/application.properties`. You can configure the database, application port, and other properties there.



## License

This project is licensed under the MIT License. See the `LICENSE` file for more details.
