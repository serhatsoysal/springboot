![Build Status](https://github.com/serhatsoysal/distributed-banking-system/actions/workflows/ci.yml/badge.svg)

# Enterprise Banking Microservices Platform

Professional Spring Boot microservices architecture demonstrating senior-level design patterns, annotations, and best practices for banking domain.

## Architecture Overview

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │
       v
┌──────────────────┐     ┌──────────────────┐
│   API Gateway    │────▶│  Eureka Server   │
│   Port: 8080     │     │   Port: 8761     │
└────────┬─────────┘     └──────────────────┘
         │                       ▲
         │                       │
         ├───────────────────────┼──────────────────┐
         │                       │                  │
         v                       │                  v
┌────────────────┐              │         ┌─────────────────┐
│ Account Service│──────────────┘         │ Transaction Svc │
│  Port: 8081    │                        │   Port: 8082    │
│   PostgreSQL   │                        │    MongoDB      │
└────────┬───────┘                        └────────┬────────┘
         │                                         │
         └──────────────┬──────────────────────────┘
                        │
                        v
                  ┌───────────┐
                  │   Kafka   │
                  └─────┬─────┘
                        │
                        v
              ┌──────────────────┐
              │Notification Svc  │
              │  Port: 8083      │
              └──────────────────┘
```

## Technologies

- Java 21 (Virtual Threads)
- Spring Boot 3.2.1
- Spring Cloud 2023.0.0
- PostgreSQL 15
- MongoDB 7
- Apache Kafka 7.5
- Docker & Docker Compose

## Services

### 1. Config Server (Port: 8888)
Centralized configuration management for all microservices.

**Key Annotations:**
- `@EnableConfigServer` - Enables Spring Cloud Config Server
- `@SpringBootApplication` - Main application entry point

### 2. Eureka Server (Port: 8761)
Service discovery and registration server.

**Key Annotations:**
- `@EnableEurekaServer` - Enables Netflix Eureka service registry

### 3. API Gateway (Port: 8080)
Single entry point with routing, authentication, and circuit breakers.

**Key Annotations:**
- `@EnableDiscoveryClient` - Registers with Eureka
- `@Configuration` - Defines route configuration
- `@Bean` - Creates RouteLocator for custom routing
- `@Component` - Marks authentication filter
- `@Order` - Controls filter execution order

### 4. Account Service (Port: 8081)
Core banking operations with PostgreSQL.

**Domain Layer:**
- `@Entity` - Maps class to database table
- `@Table` - Specifies table name and indexes
- `@Id` - Marks primary key field
- `@GeneratedValue(strategy=IDENTITY)` - Auto-generates ID values
- `@Column` - Maps field to database column
- `@Enumerated(EnumType.STRING)` - Stores enum as string
- `@ManyToOne` - Defines many-to-one relationship
- `@OneToMany` - Defines one-to-many relationship
- `@DynamicUpdate` - Updates only modified columns
- `@CreatedDate` - Auto-populates creation timestamp
- `@LastModifiedDate` - Auto-updates modification timestamp
- `@EntityListeners` - Enables JPA auditing

**Repository Layer:**
- `@Repository` - Marks data access component
- `@EntityGraph` - Solves N+1 query problem by eager fetching
- `@Query` - Defines custom JPQL/SQL queries

**Service Layer:**
- `@Service` - Marks business logic component
- `@Transactional` - Manages database transactions
- `@Validated` - Enables method-level validation
- `@RateLimiter` - Limits request rate
- `@Bulkhead` - Isolates concurrent requests
- `@CircuitBreaker` - Prevents cascading failures
- `@TimeLimiter` - Sets timeout for operations
- `@Retry` - Retries failed operations

**Controller Layer:**
- `@RestController` - Combines @Controller and @ResponseBody
- `@RequestMapping` - Maps URL patterns to controller
- `@GetMapping` - Handles HTTP GET requests
- `@PostMapping` - Handles HTTP POST requests
- `@PutMapping` - Handles HTTP PUT requests
- `@DeleteMapping` - Handles HTTP DELETE requests
- `@PathVariable` - Extracts values from URL path
- `@RequestParam` - Extracts query parameters
- `@RequestBody` - Binds request body to object
- `@Valid` - Validates request data
- `@PreAuthorize` - Enforces method-level security

**Configuration:**
- `@Configuration` - Declares configuration class
- `@EnableJpaRepositories` - Enables JPA repositories
- `@EnableJpaAuditing` - Enables auditing for entities
- `@EnableConfigurationProperties` - Enables type-safe configuration
- `@ConfigurationProperties` - Binds properties to POJO
- `@EnableWebSecurity` - Enables Spring Security
- `@EnableMethodSecurity` - Enables method-level security
- `@ConditionalOnProperty` - Conditionally loads configuration
- `@EnableAsync` - Enables asynchronous processing

**AOP:**
- `@Aspect` - Declares aspect class
- `@Component` - Registers aspect as Spring bean
- `@Around` - Wraps method execution
- `@Before` - Executes before method
- `@AfterReturning` - Executes after successful method
- `@AfterThrowing` - Executes after exception

**Exception Handling:**
- `@RestControllerAdvice` - Global exception handler
- `@ExceptionHandler` - Handles specific exceptions
- Returns `ProblemDetail` (RFC 7807 standard)

**Messaging:**
- `@Service` - Kafka producer component
- Uses `KafkaTemplate` for publishing events

**Actuator:**
- `@Endpoint` - Creates custom actuator endpoint
- `@ReadOperation` - Exposes read endpoint
- `@WriteOperation` - Exposes write endpoint
- Implements `HealthIndicator` - Custom health check

**Validation:**
- `@NotNull` - Field cannot be null
- `@NotBlank` - String cannot be empty
- `@Email` - Validates email format
- `@Min` - Minimum numeric value
- `@Max` - Maximum numeric value
- `@Pattern` - Validates regex pattern
- `@DecimalMin` - Minimum decimal value
- `@DecimalMax` - Maximum decimal value

### 5. Transaction Service (Port: 8082)
Transaction management with MongoDB and reactive programming.

**Domain Layer:**
- `@Document` - Maps class to MongoDB collection
- `@Id` - Marks document identifier
- `@Indexed` - Creates MongoDB index
- `@CompoundIndex` - Creates compound index
- `@Field` - Maps field to document property

**Repository Layer:**
- Extends `ReactiveMongoRepository` - Reactive data access
- Returns `Mono<T>` - Single reactive result
- Returns `Flux<T>` - Multiple reactive results

**Controller Layer:**
- `@RestController` - REST endpoint
- Returns reactive types for non-blocking operations

**Client:**
- `@FeignClient` - Declarative HTTP client
- Integrates with Eureka for load balancing

### 6. Notification Service (Port: 8083)
Event-driven notifications with Kafka consumer.

**Configuration:**
- `@Component` - Marks configuration component
- `@RefreshScope` - Allows dynamic config refresh
- `@ConfigurationProperties` - Type-safe configuration

**Messaging:**
- `@Component` - Kafka consumer component
- `@KafkaListener` - Consumes Kafka messages
- `@RetryableTopic` - Automatic retry with DLT
- Uses `ErrorHandlingDeserializer` - Handles poison pills

**Service:**
- `@Service` - Notification logic
- `@Async` - Asynchronous execution with virtual threads

## Annotation Reference

| Category | Annotation | Purpose |
|----------|-----------|---------|
| **Core** | `@SpringBootApplication` | Main application entry combining @Configuration, @EnableAutoConfiguration, @ComponentScan |
| | `@Configuration` | Declares configuration class for bean definitions |
| | `@Bean` | Registers method return value as Spring bean |
| | `@Component` | Generic stereotype for Spring-managed component |
| **Stereotype** | `@Service` | Business logic layer component |
| | `@Repository` | Data access layer component with exception translation |
| | `@RestController` | REST controller combining @Controller and @ResponseBody |
| **DI** | Constructor injection | Preferred dependency injection method (no @Autowired needed for single constructor) |
| **Web** | `@RequestMapping` | Maps HTTP requests to handler methods |
| | `@GetMapping` | Shortcut for GET requests |
| | `@PostMapping` | Shortcut for POST requests |
| | `@PathVariable` | Extracts URI template variables |
| | `@RequestParam` | Binds query parameters |
| | `@RequestBody` | Binds HTTP request body to object |
| **Validation** | `@Valid` | Triggers JSR-380 validation |
| | `@Validated` | Spring validation with group support |
| | `@NotNull` | Field required constraint |
| | `@Email` | Email format constraint |
| **JPA** | `@Entity` | JPA entity class |
| | `@Table` | Specifies table details |
| | `@Id` | Primary key field |
| | `@GeneratedValue` | Auto-generated ID strategy |
| | `@Column` | Column mapping details |
| | `@Enumerated` | Enum persistence strategy |
| | `@ManyToOne` | Many-to-one relationship |
| | `@OneToMany` | One-to-many relationship |
| | `@EntityGraph` | Optimizes fetching strategy, solves N+1 |
| | `@DynamicUpdate` | Updates only changed columns |
| | `@Query` | Custom JPQL/native queries |
| **Transaction** | `@Transactional` | Manages transaction boundaries |
| **MongoDB** | `@Document` | MongoDB document entity |
| | `@Indexed` | Creates MongoDB index |
| | `@CompoundIndex` | Multi-field index |
| **Microservices** | `@EnableConfigServer` | Enables config server |
| | `@EnableEurekaServer` | Enables service registry |
| | `@EnableDiscoveryClient` | Registers with service registry |
| | `@FeignClient` | Declarative HTTP client |
| **Resilience** | `@RateLimiter` | Limits request rate |
| | `@Bulkhead` | Isolates concurrent calls |
| | `@CircuitBreaker` | Prevents cascading failures |
| | `@TimeLimiter` | Sets operation timeout |
| | `@Retry` | Retries failed operations |
| **Security** | `@EnableWebSecurity` | Enables Spring Security |
| | `@EnableMethodSecurity` | Enables method-level authorization |
| | `@PreAuthorize` | Pre-method authorization check |
| **AOP** | `@Aspect` | Declares aspect class |
| | `@Around` | Wraps method execution |
| | `@Before` | Executes before method |
| | `@AfterReturning` | Executes after success |
| | `@AfterThrowing` | Executes after exception |
| **Kafka** | `@KafkaListener` | Kafka message consumer |
| | `@RetryableTopic` | Automatic retry with DLT |
| **Actuator** | `@Endpoint` | Custom actuator endpoint |
| | `@ReadOperation` | GET endpoint operation |
| | `@WriteOperation` | POST endpoint operation |
| **Config** | `@ConfigurationProperties` | Type-safe configuration binding |
| | `@RefreshScope` | Dynamic config refresh without restart |
| | `@ConditionalOnProperty` | Conditional bean loading |
| **Testing** | `@SpringBootTest` | Full application context test |
| | `@WebMvcTest` | Web layer slice test |
| | `@DataJpaTest` | JPA layer slice test |
| | `@MockBean` | Replaces bean with mock |
| | `@Testcontainers` | Docker containers for tests |
| | `@ServiceConnection` | Auto-configures Testcontainers |
| **Virtual Threads** | `spring.threads.virtual.enabled` | Enables Java 21 virtual threads |

## Running the Project

### Prerequisites
- Java 21
- Maven 3.8+
- Docker & Docker Compose

### Setup

**1. Environment Configuration:**
```bash
# Copy the example environment file
cp .env.example .env

# Generate a secure JWT secret (Linux/Mac)
openssl rand -base64 64

# Or use PowerShell (Windows)
$rng = New-Object System.Security.Cryptography.RNGCryptoServiceProvider
$bytes = New-Object byte[] 64
$rng.GetBytes($bytes)
[Convert]::ToBase64String($bytes)

# Edit .env and replace JWT_SECRET with your generated key
notepad .env  # Windows
nano .env     # Linux/Mac
```

**2. Start Infrastructure:**
```bash
docker-compose up -d
```

**3. Start Services (in order):**
```bash
# Terminal 1 - Config Server
cd config-server
mvn spring-boot:run

# Terminal 2 - Eureka Server (wait 30s)
cd eureka-server
mvn spring-boot:run

# Terminal 3 - Account Service (wait 30s)
cd account-service
mvn spring-boot:run

# Terminal 4 - Transaction Service
cd transaction-service
mvn spring-boot:run

# Terminal 5 - Notification Service
cd notification-service
mvn spring-boot:run

# Terminal 6 - API Gateway (optional)
cd api-gateway
mvn spring-boot:run
```

### Access Points
- Eureka Dashboard: http://localhost:8761
- API Gateway: http://localhost:8080
- Account Service: http://localhost:8081
- Transaction Service: http://localhost:8082
- Notification Service: http://localhost:8083
- Config Server: http://localhost:8888

## API Examples

### Create Customer
```bash
curl -X POST http://localhost:8080/api/v1/customers \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phoneNumber": "1234567890"
  }'
```

### Create Account
```bash
curl -X POST http://localhost:8080/api/v1/accounts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "customerId": 1,
    "accountNumber": "1234567890",
    "type": "SAVINGS",
    "initialBalance": 1000.00,
    "currency": "USD"
  }'
```

### Get Account
```bash
curl http://localhost:8080/api/v1/accounts/1 \
  -H "Authorization: Bearer <token>"
```

### Transfer Money
```bash
curl -X POST http://localhost:8080/api/v1/transfers \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "fromAccountNumber": "1234567890",
    "toAccountNumber": "9876543210",
    "amount": 100.00,
    "description": "Payment"
  }'
```

## Testing

### Run All Tests
```bash
mvn test
```

### Run Integration Tests
```bash
mvn verify -P integration-tests
```

## Environment Configuration

All configuration is in `.env` file:
- Database settings (PostgreSQL, MongoDB)
- Kafka configuration
- Service ports
- JWT secret
- Resilience4j settings

Default `.env` file is ready to use. Customize if needed.

## Key Features

### 1. Virtual Threads (Java 21)
Enabled in Account and Notification services for improved scalability.

### 2. RFC 7807 Problem Details
Standardized error responses across all services.

### 3. N+1 Query Optimization
Using `@EntityGraph` to fetch related entities efficiently.

### 4. Resilience4j Patterns
Proper ordering: RateLimiter → Bulkhead → CircuitBreaker → TimeLimiter → Retry

### 5. Testcontainers
Production-like testing with real PostgreSQL, MongoDB, and Kafka.

### 6. Reactive Programming
WebFlux in Transaction Service for non-blocking operations.

### 7. Event-Driven Architecture
Kafka for asynchronous communication between services.

### 8. Custom AOP Aspects
Performance monitoring and audit logging.

### 9. Custom Actuator Endpoints
Business-specific health checks and statistics.

### 10. Method-Level Security
Fine-grained authorization with SpEL expressions.

## Architecture Highlights

- **No Anti-Patterns**: Constructor injection only, no self-invocation traps
- **Production-Ready**: Docker Compose, health checks, metrics
- **Proper Layering**: Clear separation of concerns
- **Advanced JPA**: @EntityGraph, @DynamicUpdate optimizations
- **Modern Standards**: RFC 7807, Virtual Threads, WebFlux
- **Comprehensive Testing**: Integration + Slicing + Testcontainers
- **Observability**: Custom endpoints, Micrometer metrics
- **Event-Driven**: Kafka with retry topics and DLT
- **Centralized Configuration**: Environment-based configuration with .env file

## Project Structure

```
springboot/
├── config-server/              # Centralized configuration
├── eureka-server/             # Service discovery
├── api-gateway/               # API Gateway with filters
├── account-service/           # PostgreSQL + JPA + Security
├── transaction-service/       # MongoDB + WebFlux + Feign
├── notification-service/      # Kafka consumer + Async
├── shared-lib/               # Common DTOs and utilities
├── docker-compose.yml        # Infrastructure services
├── .env                      # Environment configuration
└── README.md                 # This file
```

## License

This project is created for educational purposes demonstrating enterprise Spring Boot patterns.

