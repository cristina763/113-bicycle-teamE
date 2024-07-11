package com.example.bmicalculator;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Environment;
import android.util.Log;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

//bluetooth
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
//bluetooth


public class OutdoorActivity extends AppCompatActivity {

    private TextView textViewTime, textViewSpeed;
    private Button buttonStart, buttonPause;
    private Handler handler = new Handler();
    private long startTime = 0L;
    private long timeInMilliseconds = 0L;
    private long timeSwapBuff = 0L;
    private long updateTime = 0L;
    private boolean isRunning = false;
    private boolean isRecordingSpeed = false; // 是否正在記錄速度
    private int speedRecordInterval = 60000; // 記錄速度的間隔時間（1分鐘）

    private LocationManager locationManager;
    private LocationListener locationListener;

    private ArrayList<SpeedData> speedDataList = new ArrayList<>();
    private static final int PERMISSION_REQUEST_CODE = 1;

    private static class SpeedData {
        long time;
        float speed;

        SpeedData(long time, float speed) {
            this.time = time;
            this.speed = speed;
        }
    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = System.currentTimeMillis() - startTime;
            updateTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updateTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            textViewTime.setText(String.format("%d:%02d", mins, secs));
            handler.postDelayed(this, 100);

            if (isRecordingSpeed && timeInMilliseconds % speedRecordInterval < 100) {
                // 收集速度數據
                float speed = getCurrentSpeed();
                speedDataList.add(new SpeedData(updateTime, speed));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outdoor);

        textViewTime = findViewById(R.id.textViewTime);
        textViewSpeed = findViewById(R.id.textViewSpeed);
        buttonStart = findViewById(R.id.buttonStart);
        buttonPause = findViewById(R.id.buttonPause);

        // Location Manager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // 請求位置更新
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                updateSpeedUI(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        // 開始按鈕點擊監聽
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTracking();
            }
        });

        // 暫停按鈕點擊監聽
        buttonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseTracking();
                exportToExcel();
            }
        });

        // 請求儲存權限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    // 開始追蹤方法
    private void startTracking() {
        if (!isRunning) {
            startTime = System.currentTimeMillis();
            handler.postDelayed(updateTimerThread, 0);
            isRunning = true;
            isRecordingSpeed = true; // 開始記錄速度

            // 檢查並請求位置權限
            requestLocationUpdates();
        }
    }

    // 暫停追蹤方法
    private void pauseTracking() {
        if (isRunning) {
            timeSwapBuff += timeInMilliseconds;
            handler.removeCallbacks(updateTimerThread);
            isRunning = false;
            isRecordingSpeed = false; // 停止記錄速度

            // 移除位置更新
            locationManager.removeUpdates(locationListener);
        }
    }

    // 請求位置更新
    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(OutdoorActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(OutdoorActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(OutdoorActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
    }

    // 更新速度UI
    private void updateSpeedUI(Location location) {
        float speed = location.getSpeed(); // m/s
        float speedKMH = (speed * 3600) / 1000; // 轉換為 km/h
        textViewSpeed.setText(String.format("%.2f 公里/時", speedKMH));
    }

    // 獲取當前速度
    private float getCurrentSpeed() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {
                return lastLocation.getSpeed();
            }
        }
        return 0.0f;
    }

    // 匯出至 Excel 方法
    private void exportToExcel() {
        if (speedDataList.size() > 0) {
            if (isExternalStorageWritable()) {
                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("速度數據");

                // 創建標題列
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("時間 (毫秒)");
                headerRow.createCell(1).setCellValue("速度 (公里/時)");

                // 添加數據
                for (int i = 0; i < speedDataList.size(); i++) {
                    Row dataRow = sheet.createRow(i + 1);
                    SpeedData speedData = speedDataList.get(i);
                    dataRow.createCell(0).setCellValue(speedData.time);
                    dataRow.createCell(1).setCellValue(speedData.speed);
                }

                // 儲存工作簿至文件
                try {
                    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                    File file = new File(getExternalFilesDir(null), "OutdoorActivity_" + timestamp + ".xlsx");
                    FileOutputStream outputStream = new FileOutputStream(file);
                    workbook.write(outputStream);
                    workbook.close();
                    outputStream.close();
                    Log.d("Excel", "文件儲存至: " + file.getAbsolutePath());
                    Toast.makeText(this, "Excel 文件已儲存至 " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("Excel", "文件寫入錯誤: " + e.getMessage());
                    Toast.makeText(this, "儲存 Excel 文件時發生錯誤", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "外部儲存不可用", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "無數據可匯出", Toast.LENGTH_SHORT).show();
        }
    }

    // 檢查外部儲存是否可寫入
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    // 處理權限請求結果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 權限已授權
                Log.d("Permission", "WRITE_EXTERNAL_STORAGE 權限已授權");
            } else {
                // 權限被拒絕
                Log.d("Permission", "WRITE_EXTERNAL_STORAGE 權限被拒絕");
                Toast.makeText(this, "權限被拒絕，無法儲存 Excel 文件", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 位置權限已授權
                requestLocationUpdates();
            } else {
                // 位置權限被拒絕
                Toast.makeText(this, "位置權限被拒絕，無法獲取位置數據", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


