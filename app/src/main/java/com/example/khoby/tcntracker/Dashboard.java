package com.example.khoby.tcntracker;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.khoby.tcntracker.Database.FarmerContract;
import com.example.khoby.tcntracker.Database.SQLBuyerdatabasehelper;
import com.example.khoby.tcntracker.NetworkFiles.TonTrackerNetworkMonitoring;
import com.example.khoby.tcntracker.NetworkFiles.TonTrackerNetworkService;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.cookie.Cookie;
import cz.msebera.android.httpclient.impl.cookie.BasicClientCookie;

import static com.example.khoby.tcntracker.NetworkFiles.TonTrackerNetworkService.isNetworkConnectionAvailable;

@SuppressLint("RestrictedApi")

public class Dashboard extends AppCompatActivity {

    private DrawerLayout mydrawer;
    TonTrackerNetworkMonitoring tonTrackerNetworkMonitoring;
    IntentFilter intentFilter;
    TextView dayanddatedisplay;
    ImageView connection_status;
    LocalDate localDate;


    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        final PersistentCookieStore myCookieData = new PersistentCookieStore(Dashboard.this);
        String [] months = {"January", "Feburary", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        String [] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            localDate = LocalDate.now();
        }




        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mydrawer = findViewById(R.id.navigation_drawer);
        NavigationView navigationView = findViewById(R.id.dashboard_navigation);
        tonTrackerNetworkMonitoring = new TonTrackerNetworkMonitoring();
        intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        dayanddatedisplay = findViewById(R.id.dayanddate);
        connection_status = findViewById(R.id.connectionstate);
        dayanddatedisplay.setText(days[localDate.getDayOfWeek().getValue()]+ ", " + months[localDate.getMonth().getValue()- 1] + " " + localDate.getDayOfMonth());





        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                mydrawer.closeDrawers();

                switch (menuItem.getItemId()){
                    case R.id.drawer_home:
                        Intent intent = new Intent(Dashboard.this,Dashboard.class);
                        startActivity(intent);
                        finish();
                        break;

                    case R.id.create_farmer_sale:
                        Intent intent1 = new Intent(Dashboard.this,FarmerProfiles.class);
                        startActivity(intent1);
                        finish();
                        break;

                    case R.id.create_farmer:
                        Intent intent2 = new Intent(Dashboard.this,CreateFarmerProfile.class);
                        startActivity(intent2);
                        finish();
                        break;

                    case R.id.logout:
                        Log.i("loggedout","loggedout");
                        if (!myCookieData.getCookies().isEmpty()){
                            SQLBuyerdatabasehelper sqlBuyerdatabasehelper = new SQLBuyerdatabasehelper(Dashboard.this);
                            SQLiteDatabase sqLiteDatabase = sqlBuyerdatabasehelper.getWritableDatabase();
                            sqlBuyerdatabasehelper.resetBuyerTable(sqLiteDatabase);
                            Log.d("tontracker", myCookieData.getCookies().toString());
                            myCookieData.clear();
                            Log.d("tontracker", "After clear" + myCookieData.getCookies().toString());
                            Intent switchBack = new Intent(Dashboard.this, MainActivity.class);
                            startActivity(switchBack);
                            finish();
                        }
                        break;

                }

                return false;
            }
        }); //end of navigation drawer

        saveCommunitiesOnDevice();






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


    public void saveCommunitiesOnDevice() {
        PersistentCookieStore persistentCookieStore = new PersistentCookieStore(Dashboard.this);
        BasicClientCookie basicClientCookie = null;
        String jsonObject = null;
        int i = 0;
        for (Cookie a : persistentCookieStore.getCookies()){
            i++;
            try {
                jsonObject = new JSONObject(a.getValue()).getJSONObject("buyer_info").getString("companiescompany_id");
                if (jsonObject.contains("companiescompany_id")) break;
                Log.d("tontracker","Number " + i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (!jsonObject.isEmpty()){
            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
            RequestParams requestParams = new RequestParams();
            requestParams.add("company_id", jsonObject);
            asyncHttpClient.post(FarmerContract.LOCATION_URL, requestParams, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.d("tontracker", "jsonObject returned for communities" + response);
                    PersistentCookieStore innerCookiesStore = new PersistentCookieStore(Dashboard.this);
                    BasicClientCookie cookie = new BasicClientCookie("communities", response.toString());
                    cookie.setVersion(2);
                    cookie.setDomain("http://192.168.100.9:8000");
                    cookie.setPath("/");
                    innerCookiesStore.addCookie(cookie);

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.d("tontracker", "jsonArray returned for communities");

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.d("tontracker", "jsonObject error returned for communities " + errorResponse);

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.d("tontracker", "jsonArray error returned for communities");

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Log.d("tontracker", "throwable error returned for communities");

                }
            });
        }



    }




    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(tonTrackerNetworkMonitoring, intentFilter);
        Boolean isNetworkOn = isNetworkConnectionAvailable(this);

        if(isNetworkOn){
            connection_status.setImageResource(R.drawable.ic_network_check_black_24dp);
        } else {
            connection_status.setImageResource(R.drawable.ic_unavailable_network_check_black_24dp);
        }

    }


    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(tonTrackerNetworkMonitoring);
    }
}
