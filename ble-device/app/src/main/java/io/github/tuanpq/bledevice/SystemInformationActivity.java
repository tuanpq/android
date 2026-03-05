package io.github.tuanpq.bledevice;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import static io.github.tuanpq.bledevice.Constant.REQUEST_PERMISSION;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class SystemInformationActivity extends AppCompatActivity {

    TextView phone_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_system_information);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Binding views
        phone_number = findViewById(R.id.phone_number);
    }

    public void GetNumber(View v) {
        // Permission check
        if (ActivityCompat.checkSelfPermission(this, READ_PHONE_NUMBERS) == PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, READ_PHONE_STATE) == PERMISSION_GRANTED) {
            extractPhoneNumber();
        } else {
            // Ask for permission
            requestPermission();
        }
    }

    private void extractPhoneNumber() {
        try {
            // Get the SubscriptionManager
            SubscriptionManager subscriptionManager =
                    (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

            if (subscriptionManager != null) {

                @SuppressLint("MissingPermission")
                List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();

                if (subscriptionInfoList != null && !subscriptionInfoList.isEmpty()) {

                    // Get the first subscription's phone number
                    String phoneNumber = subscriptionInfoList.get(0).getNumber();

                    if (phoneNumber != null && !phoneNumber.isEmpty()) {
                        phone_number.setText(phoneNumber);
                    } else {
                        phone_number.setText("Phone number not available");
                    }

                    // If you want to show all numbers from multiple SIMs
                    if (subscriptionInfoList.size() > 1) {
                        StringBuilder allNumbers = new StringBuilder("All numbers:\n");
                        for (SubscriptionInfo info : subscriptionInfoList) {
                            String number = info.getNumber();
                            if (number != null && !number.isEmpty()) {
                                allNumbers.append(info.getCarrierName())
                                        .append(": ")
                                        .append(number)
                                        .append("\n");
                            }
                        }
                    }

                } else {
                    phone_number.setText("No active subscriptions found");
                }
            } else {
                phone_number.setText("Subscription manager not available");
            }
        } catch (Exception e) {
            phone_number.setText("Error: " + e.getMessage());
        }
    }

    private void requestPermission() {
        requestPermissions(new String[]{READ_PHONE_NUMBERS, READ_PHONE_STATE}, REQUEST_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION) {

            // Check if all required permissions are granted
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                // Permissions granted, now get the phone number
                extractPhoneNumber();
            } else {
                Toast.makeText(this, "Permissions denied. Cannot access phone number.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}