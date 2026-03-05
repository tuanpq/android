package io.github.tuanpq.bledevice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.github.tuanpq.bledevice.databinding.FragmentLogBinding;

public class LogFragment extends Fragment {
    private FragmentLogBinding mBinding;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentLogBinding.inflate(inflater, container, false);
        setupAction();
        return mBinding.getRoot();
    }

    public void appendLog(String message) {
        FragmentActivity fragmentActivity = getActivity();
        if (fragmentActivity == null) return;

        fragmentActivity.runOnUiThread(() -> {
            String currentLog = mBinding.logTextView.getText().toString();
            String logContent = currentLog + "[" + DATE_FORMAT.format(new Date()) + "] " + message + "\n";
            mBinding.logTextView.setText(logContent);

            // Auto-scroll to bottom
            mBinding.logScroll.post(() -> mBinding.logScroll.fullScroll(View.FOCUS_DOWN));
        });
    }

    private void setupAction() {
        mBinding.clearLogButton.setOnClickListener(v -> {
            FragmentActivity fragmentActivity = getActivity();
            if (fragmentActivity == null) return;

            fragmentActivity.runOnUiThread(() -> {
                // Clear the log
                mBinding.logTextView.setText("");

                // Auto-scroll to bottom
                mBinding.logScroll.post(() -> mBinding.logScroll.fullScroll(View.FOCUS_DOWN));
            });

        });
    }

}