package io.github.tuanpq.bledevice;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> mPermissionsList;

    @SuppressLint("InlinedApi")
    private static final String[] BLUETOOTH_PERMISSIONS = new String[] {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    @SuppressLint("InlinedApi")
    private static final String[] ANDROID_13_BLUETOOTH_PERMISSIONS = new String[] {
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_ADVERTISE,
    };

    @SuppressLint("InlinedApi")
    private static final String[] ANDROID_12_BLUETOOTH_PERMISSIONS = new String[] {
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    ActivityResultLauncher<String[]> mPermissionsLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {

        @Override
        public void onActivityResult(Map<String, Boolean> result) {
            if (!result.isEmpty()) {
                int permissionsCount = 0;
                ArrayList<Boolean> list = new ArrayList<>(result.values());

                mPermissionsList = new ArrayList<>();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    for (int i = 0; i < list.size(); i++) {
                        if (shouldShowRequestPermissionRationale(ANDROID_13_BLUETOOTH_PERMISSIONS[i])) {
                            mPermissionsList.add(ANDROID_13_BLUETOOTH_PERMISSIONS[i]);
                        } else if (hasNotPermission(MainActivity.this, ANDROID_13_BLUETOOTH_PERMISSIONS[i])) {
                            permissionsCount++;
                        }
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    for (int i = 0; i < list.size(); i++) {
                        if (shouldShowRequestPermissionRationale(ANDROID_12_BLUETOOTH_PERMISSIONS[i])) {
                            mPermissionsList.add(ANDROID_12_BLUETOOTH_PERMISSIONS[i]);
                        } else if (hasNotPermission(MainActivity.this, ANDROID_12_BLUETOOTH_PERMISSIONS[i])) {
                            permissionsCount++;
                        }
                    }
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        if (shouldShowRequestPermissionRationale(BLUETOOTH_PERMISSIONS[i])) {
                            mPermissionsList.add(BLUETOOTH_PERMISSIONS[i]);
                        } else if (hasNotPermission(MainActivity.this, BLUETOOTH_PERMISSIONS[i])) {
                            permissionsCount++;
                        }
                    }
                }

                if (!mPermissionsList.isEmpty()) {
                    // Some permissions are denied and can be asked again.
                    askForPermissions(mPermissionsList);
                } else if (permissionsCount > 0) {
                    Toast.makeText(MainActivity.this, R.string.must_accept_all_required_permissions, Toast.LENGTH_SHORT).show();
                }
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button centralRole = findViewById(R.id.central_role);
        centralRole.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CentralRoleActivity.class));
        });

        Button peripheralRole = findViewById(R.id.peripheral_role);
        peripheralRole.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, PeripheralRoleActivity.class));
        });

        Button dualRole = findViewById(R.id.dual_role);
        dualRole.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, DualRoleActivity.class));
        });

        Button systemInformation = findViewById(R.id.system_information);
        systemInformation.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SystemInformationActivity.class));
        });

        mPermissionsList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mPermissionsList.addAll(Arrays.asList(ANDROID_13_BLUETOOTH_PERMISSIONS));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            mPermissionsList.addAll(Arrays.asList(ANDROID_12_BLUETOOTH_PERMISSIONS));
        } else {
            mPermissionsList.addAll(Arrays.asList(BLUETOOTH_PERMISSIONS));
        }
        askForPermissions(mPermissionsList);
    }

    private void askForPermissions(ArrayList<String> permissionsList) {
        String[] newPermissionStr = new String[permissionsList.size()];
        for (int i = 0; i < newPermissionStr.length; i++) {
            newPermissionStr[i] = permissionsList.get(i);
        }
        if (newPermissionStr.length > 0) {
            mPermissionsLauncher.launch(newPermissionStr);
        } else {
            Toast.makeText(MainActivity.this, R.string.must_accept_all_required_permissions, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean hasNotPermission(Context context, String permissionStr) {
        return ContextCompat.checkSelfPermission(context, permissionStr) != PackageManager.PERMISSION_GRANTED;
    }

}