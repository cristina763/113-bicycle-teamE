package com.example.bmicalculator;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
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

public class OutdoorActivity extends AppCompatActivity {

    private TextView textViewTime, textViewSpeed;
    private Button buttonStart, buttonPause;
    private Handler handler = new Handler();
    private long startTime = 0L;
    private long timeInMilliseconds = 0L;
    private long timeSwapBuff = 0L;
    private long updateTime = 0L;
    private boolean isRunning = false;
    private boolean isRecordingSpeed = false;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private static final int PERMISSION_REQUEST_CODE = 1;

    private Map<String, Long> powerIntervals = new HashMap<>();

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
            updateTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updateTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            textViewTime.setText(String.format("%d:%02d", mins, secs));
            handler.postDelayed(this, 100);

            /*if (isRecordingSpeed && timeInMilliseconds % 1000 < 100) {
                float speed = getCurrentSpeed();
                updateSpeedIntervals(speed);
            }*/
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outdoor);

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

        powerIntervals.put("0-10", 0L);
        powerIntervals.put("10-50", 0L);
        powerIntervals.put("50-100", 0L);

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

        // 開始按鈕
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTracking();
                connectToDevice();
            }
        });

        // 暫停按鈕
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

    private void connectToDevice() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN}, PERMISSION_REQUEST_CODE_BLUE);
            return;
        }

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(Dev_Mac);
        bluetoothGatt = device.connectGatt(this, false, gattCallback);
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(@NonNull BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            if (newState == BluetoothGatt.STATE_CONNECTED) {
                Log.i(TAG, "已連接到功率踏板");
                runOnUiThread(() -> Toast.makeText(OutdoorActivity.this, "已連接到功率踏板", Toast.LENGTH_SHORT).show());

                // 檢查是否有權限
                if (ContextCompat.checkSelfPermission(OutdoorActivity.this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(OutdoorActivity.this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED) {
                    bluetoothGatt.discoverServices();
                } else {
                    Log.e(TAG, "缺少藍牙權限，無法發現服務");
                }
            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                Log.i(TAG, "與踏板中斷連接");
                runOnUiThread(() -> Toast.makeText(OutdoorActivity.this, "與踏板中斷連接", Toast.LENGTH_SHORT).show());
            }
        }

        @Override
        public void onServicesDiscovered(@NonNull BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattService service = gatt.getService(UUID.fromString(Dev_SERVICE_UUID));
                if (service != null) {
                    readCharacteristic = service.getCharacteristic(UUID.fromString(Dev_CHAR_UUID_R));
                    if (readCharacteristic != null) {
                        // 檢查並請求BLUETOOTH_CONNECT權限
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            if (ContextCompat.checkSelfPermission(OutdoorActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(OutdoorActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, PERMISSION_REQUEST_CODE_BLUE);
                            } else {
                                setCharacteristicNotification(gatt, readCharacteristic);
                            }
                        } else {
                            setCharacteristicNotification(gatt, readCharacteristic);
                        }
                    }
                } else {
                    Log.i(TAG, "未找到服務");
                }
            }
        }

        @Override
        public void onCharacteristicChanged(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            if (characteristic.getUuid().equals(UUID.fromString(Dev_CHAR_UUID_R))) {
                byte[] data = characteristic.getValue();

                Log.d(TAG, "接收到的原始數據: " + Arrays.toString(data));
                Log.d(TAG, "接收到的十六進制數據: " + bytesToHex(data)); // 十六進制格式顯示

                final String powerData = parsePowerData(data); // 自行實現的數據解析方法

                Log.d(TAG, "解析後的功率數據: " + powerData);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "更新UI上的功率數據: " + powerData);
                        powerDataTextView.setText(powerData);

                        // 更新功率區間時間
                        int power = extractPowerValue(powerData);
                        updatePowerIntervals(power);
                    }
                });
            }
        }
        private int extractPowerValue(String powerData) {
            // 假設功率數據的格式是 "XXXW"
            String[] parts = powerData.split(" ");
            return Integer.parseInt(parts[0].replace("W", ""));
        }

        private String parsePowerData(byte[] data) {
            Log.d(TAG, "接收到的數據: " + Arrays.toString(data)); // 原始數據print

            StringBuilder result = new StringBuilder();
            if (data.length >= 4) {
                int flags = (data[0] & 0xFF) | ((data[1] & 0xFF) << 8);
                int power = (data[2] & 0xFF) | ((data[3] & 0xFF) << 8);
                result.append(power).append(" W");

                /*int index = 4;
                if ((flags & 0x0001) != 0) {
                    int balance = data[index] & 0xFF;
                    result.append(", Pedal Balance: ").append(balance).append("%");
                    index++;
                }
                if ((flags & 0x0010) != 0) {
                    long wheelRevs = ((long)data[index] & 0xFF) | (((long)data[index + 1] & 0xFF) << 8) |
                            (((long)data[index + 2] & 0xFF) << 16) | (((long)data[index + 3] & 0xFF) << 24);
                    int lastWheelEventTime = (data[index + 4] & 0xFF) | ((data[index + 5] & 0xFF) << 8);
                    result.append(", Wheel Revs: ").append(wheelRevs).append(", Last Wheel Event Time: ").append(lastWheelEventTime);
                    index += 6;
                }
                if ((flags & 0x0020) != 0) {
                    int crankRevs = (data[index] & 0xFF) | ((data[index + 1] & 0xFF) << 8);
                    int lastCrankEventTime = (data[index + 2] & 0xFF) | ((data[index + 3] & 0xFF) << 8);
                    result.append(", Crank Revs: ").append(crankRevs).append(", Last Crank Event Time: ").append(lastCrankEventTime);
                    index += 4;
                }*/
            } else {
                return "無效數據";
            }
            return result.toString();
        }
        private String bytesToHex(byte[] bytes) {
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02X ", b));
            }
            return sb.toString();
        }
    };
    private void setCharacteristicNotification(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        try {
            gatt.setCharacteristicNotification(characteristic, true);
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(Descriptor_UUID));
            if (descriptor != null) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);
            }
        } catch (SecurityException e) {
            Log.e(TAG, "設定特徵通知時缺少權限", e);
        }
    }


    // 開始追蹤方法
    private void startTracking() {
        if (!isRunning) {
            startTime = System.currentTimeMillis();
            handler.postDelayed(updateTimerThread, 0);
            isRunning = true;

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

    // 更新速度區間時間
    private void updatePowerIntervals(float power) {
        String key;
        if (power < 10) {
            key = "0-10";
        } else if (power < 50) {
            key = "10-50";
        } else {
            key = "50-100";
        }

        long previousTime = powerIntervals.get(key);
        powerIntervals.put(key, previousTime + 1); // 每秒加1
    }

    // 匯出至 Excel
    private void exportToExcel() {
        if (isExternalStorageWritable()) {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("功率數據");

            // 標題列
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("功率區間 (W)");
            headerRow.createCell(1).setCellValue("時間 (秒)");

            // 添加數據
            int rowNum = 1;
            for (Map.Entry<String, Long> entry : powerIntervals.entrySet()) {
                Row dataRow = sheet.createRow(rowNum++);
                dataRow.createCell(0).setCellValue(entry.getKey());
                dataRow.createCell(1).setCellValue(entry.getValue());
            }

            // 儲存excel至文件
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

        if (requestCode == PERMISSION_REQUEST_CODE_BLUE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 重新嘗試設置特徵通知
                if (bluetoothGatt != null && readCharacteristic != null) {
                    setCharacteristicNotification(bluetoothGatt, readCharacteristic);
                }
            } else {
                Toast.makeText(this, "需要藍牙權限", Toast.LENGTH_SHORT).show();
            }
        }
    }
}