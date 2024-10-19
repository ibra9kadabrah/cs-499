package com.example.ibrahim_bahlawan_weight;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "weightTracker.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_WEIGHTS = "weights";

    // Common column names
    public static final String COLUMN_ID = "id";

    // USERS Table - column names
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_GOAL = "goal";

    // WEIGHTS Table - column names
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_DATE = "date";

    // Table create statements
    private static final String CREATE_TABLE_USERS = "CREATE TABLE "
            + TABLE_USERS + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USERNAME + " TEXT,"
            + COLUMN_PASSWORD + " TEXT,"
            + COLUMN_GOAL + " REAL" + ")";

    private static final String CREATE_TABLE_WEIGHTS = "CREATE TABLE "
            + TABLE_WEIGHTS + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_ID + " INTEGER,"
            + COLUMN_WEIGHT + " REAL,"
            + COLUMN_DATE + " DATE,"
            + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "))";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_WEIGHTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEIGHTS);
        onCreate(db);
    }

    public long addUser(String username, String password, double goal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_GOAL, goal);
        return db.insert(TABLE_USERS, null, values);
    }

    public boolean verifyUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};
        Cursor cursor = db.query(TABLE_USERS, null, selection, selectionArgs, null, null, null);
        boolean result = cursor.moveToFirst();
        cursor.close();
        return result;
    }

    public void setWeightGoal(double goal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GOAL, goal);
        db.update(TABLE_USERS, values, COLUMN_ID + " = ?", new String[]{"1"});
    }

    public double getWeightGoal() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_GOAL}, COLUMN_ID + " = ?", new String[]{"1"}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int goalIndex = cursor.getColumnIndex(COLUMN_GOAL);
            if (goalIndex != -1) {
                double goal = cursor.getDouble(goalIndex);
                cursor.close();
                return goal;
            } else {
                cursor.close();
                return -1;
            }
        } else {
            if (cursor != null) {
                cursor.close();
            }
            return -1;
        }
    }



    // check if a user exists by username
    public boolean doesUserExist(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(TABLE_USERS, null, selection, selectionArgs, null, null, null);
        boolean userExists = cursor.moveToFirst();
        cursor.close();
        return userExists;
    }


    public void deleteWeightGoal() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.putNull(COLUMN_GOAL);
        db.update(TABLE_USERS, values, COLUMN_ID + " = ?", new String[]{"1"});
    }

    public long addWeightEntry(double weight, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, 1); // Replace with actual user ID
        values.put(COLUMN_WEIGHT, weight);
        values.put(COLUMN_DATE, date);
        return db.insert(TABLE_WEIGHTS, null, values);
    }

    public List<WeightEntry> getAllWeights() {
        List<WeightEntry> weights = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_WEIGHTS, null, COLUMN_USER_ID + " = ?", new String[]{"1"}, null, null, COLUMN_DATE + " DESC");
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int weightIndex = cursor.getColumnIndex(COLUMN_WEIGHT);
            int dateIndex = cursor.getColumnIndex(COLUMN_DATE);
            do {
                if (idIndex != -1 && weightIndex != -1 && dateIndex != -1) {
                    long id = cursor.getLong(idIndex);
                    double weight = cursor.getDouble(weightIndex);
                    String date = cursor.getString(dateIndex);
                    weights.add(new WeightEntry(id, weight, date));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return weights;
    }

    public void deleteWeightEntry(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WEIGHTS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }
}
