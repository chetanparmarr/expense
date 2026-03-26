package com.cp.expense;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExpenseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    interface OnExpenseLongClick {
        void onLongClick(Expense expense);
    }

    private final Context context;
    private final OnExpenseLongClick listener;
    private final List<Object> items = new ArrayList<>(); // String (header) or Expense

    public ExpenseAdapter(Context context, OnExpenseLongClick listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setData(Map<String, List<Expense>> grouped) {
        items.clear();
        for (Map.Entry<String, List<Expense>> entry : grouped.entrySet()) {
            items.add(formatDate(entry.getKey()) + getDateTotal(entry.getValue()));
            items.addAll(entry.getValue());
        }
        notifyDataSetChanged();
    }

    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = in.parse(dateStr);
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            if (dateStr.equals(today)) return "Today";
            SimpleDateFormat out = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault());
            return out.format(date);
        } catch (ParseException e) {
            return dateStr;
        }
    }

    private String getDateTotal(List<Expense> expenses) {
        double total = 0;
        for (Expense e : expenses) total += e.getAmount();
        NumberFormat nf = NumberFormat.getInstance(new Locale("en", "IN"));
        return "  •  ₹" + nf.format(total);
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof String ? TYPE_HEADER : TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == TYPE_HEADER) {
            View v = inflater.inflate(R.layout.item_date_header, parent, false);
            return new HeaderVH(v);
        } else {
            View v = inflater.inflate(R.layout.item_expense, parent, false);
            return new ExpenseVH(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderVH) {
            ((HeaderVH) holder).tvDate.setText((String) items.get(position));
        } else {
            Expense expense = (Expense) items.get(position);
            ExpenseVH vh = (ExpenseVH) holder;
            NumberFormat nf = NumberFormat.getInstance(new Locale("en", "IN"));
            vh.tvAmount.setText("₹" + nf.format(expense.getAmount()));
            vh.tvDesc.setText(expense.getDescription());
            vh.tvTime.setText(new SimpleDateFormat("hh:mm a", Locale.getDefault())
                    .format(new Date(expense.getTimestamp())));
            holder.itemView.setOnLongClickListener(v -> {
                listener.onLongClick(expense);
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HeaderVH extends RecyclerView.ViewHolder {
        TextView tvDate;
        HeaderVH(View v) { super(v); tvDate = v.findViewById(R.id.tv_date_header); }
    }

    static class ExpenseVH extends RecyclerView.ViewHolder {
        TextView tvAmount, tvDesc, tvTime;
        ExpenseVH(View v) {
            super(v);
            tvAmount = v.findViewById(R.id.tv_amount);
            tvDesc = v.findViewById(R.id.tv_desc);
            tvTime = v.findViewById(R.id.tv_time);
        }
    }
}
