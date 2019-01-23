package com.example.khoby.tcntracker.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

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
    private static final String TRUNCATE_TABLE = "DELETE FROM " + FarmerContract.BuyerDatabaseEntry.TABLE_NAME;


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

    public void updateBuyerLocalDevice(HashMap<String, String> buyerInformation, SQLiteDatabase database){
        ContentValues contentValues = new ContentValues();
        contentValues.put(FarmerContract.BuyerDatabaseEntry.COLUMN_NAME_FIRST_NAME, buyerInformation.get("first_name"));
        contentValues.put(FarmerContract.BuyerDatabaseEntry.COLUMN_NAME_OTHER_NAME, buyerInformation.get("other_name"));
        contentValues.put(FarmerContract.BuyerDatabaseEntry.COLUMN_NAME_LAST_NAME, buyerInformation.get("last_name"));
        contentValues.put(FarmerContract.BuyerDatabaseEntry.COLUMN_NAME_GENDER, buyerInformation.get("gender"));
        contentValues.put(FarmerContract.BuyerDatabaseEntry.COLUMN_NAME_BUYER_ID, buyerInformation.get("buyer_id"));
        contentValues.put(FarmerContract.BuyerDatabaseEntry.COLUMN_NAME_COMMPANY_ID, buyerInformation.get("company_id"));
        database.insert(FarmerContract.BuyerDatabaseEntry.TABLE_NAME, null, contentValues);
    }

    public Cursor readBuyerDataLocally(SQLiteDatabase database){
        String [] projection = {FarmerContract.BuyerDatabaseEntry.COLUMN_NAME_FIRST_NAME,
                FarmerContract.BuyerDatabaseEntry.COLUMN_NAME_OTHER_NAME,
                FarmerContract.BuyerDatabaseEntry.COLUMN_NAME_LAST_NAME,
                FarmerContract.BuyerDatabaseEntry.COLUMN_NAME_GENDER,
                FarmerContract.BuyerDatabaseEntry.COLUMN_NAME_COMMPANY_ID,
                FarmerContract.BuyerDatabaseEntry.COLUMN_NAME_BUYER_ID
        };

        return (database.query(FarmerContract.BuyerDatabaseEntry.TABLE_NAME, projection, null, null,null, null, null));

    }


    public void resetBuyerTable(SQLiteDatabase database){
        database.execSQL(DROP_TABLE);
    }



}
