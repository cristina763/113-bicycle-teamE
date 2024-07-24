package com.example.bmicalculator;
import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Googlemap extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        ImageView routeImage = findViewById(R.id.routeImage);
        TextView routeName = findViewById(R.id.routeName);
        TextView startPoint = findViewById(R.id.startPoint);
        TextView endPoint = findViewById(R.id.endPoint);

        // Get data from intent
        int imageResId = getIntent().getIntExtra("imageResId", 0);
        String name = getIntent().getStringExtra("name");
        String start = getIntent().getStringExtra("start");
        String end = getIntent().getStringExtra("end");

        // Set data to views
        routeImage.setImageResource(imageResId);
        routeName.setText(name);
        startPoint.setText("起點: " + start);
        endPoint.setText("終點: " + end);

        Button buttonOpenMap = findViewById(R.id.button_open_map);
        buttonOpenMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMaps();
            }
        });

        Button buttonoutdoor = findViewById(R.id.button_outdoor);
        buttonoutdoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Googlemap.this, OutdoorActivity.class);
                startActivity(intent);
            }
        });

        Button buttonUserGuide = findViewById(R.id.button_user_guide);
        buttonUserGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Googlemap.this, UserGuideActivity.class);
                startActivity(intent);
            }
        });
    }

    private void openGoogleMaps() {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Log.i(TAG, "無法打開Google Maps");
        }
    }
}
