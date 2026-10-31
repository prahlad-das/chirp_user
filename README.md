# Chirp User Service

A high-performance user management microservice built for the **Chirp** distributed system. It provides user identity registration and profile retrieval via both **REST HTTP** endpoints and internal **gRPC** interfaces, backed by **PostgreSQL** for persistence and **Redis** for distributed caching.

---

## Table of Contents
- [Architecture & Tech Stack](#architecture--tech-stack)
- [Key Features](#key-features)
- [Project Structure](#project-structure)
- [Prerequisites & Dependencies](#prerequisites--dependencies)
- [Configuration](#configuration)
- [Build and Run](#build-and-run)
- [API Documentation](#api-documentation)
  - [REST Endpoints](#rest-endpoints)
  - [gRPC Services](#grpc-services)
- [Caching Strategy](#caching-strategy)
- [Security](#security)
- [Database Schema](#database-schema)

---

## Architecture & Tech Stack

- **Java**: 21 (LTS)
- **Framework**: Spring Boot 4.1.2
- **Inter-service RPC**: gRPC (`spring-boot-starter-grpc-server`, Protobuf v3)
- **Web MVC**: Spring Web MVC REST API
- **Database**: PostgreSQL with Spring Data JPA & Hibernate
- **Caching**: Redis with Spring Data Redis & Cache Manager
- **Security**: Spring Security with BCrypt password hashing & API Key Filter
- **Build Tool**: Maven with `io.github.ascopes:protobuf-maven-plugin`
- **Utilities**: Project Lombok, Jakarta Validation, Jackson JSON

---

## Key Features

1. **Dual Protocol Support**:
   - HTTP/REST interface (`/api/v1/users/*`) for frontend and gateway access.
   - High-throughput gRPC service (`UserService`) for low-latency internal microservice communication.
2. **Multi-level Redis Caching**:
   - Read-through caching for user lookups by both UUID (`users`) and normalized email (`users-email`).
   - JSON serialization for cache payloads with a 5-minute time-to-live (TTL) and time-to-idle support.
3. **Secure Authentication & Password Hashing**:
   - Password encryption using Spring Security's `BCryptPasswordEncoder`.
   - Extensible API key-based authentication (`X-API-KEY` header).
4. **Protobuf & Code Generation**:
   - Automated code generation from Protocol Buffer definitions (`User.proto`).
   - Clean, null-safe protobuf message conversion (`UserConvert`).

---

## Project Structure

```text
chirp_user/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── proto/
    │   │   └── User.proto                             # Protobuf definitions for gRPC
    │   ├── resources/
    │   │   └── application.yaml                       # Application configuration
    │   └── java/com/edgesoft/chirp/
    │       ├── ChirpApplication.java                 # Spring Boot application entry point
    │       ├── config/
    │       │   ├── CacheConfig.java                   # Redis cache manager & serializers
    │       │   └── SecurityConfig.java                # Spring Security & filter chain config
    │       ├── controller/
    │       │   └── UserController.java                # REST controller for user APIs
    │       ├── dto/
    │       │   ├── ErrorResponse.java                 # Standard error envelope
    │       │   ├── LoginRequest.java                  # User credentials DTO
    │       │   ├── RegisterRequest.java               # Registration payload DTO
    │       │   ├── RegisterResponse.java              # Registration response record
    │       │   └── UserResponse.java                  # Cached user detail response DTO
    │       ├── entity/
    │       │   └── User.java                          # JPA Entity mapped to 'users' table
    │       ├── exception/
    │       │   └── GlobalExceptionHandler.java        # Centralized HTTP error handling
    │       ├── grpc/
    │       │   └── UserServiceGrpcServer.java         # gRPC server implementation
    │       ├── repository/
    │       │   └── UserRepository.java                # Spring Data JPA repository
    │       ├── security/
    │       │   ├── ApiKeyAuthenticationProvider.java  # Custom API Key auth provider
    │       │   ├── ApiKeyAuthenticationToken.java     # Authentication token wrapper
    │       │   └── ApiKeyFilter.java                  # Once-per-request API Key filter
    │       ├── service/
    │       │   └── UserService.java                   # Core business logic & cache orchestration
    │       └── utils/
    │           └── UserConvert.java                   # Model to Protobuf conversion utility
    └── test/
        └── java/com/edgesoft/chirp/
            └── ChirpApplicationTests.java            # Context load tests
```

---

## Prerequisites & Dependencies

Before running this service, ensure you have the following installed and running:
- **Java**: OpenJDK / Oracle JDK 21+
- **Maven**: 3.9+
- **PostgreSQL**: 14+ running on port `5432` with a database named `people`
- **Redis**: 6+ running on `localhost:6379`

---

## Configuration

Application configuration is managed in [src/main/resources/application.yaml](src/main/resources/application.yaml):

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
  datasource:
    url: jdbc:postgresql://localhost:5432/people
    driver-class-name: org.postgresql.Driver
    # username: postgres
    # password: password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
  application:
    name: chirp

app:
  security:
    api-key-header: x-api-key
    api-key: "your-super-secure-secret-api-key-here"
```

> **Tip**: You can override any of these properties using standard Spring environment variables (e.g. `SPRING_DATASOURCE_URL`, `SPRING_DATA_REDIS_HOST`, `APP_SECURITY_API_KEY`).

---

## Build and Run

### 1. Compile & Generate Protobuf Stubs
The project uses `io.github.ascopes:protobuf-maven-plugin` to generate Java code from `User.proto`:
```bash
mvn clean compile
```

### 2. Run Tests
```bash
mvn test
```

### 3. Start the Application
```bash
mvn spring-boot:run
```
By default:
- **HTTP REST Server** listens on `http://localhost:8080`
- **gRPC Server** listens on port `9090` (default Spring Boot gRPC server port)

---

## API Documentation

### REST Endpoints

Base path: `/api/v1/users`

#### 1. Register a New User
- **Method**: `POST`
- **URL**: `/api/v1/users/register`
- **Headers**: `Content-Type: application/json`
- **Request Body**:
  ```json
  {
    "firstName": "Ada",
    "lastName": "Lovelace",
    "email": "ada@chirp.io",
    "password": "Password123!",
    "matchingPassword": "Password123!"
  }
  ```
- **Response**: `200 OK`
  ```json
  {
    "firstName": "Ada",
    "lastName": "Lovelace",
    "email": "ada@chirp.io"
  }
  ```

#### 2. Get User by Email
- **Method**: `GET`
- **URL**: `/api/v1/users/email?email={email}`
- **Cache**: Results cached in Redis (`users-email`)
- **Response**: `200 OK`
  ```json
  {
    "userId": "c56a4180-65aa-42ec-a945-5fd21dec0538",
    "firstName": "Ada",
    "lastName": "Lovelace",
    "bio": null,
    "location": null,
    "createdAt": null,
    "date_of_birth": null,
    "followers": 0,
    "following": 0,
    "email": "ada@chirp.io"
  }
  ```

#### 3. Get User by UUID
- **Method**: `GET`
- **URL**: `/api/v1/users/id?uuid={uuid}`
- **Cache**: Results cached in Redis (`users`)
- **Response**: `200 OK`
  ```json
  {
    "userId": "c56a4180-65aa-42ec-a945-5fd21dec0538",
    "firstName": "Ada",
    "lastName": "Lovelace",
    "bio": null,
    "location": null,
    "createdAt": null,
    "date_of_birth": null,
    "followers": 0,
    "following": 0,
    "email": "ada@chirp.io"
  }
  ```

---

### gRPC Services

The service defines `UserService` inside [src/main/proto/User.proto](src/main/proto/User.proto):

```protobuf
syntax = "proto3";

import "google/protobuf/timestamp.proto";

option java_multiple_files = true;
option java_package = "com.edgesoft.chirp.proto";
option java_outer_classname = "UserServiceProto";

service UserService {
  rpc GetUserByEmail(UserEmail) returns (UserReply) {}
  rpc GetUserById(UserId) returns (UserReply) {}
}

message UserEmail {
  string email = 1;
}

message UserId {
  string uuid = 1;
}

message UserReply {
  string userId = 1;
  string firstName = 2;
  string lastName = 3;
  string bio = 4;
  string location = 5;
  google.protobuf.Timestamp createdAt = 6;
  google.protobuf.Timestamp dob = 7;
  int32 followers = 8;
  int32 following = 9;
  string email = 10;
}
```

#### Example gRPC Invocation (using `grpcurl`)
```bash
# Query user by email
grpcurl -plaintext -d '{"email": "ada@chirp.io"}' \
  localhost:9090 com.edgesoft.chirp.proto.UserService/GetUserByEmail

# Query user by UUID
grpcurl -plaintext -d '{"uuid": "c56a4180-65aa-42ec-a945-5fd21dec0538"}' \
  localhost:9090 com.edgesoft.chirp.proto.UserService/GetUserById
```

---

## Caching Strategy

The service implements a high-throughput caching layer with **Spring Data Redis**:
- **Cache Names**:
  - `users`: Keyed on User UUID (`#id`)
  - `users-email`: Keyed on normalized email string (`#email`)
- **Key Serialization**: `StringRedisSerializer`
- **Value Serialization**: `GenericJackson2JsonRedisSerializer` (`RedisSerializer.json()`)
- **TTL**: 5 minutes (`Duration.ofMinutes(5)`) with time-to-idle enabled
- **Null Caching**: Disabled to prevent caching nonexistent lookups

---

## Security

1. **Password Encryption**:
   Passwords submitted during registration are encoded with `BCryptPasswordEncoder` before database persistence.
2. **API Key Filter**:
   Requests to protected routes are filtered by `ApiKeyFilter`, reading the configured `X-API-KEY` header and validating through `ApiKeyAuthenticationProvider`.
3. **Public Routes**:
   `/api/v1/users/*` and `/error` endpoints are permitted by default for user registration and lookups in the microservice topology.

---

## Database Schema

Entity: `User` (`users` table)

| Column | Type | Constraints | Description |
|---|---|---|---|
| `userId` | `UUID` | Primary Key | Unique user identifier |
| `firstName` | `VARCHAR(100)` | NOT NULL | User's first name |
| `lastName` | `VARCHAR(255)` | NULL | User's last name |
| `email` | `VARCHAR(255)` | NOT NULL, UNIQUE, Indexed | User's unique email address |
| `password` | `VARCHAR(255)` | NOT NULL | BCrypt encrypted password hash |
| `bio` | `VARCHAR(255)` | NULL | User bio/description |
| `location` | `VARCHAR(255)` | NULL | User geographical location |
| `date_of_birth` | `TIMESTAMP` | NULL | User date of birth |
| `createdAt` | `TIMESTAMP` | NULL | Account creation timestamp |
| `followers` | `INTEGER` | Default `0` | Denormalized count of followers |
| `following` | `INTEGER` | Default `0` | Denormalized count of following users |

---

## Development & Contribution Guidelines

1. **Protobuf Updates**:
   When modifying `User.proto`, run `mvn clean compile` to refresh generated stubs before updating `UserServiceGrpcServer` or `UserConvert`.
2. **Data Model Conversions**:
   Keep model-to-protobuf conversions isolated within [UserConvert.java](src/main/java/com/edgesoft/chirp/utils/UserConvert.java) to preserve clean decoupling between domain entities and transport DTOs.
