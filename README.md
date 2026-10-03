# Resilient Order & Payment Platform

A Spring Boot backend implementing idempotent order and payment processing, transactional inventory reservation, JWT authentication, role-based authorization, and concurrency-safe order management using JPA/Hibernate and MySQL.

---

## Tech Stack

- **Java 21** / Spring Boot 4.1.0
- **Spring Security** — JWT authentication, role-based access control
- **Spring Data JPA** / Hibernate — ORM, optimistic locking
- **MySQL** — persistent storage
- **JUnit 5** / Mockito — unit testing
- **springdoc-openapi** — Swagger UI
- **Docker** / docker-compose — containerized deployment

---

## Architecture

```
Client
  │
  ├── POST /api/auth/register
  ├── POST /api/auth/login
  │
  ├── GET  /api/products          (public)
  ├── POST /api/products          (ADMIN only)
  ├── PUT  /api/products/{id}     (ADMIN only)
  │
  ├── POST /api/orders            (authenticated, requires Idempotency-Key header)
  ├── GET  /api/orders            (authenticated)
  ├── GET  /api/orders/{id}       (owner or ADMIN)
  │
  ├── POST /api/payments/{orderId} (authenticated)
  └── GET  /api/payments/{id}     (authenticated)
```

---

## Key Design Decisions

### 1. Idempotency

Every `POST /api/orders` request requires an `Idempotency-Key` header. The system:

1. SHA-256 hashes the request payload and stores it against the key in the `idempotency_keys` table
2. On a duplicate request with the **same key and same payload** — returns the original order without reprocessing
3. On a duplicate request with the **same key but different payload** — rejects with `409 Conflict`

This guarantees that a retried request (due to network failure, timeout, etc.) never creates a duplicate order or double charge.

### 2. Optimistic Locking

The `Product` entity has a `@Version` field managed by Hibernate. When two concurrent transactions attempt to update the same product row (e.g. two simultaneous orders for the last unit of stock):

- The first transaction to commit wins
- The second receives an `OptimisticLockException`, mapped to `409 Conflict` by the global exception handler

This prevents overselling without the cost of pessimistic row-level locks.

### 3. Transaction Boundaries

`OrderService.createOrder()` runs inside a single `@Transactional` boundary. This means:

- Stock deduction across all order items is atomic
- If any item fails (insufficient stock, product not found), the entire order rolls back
- The idempotency key is only persisted after a successful order save — no orphaned keys

### 4. Payment State Machine

Payment status follows a strict lifecycle:

```
PENDING → PROCESSING → SUCCESS
                     → FAILED
```

Status is never overwritten in place — each transition is timestamped via `@PreUpdate`. A payment record is immutable once `SUCCESS` or `FAILED` is reached. Calling `POST /api/payments/{orderId}` on an already-processed order returns the existing payment (idempotent).

---

## Running Locally

### Prerequisites
- Java 21
- MySQL 8.0
- Maven

### Setup

1. Create the database:
```sql
CREATE DATABASE order_payment_db;
```

2. Set environment variables:
```
DB_PASSWORD=your_mysql_password
APP_JWT_SECRET=your_256_bit_secret_key
```

3. Run the app:
```bash
./mvnw spring-boot:run
```

4. Access Swagger UI:
```
http://localhost:8080/swagger-ui/index.html
```

---

## Running with Docker

```bash
docker-compose up --build
```

Requires `DB_PASSWORD` and `APP_JWT_SECRET` to be set as system environment variables.

The stack starts MySQL first, waits for the healthcheck to pass, then starts the app.

---

## Running Tests

```bash
./mvnw test
```

15 tests covering:
- Product CRUD service logic
- Order creation, total calculation, idempotency, insufficient stock
- Idempotency filter (header presence/absence)
- Concurrency — two simultaneous requests for last unit of stock
- Payment state machine — success, duplicate prevention, order not found

---

## API Authentication

1. Register: `POST /api/auth/register`
2. Login: `POST /api/auth/login` — returns a JWT token
3. Add the token to subsequent requests: `Authorization: Bearer <token>`

---

## Roles

| Role | Permissions |
|------|-------------|
| `USER` | Place orders, view own orders, trigger payments |
| `ADMIN` | All USER permissions + create/update products |
