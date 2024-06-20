package com.example.bmicalculator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ActivityOne extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    private EditText editTextHeight, editTextWeight, editTextAge;
    private Spinner spinnerGender, spinnerExercise;
    //private RadioGroup radioGroupPowerMeter;
    //private RadioGroup radioGroupInOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);

        editTextHeight = findViewById(R.id.editTextHeight);
        editTextWeight = findViewById(R.id.editTextWeight);
        editTextAge = findViewById(R.id.editTextAge);
        spinnerGender = findViewById(R.id.spinnerGender);
        spinnerExercise = findViewById(R.id.spinnerExercise);
        //radioGroupPowerMeter = findViewById(R.id.radioGroupPowerMeter);
        //radioButtonYes = findViewById(R.id.radioButtonYes);
        //radioButtonNo = findViewById(R.id.radioButtonNo);
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

        // 請求權限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }

        // 提交按鈕點擊事件
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeDataToExcel();
                Intent intent = new Intent(ActivityOne.this, ChooseOutdoorOrIndoor.class);
                startActivity(intent);
            }
        });
    }
    private void writeDataToExcel() {
        String height = editTextHeight.getText().toString();
        String weight = editTextWeight.getText().toString();
        String age = editTextAge.getText().toString();
        String gender = spinnerGender.getSelectedItem().toString();
        String exercise = spinnerExercise.getSelectedItem().toString();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("User Data");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Height");
        headerRow.createCell(1).setCellValue("Weight");
        headerRow.createCell(2).setCellValue("Age");
        headerRow.createCell(3).setCellValue("Gender");
        headerRow.createCell(4).setCellValue("Exercise");

        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue(height);
        dataRow.createCell(1).setCellValue(weight);
        dataRow.createCell(2).setCellValue(age);
        dataRow.createCell(3).setCellValue(gender);
        dataRow.createCell(4).setCellValue(exercise);

        try {
            File file = new File(getExternalFilesDir(null), "UserData.xlsx");
            FileOutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
            Log.d("Excel", "File saved to: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Excel", "Error writing file: " + e.getMessage());
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Log.d("Permission", "WRITE_EXTERNAL_STORAGE permission granted");
            } else {
                // Permission denied
                Log.d("Permission", "WRITE_EXTERNAL_STORAGE permission denied");
            }
        }
    }
}



