package com.example.khoby.tcntracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_profiles);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        farmer_list = findViewById(R.id.farmer_list);
       // farmerModels = getFarmers();
        final PersistentCookieStore myCookieData = new PersistentCookieStore(this);
        farmersOutput = new ArrayList<>();
        readFromLocalDeviceDatabase();
        farmerListAdapter = new FarmerListAdapter(this, farmersOutput, 2.0);
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
        int count_id = 0;

        Cursor cursor = sqlDatabasehelper.readFromDeviceDatabase(sqLiteDatabase);
        while (cursor.moveToNext()){
            String fullName = cursor.getString(cursor.getColumnIndex(FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_FIRST_NAME)) + " " +
                    cursor.getString(cursor.getColumnIndex(FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_OTHER_NAME)) + " " +
                    cursor.getString(cursor.getColumnIndex(FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_LAST_NAME));
            String farmerLocation = cursor.getString(cursor.getColumnIndex(FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_COMMUNITY_NAME));

            String farmerPhone = cursor.getString(cursor.getColumnIndex(FarmerContract.FarmerDatabaseEntry.COLUMN_NAME_PHONE_NUMBER));
            count_id++;
            farmersOutput.add(new FarmerModel(count_id, fullName, farmerLocation, farmerPhone));
        }
        cursor.close();
        sqlDatabasehelper.close();

    }


}
