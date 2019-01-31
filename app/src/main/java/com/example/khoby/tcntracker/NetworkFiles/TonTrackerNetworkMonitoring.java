package com.example.khoby.tcntracker.NetworkFiles;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.example.khoby.tcntracker.Database.FarmerContract;
import com.example.khoby.tcntracker.Database.SQLBuyerdatabasehelper;
import com.example.khoby.tcntracker.Database.SQLDatabasehelper;
import com.example.khoby.tcntracker.Database.SQLSaledatabasehelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.example.khoby.tcntracker.NetworkFiles.TonTrackerNetworkService.isNetworkConnectionAvailable;

public class TonTrackerNetworkMonitoring extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d("tontracker", "Broadcast class called");
        if(isNetworkConnectionAvailable(context)){
            Log.d("tontracker", "Broadast internet connectivity checked");

            final SQLDatabasehelper sqlDatabasehelper = new SQLDatabasehelper(context);
            final  SQLBuyerdatabasehelper sqlBuyerdatabasehelper = new SQLBuyerdatabasehelper(context);
            final SQLiteDatabase sqLiteDatabase = sqlDatabasehelper.getWritableDatabase();
            final SQLiteDatabase buyersqLiteDatabase = sqlBuyerdatabasehelper.getReadableDatabase();
            final SQLSaledatabasehelper sqlSaledatabasehelper = new SQLSaledatabasehelper(context);
            final SQLiteDatabase salesdatabase = sqlSaledatabasehelper.getWritableDatabase();

            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

            RequestParams requestParams = new RequestParams();
            RequestParams salesrequestParams = new RequestParams();



            Cursor cursor = sqlDatabasehelper.readFromDeviceDatabase(sqLiteDatabase);
            Cursor buyerCursor = sqlBuyerdatabasehelper.readBuyerDataLocally(buyersqLiteDatabase);

//            String checkTableExists = "SELECT * FROM sqlite_master WHERE name= 'farmer_transactions' and type='table'";
//            SQLiteStatement table = sqLiteDatabase.compileStatement(checkTableExists);
//            Long count = table.simpleQueryForLong();
          //  Log.d("tontracker", "table size" + count);



            while (cursor.moveToNext() && buyerCursor.moveToFirst()){
                int sync_status = cursor.getInt(cursor.getColumnIndex(FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_SYNC_STATUS));


                if (sync_status == FarmerContract.SYNC_STATUS_FAILED){

                    requestParams.add("first_name", cursor.getString(cursor.getColumnIndex(FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_FIRST_NAME)));
                    requestParams.add("other_name", cursor.getString(cursor.getColumnIndex(FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_OTHER_NAME)));
                    requestParams.add("last_name", cursor.getString(cursor.getColumnIndex(FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_LAST_NAME)));
                    requestParams.add("gender", cursor.getString(cursor.getColumnIndex(FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_GENDER)));
                    requestParams.add("phone_number", cursor.getString(cursor.getColumnIndex(FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_PHONE_NUMBER)));
                    requestParams.add("buyer_id", buyerCursor.getString(buyerCursor.getColumnIndex(FarmerContract.BuyerDatabaseEntry.COLUMN_NAME_BUYER_ID)));
                    requestParams.add("community_id", String.valueOf(cursor.getInt(cursor.getColumnIndex(FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_COMMUNITY_ID))));
                    requestParams.add("company_id", buyerCursor.getString(buyerCursor.getColumnIndex(FarmerContract.BuyerDatabaseEntry.COLUMN_NAME_COMMPANY_ID)));
                    requestParams.add("created_at", cursor.getString(cursor.getColumnIndex(FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_CREATED_AT)));


                    final String farmerID = cursor.getString(cursor.getColumnIndex(FarmerContract.FarmerDatabaseEntry._ID));

                    asyncHttpClient.post(FarmerContract.REGISTER_FARMER_URL, requestParams, new JsonHttpResponseHandler(){

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);

                            try {
                                String serverResponse = response.getString("response");
                                if (serverResponse.equals("SUCCESSFUL")){
                                    sqlDatabasehelper.updatedeviceDatabase(farmerID, FarmerContract.SYNC_STATUS_SUCCESS,sqLiteDatabase);
                                    context.sendBroadcast(new Intent(FarmerContract.UPDATE_APPLICATION));
                                    Log.d("tontracker", "Intent broadcast sent successfully");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            Log.d("tontracker", errorResponse.toString());
                            Log.d("tontracker", "Failure of broadcast sent ");

                        }
                    });

                }
            }


//            sqlDatabasehelper.close();
//            sqlBuyerdatabasehelper.close();

            Cursor salesCursor = sqlSaledatabasehelper.readSalesData(salesdatabase);

            while (salesCursor.moveToNext()){
                int sync_status = salesCursor.getInt(salesCursor.getColumnIndex(FarmerContract.SaleDatabaseEntry.COLUMN_NAME_SYNC_STATUS));

                if (sync_status == FarmerContract.SYNC_STATUS_FAILED){
                    salesrequestParams.add("unit_price", String.valueOf(salesCursor.getDouble(salesCursor.getColumnIndex(FarmerContract.SaleDatabaseEntry.COLUMN_NAME_UNIT_PRICE))));
                    salesrequestParams.add("total_weight", String.valueOf(salesCursor.getDouble(salesCursor.getColumnIndex(FarmerContract.SaleDatabaseEntry.COLUMN_NAME_WEIGHT))));
                    salesrequestParams.add("total_amount_paid", String.valueOf(salesCursor.getDouble(salesCursor.getColumnIndex(FarmerContract.SaleDatabaseEntry.COLUMN_NAME_TOTAL_AMOUNT_PAID))) );
                    salesrequestParams.add("buyer_id", String.valueOf(salesCursor.getInt(salesCursor.getColumnIndex(FarmerContract.SaleDatabaseEntry.COLUMN_BUYER_ID))));
                    salesrequestParams.add("company_id", String.valueOf(salesCursor.getInt(salesCursor.getColumnIndex(FarmerContract.SaleDatabaseEntry.COLUMN_COMPANY_ID))));
                    salesrequestParams.add("phone_number", salesCursor.getString(salesCursor.getColumnIndex(FarmerContract.SaleDatabaseEntry.COLUMN_NAME_PHONE_NUMBER)));

                    final String sales_id = String.valueOf(salesCursor.getInt(salesCursor.getColumnIndex(FarmerContract.SaleDatabaseEntry._ID)));

                    asyncHttpClient.post(FarmerContract.TRANSACTIONS_URL, salesrequestParams, new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            Log.d("tontracker", "Sales response is back");

                            try {
                                String serverResponse = response.getString("response");

                                if (serverResponse.equals("SUCCESSFUL")){
                                    sqlSaledatabasehelper.updateSaleTable(sales_id, FarmerContract.SYNC_STATUS_SUCCESS, salesdatabase);
                                   context.sendBroadcast(new Intent(FarmerContract.UPDATE_APPLICATION));
                                    Log.d("tontracker", "Server sale update successful");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            Log.d("tontracker", "Error from creating sales");
                        }
                    });
                }
            }
        }
    }
}
