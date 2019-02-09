package com.example.khoby.tcntracker;

import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.khoby.tcntracker.Database.FarmerContract;
import com.example.khoby.tcntracker.Database.SQLBuyerdatabasehelper;
import com.example.khoby.tcntracker.Database.SQLDatabasehelper;
import com.example.khoby.tcntracker.NetworkFiles.TonTrackerNetworkService;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.cookie.Cookie;

import static com.example.khoby.tcntracker.MainActivity.temporaryModalDisplay;

public class CreateFarmerProfile extends AppCompatActivity {

    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
//    private final String LOCATION_URL = "http://192.168.100.9:8000/communities";


    private DrawerLayout mydrawer;
    private Spinner genderSpinner;
    private Spinner locationspinner;
    HashMap<Integer, String> localcommunity = null;
    public  static HashMap<String, Integer> returncommunity = null;
    private EditText first_name;
    private  EditText other_name;
    private  EditText last_name;
    private EditText phone_number;
    private Button register_button;
    private String selectedGender;
    private String selectedLocation;
    private Integer selectedLocationID;
    SQLBuyerdatabasehelper sqlBuyerdatabasehelper;
    SQLiteDatabase sqLiteDatabase;



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
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        final PersistentCookieStore myCookieData = new PersistentCookieStore(this);
        localcommunity = new HashMap<>();
        returncommunity = new HashMap<>();
        List<String> communityname = new ArrayList<>();
        communityname.add("Select Community");
        JSONArray jsonArray = new JSONArray();
        sqlBuyerdatabasehelper = new SQLBuyerdatabasehelper(this);
        sqLiteDatabase = sqlBuyerdatabasehelper.getReadableDatabase();



        //Filter cookies to get communities
        for (Cookie cookie : myCookieData.getCookies()){
            try {
                JSONObject object = new JSONObject(cookie.getValue());
                if(cookie.getVersion() == 2){
                    jsonArray = object.getJSONArray("community");
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //set up List array for for location spinner and set up HashMap to keep track of location and their respective id
        for (int count = 0; count < jsonArray.length(); count++){
            try {
                Integer communityID =  new JSONObject(String.valueOf(jsonArray.get(count))).getInt("community_id");
                String communityName =  new JSONObject(String.valueOf(jsonArray.get(count))).getString("community_name");
                returncommunity.put(communityName, communityID);
                communityname.add(communityName);
                Log.d("tontracker", "Locate Array" + communityID + " " + communityName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }




       // getLocaleCommunities();



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
                selectedLocationID = returncommunity.get(selectedLocation);
                Log.d("tontracker", parent.getItemAtPosition(position).toString() + " " + selectedLocationID);
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
                    Date date = new Date();
                    long time = date.getTime();
                    Timestamp timestamp = new Timestamp(time);

                    //Check if the device is not in airplane mode, or there is an active internet connection
                    collectedData.put("first_name", first_name.getText().toString());
                    collectedData.put("other_name", other_name.getText().equals("")? " " : other_name.getText().toString());
                    collectedData.put("last_name", last_name.getText().toString());
                    collectedData.put("gender", selectedGender);
                    collectedData.put("phone_number", phone_number.getText().toString());
                    collectedData.put("community_id", String.valueOf(selectedLocationID));
                    collectedData.put("community_name", selectedLocation);
                    collectedData.put("created_at", String.valueOf(timestamp));
//                    saveDataToDeviceDatabase(collectedData);
                    saveDataToServerDatabase(collectedData);
                    clearFromInputs();
                    Log.d("tontracker", "properly filled");
                }
            }
        });


        // populate location spinner with dummy data, don't forget to populate with dynamic data
//        ArrayAdapter<CharSequence> locations = ArrayAdapter.createFromResource(this, R.array.location, android.R.layout.simple_spinner_item);
//        locations.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        locationspinner.setAdapter(locations);


        ArrayAdapter<String> communities = new ArrayAdapter<String>(CreateFarmerProfile.this,android.R.layout.simple_spinner_item, communityname);
        communities.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationspinner.setAdapter(communities);


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
                            SQLBuyerdatabasehelper sqlBuyerdatabasehelper = new SQLBuyerdatabasehelper(CreateFarmerProfile.this);
                            SQLiteDatabase sqLiteDatabase = sqlBuyerdatabasehelper.getWritableDatabase();
                            sqlBuyerdatabasehelper.resetBuyerTable(sqLiteDatabase);
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

        updateUI();
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

    //get current locations and fill an ArrayList
/*    public void getLocaleCommunities(){

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
*/


    @Override
    protected void onStart() {
        super.onStart();
        updateUI();
    }

    //Validate farmer forms to ensure mandatory parts are filled
    public boolean isFormFilled(){
        boolean isFilled = true;
        if ( first_name.getText().toString().isEmpty() ||
                last_name.getText().toString().isEmpty() ||
                phone_number.getText().toString().isEmpty() ||
                selectedGender.equals("Select Gender") ||
                selectedLocation.equals("Select Community")){

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
        genderSpinner.setSelection(0);
        locationspinner.setSelection(0);
    }


    //save data to local database
    public void saveDataToDeviceDatabase(HashMap<String, String> farmerdata, int sync_status){
        SQLDatabasehelper sqlDatabasehelper = new SQLDatabasehelper(this);
        SQLiteDatabase sqLiteDatabase = sqlDatabasehelper.getWritableDatabase();
        sqlDatabasehelper.populateDeviceDatabase(farmerdata, sync_status,sqLiteDatabase);
        Log.d("tontracker", "Data saved successfully");
        sqlDatabasehelper.close();
    }


    //save data to the device whiles checking for internet connectivity
    public void saveDataToServerDatabase(final HashMap<String, String> farmerdata){

        boolean isConnectionAvailable = TonTrackerNetworkService.isNetworkConnectionAvailable(this);


        Cursor cursor = sqlBuyerdatabasehelper.readBuyerDataLocally(sqLiteDatabase);
        String buyer_id = null;
        String company_id = null;


        while (cursor.moveToNext()){
            buyer_id = cursor.getString(cursor.getColumnIndex(FarmerContract.BuyerDatabaseEntry.COLUMN_NAME_BUYER_ID));
            company_id = cursor.getString(cursor.getColumnIndex(FarmerContract.BuyerDatabaseEntry.COLUMN_NAME_COMMPANY_ID));
        }


        if (isConnectionAvailable){

            AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
            RequestParams requestParams = new RequestParams();

            requestParams.add("first_name", farmerdata.get("first_name"));
            requestParams.add("other_name", farmerdata.get("other_name"));
            requestParams.add("last_name", farmerdata.get("last_name"));
            requestParams.add("gender", farmerdata.get("gender"));
            requestParams.add("phone_number", farmerdata.get("phone_number"));
            requestParams.add("buyer_id", buyer_id);
            requestParams.add("community_id", farmerdata.get("community_id"));
            requestParams.add("company_id", company_id);
            requestParams.add("created_at", farmerdata.get("created_at"));

            asyncHttpClient.post(FarmerContract.REGISTER_FARMER_URL, requestParams, new JsonHttpResponseHandler(){

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);

                    try {

                        String responseFromServer = response.getString("response");
                        if(responseFromServer.equals("SUCCESSFUL")){
                            saveDataToDeviceDatabase(farmerdata, FarmerContract.SYNC_STATUS_SUCCESS);
                            temporaryModalDisplay(CreateFarmerProfile.this, "Saved to the server and local database", "Server response");

                            Log.d("tontracker", "Saved to the server and local database");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    saveDataToDeviceDatabase(farmerdata, FarmerContract.SYNC_STATUS_FAILED);
                    temporaryModalDisplay(CreateFarmerProfile.this, "Error encountered, but data has been saved to local device", "Server response");
                    Log.d("tontracker", "Error encountered, but data has been saved to local device");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Log.d("tontracker", "Failure response from create farmer " + responseString);
                }
            });
        } else {
            saveDataToDeviceDatabase(farmerdata, FarmerContract.SYNC_STATUS_FAILED);
            temporaryModalDisplay(CreateFarmerProfile.this, "Data has been saved to local device", "Device response");
        }
    }



    public void updateUI(){
        NavigationView navigationView = findViewById(R.id.dashboard_navigation);
        View view = navigationView.getHeaderView(0);
        TextView buyer_name  = view.findViewById(R.id.buyername);

        Cursor buyerCursor = sqlBuyerdatabasehelper.readBuyerDataLocally(sqLiteDatabase);
        String buyerName = "";

        if (buyerCursor.moveToFirst()){
            buyerName =  buyerCursor.getString(buyerCursor.getColumnIndex(FarmerContract.BuyerDatabaseEntry.COLUMN_NAME_FIRST_NAME));
            buyerName += " " + buyerCursor.getString(buyerCursor.getColumnIndex(FarmerContract.BuyerDatabaseEntry.COLUMN_NAME_LAST_NAME));
        }

        buyer_name.setText(buyerName);

    }

}
