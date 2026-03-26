package com.cp.expense;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ExpenseDatabase db;
    private ExpenseAdapter adapter;
    private TextView tvTodayTotal, tvMonthTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = ExpenseDatabase.getInstance(this);

        tvTodayTotal = findViewById(R.id.tv_today_total);
        tvMonthTotal = findViewById(R.id.tv_month_total);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ExpenseAdapter(this, expense -> {
            // Delete on long press
            new AlertDialog.Builder(this)
                    .setTitle("Delete Expense")
                    .setMessage("Remove ₹" + String.format("%.2f", expense.getAmount()) + " - " + expense.getDescription() + "?")
                    .setPositiveButton("Delete", (d, w) -> {
                        db.deleteExpense(expense.getId());
                        refreshData();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> showAddDialog());

        refreshData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    private void refreshData() {
        Map<String, List<Expense>> data = db.getAllGroupedByDate();
        adapter.setData(data);

        NumberFormat nf = NumberFormat.getInstance(new Locale("en", "IN"));
        tvTodayTotal.setText("Today: ₹" + nf.format(db.getTodayTotal()));
        tvMonthTotal.setText("This Month: ₹" + nf.format(db.getMonthTotal()));
    }

    private void showAddDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_expense, null);
        EditText etAmount = view.findViewById(R.id.et_amount);
        EditText etDesc = view.findViewById(R.id.et_desc);

        new AlertDialog.Builder(this)
                .setTitle("Add Expense")
                .setView(view)
                .setPositiveButton("Add", (dialog, which) -> {
                    String amountStr = etAmount.getText().toString().trim();
                    String desc = etDesc.getText().toString().trim();

                    if (TextUtils.isEmpty(amountStr)) {
                        Toast.makeText(this, "Please enter amount", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        double amount = Double.parseDouble(amountStr);
                        if (amount <= 0) throw new NumberFormatException();
                        db.addExpense(amount, TextUtils.isEmpty(desc) ? "Expense" : desc);
                        refreshData();
                        ExpenseWidget.updateAllWidgets(this);
                        Toast.makeText(this, "Expense added!", Toast.LENGTH_SHORT).show();
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
