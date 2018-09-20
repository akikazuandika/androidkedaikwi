package com.kedaikwi.rating;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.support.design.widget.Snackbar;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String message = "com.teknosama.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = (Button) findViewById(R.id.buttonLogin);
        Button btnToRegister = (Button) findViewById(R.id.btnToRegister);

        //On user has been login, redirect to Stores Activity
        try {
            JSONObject getInfo = getInfo();
            if(getInfo.getString("telp").equals("")){

            }else{
                Intent intent = new Intent(MainActivity.this, Stores.class);
                startActivity(intent);
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //When user click Register Button, redirect to Register Activity
        btnToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MainActivity.this, Register.class);
                        startActivity(intent);
                        finish();
                    }
                }, 500);
            }
        });

        //On user click Login Button
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(v);
            }
        });

    }

    public void login(final View v){
        final EditText telp = (EditText) findViewById(R.id.telp);
        final EditText password = (EditText) findViewById(R.id.password);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = getResources().getString(R.string.api) + "login";

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res = new JSONObject(response);
                            if(res.getString("success").toString().equals("true")){

                                JSONObject data = new JSONObject(res.getString("results"));

                                Snackbar.make(v, "Logged In", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null)
                                        .show();

                                saveLogin(data.getString("telp").toString(), data.getString("name").toString());

                                int SPLASH_TIME_OUT = 500;
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(MainActivity.this, Stores.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }, SPLASH_TIME_OUT);
                            }else{
                                Snackbar.make(v, "Server error", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null)
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse err =  error.networkResponse;
                        try {
                            JSONObject obj = new JSONObject(new String(err.data));

                            Snackbar.make(v, obj.getString("message"), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null)
                                    .show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("telp", telp.getText().toString());
                parameters.put("password", password.getText().toString());

                return parameters;
            }
        };

        requestQueue.add(stringRequest);
    }

    public void saveLogin(String telp, String name){
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("telp", telp);
        editor.putString("name", name);
        editor.putInt("login", 0);
        editor.apply();
    }

    public JSONObject getInfo() throws JSONException {
        JSONObject obj = new JSONObject();

        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        obj.put("telp", sharedPreferences.getString("telp", ""));
        obj.put("name", sharedPreferences.getString("name", ""));

        return obj;
    }


}
