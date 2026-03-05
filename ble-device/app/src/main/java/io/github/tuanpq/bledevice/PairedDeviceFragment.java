package io.github.tuanpq.bledevice;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class PairedDeviceFragment extends Fragment implements PairedDeviceRecyclerViewAdapter.PairedDeviceItemClickListener {
    private static final String TAG = PairedDeviceFragment.class.getSimpleName();
    private CentralRoleActivity mCentralRoleActivity;
    private PairedDeviceRecyclerViewAdapter mPairedDeviceRecyclerViewAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private final List<PairedDeviceItem> pairedDeviceItems = new ArrayList<>();
    private BluetoothGatt mBluetoothGatt = null;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mFirstAppearance = false;

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
            mCentralRoleActivity.addLog(TAG,"onConnectionStateChange: device = " + gatt.getDevice().getAddress() + ", status = " + status + ", state = " + newState);
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

        @Override
        public void onServiceChanged(@NonNull BluetoothGatt gatt) {
            super.onServiceChanged(gatt);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
        }
    };

    public PairedDeviceFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCentralRoleActivity = (CentralRoleActivity) getActivity();
        initialize();
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_paired_device_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.list);

        // Set the adapter
        if (recyclerView != null) {
            Context context = view.getContext();
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            mPairedDeviceRecyclerViewAdapter = new PairedDeviceRecyclerViewAdapter(new ArrayList<>());
            recyclerView.setAdapter(mPairedDeviceRecyclerViewAdapter);
            if (mPairedDevices != null) {
                mPairedDeviceRecyclerViewAdapter.setListener(this);
                mPairedDevices.forEach(pairedDevice -> {
                    pairedDeviceItems.add(new PairedDeviceItem(pairedDevice.getName(), pairedDevice.getAddress()));
                    logDeviceInformation(pairedDevice);
                });
                mPairedDeviceRecyclerViewAdapter.setData(pairedDeviceItems);
            }
        }

        return view;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onResume() {
        super.onResume();

        if (!mFirstAppearance) {
            mFirstAppearance = true;

            mCentralRoleActivity.addLog(TAG, "Paired Devices");
            mPairedDevices.forEach(this::logDeviceInformation);

            if (mBluetoothManager != null) {
                List<BluetoothDevice> connectedPeripherals = mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT_SERVER);
                if (connectedPeripherals != null && !connectedPeripherals.isEmpty()) {
                    mCentralRoleActivity.addLog(TAG, "Connected Peripherals");
                    connectedPeripherals.forEach(this::logDeviceInformation);
                }

                List<BluetoothDevice> connectedCentrals = mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
                if (connectedCentrals != null && !connectedCentrals.isEmpty()) {
                    mCentralRoleActivity.addLog(TAG, "Connected Centrals");
                    connectedCentrals.forEach(this::logDeviceInformation);
                }
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @Override
    public void onConnectButtonClicked(PairedDeviceItem item, boolean connect) {
        if (connect) {
            Optional<BluetoothDevice> result = mPairedDevices.stream().filter(device -> device.getAddress().equalsIgnoreCase(item.getMacAddress())).findFirst();
            result.ifPresent(bluetoothDevice -> {
                disconnect();

                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(item.getMacAddress().toUpperCase());
                boolean autoConnect = mCentralRoleActivity.getAutoConnectFlag();
                mBluetoothGatt = device.connectGatt(getContext(), autoConnect, mGattCallback, BluetoothDevice.TRANSPORT_LE);
            });
        } else {
            disconnect();
        }

    }

    @SuppressLint("MissingPermission")
    public void setData(Set<BluetoothDevice> pairedDevices) {
        this.mPairedDevices = pairedDevices;
    }

    private void notifyDeviceConnectionStateChange(String macAddress, int newState) {
        List<PairedDeviceItem> pairedDeviceItemList = mPairedDeviceRecyclerViewAdapter.getData();
        if (pairedDeviceItemList != null && !pairedDeviceItemList.isEmpty()) {
            for (int position = 0; position < pairedDeviceItemList.size(); position++) {
                PairedDeviceItem pairedDeviceItem = pairedDeviceItemList.get(position);
                if (pairedDeviceItem.getMacAddress().equalsIgnoreCase(macAddress)) {
                    pairedDeviceItem.setState(newState);
                    final int finalPosition = position;
                    mCentralRoleActivity.runOnUiThread(() -> {
                        mPairedDeviceRecyclerViewAdapter.notifyItemChanged(finalPosition);
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

    @SuppressLint("MissingPermission")
    private void initialize() {
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) requireContext().getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                return;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
    }

    @SuppressLint("MissingPermission")
    private void logDeviceInformation(BluetoothDevice pairedDevice) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(pairedDevice.getName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            stringBuilder.append(", ").append(pairedDevice.getAlias());
        }
        stringBuilder.append(", ").append(pairedDevice.getAddress().toUpperCase());
        stringBuilder.append(", ").append(pairedDevice.getType());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            stringBuilder.append(", ").append(pairedDevice.getAddressType());
        }
        stringBuilder.append(", ").append(pairedDevice.getBluetoothClass().toString());
        stringBuilder.append(", ").append(pairedDevice.getBondState()).append("\n====================\n");
        mCentralRoleActivity.addLog(TAG, stringBuilder.toString());
    }

}