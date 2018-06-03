package com.kedaikwi.rating;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Stores extends AppCompatActivity {

//    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stores);

//        btn = findViewById(R.id.button2);

        RequestQueue req = Volley.newRequestQueue(this);
        String url = "http://192.168.43.174/ratingkedaikwi/api/store";

        JsonObjectRequest jsonReq = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("success").toString().equals("true")){
                                JSONArray item = response.getJSONArray("results");
                                for (int i = 0; i < item.length(); i++){
                                    JSONObject data = item.getJSONObject(i);
                                    createView(data);
                                }
                            };
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("page", "1");
                params.put("per-page", "10");
                return params;
            }
        };

        req.add(jsonReq);

//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Intent intent = new Intent(Stores.this, Rating.class);
//                        intent.putExtra("ID", "1");
//                        startActivity(intent);
//                    }
//                }, 100);
//            }
//        });
    }

    protected void createView(final JSONObject data) throws JSONException {
        LinearLayout mainWrapper = findViewById(R.id.mainWrapper);
        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 260));

        ImageView  image = new ImageView(this);
        LoadImageFromWebOperations(data.getString("image"), image);
        RelativeLayout.LayoutParams layoutParamsImage = new RelativeLayout.LayoutParams(300, 300);
        image.setLayoutParams(layoutParamsImage);
        int imageId = View.generateViewId();
        image.setId(imageId);

        TextView storeName = new TextView(this);
        storeName.setText(data.getString("store_name"));
        RelativeLayout.LayoutParams layoutParamsStoreName = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsStoreName.addRule(RelativeLayout.RIGHT_OF, imageId);
        layoutParamsStoreName.setMargins(0, 20, 0, 0);
        storeName.setLayoutParams(layoutParamsStoreName);
        int storeNameId = View.generateViewId();
        storeName.setId(storeNameId);

        TextView storeLocation = new TextView(this);
        storeLocation.setText(data.getString("store_location"));
        RelativeLayout.LayoutParams layoutParamsStoreLocation = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsStoreLocation.addRule(RelativeLayout.BELOW, storeNameId);
        layoutParamsStoreLocation.addRule(RelativeLayout.RIGHT_OF, imageId);
        layoutParamsStoreLocation.setMargins(0, 50, 0, 0);
        storeLocation.setLayoutParams(layoutParamsStoreLocation);

        Button ratingProduct = new Button(this);
        ratingProduct.setId(Integer.parseInt(data.getString("id_store")));
        ratingProduct.setText("Product");
        RelativeLayout.LayoutParams layoutParamsRatingProduct = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsRatingProduct.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParamsRatingProduct.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParamsRatingProduct.setMargins(0, 10, 20, 0);
        ratingProduct.setLayoutParams(layoutParamsRatingProduct);
        ratingProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(Stores.this, Rating.class);
                        try {
                            intent.putExtra("ID", data.getString("id_store"));
                            intent.putExtra("rateType", "rating_product");
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, 100);
            }
        });

        Button ratingService = new Button(this);
        ratingService.setId(Integer.parseInt(data.getString("id_store")));
        ratingService.setText("Service");
        RelativeLayout.LayoutParams layoutParamsRartingService = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsRartingService.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParamsRartingService.setMargins(0, 130, 20, 0);
        ratingService.setLayoutParams(layoutParamsRartingService);
        ratingService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(Stores.this, Rating.class);
                        try {
                            intent.putExtra("ID", data.getString("id_store"));
                            intent.putExtra("rateType", "rating_service");
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, 100);
            }
        });

        relativeLayout.addView(image);
        relativeLayout.addView(storeName);
        relativeLayout.addView(storeLocation);
        relativeLayout.addView(ratingProduct);
        relativeLayout.addView(ratingService);
        mainWrapper.addView(relativeLayout);
    }

    public static void LoadImageFromWebOperations(String url, ImageView image) {
        Picasso.get().load(url).into(image);
    };
}
