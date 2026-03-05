package io.github.tuanpq.bledevice;

import static android.bluetooth.le.ScanSettings.CALLBACK_TYPE_ALL_MATCHES;

import static io.github.tuanpq.bledevice.Constant.CLIENT_CHARACTERISTIC_CONFIGURATION_UUID;
import static io.github.tuanpq.bledevice.Constant.MICROCHIP_TRANSPARENT_UART_RX_CHARACTERISTIC_UUID;
import static io.github.tuanpq.bledevice.Constant.MICROCHIP_TRANSPARENT_UART_SERVICE_UUID;
import static io.github.tuanpq.bledevice.Constant.MICROCHIP_TRANSPARENT_UART_TX_CHARACTERISTIC_UUID;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ScannedDeviceFragment extends Fragment implements ScannedDeviceRecyclerViewAdapter.ScannedDeviceItemClickListener {

    private static final String TAG = ScannedDeviceFragment.class.getSimpleName();

    private final List<ScannedDeviceItem> mScannedDeviceItems = new ArrayList<>();
    private ScannedDeviceRecyclerViewAdapter mScannedDeviceRecyclerViewAdapter;
    private BluetoothGatt mBluetoothGatt = null;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private BluetoothGattCharacteristic mUARTDataTxCharacteristic;
    private BluetoothGattCharacteristic mUARTDataRxCharacteristic;
    private boolean mScanning;
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onCharacteristicChanged(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value) {
            super.onCharacteristicChanged(gatt, characteristic, value);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            centralRoleActivity.addLog(TAG,"onConnectionStateChange: device = " + gatt.getDevice().getAddress() + ", status = " + status + ", state = " + newState);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        notifyDeviceConnectionStateChange(gatt.getDevice().getAddress(), newState);
                        mBluetoothGatt.discoverServices();
                    }
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    notifyDeviceConnectionStateChange(gatt.getDevice().getAddress(), newState);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onCharacteristicRead(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value, int status) {
            super.onCharacteristicRead(gatt, characteristic, value, status);
        }

        @Override
        public void onDescriptorRead(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattDescriptor descriptor, int status, @NonNull byte[] value) {
            super.onDescriptorRead(gatt, descriptor, status, value);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }

        @Override
        public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyRead(gatt, txPhy, rxPhy, status);
        }

        @Override
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @SuppressLint("MissingPermission")
        @RequiresApi(Build.VERSION_CODES.S)
        @Override
        public void onServiceChanged(@NonNull BluetoothGatt gatt) {
            super.onServiceChanged(gatt);
            gatt.discoverServices();
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            registerServices(gatt);
        }
    };

    public ScannedDeviceFragment() {
    }

    private CentralRoleActivity centralRoleActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        centralRoleActivity = (CentralRoleActivity) getActivity();
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scanned_device_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.list);

        // Set the adapter
        if (recyclerView != null) {
            Context context = view.getContext();
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            mScannedDeviceRecyclerViewAdapter = new ScannedDeviceRecyclerViewAdapter(new ArrayList<>());
            mScannedDeviceRecyclerViewAdapter.setListener(this);
            recyclerView.setAdapter(mScannedDeviceRecyclerViewAdapter);
            mScannedDeviceRecyclerViewAdapter.setData(mScannedDeviceItems);
            startScan();
        }

        return view;
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @Override
    public void onConnectButtonClicked(ScannedDeviceItem item, boolean connect) {
        if (connect) {
            stopScan();

            disconnect();

            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(item.getMacAddress().toUpperCase());
            boolean autoConnect = centralRoleActivity.getAutoConnectFlag();
            mBluetoothGatt = device.connectGatt(getContext(), autoConnect, mGattCallback, BluetoothDevice.TRANSPORT_LE);
        } else {
            disconnect();
        }
    }

    @SuppressLint("MissingPermission")
    private void startScan() {
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) requireContext().getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                return;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            return;
        }

        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (mBluetoothLeScanner == null) {
            return;
        }

        if (!mScanning) {
            mScanning = true;
            mBluetoothLeScanner.startScan(buildScanFilters(), buildScanSettings(), mScanCallback);
        }
    }

    @SuppressLint("MissingPermission")
    private void stopScan() {
        mBluetoothLeScanner.stopScan(mScanCallback);
    }

    private List<ScanFilter> buildScanFilters() {
        List<ScanFilter> scanFilters = new ArrayList<>();
        ScanFilter.Builder dataServiceFilterBuilder = new ScanFilter.Builder();
        dataServiceFilterBuilder.setServiceUuid(new ParcelUuid(MICROCHIP_TRANSPARENT_UART_SERVICE_UUID));
        scanFilters.add(dataServiceFilterBuilder.build());
        return scanFilters;
    }

    private ScanSettings buildScanSettings() {
        ScanSettings.Builder builder = new ScanSettings.Builder();
        builder.setCallbackType(CALLBACK_TYPE_ALL_MATCHES);
        builder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
        return builder.build();
    }

    @SuppressLint("MissingPermission")
    private void registerServices(BluetoothGatt gatt) {
        if (gatt == null) {
            return;
        }

        List<BluetoothGattService> deviceServices = gatt.getServices();
        if (deviceServices == null || deviceServices.isEmpty()) {
            return;
        }

        try {
            boolean foundUARTDataService = false;
            for (BluetoothGattService gattService : deviceServices) {
                List<BluetoothGattCharacteristic> serviceCharacteristics = gattService.getCharacteristics();

                if (gattService.getUuid().equals(MICROCHIP_TRANSPARENT_UART_SERVICE_UUID)) {

                    if (serviceCharacteristics != null) {
                        boolean foundUARTDataRxCharacteristic = false;
                        boolean foundUARTDataTxCharacteristic = false;
                        boolean result = false;

                        for (BluetoothGattCharacteristic serviceCharacteristic : serviceCharacteristics) {
                            if (serviceCharacteristic.getUuid().equals(MICROCHIP_TRANSPARENT_UART_TX_CHARACTERISTIC_UUID)) {

                                mUARTDataTxCharacteristic = serviceCharacteristic;
                                boolean notification = (this.mUARTDataTxCharacteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0;
                                result = mBluetoothGatt.setCharacteristicNotification(mUARTDataTxCharacteristic, notification);
                                Log.d(TAG, "setCharacteristicNotification: " + result);

                                BluetoothGattDescriptor descriptor = serviceCharacteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIGURATION_UUID);
                                if (descriptor != null) {
                                    result = descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                    result &= mBluetoothGatt.writeDescriptor(descriptor);
                                    Log.d(TAG, "writeDescriptor: " + result);
                                }

                                foundUARTDataTxCharacteristic = true;
                            }

                            if (serviceCharacteristic.getUuid().equals(MICROCHIP_TRANSPARENT_UART_RX_CHARACTERISTIC_UUID)) {
                                mUARTDataRxCharacteristic = serviceCharacteristic;
                                foundUARTDataRxCharacteristic = true;
                            }

                            if (foundUARTDataRxCharacteristic && foundUARTDataTxCharacteristic) {
                                break;
                            }
                        }
                    }

                    foundUARTDataService = true;
                }

                if (foundUARTDataService) {
                    break;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    // Device scan callback.
    private final ScanCallback mScanCallback = new ScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (mScanning && result != null && result.getDevice() != null) {
                ScannedDeviceItem scannedDeviceItem = new ScannedDeviceItem(result.getDevice().getName(), result.getDevice().getAddress());
                mScannedDeviceRecyclerViewAdapter.appendData(scannedDeviceItem);
            }
        }
    };

    private void notifyDeviceConnectionStateChange(String macAddress, int newState) {
        List<ScannedDeviceItem> scannedDeviceItemList = mScannedDeviceRecyclerViewAdapter.getData();
        if (scannedDeviceItemList != null && !scannedDeviceItemList.isEmpty()) {
            for (int position = 0; position < scannedDeviceItemList.size(); position++) {
                ScannedDeviceItem scannedDeviceItem = scannedDeviceItemList.get(position);
                if (scannedDeviceItem.getMacAddress().equalsIgnoreCase(macAddress)) {
                    scannedDeviceItem.setState(newState);
                    final int finalPosition = position;
                    centralRoleActivity.runOnUiThread(() -> {
                        mScannedDeviceRecyclerViewAdapter.notifyItemChanged(finalPosition);
                    });
                    break;
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void disconnect() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
            notifyDeviceConnectionStateChange(mBluetoothGatt.getDevice().getAddress(), BluetoothProfile.STATE_DISCONNECTED);
            mBluetoothGatt = null;
        }
    }

}