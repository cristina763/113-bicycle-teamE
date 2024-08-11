package com.example.bmicalculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView textViewLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewLevel = findViewById(R.id.textViewLevel); // 初始化 textViewLevel

        Intent intent = getIntent();
        String result = intent.getStringExtra("result");

        if (result != null) {
            textViewLevel.setText(result);
        } else {
            textViewLevel.setText(" "); // 或者顯示空字符串或其他預設信息
        }

        Button buttonOne = findViewById(R.id.buttonOne);
        Button buttonTwo = findViewById(R.id.buttonTwo);

        buttonOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ActivityOne.class); //從本頁跳到ActivityOne.java
                startActivity(intent);
            }
        });

        buttonTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChooseMap.class);
                startActivity(intent);
            }
        });

    }
}
