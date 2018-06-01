package com.teknosama.rating;

import android.app.VoiceInteractor;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

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

        Button btn = findViewById(R.id.buttonLogin);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(v);
            }
        });

        try {
            JSONObject getInfo = getInfo();
            if(getInfo.getString("email").equals("")){

            }else{
                Intent intent = new Intent(MainActivity.this, Rating.class);
                startActivity(intent);
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void login(final View v){
        final EditText email = findViewById(R.id.email);
        final EditText password = findViewById(R.id.password);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = "http://192.168.43.174/ratingkedaikwi/api/login";

//        JsonObjectRequest objectRequest = new JsonObjectRequest(
//                Request.Method.GET,
//                URL,
//                null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//
//                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//
//                            builder.setMessage(response.getString("message"));
//
//                            AlertDialog alert = builder.create();
//                            alert.show();
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e("Rest response ", error.toString());
//                    }
//                }
//        );

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

                                saveLogin(data.getString("email").toString(), data.getString("name").toString(), data.getString("address").toString());

                                int SPLASH_TIME_OUT = 500;
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(MainActivity.this, Rating.class);
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
                parameters.put("email", email.getText().toString());
                parameters.put("password", password.getText().toString());

                return parameters;
            }
        };

        requestQueue.add(stringRequest);

//        if(email.getText().toString().equals("akikazuandika@gmail.com") && password.getText().toString().equals("andika")){
//            Snackbar.make(v, "Logged In", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null)
//                    .show();
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Intent intent = new Intent(MainActivity.this, Rating.class);
//                    intent.putExtra(message, email.getText().toString());
//                    startActivity(intent);
//                    finish();
//                }
//            }, SPLASH_TIME_OUT);
//
//        }else{
//            Snackbar.make(v, "Wrong username or password. ", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null)
//                    .show();
//        }
    }

    public void saveLogin(String email, String name, String address){
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.putString("name", name);
        editor.putString("address", address);
        editor.apply();
    }

    public JSONObject getInfo() throws JSONException {
        JSONObject obj = new JSONObject();

        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        obj.put("email", sharedPreferences.getString("email", ""));
        obj.put("name", sharedPreferences.getString("name", ""));
        obj.put("address", sharedPreferences.getString("address", ""));

        return obj;
    }


}
