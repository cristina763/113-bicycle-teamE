//藍芽連功率踏板(目前這個.java檔沒有用到，已經跟OutdoorActivity整合)
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
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Arrays;
import java.util.UUID;

public class blue extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outdoor);

        powerDataTextView = findViewById(R.id.powerDataTextView);
        Button connectButton = findViewById(R.id.buttonStart); // 修改為你按鈕的ID

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "請啟用藍牙", Toast.LENGTH_SHORT).show();
            return;
        }

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToDevice();
            }
        });
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
                Log.i(TAG, "已連接到感測器");
                runOnUiThread(() -> Toast.makeText(blue.this, "已連接到感測器", Toast.LENGTH_SHORT).show());

                // 檢查是否有權限
                if (ContextCompat.checkSelfPermission(blue.this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(blue.this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED) {
                    bluetoothGatt.discoverServices();
                } else {
                    Log.e(TAG, "缺少藍牙權限，無法發現服務");
                }
            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                Log.i(TAG, "與感測器斷開連接");
                runOnUiThread(() -> Toast.makeText(blue.this, "與感測器斷開連接", Toast.LENGTH_SHORT).show());
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
                            if (ContextCompat.checkSelfPermission(blue.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(blue.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, PERMISSION_REQUEST_CODE_BLUE);
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

                Log.d(TAG, "解析后的功率數據: " + powerData);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "更新UI上的功率數據: " + powerData);
                        powerDataTextView.setText("功率數據: " + powerData);
                    }
                });
            }
        }

        private String parsePowerData(byte[] data) {
            Log.d(TAG, "接收到的數據: " + Arrays.toString(data)); // 打印原始數據

            StringBuilder result = new StringBuilder();
            if (data.length >= 4) {
                int flags = (data[0] & 0xFF) | ((data[1] & 0xFF) << 8);
                int power = (data[2] & 0xFF) | ((data[3] & 0xFF) << 8);
                result.append("Power: ").append(power).append("W");

                int index = 4;
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
                }
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

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
}
