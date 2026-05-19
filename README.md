# CEX Backend System

A role-based backend system that simulates controlled financial operations by combining traditional banking workflows with crypto asset management.

## Table of Contents

- [Project Overview](#project-overview)
- [Domain Model](#domain-model)
- [Roles and Responsibilities](#roles-and-responsibilities)
- [Requirements and Design Approach](#requirements-and-design-approach)
- [Design Decisions](#design-decisions)
- [API Overview](#api-overview)
- [Exception Handling](#exception-handling)
- [Example Flow](#example-flow)

## Project Overview

This project is the backend of a role-based system that combines classic banking workflows and crypto asset management.

The system models a controlled environment where financial operations are not executed directly. Instead, they go through a structured process involving explicit business rules, clearly defined roles, strict access control, and approval-based transaction workflows.

The primary goal of the project is not to build a production-ready exchange, but to design and implement a consistent domain model that reflects how such a system could behave under real-world constraints.

Key capabilities of the system include:

- Organizational hierarchy management: `Center -> Branch -> Employee`
- Customer and wallet management
- Multi-asset wallet structure based on crypto assets
- Transaction request and approval workflow
- Deposit and withdrawal operations
- Asset conversion with fee handling
- Immutable transaction recording
- Role-based data scoping and authorization

The project is implemented as a modular monolith, with a focus on clarity of design rather than distribution complexity.

## Domain Model

The system is divided into two main domains:

- Organization Domain
- Banking Domain

### Organization Domain

This domain defines the structure of the system and determines how user access is scoped.

```text
Center -> Branch -> Employee
```

- A `Center` is a high-level organizational unit.
- A `Branch` operates under a specific center.
- An `Employee` belongs either to a center or a branch depending on their role.

This hierarchy is critical because all access control decisions are derived from it.

### Banking Domain

This domain handles financial entities and operations.

```text
Customer -> Wallet -> WalletAsset
                     └── Coin + Amount
```

- A `Customer` is a global entity and is not directly tied to a branch.
- A `Wallet` belongs to a specific branch.
- A `WalletAsset` represents a specific coin balance within a wallet.

Wallets store multiple assets instead of having a single balance. This allows:

- Multi-currency support
- Precise validation
- More realistic crypto behavior

### Transaction Model

The system does not allow direct transfers between wallets. Instead, it enforces a two-step process:

```text
TransactionRequest -> Approval -> Transaction
```

- A `TransactionRequest` represents an intention to transfer assets.
- A `Transaction` is created only after approval.
- Transactions are immutable once created.

## Roles and Responsibilities

The system is strictly role-driven. Each role has clearly defined responsibilities and access boundaries.

### ORG_ADMIN

Has global authority within the system. This role is responsible for system setup and can create and manage centers, branches, and employees. It has unrestricted access to all data.

### BRANCH_OPERATOR

This role is the main entry point for financial activity in the system.

Can:

- Create customers
- Create wallets
- Perform deposits and withdrawals in USDT
- Convert assets between coins
- Create transaction requests
- Track their own transactions

### CENTER_OPERATOR

Responsible for approving or rejecting transaction requests. This role cannot create transactions directly, which ensures separation between creation and approval.

### BRANCH_ADMIN

Supervises a branch. Can view wallets, employees, transaction requests, and transaction history belonging to their branch. This role focuses on monitoring rather than execution.

### CENTER_ADMIN

Manages a specific center. Can monitor branches under their center but does not perform operational financial actions.

### Core Design Principles

- Creation and approval must be separated.
- Requests are created at the branch level.
- Requests are approved at the center level.
- No single role has full control over a transaction.
- Financial actions are auditable and controlled.

## Requirements and Design Approach

This project was developed as if it were based on a real-world requirements specification document. Even though such a document did not formally exist, the system was not built by randomly adding features. Instead, every design decision was guided by a consistent set of assumed requirements.

The core idea is that the system was designed as if the requirements already existed, and the implementation was an interpretation of them.

### Functional Requirements

- A customer must be able to own one or more wallets.
- A wallet must be able to hold multiple assets.
- The system must support USDT-based deposit and withdrawal operations.
- Users must be able to convert assets between coins.
- Asset conversion must apply a fixed fee.
- A transfer between wallets must not be executed directly.
- Every transfer must go through a request and approval process.
- Approved requests must result in a transaction record.
- The system must keep a history of all executed transactions.

### Authorization Requirements

- All operations must be restricted based on role and scope.
- A branch operator must only operate on wallets within their branch.
- A center operator must only approve requests within their center.
- Users must not access data outside their assigned scope.

### Business Rules

- A transaction request must not be approved by its creator.
- A transaction must not be executed if the source wallet lacks sufficient balance.
- Wallet operations must fail if the wallet, branch, or center is inactive.
- Coin prices must be available for conversion and transaction execution.
- Asset balances must be updated consistently during every operation.

### System Constraints

- Transactions must be immutable once created.
- Wallet balances must not be stored as a single value.
- All financial operations must be validated before execution.

### Design Approach

This system is built around a fictional but structured idea: a mix of classic banking workflows and crypto asset management.

In reality, these two worlds do not perfectly overlap, but that was not the point. The goal was to treat this system as if it came from a real requirements specification document, even if that document never actually existed.

This approach made the project domain more focused and prevented the addition of loosely connected features.

Some examples of decisions that came out of this approach:

- Customers are global, because tying them directly to a branch did not feel realistic.
- Wallets are branch-bound, to reflect operational responsibility.
- Transactions require approval, to enforce control and auditability.
- Wallet balances are asset-based, not represented as a single number.

Even though the system itself is partially fictional, the thinking process behind it is not.

## Design Decisions

This section explains the main design choices made during the implementation. The goal was not to follow patterns blindly, but to make decisions that fit the system and its assumed requirements.

### Modular Monolith

The system is implemented as a modular monolith.

Instead of jumping into microservices early, the project keeps everything in a single codebase and focuses on:

- Clear domain boundaries
- Understandable structure
- Faster iteration

A modular monolith was a suitable approach for this project because the system benefits more from clear domain separation than from distributed infrastructure complexity.

### Explicit DTO Naming

DTO names are intentionally verbose, for example:

- `TransactionRequestRequest`
- `TransactionRequestResponse`

This naming is not the prettiest option, but it is explicit and consistent.

### Service Layer Focus

All business logic lives in the service layer.

Controllers are kept thin and are only responsible for:

- Receiving requests
- Returning responses

This keeps behavior consistent and avoids spreading business logic across the application.

### Validation Strategy

Validation is handled in two layers:

- DTO-level validation with `@Valid` for basic input checks
- Service-level validation for actual business rules

This way, invalid input is rejected early and business constraints are enforced in one place.

I intentionally avoided creating separate validation packages or over-engineering this part. However, service classes with heavy business rules, such as `TransactionRequestService` and `WalletService`, became delicate to manage as the project grew.

### WalletAsset Instead of Balance

Instead of giving `Wallet` a single balance field, the model uses the following structure:

```text
Wallet -> multiple WalletAssets
```

This allows multiple coins per wallet, more realistic crypto behavior, and better control over validations. It also avoids edge cases that come from forcing every asset into a single balance value.

### TransactionRequest vs Transaction

Transactions are not created directly. Instead, the system follows this flow:

```text
TransactionRequest -> Approval -> Transaction
```

This separation exists for control, auditability, and separation of duties. A `Transaction` object only exists if the `TransactionRequest` is explicitly approved.

### Snapshotting User Information

Transactions store snapshot fields such as:

- `requestedById`
- `requestedByUsername`
- `reviewedById`
- `reviewedByUsername`

This is intentional. Even if an employee changes later, the transaction still reflects who actually performed the action at that time.

### No Public Registration

There is no sign-up mechanism in the system. All employees are created by `ORG_ADMIN`.

This is not a public-facing system, but a controlled operational environment.

### Scheduled Coin Sync

Coin data is periodically updated through a scheduled task. This keeps prices fresh and allows conversions to use updated market data.

## API Overview

Below is a high-level overview of the main API groups.

### Organization Management

```text
/api/v1/admin/centers
/api/v1/admin/branches
/api/v1/admin/employees
```

Managed by `ORG_ADMIN` and `CENTER_ADMIN`. Used to build and maintain the organizational hierarchy.

### Customer and Wallet Management

```text
/api/v1/admin/customers
/api/v1/admin/wallets
/api/v1/wallets
/api/v1/wallets/{walletId}/assets
```

Customers are created by branch-level roles. Wallets are assigned to branches. Wallet assets can be viewed per wallet.

### Wallet Operations

```http
POST /api/v1/wallets/{walletId}/deposit
POST /api/v1/wallets/{walletId}/withdraw
POST /api/v1/wallets/{walletId}/convert
```

Performed by `BRANCH_OPERATOR`.

Deposit and withdrawal operations are limited to USDT. Conversion allows asset transformation between coins with a fixed fee.

### Transaction Requests

```http
POST /api/v1/transaction-requests
GET  /api/v1/transaction-requests
POST /api/v1/transaction-requests/{id}/approve
POST /api/v1/transaction-requests/{id}/reject
```

Created by `BRANCH_OPERATOR`. Approved or rejected by `CENTER_OPERATOR`. This follows a strict approval workflow.

### Transaction History

```http
GET /api/v1/admin/transactions
```

Accessible by `BRANCH_ADMIN`, `CENTER_ADMIN`, and `ORG_ADMIN`. Returns immutable transaction records scoped based on role.

### Coin Data

```http
GET  /api/v1/coins
POST /api/v1/admin/coins/sync
```

Coin data is fetched from an external API. Prices are used for conversions and transaction execution.

## Exception Handling

Because this project models financial operations, a large part of the service layer is dedicated to preventing invalid actions. In this kind of system, exceptions represent violated business rules.

Examples include:

- Trying to transfer more assets than a wallet owns
- Trying to approve a request from another center
- Trying to operate on an inactive wallet
- Trying to access another branch's data

These are expected failure cases, not random server crashes.

### Custom Exceptions

Instead of generic `RuntimeException`, the project uses the following exception types:

- `BusinessException`
- `ForbiddenException`
- `NotFoundException`
- `AuthorizationException`

Each one represents a different kind of failure:

- `BusinessException`: invalid business action
- `ForbiddenException`: authenticated user trying to access something outside their scope
- `NotFoundException`: requested resource does not exist
- `AuthorizationException`: authentication or authorization-related problem

### Global Exception Handler

All custom exceptions are handled by a centralized `GlobalExceptionHandler`.

Instead of returning a generic `500 Internal Server Error`, the API returns structured responses such as:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Insufficient balance",
  "path": "/api/v1/transaction-requests"
}
```

## Example Flow

The following scenario demonstrates a typical end-to-end workflow.

### Step 1 - Setup

`ORG_ADMIN` creates:

- Center
- Branch
- Employees: Branch Operator and Center Operator

### Step 2 - Customer and Wallet Creation

Branch Operator:

- Creates a customer
- Creates a wallet for that customer

### Step 3 - Deposit

Deposit 1000 USDT into the wallet.

Wallet now holds:

```text
USDT = 1000
```

### Step 4 - Conversion

Convert 1000 USDT to BTC.

```text
Fee: 1% -> 10 USDT
Net: 990 USDT
```

Conversion is based on coin prices.

Result:

```text
USDT = 0
BTC ≈ calculated amount
```

### Step 5 - Transaction Request

Branch Operator creates a transfer request:

```text
From Wallet A -> Wallet B
Amount: BTC
Status: PENDING
```

### Step 6 - Approval

Center Operator reviews the request:

```text
Approve -> Transaction is executed
Reject  -> Request is closed
```

### Step 7 - Transaction Execution

If approved:

- Assets are transferred between wallets.
- A transaction record is created.
- The transaction becomes immutable.

### Step 8 - History

Admin roles can view:

- Transaction history
- Transaction requests
- Wallet states
