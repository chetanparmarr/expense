package com.cp.expense;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WidgetAddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_add);

        EditText etAmount = findViewById(R.id.et_widget_amount);
        EditText etDesc = findViewById(R.id.et_widget_desc);
        Button btnAdd = findViewById(R.id.btn_widget_add);
        Button btnCancel = findViewById(R.id.btn_widget_cancel);

        btnAdd.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();

            if (TextUtils.isEmpty(amountStr)) {
                etAmount.setError("Enter amount");
                return;
            }

            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) throw new NumberFormatException();

                ExpenseDatabase.getInstance(this).addExpense(amount,
                        TextUtils.isEmpty(desc) ? "Expense" : desc);

                ExpenseWidget.updateAllWidgets(this);
                Toast.makeText(this, "₹" + amount + " added!", Toast.LENGTH_SHORT).show();
                finish();
            } catch (NumberFormatException e) {
                etAmount.setError("Invalid amount");
            }
        });

        btnCancel.setOnClickListener(v -> finish());
    }
}
