package io.github.tuanpq.bledevice;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresPermission;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ConnectedDeviceFragment extends Fragment implements ConnectedDeviceRecyclerViewAdapter.ConnectedDeviceItemClickListener {
    private static final String TAG = ConnectedDeviceFragment.class.getSimpleName();
    private PeripheralRoleActivity mPeripheralRoleActivity;
    private ConnectedDeviceRecyclerViewAdapter mConnectedDeviceRecyclerViewAdapter;
    private List<BluetoothDevice> mConnectedDevices;
    private BluetoothManager mBluetoothManager;
    private boolean mFirstAppearance = false;

    public ConnectedDeviceFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPeripheralRoleActivity = (PeripheralRoleActivity) getActivity();
        initialize();
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connected_device_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.list);

        // Set the adapter
        if (recyclerView != null) {
            Context context = view.getContext();
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            mConnectedDeviceRecyclerViewAdapter = new ConnectedDeviceRecyclerViewAdapter(new ArrayList<>());
            recyclerView.setAdapter(mConnectedDeviceRecyclerViewAdapter);
            if (mConnectedDevices != null) {
                mConnectedDeviceRecyclerViewAdapter.setListener(this);
                final List<ConnectedDeviceItem> connectedDeviceItems = new ArrayList<>();
                mConnectedDevices.forEach(connectedDevice -> {
                    connectedDeviceItems.add(new ConnectedDeviceItem(connectedDevice.getName(), connectedDevice.getAddress()));
                    logDeviceInformation(connectedDevice);
                });
                mConnectedDeviceRecyclerViewAdapter.setData(connectedDeviceItems);
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

            if (mBluetoothManager != null) {
                List<BluetoothDevice> connectedCentrals = mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
                if (connectedCentrals != null && !connectedCentrals.isEmpty()) {
                    mPeripheralRoleActivity.addLog(TAG, "Connected Centrals");
                    connectedCentrals.forEach(this::logDeviceInformation);
                }
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @Override
    public void onDataTransferButtonClicked(ConnectedDeviceItem item) {

    }

    public void notifyDeviceConnectionStateChange(String name, String macAddress, int newState) {
        if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            mPeripheralRoleActivity.runOnUiThread(() -> {
                mConnectedDeviceRecyclerViewAdapter.remove(macAddress);
            });
        } else if (newState == BluetoothProfile.STATE_CONNECTED) {
            mPeripheralRoleActivity.runOnUiThread(() -> {
                mConnectedDeviceRecyclerViewAdapter.append(new ConnectedDeviceItem(name, macAddress));
            });
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

        mConnectedDevices = mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
    }

    @SuppressLint("MissingPermission")
    private void logDeviceInformation(BluetoothDevice connectedDevice) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(connectedDevice.getName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            stringBuilder.append(", ").append(connectedDevice.getAlias());
        }
        stringBuilder.append(", ").append(connectedDevice.getAddress().toUpperCase());
        stringBuilder.append(", ").append(connectedDevice.getType());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            stringBuilder.append(", ").append(connectedDevice.getAddressType());
        }
        stringBuilder.append(", ").append(connectedDevice.getBluetoothClass().toString());
        stringBuilder.append(", ").append(connectedDevice.getBondState()).append("\n====================\n");
        mPeripheralRoleActivity.addLog(TAG, stringBuilder.toString());
    }

}