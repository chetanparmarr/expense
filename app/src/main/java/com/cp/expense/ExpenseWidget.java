package com.cp.expense;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ExpenseWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, widgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if ("com.cp.expense.ADD_EXPENSE".equals(intent.getAction())) {
            // Open the quick-add dialog activity
            Intent addIntent = new Intent(context, WidgetAddActivity.class);
            addIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(addIntent);
        }
    }

    public static void updateAllWidgets(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName component = new ComponentName(context, ExpenseWidget.class);
        int[] ids = manager.getAppWidgetIds(component);
        for (int id : ids) updateWidget(context, manager, id);
    }

    private static void updateWidget(Context context, AppWidgetManager manager, int widgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_expense);

        ExpenseDatabase db = ExpenseDatabase.getInstance(context);
        NumberFormat nf = NumberFormat.getInstance(new Locale("en", "IN"));

        // Today's total
        double todayTotal = db.getTodayTotal();
        views.setTextViewText(R.id.widget_today_total, "Today: ₹" + nf.format(todayTotal));

        // Month total
        double monthTotal = db.getMonthTotal();
        views.setTextViewText(R.id.widget_month_total, "Month: ₹" + nf.format(monthTotal));

        // Recent expenses (last 3)
        List<Expense> recent = db.getRecent(3);
        StringBuilder sb = new StringBuilder();
        for (Expense e : recent) {
            sb.append("• ₹").append(nf.format(e.getAmount()))
              .append("  ").append(e.getDescription()).append("\n");
        }
        views.setTextViewText(R.id.widget_recent, recent.isEmpty() ? "No expenses yet" : sb.toString().trim());

        // Add button → opens WidgetAddActivity
        Intent addIntent = new Intent(context, ExpenseWidget.class);
        addIntent.setAction("com.cp.expense.ADD_EXPENSE");
        PendingIntent addPending = PendingIntent.getBroadcast(context, 0, addIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_btn_add, addPending);

        // Tap widget → open app
        Intent appIntent = new Intent(context, MainActivity.class);
        PendingIntent appPending = PendingIntent.getActivity(context, 0, appIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_title, appPending);

        manager.updateAppWidget(widgetId, views);
    }
}
