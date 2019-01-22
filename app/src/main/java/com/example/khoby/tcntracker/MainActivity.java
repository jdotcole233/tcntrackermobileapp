package com.example.khoby.tcntracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieStore;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.cookie.Cookie;
import cz.msebera.android.httpclient.impl.client.BasicCookieStore;
import cz.msebera.android.httpclient.impl.cookie.BasicClientCookie;

public class MainActivity extends AppCompatActivity {

    final private String LOGIN_URL = "http://192.168.100.9:8000/loginfrommobile";
    AsyncHttpClient buyerRequest = new AsyncHttpClient();
    static JSONObject jsonObject = null;
    private EditText buyerEmail;
    private EditText buyerPassword;
    private Button loginButton;

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


                    buyerRequest.post(LOGIN_URL, buyerLoginData, new JsonHttpResponseHandler(){
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


    public static JSONObject jsonDecoder(String data){

        try {
            JSONObject obj = new JSONObject(data);
            JSONObject jsonObject = new JSONObject(obj.getString("buyer_info"));
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

}
