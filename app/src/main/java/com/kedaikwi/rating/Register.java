package com.kedaikwi.rating;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

public class Register extends AppCompatActivity {

    EditText inputName;
    EditText inputEmail;
    EditText inputPassword;
    EditText inputPasswordConfirm;
    EditText inputAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button btnRegister = findViewById(R.id.btnRegister);
        inputPassword = findViewById(R.id.inputPassword);
        inputPasswordConfirm = findViewById(R.id.inputPasswordConfirm);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputPassword.getText().toString().equals("") || inputPasswordConfirm.getText().toString().equals("")){
                    Snackbar.make(v, "Password is empty", Snackbar.LENGTH_LONG)
                            .setAction("Action", null)
                            .show();
                }
                else{
                    if(inputPassword.getText().toString().equals(inputPasswordConfirm.getText().toString())){
                        register(v);
                    }else{
                        Snackbar.make(v, "Confirm password not same", Snackbar.LENGTH_LONG)
                                .setAction("Action", null)
                                .show();
                    }
                }
            }
        });
    }

    protected void register(final View v){

        inputName = findViewById(R.id.inputName);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputPasswordConfirm = findViewById(R.id.inputPasswordConfirm);
        inputAddress = findViewById(R.id.inputAddress);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = "http://192.168.43.174/ratingkedaikwi/api/register";

        StringRequest strReq = new StringRequest(
                Request.Method.POST,
                URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res = new JSONObject(response);
                            if(res.getString("success").toString().equals("true")){
                                Snackbar.make(v, inputName.getText().toString() + " has been registered", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null)
                                        .show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(Register.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }, 1000);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Snackbar.make(v, "Server error", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null)
                                    .show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse errRes = error.networkResponse;
                        try {
                            JSONObject err = new JSONObject(new String(errRes.data));

                            Snackbar.make(v, err.getString("message"), Snackbar.LENGTH_LONG)
                                    .setAction("Actioin", null)
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
                parameters.put("email", inputEmail.getText().toString());
                parameters.put("password", inputPassword.getText().toString());
                parameters.put("name", inputName.getText().toString());
                parameters.put("address", inputAddress.getText().toString());

                return parameters;
            }
        };

        requestQueue.add(strReq);
    }
}
