package com.example.khoby.tcntracker;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.khoby.tcntracker.Database.SQLDatabasehelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

import static com.example.khoby.tcntracker.MainActivity.jsonObjectRead;

public class CreateFarmerProfile extends AppCompatActivity {

    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private final String LOCATION_URL = "http://192.168.100.8:8000/communities";


    private DrawerLayout mydrawer;
    private Spinner genderSpinner;
    private Spinner locationspinner;
    HashMap<Integer, String> localcommunity = null;
    private EditText first_name;
    private  EditText other_name;
    private  EditText last_name;
    private EditText phone_number;
    private Button register_button;
    private String selectedGender;
    private String selectedLocation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_farmer_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        selectedGender = "";
        selectedLocation = "";
        genderSpinner = findViewById(R.id.genderspinner);
        locationspinner = findViewById(R.id.locationspinner);
        first_name = findViewById(R.id.first_name);
        other_name = findViewById(R.id.other_name);
        last_name = findViewById(R.id.last_name);
        phone_number = findViewById(R.id.phone_number);
        register_button = findViewById(R.id.register_button);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        final PersistentCookieStore myCookieData = new PersistentCookieStore(this);
        localcommunity = new HashMap<>();
        getLocaleCommunities();

        //get locations
        for (String a : localcommunity.values()){
            Log.d("tontracker", a);
        }


//        Populate gender spinner
        ArrayAdapter<CharSequence> genders = ArrayAdapter.createFromResource(this, R.array.gender, android.R.layout.simple_spinner_item);
        genders.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genders);



        //Gender spinner Listener
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGender = parent.getItemAtPosition(position).toString();
                Log.d("tontracker", parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Location spinner Listener
        locationspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLocation = parent.getItemAtPosition(position).toString();
                Log.d("tontracker", parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFormFilled()){
                    Log.d("tontracker", "Fill out the necessary parts");
                } else{

                    HashMap<String, String> collectedData = new HashMap<>();
                    //Check if the device is not in airplane mode, or there is an active internet connection
                    collectedData.put("first_name", first_name.getText().toString());
                    collectedData.put("other_name", other_name.getText().toString());
                    collectedData.put("last_name", last_name.getText().toString());
                    collectedData.put("gender", selectedGender);
                    collectedData.put("phone_number", phone_number.getText().toString());
                    collectedData.put("location", selectedLocation);
                    saveDataToDeviceDatabase(collectedData);
                    clearFromInputs();
                    Log.d("tontracker", "properly filled");
                }
            }
        });


        // populate location spinner with dummy data, don't forget to populate with dynamic data
//        ArrayAdapter<CharSequence> locations = ArrayAdapter.createFromResource(this, R.array.location, android.R.layout.simple_spinner_item);
//        communities.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        locationspinner.setAdapter(communities);


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
                        if (!myCookieData.getCookies().isEmpty()){
                            Log.d("tontracker", myCookieData.getCookies().toString());
                            myCookieData.clear();
                            Log.d("tontracker", "After clear" + myCookieData.getCookies().toString());
                            Intent switchBack = new Intent(CreateFarmerProfile.this, MainActivity.class);
                            startActivity(switchBack);
                            finish();
                        }
                        break;

                }

                return false;
            }
        });


    }

    //get current locations and fill an ArrayList
    public void getLocaleCommunities(){

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
                            List<String> comm_name = new ArrayList<>();
                            final HashMap<Integer,String> locales = new HashMap<>();
                            for (int i = 0; i < arraysize; i++){
                                JSONObject object = response.getJSONArray("community").getJSONObject(i);
                                Log.d("tontracker", object.getInt("community_id") + " " + object.getString("community_name") );
                                locales.put(object.getInt("community_id"), object.getString("community_name"));
                                comm_name.add(object.getString("community_name"));
                            }
                            passCommunities(locales);


                            ArrayAdapter<String> communities = new ArrayAdapter<String>(CreateFarmerProfile.this,android.R.layout.simple_spinner_item,comm_name);
                            communities.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            locationspinner.setAdapter(communities);
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
//
//        return communities;
    }



    public HashMap<Integer, String> passCommunities(HashMap<Integer, String> locales){
        localcommunity = locales;
        Log.d("tontracker", "passed0");
        return localcommunity;
    }


    //Validate farmer forms to ensure mandatory parts are filled
    public boolean isFormFilled(){
        boolean isFilled = true;
        if ( first_name.getText().toString().isEmpty() ||
                last_name.getText().toString().isEmpty() ||
                phone_number.getText().toString().isEmpty() ||
                selectedGender.equals("Select Gender") ){

            isFilled = false;

        }
        return isFilled;
    }


    //clear the farmer forms for next input
    public void clearFromInputs(){
        first_name.setText("");
        other_name.setText("");
        last_name.setText("");
        phone_number.setText("");
    }




    //save data to the device whiles checking for internet connectivity
    public void saveDataToDeviceDatabase(HashMap<String, String> farmerdata){
        boolean isConnectionAvailable = TonTrackerNetworkService.isNetworkConnectionAvailable(this);
        SQLDatabasehelper sqlDatabasehelper = new SQLDatabasehelper(this);
        SQLiteDatabase sqLiteDatabase = sqlDatabasehelper.getWritableDatabase();

        if (isConnectionAvailable){

        } else {
            sqlDatabasehelper.populateDeviceDatabase(farmerdata, sqLiteDatabase);
            Log.d("tontracker", "Data saved successfully");
        }
        sqlDatabasehelper.close();
    }

}
