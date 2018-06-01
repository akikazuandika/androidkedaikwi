package com.teknosama.rating;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class Rating extends AppCompatActivity {


    Button save;
    RatingBar custRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.message);

        custRating = findViewById(R.id.customerRating);
        save = findViewById(R.id.btnSave);

        custRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, final float rating, boolean fromUser) {
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        save(v, rating);
                    }
                });
            }
        });



    }

    protected void save(View v, float rating){
        TextView name = findViewById(R.id.storeName);
        EditText comment = findViewById(R.id.customerComment);
        TextView location = findViewById(R.id.storeLocation);
        name.setText(String.valueOf(rating));
        location.setText(comment.getText());
    }


}
