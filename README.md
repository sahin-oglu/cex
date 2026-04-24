# CEX - Banking / Crypto Backend System

## Overview

This project is a modular monolith backend system that simulates a banking + crypto transaction platform.

The idea was to first build a **clean organizational structure** (centers, branches, employees) and only after that move into the more complex part: **transaction logic**.

Instead of jumping straight into “send money” type features, the goal here was to model something closer to real-world systems:
- scoped access control
- approval-based workflows
- multi-asset wallets

---

## Architecture

The system is divided into two main domains:
Organization
├── Center
├── Branch
└── Employee

Banking
├── Customer
├── Wallet
├── WalletAsset
├── TransactionRequest
└── Transaction

This is a **feature-based package structure**, not a layered one.

---

## Core Concepts

### Role vs Scope

One of the main design decisions in this project is separating:

- **Role → what you can do**
- **Scope → where you can do it**

Examples:

- `BRANCH_OPERATOR`
  - can create transaction requests
  - but only within their own branch

- `CENTER_OPERATOR`
  - can approve/reject requests
  - but only within their own center

- `ORG_ADMIN`
  - has global access

Authorization is handled in the **service layer**, not just in Spring Security config.

---

### Transaction Flow

Transactions are not created directly.

Instead:

1. A **Branch Operator** creates a `TransactionRequest` (PENDING)
2. A **Center Operator** reviews it
3. If approved:
   - balances are updated
   - a `Transaction` is created
4. If rejected:
   - nothing changes, just status update

This separation is intentional:

- `TransactionRequest` = intent (mutable)
- `Transaction` = actual event (immutable)

---

### Wallet Model (Multi-Asset)

At first, wallets had a simple `balance` field.

That turned out to be wrong for a crypto-like system.

Instead, wallets are modeled like this:
Wallet
└── WalletAsset
├── Coin
└── amount

Example:

- BTC → 0.5
- ETH → 1.2

This avoids ambiguity like:
> “0.5 of what?”

Also prevents duplicate entries via a unique constraint:
(wallet_id, coin_id)

---

### Coin Model

Coins are stored in the system and can be synced from an external API (planned).

Each coin includes:

- id (e.g. "bitcoin")
- symbol (BTC, ETH)
- price (used as snapshot reference)
- market cap (optional)

Important detail:

> Transaction stores `priceAtExecution` to avoid future inconsistencies.

---

## Business Rules

### Organization

- A Branch must belong to a Center
- Inactive structures should not be usable

---

### Employee

- Branch roles must have a branch assigned
- Center roles must not have a branch
- Role defines authority, not scope

---

### Wallet

- A wallet belongs to a Customer and a Branch
- Wallets are multi-asset
- A wallet cannot have duplicate assets for the same coin

---

### Transactions

- Transactions cannot be created directly
- Only `BRANCH_OPERATOR` can create requests
- Only `CENTER_OPERATOR` can approve/reject
- A request must be `PENDING` to be processed
- Balance must be sufficient at approval time

---

## API Overview (simplified)
POST /api/v1/transactions/request
→ create transaction request

POST /api/v1/transactions/{id}/approve
→ approve request

POST /api/v1/transactions/{id}/reject
→ reject request

GET /api/v1/transactions/wallet/{walletId}
→ get transaction history

---

## Design Notes

Some intentional decisions:

- Modular monolith instead of microservices
- Feature-based packaging
- Manual mapping instead of heavy frameworks
- Business logic inside services, not controllers
- Approval-based flow instead of direct execution

This project is more about **modeling real-world constraints correctly** than just building CRUD endpoints.

---

## Current Status

- Organization system → mostly complete
- Security & scoped access → working
- Wallet & asset model → implemented
- Transaction request flow → implemented
- Approval / reject → implemented
- Transaction history → in progress

---

## Future Improvements

- Real coin API integration
- Exception handling layer
- Pagination & filtering
- Concurrency control (locking)
- Better audit/logging

---

## Final Note

This project is mainly an attempt to understand:
> how real financial systems structure their logic

Not just “how to send money from A to B”.