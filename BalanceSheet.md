# Splitwise LLD — Balance Sheet & Simplify Debts

---

## 📌 Table of Contents
1. [What is the Balance Sheet?](#what-is-the-balance-sheet)
2. [The Central Graph](#the-central-graph)
3. [How Expenses Update the Graph](#how-expenses-update-the-graph)
4. [Simplify Debts Algorithm](#simplify-debts-algorithm)
5. [Full Example Walkthrough](#full-example-walkthrough)
6. [Code Structure](#code-structure)

---

## What is the Balance Sheet?

In Splitwise, when friends share expenses, each person needs to know:
- **Who owes them money?**
- **Who do they owe money to?**
- **How much in each case?**

We track this using a **Balance Sheet** per user.

```
Alice's Balance Sheet
─────────────────────
Bob   → +300    (Bob owes Alice ₹300)
Carol → -100    (Alice owes Carol ₹100)

Positive = other person owes YOU
Negative = YOU owe other person
```

---

## The Central Graph

The `BalanceSheetController` holds **all** balance sheets in one place.
It is modelled as a **Weighted Directed Graph**:

```
Nodes     →  Users
Edges     →  Debt between two users
Weight    →  Amount owed
Direction →  Who owes whom
```

### The Data Structure

```java
Map<String, BalanceSheet> balanceSheetMap;
// userId → their BalanceSheet (adjacency list)
```

This is literally an **adjacency list** representation of the graph:

```
balanceSheetMap = {
  "alice" → { "bob": +300, "carol": -100 }   ← Alice's edge list
  "bob"   → { "alice": -300, "carol": +200 } ← Bob's edge list
  "carol" → { "alice": +100, "bob": -200 }   ← Carol's edge list
}
```

Visual representation:

```
        ₹300
Alice ────────→ Bob
  ↑               ↑
  │ ₹100          │ ₹200
  │               │
Carol ────────────┘
```

---

## How Expenses Update the Graph

Every time an expense is added, **two edges** are updated:

```
Alice pays ₹900 for dinner (equal split among 3 people)
Each share = 900/3 = ₹300

For Bob's split (₹300):
  Edge 1: Alice → Bob  +300  (Bob owes Alice ₹300)
  Edge 2: Bob → Alice  -300  (Alice is owed ₹300 by Bob)

For Carol's split (₹300):
  Edge 1: Alice → Carol  +300  (Carol owes Alice ₹300)
  Edge 2: Carol → Alice  -300  (Alice is owed ₹300 by Carol)
```

Graph after this expense:

```
Bob   ──₹300──→ Alice
Carol ──₹300──→ Alice
```

---

## Simplify Debts Algorithm

### The Problem

After many expenses the graph can get messy with **many edges**:

```
Alice → Bob   ₹100
Carol → Alice ₹50
Carol → Bob   ₹50
Bob   → Dave  ₹200
Dave  → Alice ₹80
...
```

Too many transactions to settle! Can we **reduce** them?

### The Key Insight

> Stop thinking about who owes whom.
> Just figure out each person's **net balance** — how much they
> should ultimately pay or receive overall.

### Step 1 — Compute Net Balance Per Person

```
Net Balance = sum of all edges for that person

Alice:  receives ₹50 from Carol, pays ₹100 to Bob
        net = +50 - 100 = -50   → PAYER   (owes ₹50 net)

Bob:    receives ₹100 from Alice, receives ₹50 from Carol
        net = +100 + 50 = +150  → RECEIVER (gets ₹150 net)

Carol:  pays ₹50 to Alice, pays ₹50 to Bob
        net = -50 - 50 = -100   → PAYER   (owes ₹100 net)
```

### Step 2 — Separate Into Two Buckets

```
RECEIVERS (positive net) → should RECEIVE money
┌──────────────────────┐
│  Bob  → +150         │
└──────────────────────┘

PAYERS (negative net) → should PAY money
┌──────────────────────┐
│  Carol → -100        │
│  Alice → -50         │
└──────────────────────┘
```

Use a **Max Heap** for both — always process the largest first.

### Step 3 — Greedy Matching

**Always match the biggest payer with the biggest receiver.**

```
Receivers heap → [ Bob(150) ]
Payers heap    → [ Carol(100), Alice(50) ]

Round 1:
  Biggest receiver → Bob   (150)
  Biggest payer    → Carol (100)
  Settle amount    → min(150, 100) = 100

  ✅ Carol pays Bob ₹100

  Bob   remainder = 150 - 100 = 50  → push back
  Carol remainder = 100 - 100 = 0   → fully settled ✅

Receivers heap → [ Bob(50)  ]
Payers heap    → [ Alice(50) ]

Round 2:
  Biggest receiver → Bob   (50)
  Biggest payer    → Alice (50)
  Settle amount    → min(50, 50) = 50

  ✅ Alice pays Bob ₹50

  Both fully settled ✅

Receivers heap → [ empty ]
Payers heap    → [ empty ]
Done!
```

### Result

```
BEFORE (3 transactions)      AFTER (2 transactions)
────────────────────────     ──────────────────────
Alice → Bob   ₹100           Carol → Bob ₹100
Carol → Alice ₹50     →      Alice → Bob ₹50
Carol → Bob   ₹50
```

---

## Full Example Walkthrough

### Setup — 3 Friends, 3 Expenses

```
Users: Alice (u1), Bob (u2), Carol (u3)
```

**Expense 1** — Alice pays ₹900 for Dinner (equal split)

```
Each share = ₹300

Graph updates:
  Alice  → { Bob: +300, Carol: +300 }
  Bob    → { Alice: -300 }
  Carol  → { Alice: -300 }
```

**Expense 2** — Bob pays ₹1000 for Hotel (exact: Alice ₹400, Bob ₹400, Carol ₹200)

```
Graph updates:
  Alice  → { Bob: 300-400= -100, Carol: +300 }
  Bob    → { Alice: -300+400= +100, Carol: +200 }
  Carol  → { Alice: -300, Bob: -200 }
```

**Expense 3** — Carol pays ₹500 for Cab (50%, 30%, 20%)

```
Alice 50% = ₹250, Bob 30% = ₹150, Carol 20% = ₹100 (skipped)

Graph updates:
  Alice  → { Bob: -100, Carol: -100+250= +150  }  wait...

  Actually:
  Alice  → { Bob: -100,        Carol: 300-250= +50  }
  Bob    → { Alice: +100,      Carol: 200-150= +50  }
  Carol  → { Alice: -300+250= -50, Bob: -200+150= -50 }
```

**Final Balance Map:**

```
Alice  → { Bob: -100,  Carol: +50  }
Bob    → { Alice: +100, Carol: +50 }
Carol  → { Alice: -50,  Bob: -50  }
```

**In plain English:**

```
Alice owes Bob   ₹100
Carol owes Alice ₹50
Carol owes Bob   ₹50
```

**Simplify Debts:**

```
Net balances:
  Alice  = -100 + 50  = -50   PAYER
  Bob    = +100 + 50  = +150  RECEIVER
  Carol  = -50 - 50   = -100  PAYER

Receivers → [ Bob(150) ]
Payers    → [ Carol(100), Alice(50) ]

Round 1: Carol pays Bob ₹100
Round 2: Alice pays Bob ₹50

✅ Done in 2 transactions instead of 3!
```

---

## Code Structure

```
balanceSheet/
  ├── BalanceSheet.java
  │     → one user's ledger (adjacency list for one node)
  │     → Map<String, Double>
  │     → updateBalance(), getBalance(), getBalanceSheet()
  │
  └── BalanceSheetController.java
        → central graph (full adjacency list)
        → Map<String, BalanceSheet>
        → initUser()           add node to graph
        → updateBalanceSheet() update edges on expense
        → showBalance()        show one node's edges
        → settleUp()           reduce edge weight
        → simplifyDebts()      minimize total edges (greedy heap)
```

### Key Algorithms

| Operation | Graph Operation | Time Complexity |
|---|---|---|
| Add Expense | Update edge weights | O(N) per expense |
| Show Balance | Traverse node's edges | O(N) |
| Settle Up | Reduce one edge weight | O(1) |
| Simplify Debts | Rebuild graph, minimize edges | O(N log N) |

---

## Design Patterns Used

| Pattern | Where | Why |
|---|---|---|
| **Strategy** | Split calculation | New split types without changing existing code |
| **Factory** | SplitFactory | Decouple split creation from usage |
| **Observer** | Notifications | Decouple notification from expense logic |
| **Facade** | ExpenseController | Single entry point, hides complexity |

---

Made with ❤️ by [@vaibhav25-mnnit](https://github.com/vaibhav25-mnnit)