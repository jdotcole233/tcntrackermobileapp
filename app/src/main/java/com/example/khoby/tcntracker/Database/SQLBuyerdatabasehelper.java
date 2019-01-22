package com.example.khoby.tcntracker.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLBuyerdatabasehelper extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 2;



    private static final String CREATE_TABLE = "CREATE TABLE " + FarmerContract.BuyerDatabaseEntry.TABLE_NAME + " (" +
            FarmerContract.BuyerDatabaseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            FarmerContract.BuyerDatabaseEntry.COLUMN_NAME_FIRST_NAME + " TEXT," +
            FarmerContract.BuyerDatabaseEntry.COLUMN_NAME_OTHER_NAME + " TEXT," +
            FarmerContract.BuyerDatabaseEntry.COLUMN_NAME_LAST_NAME + " TEXT," +
            FarmerContract.BuyerDatabaseEntry.COLUMN_NAME_BUYER_ID + " INTEGER," +
            FarmerContract.BuyerDatabaseEntry.COLUMN_NAME_COMMPANY_ID + " INTEGER," +
            FarmerContract.BuyerDatabaseEntry.COLUMN_NAME_GENDER + " TEXT)";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + FarmerContract.BuyerDatabaseEntry.TABLE_NAME;


    public SQLBuyerdatabasehelper(Context context) {
        super(context, FarmerContract.BUYER_DATABASE_NAME, null, DATABASE_VERSION);
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





}
