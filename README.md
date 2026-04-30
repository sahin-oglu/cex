CEX Backend System

1. Project Overview

This project is the backend of a role-based system that combines CLASSIC BANKING workflows and CRYPTO ASSET MANAGEMENT.

The system models a controlled environment where financial operations are not executed directly, but instead go through a structured process involving:

explicit business rules
clearly defined roles
strict access control rules for users
approval-based transaction processes

The primary goal of the project is not to build a production-ready exchange, but to DESIGN and IMPLEMENT a consistent domain model that reflects how such a system could behave under REAL-WORLD CONSTRAINTS.

Key capabilities of the system include:

Organizational hierarchy management (Center->Branch->Employee)
Customer and wallet management
Multi-asset wallet structure (crypto-based)
Deposit and withdrawal operations (USDT-based)
Asset conversion with fee handling
Transaction request and approval workflow
Immutable transaction recording
Role-based data scoping and authorization

The project is implemented as a modular monolith, focusing on clarity of design rather than distribution complexity.

2. Domain Model

The system is divided into two main domains:

Organization Domain
Banking Domain
Organization Domain

This domain defines the structure of the system and determines how access is scoped.

Center
 └── Branch
      └── Employee
A Center represents a high-level organizational unit.
A Branch operates under a specific center.
An Employee belongs either to a center or a branch depending on their role.

This hierarchy is critical because all access control decisions are derived from it.

Banking Domain

This domain handles financial entities and operations.

Customer
 └── Wallet
      └── WalletAsset (Coin + Amount)
A Customer is a global entity and is not directly tied to a branch.
A Wallet belongs to a specific branch.
A WalletAsset represents a specific coin balance within a wallet.

Instead of storing a single balance, wallets maintain multiple assets, enabling:

multi-currency support
precise validation
realistic crypto behavior
Transaction Model

The system does not allow direct transfers between wallets.

Instead, it enforces a two-step process:

TransactionRequest → (Approved) → Transaction
A TransactionRequest represents an intention to transfer assets.
A Transaction is created only after approval.
Transactions are immutable once created.

This design ensures:

separation of duties
auditability
controlled execution of financial operations
3. Roles and Responsibilities

The system is strictly role-driven. Each role has clearly defined responsibilities and access boundaries.

ORG_ADMIN
Global authority within the system
Responsible for system setup
Can create:
centers
branches
employees
Has unrestricted access to all data
CENTER_ADMIN
Manages a specific center
Can view and monitor:
branches under the center
employees within the center
transactions and requests
Does not perform operational actions
CENTER_OPERATOR
Responsible for approval of transaction requests
Can:
approve or reject requests
Cannot create transactions directly
Ensures separation between creation and approval
BRANCH_ADMIN
Supervises a branch
Can view:
wallets
employees
transaction requests
transaction history
Focused on monitoring rather than execution
BRANCH_OPERATOR
Handles day-to-day operations
Can:
create customers
create wallets
perform deposits and withdrawals (USDT)
convert assets between coins
create transaction requests

This role is the main entry point for financial activity in the system.

Core Principle
Creation and approval must be separated
Requests are created at the branch level
Requests are approved at the center level

This ensures that:

no single role has full control over a transaction
financial actions are auditable and controlled


4. Requirements

The system was designed based on a set of implicit requirements, similar to what would be expected in a real-world specification document.

Instead of building features independently, the implementation followed a consistent set of assumptions about how such a system should behave.

Functional Requirements
A customer must be able to own one or more wallets
A wallet must be able to hold multiple assets (different coins)
The system must support deposit and withdrawal operations (USDT-based)
Users must be able to convert assets between coins
Asset conversion must apply a fixed fee
A transfer between wallets must not be executed directly
Every transfer must go through a request and approval process
Approved requests must result in a transaction record
The system must keep a history of all executed transactions
Authorization Requirements
All operations must be restricted based on role and scope
A branch operator must only operate on wallets within their branch
A center operator must only approve requests within their center
Users must not access data outside their assigned scope
Business Rules
A transaction request must not be approved by its creator
A transaction must not be executed if the source wallet lacks sufficient balance
Wallet operations must fail if the wallet, branch, or center is inactive
Coin prices must be available for conversion and transaction execution
Asset balances must be updated consistently during every operation
System Constraints
Transactions must be immutable once created
Wallet balances must not be stored as a single value
All financial operations must be validated before execution
5. Requirements-Driven Design

Although no formal requirements document existed at the beginning, the system was intentionally designed as if such a document was guiding the implementation.

This mindset influenced several key modeling decisions:

Customers were modeled as global entities, instead of being tied to a branch
Wallets were explicitly tied to branches to enforce operational responsibility
Transaction execution was separated into request and approval phases to enforce control and auditability
Asset-based accounting was chosen instead of a single balance field to support multi-asset behavior

By treating the system as if it had predefined requirements, arbitrary decisions were avoided and the model remained consistent throughout development.

6. Design Decisions

This section explains the key design choices made during the implementation.

Modular Monolith Architecture

The project is implemented as a modular monolith.

Instead of splitting the system into microservices prematurely, the focus was on:

clear domain separation
maintainable code structure
simplified development and testing
Explicit DTO Naming

DTOs are intentionally named in a verbose way:

TransactionRequestRequest
TransactionRequestResponse

This avoids ambiguity and makes the intent of each class explicit, even if it is not aesthetically minimal.

Service Layer as the Source of Truth

All business logic is implemented in the service layer.

No validation or business rules are placed in controllers.

This allows:

centralized rule management
easier testing
consistent behavior across endpoints
Validation Strategy

Validation is split into two layers:

DTO validation (@Valid) for input format
Service-level validation for business rules

This ensures that:

invalid input is rejected early
business constraints are enforced consistently
WalletAsset Instead of Balance Field

Instead of storing a single balance in the wallet:

Wallet → multiple WalletAssets

This decision enables:

support for multiple coins
more realistic financial modeling
fine-grained validation
TransactionRequest and Transaction Separation

Transactions are not created directly.

Instead:

TransactionRequest → approval → Transaction

This ensures:

separation of duties
auditability
controlled execution

Transactions are also immutable, meaning they are never updated after creation.

Snapshotting User Information in Transactions

Transaction entities store:

requestedById / username
reviewedById / username

instead of only referencing the Employee entity.

This acts as a snapshot, ensuring that historical records remain valid even if user data changes later.

No Public Registration

The system does not support public user registration.

All employees are created by administrators.

This reflects a controlled organizational environment rather than an open system.

Scheduled Coin Synchronization

Coin data is periodically updated using a scheduled task.

This ensures:

up-to-date pricing
consistent conversion behavior
Summary
This project is not just an implementation of features,
but an attempt to design a system based on consistent assumptions and rules.


7. API Overview

Below is a high-level overview of the main API groups.

Organization Management
/api/v1/admin/centers
/api/v1/admin/branches
/api/v1/admin/employees
Managed by ORG_ADMIN and CENTER_ADMIN
Used to build and maintain the organizational hierarchy
Customer and Wallet Management
/api/v1/admin/customers
/api/v1/admin/wallets
/api/v1/wallets
/api/v1/wallets/{walletId}/assets
Customers are created by branch-level roles
Wallets are assigned to branches
Wallet assets can be viewed per wallet
Wallet Operations
POST /api/v1/wallets/{walletId}/deposit
POST /api/v1/wallets/{walletId}/withdraw
POST /api/v1/wallets/{walletId}/convert
Performed by BRANCH_OPERATOR
Deposit/withdraw operations are limited to USDT
Conversion allows asset transformation between coins with a fixed fee
Transaction Requests
POST /api/v1/transaction-requests
GET  /api/v1/transaction-requests
POST /api/v1/transaction-requests/{id}/approve
POST /api/v1/transaction-requests/{id}/reject
Created by BRANCH_OPERATOR
Approved or rejected by CENTER_OPERATOR
Follows a strict approval workflow
Transaction History
GET /api/v1/admin/transactions
Accessible by admin roles
Returns immutable transaction records
Scoped based on role (branch / center / global)
Coin Data
GET /api/v1/coins
POST /api/v1/admin/coins/sync
Coin data is fetched from an external API
Prices are used for conversions and transaction execution
8. Example Flow

The following scenario demonstrates a typical end-to-end workflow:

Step 1 — Setup (Admin)
ORG_ADMIN creates:
Center
Branch
Employees (Branch Operator & Center Operator)
Step 2 — Customer & Wallet Creation
Branch Operator:
Creates a customer
Creates a wallet for that customer
Step 3 — Deposit
Deposit 1000 USDT into wallet
Wallet now holds:
USDT = 1000
Step 4 — Conversion
Convert 1000 USDT → BTC
Fee: 1% → 10 USDT
Net: 990 USDT
Conversion based on coin prices

Result:

USDT = 0
BTC ≈ calculated amount
Step 5 — Transaction Request
Branch Operator creates a transfer request:
From Wallet A → Wallet B
Amount: BTC
Status: PENDING
Step 6 — Approval
Center Operator reviews the request:
Approve → Transaction is executed
Reject  → Request is closed
Step 7 — Transaction Execution

If approved:

Assets are transferred between wallets
A Transaction record is created
The transaction becomes immutable
Step 8 — History
Admin roles can view:
Transaction history
Transaction requests
Wallet states
Final Note
This project focuses on modeling a controlled financial system
where rules, roles, and workflows are more important than raw functionality.


9. Possible Future Improvements

While the current implementation focuses on core functionality and domain modeling, several improvements can be made to bring the system closer to a production-ready architecture.

Concurrency and Consistency

The current implementation assumes single-threaded execution for critical financial operations.

Future improvements may include:

Optimistic locking (@Version) for wallet assets
Pessimistic locking for critical transactions
Protection against double-spend scenarios
Pagination and Query Optimization

List endpoints currently return full datasets.

This can be improved by:

Adding pagination support (page, size)
Introducing sorting and filtering options
Optimizing database queries for large datasets
Integration Testing

The system is currently tested manually via API calls.

Future improvements:

End-to-end integration tests for critical flows
Role-based scenario testing (branch → center → approval)
Automated regression testing
Audit and Logging

Although transactions are immutable, audit capabilities can be expanded:

Detailed audit logs for all operations
Tracking changes in wallet assets
Logging approval and rejection actions
External System Integration

The system currently uses a single external source for coin data.

Possible extensions:

Multiple price providers
Fallback mechanisms in case of API failure
Rate limiting and retry strategies
Fee Management

Conversion fees are currently fixed.

This could be improved by:

Configurable fee rates
Different fee policies per coin
Fee tracking and reporting
User Authentication Enhancements

The current authentication model is basic.

Possible improvements:

JWT-based authentication
Token expiration and refresh mechanisms
More granular permission management
UI / Dashboard Layer

The system currently exposes only backend APIs.

A future improvement would be:

A dashboard for branch and center admins
Visualization of transactions, wallets, and activity
Real-time operational insights
Closing Thought
This project establishes a solid foundation for a role-based financial system.
Future improvements would focus on scalability, robustness, and operational visibility.