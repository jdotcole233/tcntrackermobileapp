package com.example.khoby.tcntracker.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLSaledatabasehelper extends SQLiteOpenHelper {

    private final static int DATABASE_VERSION = 1;
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + FarmerContract.SaleDatabaseEntry.TABLE_NAME +
            " (" + FarmerContract.SaleDatabaseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            FarmerContract.SaleDatabaseEntry.COLUMN_BUYER_ID + " INTEGER," +
            FarmerContract.SaleDatabaseEntry.COLUMN_COMPANY_ID + " INTEGER," +
            FarmerContract.SaleDatabaseEntry.COLUMN_NAME_TOTAL_AMOUNT_PAID + " DECIMAL(10,2)," +
            FarmerContract.SaleDatabaseEntry.COLUMN_NAME_UNIT_PRICE + " DECIMAL(10,2)," +
            FarmerContract.SaleDatabaseEntry.COLUMN_NAME_SYNC_STATUS + " INTEGER," +
            FarmerContract.SaleDatabaseEntry.COLUMN_NAME_PHONE_NUMBER + " TEXT," +
            FarmerContract.SaleDatabaseEntry.COLUMN_NAME_WEIGHT + " DECIMAL(10,2)," +

            FarmerContract.SaleDatabaseEntry.COLUMN_CREATED_AT + " TIMESTAMP)";
    private static  final String DROP_TABLE = "DROP TABLE IF EXISTS " + FarmerContract.SaleDatabaseEntry.TABLE_NAME;
    private static final  String CHECK_IF_TABLE_EXISTS = "SELECT * FROM sqlite_master WHERE " +
            "type='table' AND name='" + FarmerContract.SaleDatabaseEntry.TABLE_NAME + "'";

    public SQLSaledatabasehelper(Context context){
        super(context, FarmerContract.SALE_DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

//        Cursor checktable = db.rawQuery(CHECK_IF_TABLE_EXISTS, null);
//
//        if (checktable != null){
//            Log.d("tontracker", "table already exists");
//              return;
//        }
        db.execSQL(CREATE_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public void populateSaleTable(HashMap<String, String>sales_value,int sync_status, SQLiteDatabase database){
        ContentValues contentValues = new ContentValues();

        contentValues.put(FarmerContract.SaleDatabaseEntry.COLUMN_COMPANY_ID, sales_value.get("company_id"));
        contentValues.put(FarmerContract.SaleDatabaseEntry.COLUMN_BUYER_ID, sales_value.get("buyer_id"));
        contentValues.put(FarmerContract.SaleDatabaseEntry.COLUMN_NAME_TOTAL_AMOUNT_PAID, sales_value.get("total_amount_paid"));
        contentValues.put(FarmerContract.SaleDatabaseEntry.COLUMN_NAME_UNIT_PRICE, sales_value.get("unit_price"));
        contentValues.put(FarmerContract.SaleDatabaseEntry.COLUMN_NAME_SYNC_STATUS, sync_status);
        contentValues.put(FarmerContract.SaleDatabaseEntry.COLUMN_NAME_PHONE_NUMBER, sales_value.get("phone_number"));
        contentValues.put(FarmerContract.SaleDatabaseEntry.COLUMN_NAME_WEIGHT, sales_value.get("total_weight"));
        contentValues.put(FarmerContract.SaleDatabaseEntry.COLUMN_CREATED_AT, sales_value.get("created_at"));

       database.insert(FarmerContract.SaleDatabaseEntry.TABLE_NAME, null, contentValues);
    }

    public void updateSaleTable(String sales_id, int sync_update_status, SQLiteDatabase database){
        ContentValues contentValues = new ContentValues();
        contentValues.put(FarmerContract.SaleDatabaseEntry.COLUMN_NAME_SYNC_STATUS, sync_update_status);
        String selection = "_id= ?";
        String [] selection_args = {sales_id};

        database.update(FarmerContract.SaleDatabaseEntry.TABLE_NAME, contentValues, selection, selection_args);

    }

    public Cursor readSalesData(SQLiteDatabase database){

        String [] column_names = {
                FarmerContract.SaleDatabaseEntry._ID,
                FarmerContract.SaleDatabaseEntry.COLUMN_NAME_PHONE_NUMBER,
                FarmerContract.SaleDatabaseEntry.COLUMN_CREATED_AT,
                FarmerContract.SaleDatabaseEntry.COLUMN_NAME_WEIGHT,
                FarmerContract.SaleDatabaseEntry.COLUMN_NAME_UNIT_PRICE,
                FarmerContract.SaleDatabaseEntry.COLUMN_NAME_TOTAL_AMOUNT_PAID,
                FarmerContract.SaleDatabaseEntry.COLUMN_COMPANY_ID,
                FarmerContract.SaleDatabaseEntry.COLUMN_NAME_SYNC_STATUS,
                FarmerContract.SaleDatabaseEntry.COLUMN_BUYER_ID
        };

        return (database.query(FarmerContract.SaleDatabaseEntry.TABLE_NAME, column_names, null,null, null, null, null));
    }


    public Double getTotalWeight(SQLiteDatabase database){
        String query = "SELECT sum(total_weight) AS Total FROM " + FarmerContract.SaleDatabaseEntry.TABLE_NAME;
        Double total = 0.0;
        Log.d("tontracker", database.rawQuery(query,null).toString());
        Cursor cursor = database.rawQuery(query,null);
        if (cursor.moveToFirst()){
            total = cursor.getDouble(cursor.getColumnIndex("Total"));
        }
        return total;
    }
}
