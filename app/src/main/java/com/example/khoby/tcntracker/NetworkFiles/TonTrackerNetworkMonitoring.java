package com.example.khoby.tcntracker.NetworkFiles;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.khoby.tcntracker.Database.FarmerContract;
import com.example.khoby.tcntracker.Database.SQLBuyerdatabasehelper;
import com.example.khoby.tcntracker.Database.SQLDatabasehelper;
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

            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
            RequestParams requestParams = new RequestParams();

            Cursor cursor = sqlDatabasehelper.readFromDeviceDatabase(sqLiteDatabase);
            Cursor buyerCursor = sqlBuyerdatabasehelper.readBuyerDataLocally(buyersqLiteDatabase);


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

        }
    }
}
