package com.example.bmicalculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

public class ChooseOutdoorOrIndoor extends AppCompatActivity {
    //private RadioButton radioButtonYes, radioButtonNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooseinout);

        Button buttonIndoor = findViewById(R.id.buttonIndoor);
        Button buttonOutdoor = findViewById(R.id.buttonOutdoor);

        buttonIndoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseOutdoorOrIndoor.this, IndoorActivity.class);
                startActivity(intent);
            }
        });

        buttonOutdoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseOutdoorOrIndoor.this, ChooseMap.class);
                startActivity(intent);
            }
        });
    }
}
