package io.github.tuanpq.bledevice;

import static android.bluetooth.BluetoothProfile.GATT;

import static io.github.tuanpq.bledevice.Constant.MICROCHIP_TRANSPARENT_UART_RX_CHARACTERISTIC_UUID;
import static io.github.tuanpq.bledevice.Constant.MICROCHIP_TRANSPARENT_UART_SERVICE_UUID;
import static io.github.tuanpq.bledevice.Constant.MICROCHIP_TRANSPARENT_UART_TX_CHARACTERISTIC_UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import java.util.Arrays;
import java.util.List;

import io.github.tuanpq.bledevice.databinding.ActivityPeripheralRoleBinding;

public class PeripheralRoleActivity extends AppCompatActivity {
    private static final String TAG = PeripheralRoleActivity.class.getSimpleName();
    private ActivityPeripheralRoleBinding mBinding;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    private BluetoothGattServer mBluetoothGattServer;
    private BluetoothGattService mMicrochipTransparentUARTService;
    private BluetoothGattCharacteristic mMicrochipTransparentUARTRxCharacteristic;
    private BluetoothGattCharacteristic mMicrochipTransparentUARTTxCharacteristic;
    private ConnectedDeviceFragment mConnectedDeviceFragment;
    private LogFragment mLogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityPeripheralRoleBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        EdgeToEdge.enable(this);
        setContentView(view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        mConnectedDeviceFragment = (ConnectedDeviceFragment) fragmentManager.findFragmentById(R.id.fragment_connected_device_container_view);
        mLogFragment = new LogFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.log_container, mLogFragment)
                .commit();

        initialize();

        setupAction();
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onDestroy() {
        if (mBluetoothLeAdvertiser != null) {
            mBluetoothLeAdvertiser.stopAdvertising(advertiseCallback);
            advertiseCallback = null;
        }

        if (mBluetoothManager != null && mBluetoothGattServer != null) {
            List<BluetoothDevice> bluetoothDeviceList = mBluetoothManager.getConnectedDevices(GATT);
            if (bluetoothDeviceList != null) {
                for (BluetoothDevice device : bluetoothDeviceList) {
                    mBluetoothGattServer.cancelConnection(device);
                }
            }
            mBluetoothGattServer.close();
        }

        super.onDestroy();
    }

    public void addLog(String tag, String text) {
        if (mLogFragment != null) {
            mLogFragment.appendLog(tag + ":" + text);
        }
    }

    @SuppressLint({"MissingPermission", "HardwareIds", "DefaultLocale"})
    private void initialize() {
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (mBluetoothManager == null) {
            return;
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            return;
        }

        mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        if (mBluetoothLeAdvertiser == null) {
            return;
        }

        addLog(TAG, String.format("Adapter name:%s, Adapter address:%s, state:%d", mBluetoothAdapter.getName(), mBluetoothAdapter.getAddress(), mBluetoothAdapter.getState()));

        mBluetoothGattServer = mBluetoothManager.openGattServer(this, gattServerCallback);
        if (mBluetoothGattServer == null) {
            return;
        }

        mMicrochipTransparentUARTService = new BluetoothGattService(MICROCHIP_TRANSPARENT_UART_SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY);
        mMicrochipTransparentUARTRxCharacteristic = new BluetoothGattCharacteristic(MICROCHIP_TRANSPARENT_UART_RX_CHARACTERISTIC_UUID, BluetoothGattCharacteristic.PROPERTY_NOTIFY, BluetoothGattCharacteristic.PERMISSION_READ);
        mMicrochipTransparentUARTRxCharacteristic.setValue(new byte[0]);
        mMicrochipTransparentUARTTxCharacteristic = new BluetoothGattCharacteristic(MICROCHIP_TRANSPARENT_UART_TX_CHARACTERISTIC_UUID, BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE, BluetoothGattCharacteristic.PERMISSION_WRITE);
        mMicrochipTransparentUARTTxCharacteristic.setValue(new byte[0]);
        mMicrochipTransparentUARTService.addCharacteristic(mMicrochipTransparentUARTRxCharacteristic);
        mMicrochipTransparentUARTService.addCharacteristic(mMicrochipTransparentUARTTxCharacteristic);

        mBluetoothGattServer.addService(mMicrochipTransparentUARTService);
    }

    private AdvertiseSettings buildAdvertiseSettings() {
        AdvertiseSettings.Builder settingsBuilder = new AdvertiseSettings.Builder();
        settingsBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER);
        settingsBuilder.setTimeout(0);
        return settingsBuilder.build();
    }

    private AdvertiseData buildAdvertiseData() {
        AdvertiseData.Builder dataBuilder = new AdvertiseData.Builder();
        dataBuilder.addServiceUuid(ParcelUuid.fromString(MICROCHIP_TRANSPARENT_UART_SERVICE_UUID.toString()));
        return dataBuilder.build();
    }

    private AdvertiseCallback advertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            addLog(TAG, "AdvertiseCallback.onStartFailure: errorCode = " + errorCode);
        }

        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            addLog(TAG, "AdvertiseCallback.onStartSuccess: settingsInEffect = " + settingsInEffect.toString());
        }
    };

    private final BluetoothGattServerCallback gattServerCallback = new BluetoothGattServerCallback() {

        @SuppressLint({"DefaultLocale", "MissingPermission"})
        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            super.onConnectionStateChange(device, status, newState);
            String state = (newState == BluetoothProfile.STATE_CONNECTED) ? "Connected" : "Disconnected";
            addLog(TAG, String.format("onConnectionStateChange: device: %s, status:%s, newState: %d", device.getAddress(), state, newState));
            mConnectedDeviceFragment.notifyDeviceConnectionStateChange(device.getName(), device.getAddress(), newState);
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
            addLog(TAG, String.format("onCharacteristicReadRequest: device: %s, requestId:%d, offset:%d, value:%s", device.getAddress(), requestId, offset, Arrays.toString(characteristic.getValue())));
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
            addLog(TAG, String.format("onCharacteristicWriteRequest: device: %s, requestId:%d, preparedWrite:%s, responseNeeded:%s, offset:%d, value:%s", device.getAddress(), requestId, preparedWrite, responseNeeded, offset, Arrays.toString(value)));
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
            super.onDescriptorReadRequest(device, requestId, offset, descriptor);
            addLog(TAG, String.format("onDescriptorReadRequest: device: %s, requestId:%d, offset:%d, value:%s", device.getAddress(), requestId, offset, descriptor.toString()));
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value);
            addLog(TAG, String.format("onDescriptorWriteRequest: device: %s, requestId:%d, preparedWrite:%s, responseNeeded:%s, offset:%d, value:%s", device.getAddress(), requestId, preparedWrite, responseNeeded, offset, Arrays.toString(value)));
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {
            super.onExecuteWrite(device, requestId, execute);
            addLog(TAG, String.format("onExecuteWrite: device: %s, requestId:%d, execute:%s", device.getAddress(), requestId, execute));
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onMtuChanged(BluetoothDevice device, int mtu) {
            super.onMtuChanged(device, mtu);
            addLog(TAG, String.format("onMtuChanged: device: %s, mtu:%d", device.getAddress(), mtu));
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
            super.onNotificationSent(device, status);
            addLog(TAG, String.format("onNotificationSent: device: %s, status:%d", device.getAddress(), status));
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onPhyRead(BluetoothDevice device, int txPhy, int rxPhy, int status) {
            super.onPhyRead(device, txPhy, rxPhy, status);
            addLog(TAG, String.format("onPhyRead: device: %s, txPhy:%d, rxPhy:%d, status:%d", device.getAddress(), txPhy, rxPhy, status));
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onPhyUpdate(BluetoothDevice device, int txPhy, int rxPhy, int status) {
            super.onPhyUpdate(device, txPhy, rxPhy, status);
            addLog(TAG, String.format("onPhyUpdate: device: %s, txPhy:%d, rxPhy:%d, status:%d", device.getAddress(), txPhy, rxPhy, status));
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onServiceAdded(int status, BluetoothGattService service) {
            super.onServiceAdded(status, service);
            addLog(TAG, String.format("onServiceAdded: status:%d, UUID:%s, type:%d, InstanceId:%d", status, service.getUuid().toString(), service.getType(), service.getInstanceId()));
        }

    };

    private void setupAction() {
        mBinding.advertisingSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                startAdvertising();
            } else {
                stopAdvertising();
            }
        });
    }

    private void startAdvertising() {
        if (mBluetoothLeAdvertiser != null) {
            AdvertiseSettings settings = buildAdvertiseSettings();
            AdvertiseData data = buildAdvertiseData();
            mBluetoothLeAdvertiser.startAdvertising(settings, data, advertiseCallback);
        }
    }

    @SuppressLint("MissingPermission")
    private void stopAdvertising() {
        if (mBluetoothLeAdvertiser != null) {
            mBluetoothLeAdvertiser.stopAdvertising(advertiseCallback);
        }
    }

}