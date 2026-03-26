# CP'S Expense — Android App

A simple personal expense tracker with a **home screen widget**.

---

## Features
- ➕ Add expense with amount + description
- 📅 Auto-records today's date & time
- 📋 Expenses grouped by date in the main screen
- 📊 Today's total & month's total shown in header
- 🏠 **Home Screen Widget** — shows totals + last 3 expenses
- ➕ Widget **"+ Add"** button opens quick-add dialog directly
- 🗑️ Long-press any expense to delete it
- 💾 All data stored locally (SQLite, no internet needed)

---

## How to Build the APK

### Requirements
- [Android Studio](https://developer.android.com/studio) (free, any recent version)
- JDK 8 or higher (comes with Android Studio)

### Steps

1. **Extract** the `CPExpense.zip` file
2. **Open Android Studio** → `File` → `Open` → select the `CPExpense` folder
3. Wait for **Gradle sync** to finish (first time downloads dependencies ~2 min)
4. Click **Build** → **Build Bundle(s) / APK(s)** → **Build APK(s)**
5. APK will be at:
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```
6. **Transfer to phone** via USB, WhatsApp, Google Drive, etc.
7. On your phone: Settings → **Allow install from unknown sources** → install the APK

---

## How to Add the Widget

1. Install the app and open it once
2. Long-press your Android home screen
3. Tap **Widgets**
4. Find **"CP'S Expense Widget"**
5. Drag it to your home screen
6. Tap **"+ Add"** on the widget to quickly add an expense

---

## App Structure

```
com.cp.expense/
├── MainActivity.java        — Main list screen
├── Expense.java             — Data model
├── ExpenseDatabase.java     — SQLite database helper
├── ExpenseAdapter.java      — RecyclerView adapter
├── ExpenseWidget.java       — Home screen widget provider
└── WidgetAddActivity.java   — Quick-add dialog from widget
```

---

Built with ❤️ — CP'S Expense v1.0
