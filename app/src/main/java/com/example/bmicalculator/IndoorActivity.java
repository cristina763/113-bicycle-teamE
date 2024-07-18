package com.example.bmicalculator;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import androidx.core.content.ContextCompat;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class IndoorActivity extends AppCompatActivity {

    private TextView textViewTime, textViewSpeed;
    private Button buttonStart, buttonPause;
    private Handler handler = new Handler();
    private long startTime = 0L;
    private long timeInMilliseconds = 0L;
    private long timeSwapBuff = 0L;
    private long updateTime = 0L;
    private long countdownTime = 1200 * 1000L;  // 20 minutes (以毫秒表示)
    private boolean isRunning = false;
    private boolean isRecordingSpeed = false;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final int PERMISSION_REQUEST_CODE = 1;

    private Map<String, Long> speedIntervals = new HashMap<>();

    private static final String TAG = "blue";
    String Dev_SERVICE_UUID = "00001818-0000-1000-8000-00805F9B34FB";
    String Dev_CHAR_UUID_R = "00002A63-0000-1000-8000-00805F9B34FB";
    String Dev_CHAR_UUID_W = "6A4E4C80-667B-11E3-949A-0800200C9A66";
    String Dev_Mac = "C5:89:0A:0A:B8:8B";
    String Descriptor_UUID = "00002902-0000-1000-8000-00805f9b34fb"; // 常見的通知描述符UUID

    private static final int PERMISSION_REQUEST_CODE_BLUE = 1;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCharacteristic readCharacteristic;
    private TextView powerDataTextView;

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = System.currentTimeMillis() - startTime;
            updateTime = countdownTime - (timeSwapBuff + timeInMilliseconds);
            int secs = (int) (updateTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;

            textViewTime.setText("" + mins + ":" + String.format("%02d", secs));

            if (updateTime <= 0) {
                handler.removeCallbacks(this);
                isRunning = false;
                Toast.makeText(IndoorActivity.this, "Time's up.", Toast.LENGTH_SHORT).show();
            } else {
                handler.postDelayed(this, 100);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indoor);

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "請啟用藍牙", Toast.LENGTH_SHORT).show();
            return;
        }

        textViewTime = findViewById(R.id.textViewTime);
        textViewSpeed = findViewById(R.id.textViewSpeed);
        buttonStart = findViewById(R.id.buttonStart);
        buttonPause = findViewById(R.id.buttonPause);
        powerDataTextView = findViewById(R.id.powerDataTextView);



        // Location Manager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Request location updates
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                float speed = location.getSpeed(); // m/s
                float speedKMH = (speed * 3600) / 1000; // 轉成km/h

                // Update UI with speed
                textViewSpeed.setText(String.format("%.2f km/h", speedKMH));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRunning) {
                    startTime = System.currentTimeMillis();
                    handler.postDelayed(updateTimerThread, 0);
                    isRunning = true;

                    // Check permission and request if needed
                    if (ActivityCompat.checkSelfPermission(IndoorActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(IndoorActivity.this,
                                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(IndoorActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        return;
                    }
                    // Request location updates
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
                }
            }
        });

        buttonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRunning) {
                    timeSwapBuff += timeInMilliseconds;
                    handler.removeCallbacks(updateTimerThread);
                    isRunning = false;

                    // Remove location updates
                    locationManager.removeUpdates(locationListener);
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
                }
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
