package com.example.khoby.tcntracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.khoby.tcntracker.Database.FarmerContract;
import com.example.khoby.tcntracker.Database.SQLBuyerdatabasehelper;
import com.example.khoby.tcntracker.Database.SQLDatabasehelper;
import com.example.khoby.tcntracker.Model.FarmerModel;
import com.loopj.android.http.PersistentCookieStore;

import java.util.ArrayList;
import java.util.Map;

public class FarmerProfiles extends AppCompatActivity {

    private ListView farmer_list;
    private ArrayList<FarmerModel> farmerModels;
    private FarmerListAdapter farmerListAdapter;
    private ArrayList<FarmerModel> farmersOutput;
    private DrawerLayout mydrawer;
    private Integer buyer_id;
    private Integer company_id;
    private Double current_price;
    SQLBuyerdatabasehelper sqlBuyerdatabasehelper;
    SQLiteDatabase sqLiteDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_profiles);
        sqlBuyerdatabasehelper = new SQLBuyerdatabasehelper(this);
        sqLiteDatabase = sqlBuyerdatabasehelper.getReadableDatabase();
        current_price = 0.0;
        updateUI();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        farmer_list = findViewById(R.id.farmer_list);
       // farmerModels = getFarmers();
        final PersistentCookieStore myCookieData = new PersistentCookieStore(this);
        farmersOutput = new ArrayList<>();
        buyer_id = 1;
        company_id = 1;
        readFromLocalDeviceDatabase();


        farmerListAdapter = new FarmerListAdapter(this, farmersOutput, current_price, buyer_id, company_id);
        farmer_list.setAdapter(farmerListAdapter);




        mydrawer = findViewById(R.id.navigation_drawer);
        NavigationView navigationView = findViewById(R.id.dashboard_navigation);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                mydrawer.closeDrawers();

                switch (menuItem.getItemId()){
                    case R.id.drawer_home:
                        Intent intent = new Intent(FarmerProfiles.this,Dashboard.class);
                        startActivity(intent);
                        finish();
                        break;

                    case R.id.create_farmer_sale:
                        Intent intent1 = new Intent(FarmerProfiles.this,FarmerProfiles.class);
                        startActivity(intent1);
                        finish();
                        break;

                    case R.id.create_farmer:
                        Intent intent2 = new Intent(FarmerProfiles.this,CreateFarmerProfile.class);
                        startActivity(intent2);
                        finish();
                        break;

                    case R.id.logout:
                        Log.i("loggedout","loggedout");
                        if (!myCookieData.getCookies().isEmpty()){
                            Log.d("tontracker", myCookieData.getCookies().toString());
                            SQLBuyerdatabasehelper sqlBuyerdatabasehelper = new SQLBuyerdatabasehelper(FarmerProfiles.this);
                            SQLiteDatabase sqLiteDatabase = sqlBuyerdatabasehelper.getWritableDatabase();
                            sqlBuyerdatabasehelper.resetBuyerTable(sqLiteDatabase);
                            myCookieData.clear();
                            Log.d("tontracker", "After clear" + myCookieData.getCookies().toString());
                            Intent switchBack = new Intent(FarmerProfiles.this, MainActivity.class);
                            startActivity(switchBack);
                            finish();
                        }
                        break;

                }

                return false;
            }
        });

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                mydrawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
//    public static ArrayList<FarmerModel> getFarmers(){
//            ArrayList<FarmerModel> farmerModels = new ArrayList<>();
//
//        farmerModels.add(new FarmerModel(1,"Kwame Ampong","Drobo","0503848404"));
//        farmerModels.add(new FarmerModel(2,"Mary Affum","Techiman","0503848404"));
//        farmerModels.add(new FarmerModel(3,"Sampson Hackeem","Kumasi","0503848404"));
//        farmerModels.add(new FarmerModel(4,"Ama Mercy","Bono","0503848404"));
//        farmerModels.add(new FarmerModel(5,"Sassu Sarfo","Techiman","0503848404"));
//        farmerModels.add(new FarmerModel(6,"Francis Duman","Drobo","0503848404"));
//        farmerModels.add(new FarmerModel(7,"Eric Aseidu","Kumasi","0503848404"));
//        farmerModels.add(new FarmerModel(8,"Deborah Sare","Bono","0503848404"));
//
//            return  farmerModels;
//    }


    public void readFromLocalDeviceDatabase(){
        SQLDatabasehelper sqlDatabasehelper = new SQLDatabasehelper(this);
        SQLiteDatabase sqLiteDatabase = sqlDatabasehelper.getReadableDatabase();

        SQLBuyerdatabasehelper sqlBuyerdatabasehelper = new SQLBuyerdatabasehelper(FarmerProfiles.this);
        SQLiteDatabase buyerSqlDatabase = sqlBuyerdatabasehelper.getReadableDatabase();

        int count_id = 0;

        Cursor cursor = sqlDatabasehelper.readFromDeviceDatabase(sqLiteDatabase);
        Cursor buyerCursor = sqlBuyerdatabasehelper.readBuyerDataLocally(buyerSqlDatabase);

//        while (buyerCursor.moveToFirst()){
//            buyer_id = Integer.parseInt(buyerCursor.getString(buyerCursor.getColumnIndex(FarmerContract.BuyerDatabaseEntry.COLUMN_NAME_BUYER_ID)));
//            company_id = Integer.parseInt(buyerCursor.getString(buyerCursor.getColumnIndex(FarmerContract.BuyerDatabaseEntry.COLUMN_NAME_COMMPANY_ID)));
//            break;
//        }
       // buyerSqlDatabase.close();

        while (cursor.moveToNext() && buyerCursor.moveToFirst() ){
            buyer_id = Integer.parseInt(buyerCursor.getString(buyerCursor.getColumnIndex(FarmerContract.BuyerDatabaseEntry.COLUMN_NAME_BUYER_ID)));
            company_id = Integer.parseInt(buyerCursor.getString(buyerCursor.getColumnIndex(FarmerContract.BuyerDatabaseEntry.COLUMN_NAME_COMMPANY_ID)));

            String fullName = cursor.getString(cursor.getColumnIndex(FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_FIRST_NAME)) + " " +
                    cursor.getString(cursor.getColumnIndex(FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_OTHER_NAME)) + " " +
                    cursor.getString(cursor.getColumnIndex(FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_LAST_NAME));
            String farmerLocation = cursor.getString(cursor.getColumnIndex(FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_COMMUNITY_NAME));

            String farmerPhone = cursor.getString(cursor.getColumnIndex(FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_PHONE_NUMBER));
            Integer sync_status = cursor.getInt(cursor.getColumnIndex(FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_SYNC_STATUS));
            count_id++;
            if (sync_status.equals(FarmerContract.SYNC_STATUS_FAILED)){

                farmersOutput.add(new FarmerModel(count_id, fullName, farmerLocation, farmerPhone, FarmerContract.SYNC_STATUS_FAILED));
            }else{
                farmersOutput.add(new FarmerModel(count_id, fullName, farmerLocation, farmerPhone, FarmerContract.SYNC_STATUS_SUCCESS));
            }
        }
        cursor.close();
        buyerCursor.close();



    }

    @Override
    protected void onStart() {
        super.onStart();
        farmerListAdapter.notifyDataSetChanged();
        updateUI();

    }


    public void updateUI(){
        NavigationView navigationView = findViewById(R.id.dashboard_navigation);
        View view = navigationView.getHeaderView(0);
        TextView buyer_name  = view.findViewById(R.id.buyername);
       // Double current_price = 0.0;
        Cursor buyerCursor = sqlBuyerdatabasehelper.readBuyerDataLocally(sqLiteDatabase);
        String buyerName = "";

        if (buyerCursor.moveToFirst()){
            buyerName =  buyerCursor.getString(buyerCursor.getColumnIndex(FarmerContract.BuyerDatabaseEntry.COLUMN_NAME_FIRST_NAME));
            buyerName += " " + buyerCursor.getString(buyerCursor.getColumnIndex(FarmerContract.BuyerDatabaseEntry.COLUMN_NAME_LAST_NAME));
            current_price = buyerCursor.getDouble(buyerCursor.getColumnIndex(FarmerContract.BuyerDatabaseEntry.COLUMN_NAME_CURRENT_PRICE));
        }

        buyer_name.setText(buyerName);
      //  return current_price;
    }
}
