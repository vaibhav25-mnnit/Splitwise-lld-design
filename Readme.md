# 💸 Splitwise — Low Level Design (LLD)

A complete Java implementation of Splitwise covering entity design,
split strategies, balance tracking via a weighted directed graph,
and the Simplify Debts algorithm.

---

## 📌 Table of Contents

1. [Problem Statement](#problem-statement)
2. [Requirements](#requirements)
3. [Package Structure](#package-structure)
4. [UML Class Diagram](#uml-class-diagram)
5. [Core Concepts](#core-concepts)
6. [Design Patterns](#design-patterns)
7. [Flow Walkthrough](#flow-walkthrough)
8. [The Balance Graph](#the-balance-graph)
9. [Simplify Debts Algorithm](#simplify-debts-algorithm)
10. [Class Responsibilities](#class-responsibilities)

---

## Problem Statement

> When a group of people share expenses, track who owes whom how much
> and settle all debts with the **minimum number of transactions**.

---

## Requirements

### Functional
- Add users and groups
- Add expenses with 3 split types: **Equal**, **Unequal**, **Percentage**
- View balance for any user
- Settle up between two users
- Simplify debts — minimize total transactions

### Non-Functional
- Extensible split strategies (Open/Closed Principle)
- Clean OOP design with Single Responsibility
- Immutable data classes where applicable

---

## Package Structure

```
Splitwise/
│
├── Main.java
│
├── user/
│   └── User.java                          → identity only
│
├── expense/
│   ├── Expense.java                       → immutable expense record
│   ├── ExpenseController.java             → orchestrates expense creation
│   ├── ExpenseSplitType.java              → enum: EQUAL, UNEQUAL, PERCENTAGE
│   │
│   └── split/
│       ├── Split.java                     → base data class (user + amount)
│       ├── PercentageSplit.java           → extends Split, adds percentage field
│       ├── SplitFactory.java             → creates right strategy
│       │
│       └── strategies/
│           ├── ExpenseSplit.java          → strategy interface
│           ├── EqualExpenseSplit.java     → computes equal shares
│           ├── UnequalExpenseSplit.java   → validates exact amounts
│           └── PercentageExpenseSplit.java → validates % and computes amounts
│
├── balanceSheet/
│   ├── BalanceSheet.java                  → one user's ledger (adjacency list node)
│   └── BalanceSheetController.java        → central directed graph
│
└── group/
    └── Group.java                         → holds members + expenses
```

---

## UML Class Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           CLASS DIAGRAM                                 │
└─────────────────────────────────────────────────────────────────────────┘

          ┌──────────────┐
          │     User     │
          ├──────────────┤
          │ -userId: String  (final)   │
          │ -userName: String (final)  │
          ├──────────────┤
          │ +getUserId() │
          │ +getUserName()│
          └──────┬───────┘
                 │ uses
                 │
    ┌────────────┼────────────────────────────────────────┐
    │            │                                        │
    ▼            ▼                                        ▼
┌─────────┐  ┌──────────┐                    ┌───────────────────────┐
│  Split  │  │  Group   │                    │BalanceSheetController │
├─────────┤  ├──────────┤                    ├───────────────────────┤
│-user    │  │-groupId  │                    │-balanceSheetMap:      │
│-amount  │  │-groupName│                    │ Map<String,           │
├─────────┤  │-members  │                    │  BalanceSheet>        │
│+getUser │  │-expenses │                    ├───────────────────────┤
│+getAmount│ │-controller│                   │+initUser()            │
│+setAmount│ ├──────────┤                    │+updateBalanceSheet()  │
└────┬────┘  │+addMember│                    │+showBalance()         │
     │       │+createExpense│                │+settleUp()            │
     │       └──────────┘                    │+simplifyDebts()       │
     │                                       └──────────┬────────────┘
     │ extends                                          │ contains
     ▼                                                  ▼
┌──────────────────┐                        ┌─────────────────────┐
│  PercentageSplit │                        │    BalanceSheet      │
├──────────────────┤                        ├─────────────────────┤
│-percentage:double│                        │-balanceSheet:        │
├──────────────────┤                        │ Map<String, Double>  │
│+getPercentage()  │                        ├─────────────────────┤
└──────────────────┘                        │+updateBalance()      │
                                            │+getBalance()         │
                                            │+getBalanceSheet()    │
                                            └─────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                    STRATEGY HIERARCHY                        │
└─────────────────────────────────────────────────────────────┘

         ┌─────────────────────┐
         │    <<interface>>    │
         │     ExpenseSplit    │
         ├─────────────────────┤
         │+validateSplitRequest│
         │ (List<Split>,double)│
         └──────────┬──────────┘
                    │ implements
          ┌─────────┼──────────┐
          ▼         ▼          ▼
┌──────────────┐ ┌──────────┐ ┌─────────────────────┐
│EqualExpense  │ │Unequal   │ │PercentageExpense     │
│Split         │ │Expense   │ │Split                 │
├──────────────┤ │Split     │ ├─────────────────────┤
│compute equal │ ├──────────┤ │validate % sums to100 │
│share for each│ │validate  │ │compute rupee amounts │
│user          │ │sum==total│ │from percentages      │
└──────────────┘ └──────────┘ └─────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                     EXPENSE FLOW                             │
└─────────────────────────────────────────────────────────────┘

┌──────────┐    ┌───────────────────┐    ┌──────────────────────┐
│  Group   │───▶│ ExpenseController │───▶│   SplitFactory       │
└──────────┘    └─────────┬─────────┘    └──────────────────────┘
                          │                         │
                          │              ┌──────────▼──────────┐
                          │              │   ExpenseSplit       │
                          │              │  (right strategy)    │
                          │              └──────────┬──────────┘
                          │                         │ validate+compute
                          ▼                         ▼
                   ┌─────────────┐    ┌─────────────────────────┐
                   │   Expense   │    │  BalanceSheetController  │
                   │  (created)  │    │  (graph edges updated)   │
                   └─────────────┘    └─────────────────────────┘
```

---

## Core Concepts

### 1. Split Types

| Type | How it works | Validation |
|---|---|---|
| **EQUAL** | totalAmount ÷ N users | None needed |
| **UNEQUAL** | Caller sets exact amounts | Sum must equal totalAmount |
| **PERCENTAGE** | Caller sets % per user | Percentages must sum to 100 |

### 2. Split Class Hierarchy

```
Split (base)
  └── PercentageSplit (extends Split)
        → adds percentage field
        → amount starts as 0
        → strategy computes and sets actual rupee amount
```

**Why subclass?**
```java
// Without subclass → ambiguous
new Split(alice, 50);  // is 50 rupees or 50%? nobody knows!

// With subclass → crystal clear
new PercentageSplit(alice, 50);  // clearly 50%
new Split(alice, 50);            // clearly ₹50
```

### 3. Immutability

Fields marked `final` cannot change after construction:

```java
// User — identity never changes
private final String userId;
private final String userName;

// Expense — bill details never change
private final String expenseId;
private final double expenseAmount;
private final User paidByUser;

// Split — who is in the split never changes
// (amount is NOT final — strategy sets it later)
private final User user;
private double amount;  // set by strategy
```

### 4. Double Comparison Rule

```java
// WRONG — floating point precision issues
if (sum == totalAmount)         // ❌
if (percentageSum != 100.0)     // ❌

// CORRECT — always use tolerance
if (Math.abs(sum - totalAmount) > 0.01)       // ✅
if (Math.abs(percentageSum - 100.0) > 0.01)  // ✅
```

### 5. Map Key Rule

```java
// WRONG — User doesn't override hashCode/equals
Map<User, BalanceSheet> map;
map.put(user, sheet);
map.get(user);  // might return null! ❌

// CORRECT — String has hashCode/equals built in
Map<String, BalanceSheet> map;
map.put(user.getUserId(), sheet);
map.get(user.getUserId());  // always works ✅
```

---

## Design Patterns

### 1. Strategy Pattern — Split Calculation

**Problem:** Different split types need different calculation logic.
Adding a new split type should not touch existing code.

```
                    ┌──────────────┐
                    │ ExpenseSplit │  ← interface
                    │ (Strategy)   │
                    └──────┬───────┘
                           │
          ┌────────────────┼────────────────┐
          ▼                ▼                ▼
    EqualExpense    UnequalExpense   PercentageExpense
       Split           Split            Split
  (compute share)  (validate sum)   (validate % + compute)
```

**Benefit:** Add `ShareBasedSplit` tomorrow → just add one class, nothing else changes.

---

### 2. Factory Pattern — SplitFactory

**Problem:** Caller should not know which concrete strategy to instantiate.

```java
// Without factory → caller knows too much
ExpenseSplit s = new EqualExpenseSplit();  // tightly coupled ❌

// With factory → caller just says what type
ExpenseSplit s = SplitFactory.createSplit(ExpenseSplitType.EQUAL); // ✅
```

**Rule:** Factory always throws exception for unknown types — never returns null.

```java
default:
    throw new IllegalArgumentException("Unknown split type: " + splitType);
    // NOT: return null; ❌
```

---

### 3. Facade Pattern — ExpenseController

**Problem:** Creating an expense involves many steps — validate, compute, store, update graph. Caller should not need to know all this.

```
Caller (Group)                    ExpenseController (Facade)
──────────────                    ──────────────────────────
group.createExpense(...)   ──▶    1. SplitFactory.createSplit()
                                  2. strategy.validateSplitRequest()
                                  3. new Expense()
                                  4. balanceSheetController.update()
```

---

### 4. Constructor Injection — Loose Coupling

```java
// WRONG — tightly coupled, hard to test ❌
public ExpenseController() {
    this.controller = new BalanceSheetController();
}

// CORRECT — inject dependency ✅
public ExpenseController(BalanceSheetController controller) {
    this.controller = controller;
}
```

**Why?** One shared `BalanceSheetController` across all groups ensures Bob's balance is consistent everywhere.

---

## Flow Walkthrough

### Adding an Expense

```
1. group.createExpense("e1", "Dinner", tony,
                        EQUAL, 12000, splits)
          │
          ▼
2. expenseController.createExpense(...)
          │
          ├─▶ SplitFactory.createSplit(EQUAL)
          │         └─▶ returns EqualExpenseSplit
          │
          ├─▶ EqualExpenseSplit.validateSplitRequest(splits, 12000)
          │         └─▶ computes 12000/6 = ₹2000
          │             sets amount=2000 on each Split object
          │
          ├─▶ new Expense(...) → captures splits with correct amounts
          │
          └─▶ balanceSheetController.updateBalanceSheet(tony, splits)
                    └─▶ for each split (skip tony):
                          tony's sheet:  otherUser → +2000
                          other's sheet: tony      → -2000
```

---

## The Balance Graph

`BalanceSheetController` is a **Weighted Directed Graph**:

```
Nodes  →  Users
Edges  →  Debt between two users
Weight →  Amount owed
Arrow  →  Direction of debt
```

### Data Structure

```java
Map<String, BalanceSheet> balanceSheetMap;
// = adjacency list of the graph

// Each BalanceSheet = one node's edge list
Map<String, Double> balanceSheet;
// key   = otherUserId
// value = net amount
//   positive → other user owes YOU
//   negative → YOU owe other user
```

### Example Graph After 3 Expenses

```
        Tony
       ↗    ↖
  ₹100       ₹50
  ↗               ↖
Steve             Natasha

         ↑
       ₹200
         │
        Bruce
```

### Edge Update Rule

```
When Alice pays and Bob owes ₹300:

  aliceSheet.updateBalance(bobId,   +300)  // Bob owes Alice
  bobSheet.updateBalance(aliceId,   -300)  // Alice is owed by Bob

Always update BOTH directions to keep graph consistent.
```

---

## Simplify Debts Algorithm

### The Problem

After many expenses the graph is dense with many edges:
```
5 people → up to 20 transactions to settle
```

Can we achieve the same result with fewer transactions?

### Key Insight

> Forget individual debts. Just ask:
> **"Is each person a net payer or net receiver — and by how much?"**

### Algorithm — O(N log N)

```
Step 1: Compute net balance per person
────────────────────────────────────────
net = sum of all edges for that node

  Tony    net = +8000  → RECEIVER  (gets money)
  Steve   net = +2000  → RECEIVER
  Bruce   net = -4000  → PAYER     (pays money)
  Thor    net = -3000  → PAYER
  Natasha net = -2000  → PAYER
  Clint   net = -1000  → PAYER


Step 2: Separate into two Max Heaps
────────────────────────────────────────
Receivers heap → [ Tony(8000), Steve(2000) ]
Payers heap    → [ Bruce(4000), Thor(3000),
                   Natasha(2000), Clint(1000) ]


Step 3: Greedy match — largest payer → largest receiver
────────────────────────────────────────────────────────
Round 1: Bruce(4000) → Tony(8000)
  settle = min(4000,8000) = 4000
  ✅ Bruce pays Tony ₹4000
  Tony remainder = 8000-4000 = 4000 → back to heap

Round 2: Thor(3000) → Tony(4000)
  settle = min(3000,4000) = 3000
  ✅ Thor pays Tony ₹3000
  Tony remainder = 4000-3000 = 1000 → back to heap

Round 3: Natasha(2000) → Tony(1000)
  settle = min(2000,1000) = 1000
  ✅ Natasha pays Tony ₹1000
  Tony done ✅
  Natasha remainder = 2000-1000 = 1000 → back to heap

Round 4: Natasha(1000) → Steve(2000)
  settle = min(1000,2000) = 1000
  ✅ Natasha pays Steve ₹1000
  Natasha done ✅
  Steve remainder = 2000-1000 = 1000 → back to heap

Round 5: Clint(1000) → Steve(1000)
  settle = min(1000,1000) = 1000
  ✅ Clint pays Steve ₹1000
  Both done ✅

Result: 5 transactions instead of potentially 20!
```

### Why Greedy Works Here

```
Net flow at every node is preserved
regardless of HOW we route the payments.

Greedy ensures:
  → each iteration fully settles at least one person
  → minimum iterations = minimum transactions
  → O(N log N) due to heap operations
```

---

## Class Responsibilities

| Class | Responsibility | Pattern |
|---|---|---|
| `User` | Identity only (userId, userName) | Value Object |
| `Split` | Holds user + amount for one share | Data Class |
| `PercentageSplit` | Extends Split with percentage field | Inheritance |
| `ExpenseSplit` | Defines validation contract | Strategy Interface |
| `EqualExpenseSplit` | Computes equal shares | Concrete Strategy |
| `UnequalExpenseSplit` | Validates exact amounts sum | Concrete Strategy |
| `PercentageExpenseSplit` | Validates % and computes amounts | Concrete Strategy |
| `SplitFactory` | Creates right strategy for split type | Factory |
| `Expense` | Immutable record of an expense | Immutable Data Class |
| `ExpenseController` | Orchestrates expense creation flow | Facade |
| `BalanceSheet` | One user's ledger — adjacency list node | Data Class |
| `BalanceSheetController` | Central directed graph — all balances | Controller/Graph |
| `Group` | Holds members and delegates to controller | Aggregate |

---

## Key Rules to Remember

```
1. User is dumb               → only identity, no business logic
2. Immutable where possible   → final fields, no setters
3. Map keys are Strings       → never use objects as map keys
4. Double comparison          → always Math.abs() with 0.01 tolerance
5. Factory never returns null → throw exception for unknown types
6. Validate before creating   → strategy runs before new Expense()
7. Constructor injection      → never new dependencies inside classes
8. One BalanceSheetController → shared across all groups
```

---

Made with ❤️ by [@vaibhav25-mnnit](https://github.com/vaibhav25-mnnit)