package com.example.bmicalculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ResultActivity extends AppCompatActivity {
    private TextView textViewTime, textViewSlope, textViewFTP, textViewPredict;
    private EditText editTextAscent, editTextDistance, editTextSlope, editTextTime;
    private Button buttonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        textViewTime = findViewById(R.id.textViewTime);
        textViewSlope = findViewById(R.id.textViewSlope);
        textViewFTP = findViewById(R.id.textViewFTP);
        textViewPredict = findViewById(R.id.textViewPredict);

        editTextAscent = findViewById(R.id.editTextAscent);
        editTextDistance = findViewById(R.id.editTextDistance);
        editTextSlope = findViewById(R.id.editTextSlope);
        editTextTime = findViewById(R.id.editTextTime);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        // 接收數據
        Intent intent = getIntent();
        String time = intent.getStringExtra("TIME");
        double maxSlope = intent.getDoubleExtra("MAX_SLOPE", 0.0);
        String ftp = intent.getStringExtra("FTP");

        // 顯示數據
        textViewTime.setText(time);
        textViewSlope.setText(String.format("%.2f%%", maxSlope * 100));
        textViewFTP.setText(ftp);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ascent = editTextAscent.getText().toString();
                String distance = editTextDistance.getText().toString();
                String slope = editTextSlope.getText().toString();
                String time = editTextTime.getText().toString();

                saveDataToCsv(ascent, distance, slope, time);
                runModelAndDisplayResult();
            }
        });
    }
    private void saveDataToCsv(String ascent, String distance, String slope, String time) {
        File csvFile = new File(getExternalFilesDir(null), "input_data.csv");

        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.append("爬升,距離,坡度,時間\n");
            writer.append(ascent).append(",")
                    .append(distance).append(",")
                    .append(slope).append(",")
                    .append(time).append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void runModelAndDisplayResult() {
        File csvFile = new File(getExternalFilesDir(null), "input_data.csv");
        String url = "https://3d2a-34-106-210-166.ngrok-free.app/predict"; // 用從Colab執行的ngrok URL

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", csvFile.getName(),
                        RequestBody.create(MediaType.parse("text/csv"), csvFile))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String result = response.body().string();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(ResultActivity.this, "預測結果: " + result, Toast.LENGTH_LONG).show();
                            textViewPredict.setText(result);
                        }
                    });
                }
            }
        });
    }

}
