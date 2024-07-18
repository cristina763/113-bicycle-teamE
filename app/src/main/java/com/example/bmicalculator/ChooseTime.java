package com.example.bmicalculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

public class ChooseTime extends AppCompatActivity {
    //private RadioButton radioButtonYes, radioButtonNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosetime);

        Button buttonIndoor = findViewById(R.id.button20Minutes);
        Button buttonOutdoor = findViewById(R.id.button60Minutes);

        buttonIndoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseTime.this, IndoorActivity.class);
                startActivity(intent);
            }
        });

        buttonOutdoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseTime.this, IndoorActivity.class);
                startActivity(intent);
            }
        });
    }
}
