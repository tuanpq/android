package io.github.tuanpq.bledevice;

import static io.github.tuanpq.bledevice.Constant.REQUEST_BLUETOOTH_CONNECT;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import java.util.Set;

import io.github.tuanpq.bledevice.databinding.ActivityCentralRoleBinding;

public class CentralRoleActivity extends AppCompatActivity {

    private static final String TAG = CentralRoleActivity.class.getSimpleName();
    private ActivityCentralRoleBinding mBinding;

    private LogFragment mLogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityCentralRoleBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        EdgeToEdge.enable(this);
        setContentView(view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Fragment
        mLogFragment = new LogFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.log_container, mLogFragment)
                .commit();

        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        Set<BluetoothDevice> pairedDevices = null;
        if (bluetoothAdapter != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    String[] permissions = {Manifest.permission.BLUETOOTH_CONNECT};
                    requestPermissions(permissions, REQUEST_BLUETOOTH_CONNECT);
                    return;
                }
            }
            pairedDevices = bluetoothAdapter.getBondedDevices();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        PairedDeviceFragment pairedDeviceFragment = (PairedDeviceFragment) fragmentManager.findFragmentById(R.id.fragment_paired_device_container_view);
        if (pairedDeviceFragment != null && pairedDevices != null) {
            pairedDeviceFragment.setData(pairedDevices);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_CONNECT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addLog(TAG,"PERMISSION_GRANTED");
            } else {
                addLog(TAG,"PERMISSION_DENIED");
            }
        }
    }

    public void addLog(String tag, String text) {
        if (mLogFragment != null) {
            mLogFragment.appendLog(tag + ":" + text);
        }
    }

    public boolean getAutoConnectFlag() {
        return mBinding.autoConnectSwitch.isChecked();
    }

    private void systemInformation() {
        /*
        String deviceName = Settings.Global.getString(getContentResolver(), Settings.Global.DEVICE_NAME);
        Log.d("XXXXXXXXXX-DeviceName", deviceName);
        Log.d("XXXXXXXXXX-MODEL", Build.MODEL);
        Log.d("XXXXXXXXXX-USER", Build.USER);
        Log.d("XXXXXXXXXX-TYPE", Build.TYPE);
        Log.d("XXXXXXXXXX-TAGS", Build.TAGS);
        Log.d("XXXXXXXXXX-SKU", Build.SKU);
        Log.d("XXXXXXXXXX-SOC_MANUFACTURER", Build.SOC_MANUFACTURER);
        Log.d("XXXXXXXXXX-PRODUCT", Build.PRODUCT);
        Log.d("XXXXXXXXXX-ODM_SKU", Build.ODM_SKU);
        Log.d("XXXXXXXXXX-MANUFACTURER", Build.MANUFACTURER);
        Log.d("XXXXXXXXXX-HOST", Build.HOST);
        Log.d("XXXXXXXXXX-BRAND", Build.BRAND);
        Log.d("XXXXXXXXXX-DEVICE", Build.DEVICE);
        Log.d("XXXXXXXXXX-HARDWARE", Build.HARDWARE);
        Log.d("XXXXXXXXXX-FINGERPRINT", Build.FINGERPRINT);
        Log.d("XXXXXXXXXX-DISPLAY", Build.DISPLAY);
        Log.d("XXXXXXXXXX-BOOTLOADER", Build.BOOTLOADER);
        Log.d("XXXXXXXXXX-ID", Build.ID);
        Log.d("XXXXXXXXXX-RadioVersion", Build.getRadioVersion());
         */
    }

}