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
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;


public class ActivityOne extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    private EditText editTextName, editTextHeight, editTextWeight, editTextAge;
    private Spinner spinnerGender, spinnerExercise;
    //private RadioGroup radioGroupPowerMeter;
    //private RadioGroup radioGroupInOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);

        editTextName = findViewById(R.id.editTextName);
        editTextHeight = findViewById(R.id.editTextHeight);
        editTextWeight = findViewById(R.id.editTextWeight);
        editTextAge = findViewById(R.id.editTextAge);
        spinnerGender = findViewById(R.id.spinnerGender);
        spinnerExercise = findViewById(R.id.spinnerExercise);
        //radioGroupPowerMeter = findViewById(R.id.radioGroupPowerMeter);
        //radioButtonYes = findViewById(R.id.radioButtonYes);
        //radioButtonNo = findViewById(R.id.radioButtonNo);
        Button buttonSubmit = findViewById(R.id.buttonSubmit);

        // 性別
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);

        // 運動習慣
        ArrayAdapter<CharSequence> exerciseAdapter = ArrayAdapter.createFromResource(this,
                R.array.exercise_array, android.R.layout.simple_spinner_item);
        exerciseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExercise.setAdapter(exerciseAdapter);

        // 請求權限
        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }*/

        // 提交按鈕點擊事件
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //writeDataToExcel();
                Intent intent = new Intent(ActivityOne.this, ChooseOutdoorOrIndoor.class);
                startActivity(intent);
            }
        });
    }
    /*private void writeDataToExcel() {
        String height = editTextHeight.getText().toString();
        int height1 = Integer.parseInt(height);
        String weight = editTextWeight.getText().toString();
        int weight1 = Integer.parseInt(weight);
        String age = editTextAge.getText().toString();
        int age1 = Integer.parseInt(age);
        String gender = spinnerGender.getSelectedItem().toString();
        int gender1 = 0;
        if (gender.equals("Male")){
            gender1 = 0;
        } else {
            gender1 = 1;
        }
        String exercise = spinnerExercise.getSelectedItem().toString();
        int excercise1 = 0;
        if (exercise.equals("None")){
            excercise1 = 0;
        } else if (exercise.equals("Light")) {
            excercise1 = 1;
        } else if (exercise.equals("Moderate")) {
            excercise1 = 2;
        } else {
            excercise1 = 3;
        }
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("User Data");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Height");
        headerRow.createCell(1).setCellValue("Weight");
        headerRow.createCell(2).setCellValue("Age");
        headerRow.createCell(3).setCellValue("Gender");
        headerRow.createCell(4).setCellValue("Exercise");

        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue(height1);
        dataRow.createCell(1).setCellValue(weight1);
        dataRow.createCell(2).setCellValue(age1);
        dataRow.createCell(3).setCellValue(gender1);
        dataRow.createCell(4).setCellValue(excercise1);

        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File file = new File(getExternalFilesDir(null), "UserData_" + timestamp + ".xlsx");
            FileOutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
            Log.d("Excel", "Saved to: " + file.getAbsolutePath());
            Toast.makeText(this, "Excel 文件已儲存至 " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Excel", "文件寫入錯誤: " + e.getMessage());
        }
    }*/
    /*@Override
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
    }*/

}


