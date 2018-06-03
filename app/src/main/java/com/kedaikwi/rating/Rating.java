package com.kedaikwi.rating;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class Rating extends AppCompatActivity {


    Button save;
    RatingBar custRating;
    String email;
    EditText customerComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();
        final String id = extras.getString("ID");
        final String rateType = extras.getString("rateType");
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        email = sharedPreferences.getString("email", "");

        if(!id.equals("")){
            getStore(id);
        }

        custRating = findViewById(R.id.customerRating);
        save = findViewById(R.id.btnSave);

        custRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, final float rating, boolean fromUser) {
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JSONObject obj = new JSONObject();
                        customerComment = findViewById(R.id.customerComment);

                        try {
                            obj.put("id_store", id);
                            obj.put("email_customer", email);
                            obj.put("value", rating);
                            obj.put("comment", customerComment.getText().toString());
                            obj.put("type_rate", rateType);

                            save(v, obj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });



    }

    protected void getStore(String id){
        RequestQueue req = Volley.newRequestQueue(this);
        String URL = "http://192.168.43.174/ratingkedaikwi/api/store/id/" + id;

        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject results = new JSONObject(response.getString("results"));
                            TextView storeName = findViewById(R.id.storeName);
                            TextView storeLocation = findViewById(R.id.storeLocation);
                            ImageView image = findViewById(R.id.images);

                            storeName.setText(results.getString("store_name"));
                            storeLocation.setText(results.getString("store_location"));
                            LoadImageFromWebOperations(results.getString("image"), image);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Rest response ", error.toString());
                    }
                }
        );

        req.add(objectRequest);
    }

    public static void LoadImageFromWebOperations(String url, ImageView image) {
        Picasso.get().load(url).into(image);
    };

    protected void save(final View v, final JSONObject data){
        RequestQueue reqQue = Volley.newRequestQueue(this);
        String url = "http://192.168.43.174/ratingkedaikwi/api/store/rate";

        StringRequest strReq = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res = new JSONObject(response);
                            if(res.getString("success").toString().equals("true")){
                                Snackbar.make(v, res.getString("message"), Snackbar.LENGTH_LONG)
                                        .setAction("Action", null)
                                        .show();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(Rating.this, Stores.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }, 1000);
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
                        NetworkResponse err = error.networkResponse;

                        try {
                            JSONObject errRes = new JSONObject(String.valueOf(err.data));
                            Snackbar.make(v, errRes.getString("message"), Snackbar.LENGTH_LONG)
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
                Map<String, String> params = new HashMap<String, String>();
                try {
                    params.put("id_store", data.getString("id_store"));
                    params.put("email_customer", data.getString("email_customer"));
                    params.put("value", data.getString("value"));
                    params.put("comment", data.getString("comment"));
                    params.put("type_rate", data.getString("type_rate"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return params;
            }
        };

        reqQue.add(strReq);
    };
}
