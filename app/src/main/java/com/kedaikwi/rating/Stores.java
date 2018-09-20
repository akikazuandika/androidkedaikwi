package com.kedaikwi.rating;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
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

        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        int login = sharedPreferences.getInt("login", 0);

        showWelcomeDialog();
        if(login < 1){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("login", 1);
            editor.apply();
        }

        RequestQueue req = Volley.newRequestQueue(this);
        String url = getResources().getString(R.string.api) + "store";

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
    }

    protected void showWelcomeDialog(){
        AlertDialog.Builder dialog =  new AlertDialog.Builder(this);

        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("name", "");
        String arrName[] = name.split(" ");

        dialog.setTitle("Welcome " + arrName[0]);
        RelativeLayout layoutLogo = new RelativeLayout(this);
        ImageView logo = new ImageView(this);
        logo.setImageResource(R.mipmap.ic_launcher);
        layoutLogo.addView(logo);
        dialog.setView(layoutLogo);

        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
        alertDialog.getWindow().setLayout(600,600);

    }

    protected void createView(final JSONObject data) throws JSONException {
        LinearLayout mainWrapper = (LinearLayout) findViewById(R.id.mainWrapper);
        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        RelativeLayout.LayoutParams layoutParamsMain = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 300);
        layoutParamsMain.setMargins(0,0,0,10);
        relativeLayout.setLayoutParams(layoutParamsMain);

        //Text Store Name
        TextView storeName = new TextView(this);
        storeName.setTextSize(17);
        storeName.setText(data.getString("store_name"));
        storeName.setTextColor(getResources().getColor(R.color.blue));
        storeName.setTypeface(null, Typeface.BOLD);
        RelativeLayout.LayoutParams layoutParamsStoreName = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsStoreName.setMargins(20, 60, 0, 0);
        storeName.setLayoutParams(layoutParamsStoreName);
        int storeNameId = View.generateViewId();
        storeName.setId(storeNameId);

        //Text Location
        TextView storeLocation = new TextView(this);
        storeLocation.setTextSize(17);
        storeLocation.setTextColor(getResources().getColor(R.color.blueMaterial));
        storeLocation.setText(data.getString("store_location"));
        RelativeLayout.LayoutParams layoutParamsStoreLocation = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsStoreLocation.setMargins(20, 140, 0, 0);
        storeLocation.setLayoutParams(layoutParamsStoreLocation);

        //Rate product
        Button ratingProduct = new Button(this);
//        ratingProduct.setBackgroundColor(getResources().getColor(R.color.cyan));
        ratingProduct.setBackground(getResources().getDrawable(R.drawable.rounded_button));
        ratingProduct.setTextColor(getResources().getColor(R.color.white));
        ratingProduct.setId(Integer.parseInt(data.getString("id_store")));
        ratingProduct.setText("Product");
        RelativeLayout.LayoutParams layoutParamsRatingProduct = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsRatingProduct.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParamsRatingProduct.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParamsRatingProduct.setMargins(0, 30, 20, 0);
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

        //Rate Service
        Button ratingService = new Button(this);
//        ratingService.setBackgroundColor(getResources().getColor(R.color.blue));
        ratingService.setBackground(getResources().getDrawable(R.drawable.rounded_button));
        ratingService.setId(Integer.parseInt(data.getString("id_store")));
        ratingService.setText("Service");
        ratingService.setTextColor(getResources().getColor(R.color.white));
        RelativeLayout.LayoutParams layoutParamsRartingService = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsRartingService.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParamsRartingService.setMargins(0, 180, 20, 20);
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

        relativeLayout.addView(storeName);
        relativeLayout.addView(storeLocation);
        relativeLayout.addView(ratingProduct);
        relativeLayout.addView(ratingService);

        View viewDivider = new View(this);
        float dividerHeight = getResources().getDisplayMetrics().density * 1; // 1dp to pixels
        RelativeLayout.LayoutParams dividerParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) dividerHeight);
        viewDivider.setLayoutParams(dividerParams);
        viewDivider.setBackgroundColor(Color.parseColor("#cccccc"));
        relativeLayout.addView(viewDivider);

        mainWrapper.addView(relativeLayout);
    }

    public static void LoadImageFromWebOperations(String url, ImageView image) {
        Picasso.get().load(url).into(image);
    };
}
