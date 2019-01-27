package com.example.khoby.tcntracker.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

public class SQLSaledatabasehelper extends SQLiteOpenHelper {

    private final static int DATABASE_VERSION = 1;
    private static final String CREATE_TABLE = "CREATE TABLE " + FarmerContract.SaleDatabaseEntry.TABLE_NAME +
            " (" + FarmerContract.SaleDatabaseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            FarmerContract.SaleDatabaseEntry.COLUMN_BUYER_ID + " INTEGER," +
            FarmerContract.SaleDatabaseEntry.COLUMN_COMPANY_ID + " INTEGER," +
            FarmerContract.SaleDatabaseEntry.COLUMN_NAME_TOTAL_AMOUNT_PAID + " DECIMAL(10,2)," +
            FarmerContract.SaleDatabaseEntry.COLUMN_NAME_UNIT_PRICE + " DECIMAL(10,2)," +
            FarmerContract.SaleDatabaseEntry.COLUMN_NAME_WEIGHT + " DECIMAL(10,2)," +
            FarmerContract.SaleDatabaseEntry.COLUMN_CREATED_AT + " TIMESTAMP)";
    private static  final String DROP_TABLE = "DROP TABLE IF EXISTS " + FarmerContract.SaleDatabaseEntry.TABLE_NAME;

    public SQLSaledatabasehelper(Context context){
        super(context, FarmerContract.DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public void populateSaleTable(HashMap<String, String>sales_value, SQLiteDatabase database){
        ContentValues contentValues = new ContentValues();

        contentValues.put(FarmerContract.SaleDatabaseEntry.COLUMN_COMPANY_ID, sales_value.get("company_id"));
        contentValues.put(FarmerContract.SaleDatabaseEntry.COLUMN_BUYER_ID, sales_value.get("buyer_id"));
        contentValues.put(FarmerContract.SaleDatabaseEntry.COLUMN_NAME_TOTAL_AMOUNT_PAID, sales_value.get("total_amount_paid"));
        contentValues.put(FarmerContract.SaleDatabaseEntry.COLUMN_NAME_UNIT_PRICE, sales_value.get("unit_price"));
        contentValues.put(FarmerContract.SaleDatabaseEntry.COLUMN_NAME_WEIGHT, sales_value.get("total_weight"));
        contentValues.put(FarmerContract.SaleDatabaseEntry.COLUMN_CREATED_AT, sales_value.get("created_at"));

       database.insert(FarmerContract.SaleDatabaseEntry.TABLE_NAME, null, contentValues);
    }


}
