package com.example.khoby.tcntracker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;

import static com.example.khoby.tcntracker.MainActivity.jsonObjectRead;

public class CreateFarmerProfile extends AppCompatActivity {

    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private final String LOCATION_URL = "http://192.168.100.10:8000/communities";

    private DrawerLayout mydrawer;
    private Spinner genderSpinner;
    private Spinner locationspinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_farmer_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        genderSpinner = findViewById(R.id.genderspinner);
        locationspinner = findViewById(R.id.locationspinner);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        HashMap<Integer, String > comm = getLocaleCommunities();
        ArrayList<String> communities = new ArrayList<>();

        for (String community_name : comm.values()){
            communities.add(community_name);
        }

//        Populate gender spinner
        ArrayAdapter<CharSequence> genders = ArrayAdapter.createFromResource(this,communities, android.R.layout.simple_spinner_item);
        genders.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genders);

        // populate location spinner with dummy data, don't forget to populate with dynamic data
        ArrayAdapter<CharSequence> locations = ArrayAdapter.createFromResource(this, R.array.location, android.R.layout.simple_spinner_item);
        locations.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationspinner.setAdapter(locations);


        mydrawer = findViewById(R.id.navigation_drawer);
        NavigationView navigationView = findViewById(R.id.dashboard_navigation);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                mydrawer.closeDrawers();

                switch (menuItem.getItemId()){
                    case R.id.drawer_home:
                        Intent intent = new Intent(CreateFarmerProfile.this,Dashboard.class);
                        startActivity(intent);
                        finish();
                        break;

                    case R.id.create_farmer_sale:
                        Intent intent1 = new Intent(CreateFarmerProfile.this,FarmerProfiles.class);
                        startActivity(intent1);
                        finish();
                        break;

                    case R.id.create_farmer:
                        Intent intent2 = new Intent(CreateFarmerProfile.this,CreateFarmerProfile.class);
                        startActivity(intent2);
                        finish();
                        break;

                    case R.id.logout:
                        Log.i("loggedout","loggedout");
                        break;

                }

                return false;
            }
        });
        //get locations


    }

    //get current locations and fill an ArrayList
    public HashMap<Integer,String> getLocaleCommunities(){
        final HashMap<Integer,String> communities = new HashMap<>();

        try {
            JSONObject jsonObject = jsonObjectRead();
            if (jsonObject != null){
                RequestParams params = new RequestParams("company_id", jsonObject.getInt("companiescompany_id"));
                asyncHttpClient.post(LOCATION_URL, params, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try{
                            Log.d("tontracker", "" + statusCode + " -> Response " + response.getJSONArray("community").get(0).toString());
                            int arraysize = response.getJSONArray("community").length();
                            for (int i = 0; i < arraysize; i++){
                                JSONObject object = response.getJSONArray("community").getJSONObject(i);
                                communities.put(object.getInt("community_id"), object.getString("community_name"));
                            }

                        }catch (JSONException e){
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Log.d("tontracker", "Error from create farmer " + statusCode + "Response " + responseString);
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return communities;
    }

}
