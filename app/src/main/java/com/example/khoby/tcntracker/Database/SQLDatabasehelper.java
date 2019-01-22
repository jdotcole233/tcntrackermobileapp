package com.example.khoby.tcntracker.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Timestamp;
import java.util.HashMap;

public class SQLDatabasehelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_TABLE = "CREATE TABLE " + FarmerContract.FarmerDatabaseEntry.TABLE_NAME
            + " (" + FarmerContract.FarmerDatabaseEntry._ID + "INTEGER PRIMARY KEY AUTOINCREMENT, " +
            FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_FIRST_NAME + " TEXT," + FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_OTHER_NAME
            + " TEXT," + FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_LAST_NAME + " TEXT," +
            FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_GENDER + " TEXT," +
            FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_PHONE_NUMBER + " TEXT," +
            FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_COMMUNITY_ID + " INTEGER," +
            FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_SYNC_STATUS + " INTEGER," +
            FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_CREATED_AT + " TIMESTAMP," +
            FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_UPDATED_AT + "TIMESTAMP)";
    private static final String DELETE_TABLE  = "DROP TABLE IF EXISTS " + FarmerContract.FarmerDatabaseEntry.TABLE_NAME;



    public SQLDatabasehelper(Context context){
        super(context, FarmerContract.DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_TABLE);
        onCreate(db);
    }


    public void populateDeviceDatabase(HashMap<String, String> farmerdata,  SQLiteDatabase database){
        ContentValues contentValues = new ContentValues();

        contentValues.put(FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_FIRST_NAME, farmerdata.get("first_name"));
        contentValues.put(FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_OTHER_NAME, farmerdata.get("other_name"));
        contentValues.put(FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_LAST_NAME, farmerdata.get("last_name"));
        contentValues.put(FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_GENDER, farmerdata.get("gender"));
        contentValues.put(FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_PHONE_NUMBER, farmerdata.get("phone_number"));
        contentValues.put(FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_COMMUNITY_ID, Integer.parseInt(farmerdata.get("community_id")));
        contentValues.put(FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_SYNC_STATUS, Integer.parseInt(farmerdata.get("sync_status")));
        contentValues.put(FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_CREATED_AT, farmerdata.get("created_at"));
        contentValues.put(FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_UPDATED_AT, farmerdata.get("updated_at"));

        database.insert(FarmerContract.FarmerDatabaseEntry.TABLE_NAME,null, contentValues);

    }

    public Cursor readFromDeviceDatabase(SQLiteDatabase database){
            String [] projection = {FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_FIRST_NAME,
                    FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_OTHER_NAME,
                    FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_LAST_NAME,
                    FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_GENDER,
                    FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_PHONE_NUMBER,
                    FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_CREATED_AT
            };

            return (database.query(FarmerContract.FarmerDatabaseEntry.TABLE_NAME, projection, null, null, null, null, null));
    }

    public void updatedeviceDatabase(String name,int syn_status, SQLiteDatabase database){

        ContentValues contentValues = new ContentValues();
        contentValues.put(FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_SYNC_STATUS, syn_status);
        String selection = FarmerContract.FarmerDatabaseEntry.TABLE_NAME + " LIKE ?";
        String [] selction_args = {name};


        database.update(FarmerContract.FarmerDatabaseEntry.TABLE_NAME, contentValues, selection, selction_args);
    }
}
