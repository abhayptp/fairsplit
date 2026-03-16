# FairSplit

A Splitwise-inspired Android app for splitting expenses among groups of people.

## Features

- **Friends** — Add and manage friends by name
- **Groups** — Create groups with selected members
- **Expenses** — Add expenses with equal or custom splits, track who paid
- **Balances** — View who owes whom within each group (debt simplification algorithm)
- **Overall Balances** — See net balances across all groups
- **Settle Up** — Record payments to clear debts

## Tech Stack

- **Language**: Kotlin
- **Architecture**: MVVM (ViewModel + LiveData)
- **Database**: Room (SQLite, local storage only)
- **Navigation**: Navigation Component with Safe Args
- **UI**: Material Design 3, RecyclerView, ViewBinding
- **Build**: Gradle with version catalog (`libs.versions.toml`)

## Project Structure

```
app/src/main/java/com/fairsplit/app/
├── data/
│   ├── db/
│   │   ├── entity/          # Room entities (User, Group, Expense, etc.)
│   │   ├── dao/             # Data Access Objects
│   │   └── FairSplitDatabase.kt
│   └── repository/          # Repositories (business logic, balance calculation)
├── ui/
│   ├── users/               # Friends tab
│   ├── groups/              # Groups tab
│   ├── groupdetail/         # Group detail + balances
│   ├── addexpense/          # Add expense screen
│   └── balances/            # Overall balances tab
├── FairSplitApp.kt          # Application class (dependency holder)
└── MainActivity.kt
```

## Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- Android SDK 24+

### Build & Run

1. Clone the repository
2. Open in Android Studio
3. Let Gradle sync and download dependencies
4. Run on an emulator or physical device (Android 7.0+)

```bash
./gradlew assembleDebug
```

## Data Model

| Entity | Description |
|--------|-------------|
| `User` | A person (friend) |
| `Group` | A named group of users |
| `GroupMember` | Maps users to groups |
| `Expense` | A shared expense in a group |
| `ExpenseSplit` | How much each member owes for an expense |
| `Settlement` | Records a payment between two users |

## Balance Calculation

Balances are computed by:
1. For each expense: credit the payer, debit each member by their split amount
2. Subtract settlements (payments already made)
3. Apply a greedy debt-simplification algorithm to minimize transactions

All data is stored locally — no backend or account required.
