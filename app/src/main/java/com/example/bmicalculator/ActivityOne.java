package com.example.bmicalculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

public class ActivityOne extends AppCompatActivity {

    private EditText editTextHeight, editTextWeight, editTextAge;
    private Spinner spinnerGender, spinnerExercise;
    private RadioGroup radioGroupPowerMeter;
    private RadioButton radioButtonYes, radioButtonNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);

        editTextHeight = findViewById(R.id.editTextHeight);
        editTextWeight = findViewById(R.id.editTextWeight);
        editTextAge = findViewById(R.id.editTextAge);
        spinnerGender = findViewById(R.id.spinnerGender);
        spinnerExercise = findViewById(R.id.spinnerExercise);
        radioGroupPowerMeter = findViewById(R.id.radioGroupPowerMeter);
        radioButtonYes = findViewById(R.id.radioButtonYes);
        radioButtonNo = findViewById(R.id.radioButtonNo);
        Button buttonSubmit = findViewById(R.id.buttonSubmit);

        // 設置性別選項
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);

        // 設置運動習慣選項
        ArrayAdapter<CharSequence> exerciseAdapter = ArrayAdapter.createFromResource(this,
                R.array.exercise_array, android.R.layout.simple_spinner_item);
        exerciseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExercise.setAdapter(exerciseAdapter);

        // 提交按鈕點擊事件
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedPowerMeterId = radioGroupPowerMeter.getCheckedRadioButtonId();
                if (selectedPowerMeterId == radioButtonYes.getId()) {
                    Intent intent = new Intent(ActivityOne.this, ActivityWithPowerMeter.class);
                    startActivity(intent);
                } else if (selectedPowerMeterId == radioButtonNo.getId()) {
                    Intent intent = new Intent(ActivityOne.this, ActivityWithoutPowerMeter.class);
                    startActivity(intent);
                }
            }
        });
    }
}

