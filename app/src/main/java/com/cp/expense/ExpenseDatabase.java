package com.cp.expense;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExpenseDatabase extends SQLiteOpenHelper {

    private static final String DB_NAME = "cp_expense.db";
    private static final int DB_VERSION = 1;

    private static final String TABLE = "expenses";
    private static final String COL_ID = "id";
    private static final String COL_AMOUNT = "amount";
    private static final String COL_DESC = "description";
    private static final String COL_DATE = "date";
    private static final String COL_TIMESTAMP = "timestamp";

    private static ExpenseDatabase instance;

    public static synchronized ExpenseDatabase getInstance(Context context) {
        if (instance == null) {
            instance = new ExpenseDatabase(context.getApplicationContext());
        }
        return instance;
    }

    private ExpenseDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_AMOUNT + " REAL NOT NULL, " +
                COL_DESC + " TEXT, " +
                COL_DATE + " TEXT NOT NULL, " +
                COL_TIMESTAMP + " INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public long addExpense(double amount, String description) {
        SQLiteDatabase db = getWritableDatabase();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        long ts = System.currentTimeMillis();

        ContentValues cv = new ContentValues();
        cv.put(COL_AMOUNT, amount);
        cv.put(COL_DESC, description);
        cv.put(COL_DATE, today);
        cv.put(COL_TIMESTAMP, ts);

        return db.insert(TABLE, null, cv);
    }

    public boolean deleteExpense(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE, COL_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }

    // Returns expenses grouped by date, most recent date first
    public Map<String, List<Expense>> getAllGroupedByDate() {
        Map<String, List<Expense>> result = new LinkedHashMap<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE + " ORDER BY " + COL_TIMESTAMP + " DESC", null);

        while (cursor.moveToNext()) {
            Expense e = fromCursor(cursor);
            if (!result.containsKey(e.getDate())) {
                result.put(e.getDate(), new ArrayList<>());
            }
            result.get(e.getDate()).add(e);
        }
        cursor.close();
        return result;
    }

    public double getTodayTotal() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + COL_AMOUNT + ") FROM " + TABLE + " WHERE " + COL_DATE + "=?",
                new String[]{today});
        double total = 0;
        if (cursor.moveToFirst()) total = cursor.isNull(0) ? 0 : cursor.getDouble(0);
        cursor.close();
        return total;
    }

    public double getMonthTotal() {
        String month = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + COL_AMOUNT + ") FROM " + TABLE + " WHERE " + COL_DATE + " LIKE ?",
                new String[]{month + "%"});
        double total = 0;
        if (cursor.moveToFirst()) total = cursor.isNull(0) ? 0 : cursor.getDouble(0);
        cursor.close();
        return total;
    }

    // Last 5 expenses for widget
    public List<Expense> getRecent(int limit) {
        List<Expense> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE + " ORDER BY " + COL_TIMESTAMP + " DESC LIMIT " + limit, null);
        while (cursor.moveToNext()) list.add(fromCursor(cursor));
        cursor.close();
        return list;
    }

    private Expense fromCursor(Cursor c) {
        return new Expense(
                c.getLong(c.getColumnIndexOrThrow(COL_ID)),
                c.getDouble(c.getColumnIndexOrThrow(COL_AMOUNT)),
                c.getString(c.getColumnIndexOrThrow(COL_DESC)),
                c.getString(c.getColumnIndexOrThrow(COL_DATE)),
                c.getLong(c.getColumnIndexOrThrow(COL_TIMESTAMP))
        );
    }
}
