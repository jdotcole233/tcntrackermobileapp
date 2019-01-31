package com.example.khoby.tcntracker;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.khoby.tcntracker.Database.FarmerContract;
import com.example.khoby.tcntracker.Database.SQLBuyerdatabasehelper;
import com.example.khoby.tcntracker.Database.SQLDatabasehelper;
import com.example.khoby.tcntracker.Database.SQLSaledatabasehelper;
import com.example.khoby.tcntracker.NetworkFiles.TonTrackerNetworkMonitoring;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.CookieStore;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.cookie.Cookie;
import cz.msebera.android.httpclient.impl.client.BasicCookieStore;
import cz.msebera.android.httpclient.impl.cookie.BasicClientCookie;

public class MainActivity extends AppCompatActivity {

    AsyncHttpClient buyerRequest = new AsyncHttpClient();
    static JSONObject jsonObject = null;
    private EditText buyerEmail;
    private EditText buyerPassword;
    private Button loginButton;
    SQLBuyerdatabasehelper sqlBuyerdatabasehelper;
    SQLSaledatabasehelper sqlSaledatabasehelper;
    SQLDatabasehelper sqlDatabasehelper;
    SQLiteDatabase sqLiteDatabase;
    SQLiteDatabase salesDatabase;
    SQLiteDatabase farmerssqlDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //parameters to be sent
        final RequestParams buyerLoginData = new RequestParams();

        buyerEmail = findViewById(R.id.buyeremail);
        buyerPassword = findViewById(R.id.buyerpassword);
        loginButton = findViewById(R.id.loginbutton);
        final PersistentCookieStore myCookieData = new PersistentCookieStore(MainActivity.this);

        sqlBuyerdatabasehelper = new SQLBuyerdatabasehelper(MainActivity.this);
        sqlDatabasehelper = new SQLDatabasehelper(MainActivity.this);
        sqlSaledatabasehelper = new SQLSaledatabasehelper(MainActivity.this);


        sqLiteDatabase = sqlBuyerdatabasehelper.getWritableDatabase();
        farmerssqlDatabase = sqlDatabasehelper.getWritableDatabase();
        salesDatabase = sqlSaledatabasehelper.getWritableDatabase();

        sqlDatabasehelper.onCreate(farmerssqlDatabase);

        sqlBuyerdatabasehelper.onCreate(sqLiteDatabase);
        sqlSaledatabasehelper.onCreate(salesDatabase);

        if (myCookieData.getCookies().isEmpty()){

        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String userEmail = buyerEmail.getText().toString();
                String userPassword = buyerPassword.getText().toString();

                if (userEmail.isEmpty() && userPassword.isEmpty()){
                    Toast.makeText(MainActivity.this, "Login credentials needed!!!", Toast.LENGTH_LONG).show();
                    return;
                } else if (userEmail.isEmpty() || userPassword.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Both fields are mandatory!!!", Toast.LENGTH_LONG).show();
                    return;
                } else{
//                    Boolean checkValidEmail = isValidEmail(userEmail);
                    if (!isValidEmail(userEmail)){
                        Toast.makeText(MainActivity.this, "wrong email format ", Toast.LENGTH_LONG).show();
                        return;
                    }

                    buyerLoginData.put("email", userEmail);
                    buyerLoginData.put("password", userPassword);

                    ProgressBar progressBar = new ProgressBar(MainActivity.this);
                    buyerRequest.post(FarmerContract.LOGIN_URL, buyerLoginData, new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            saveBuyerDataOnDevice(response);
                            PersistentCookieStore CookieData = new PersistentCookieStore(MainActivity.this);

                            Log.d("tontracker", "status code " + statusCode + " response " + response.toString());
                            String data = "";
                            for (Cookie a : CookieData.getCookies()){
                                data = a.getValue();
                                Log.d("tontrackercookier", a.getValue().toString());

                            }
                            jsonObject = jsonDecoder(data);


                            Intent changeScreen = new Intent(MainActivity.this, Dashboard.class);
                            startActivity(changeScreen);
                            finish();


                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);

                            Log.d("tontracker", " status code " + statusCode + "r " + responseString);
                        }
                    });

                    Toast.makeText(MainActivity.this, "You clicked me ", Toast.LENGTH_LONG).show();

                }


            }
        });
        } else {
            Intent changeScreen = new Intent(MainActivity.this, Dashboard.class);
            startActivity(changeScreen);
            finish();
        }


    }


    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public  void saveBuyerDataOnDevice(JSONObject data){
        PersistentCookieStore myCookieData = new PersistentCookieStore(this);
        BasicClientCookie personalCookies = new BasicClientCookie("buyerData", data.toString());
        personalCookies.setVersion(1);
        personalCookies.setDomain("http://192.168.100.9:8000");
        personalCookies.setPath("/");
        myCookieData.addCookie(personalCookies);
        Log.d("tontracker", "cookie saved successfully ");
    }


    public  JSONObject jsonDecoder(String data){

        try {
            JSONObject obj = new JSONObject(data);
            JSONObject jsonObject = new JSONObject(obj.getString("buyer_info"));

            HashMap<String, String> responseData = new HashMap<>();
            responseData.put("first_name", jsonObject.getString("first_name"));
            responseData.put("other_name", jsonObject.getString("other_name"));
            responseData.put("last_name", jsonObject.getString("last_name"));
            responseData.put("gender", jsonObject.getString("gender"));
            responseData.put("company_id", String.valueOf(jsonObject.getInt("companiescompany_id")));
            responseData.put("current_price", String.valueOf(obj.getDouble("current_price")));
            responseData.put("buyer_id", String.valueOf(jsonObject.getInt("buyer_id")));
            saveBuyerInformationToLocalDatabase(responseData);

            Log.d("tontracker", jsonObject.get("companiescompany_id").toString());
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONObject jsonObjectRead(){
        return jsonObject;
    }

    public void saveBuyerInformationToLocalDatabase(HashMap<String ,String> buyer){


        sqlBuyerdatabasehelper.updateBuyerLocalDevice(buyer, sqLiteDatabase);
        Log.d("tontracker", "Buyer information sent to local database");
        sqlBuyerdatabasehelper.close();

    }




}
