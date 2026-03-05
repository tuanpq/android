package io.github.tuanpq.bledevice;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothProfile;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Optional;

import io.github.tuanpq.bledevice.databinding.FragmentScannedDeviceItemBinding;

public class ScannedDeviceRecyclerViewAdapter extends RecyclerView.Adapter<ScannedDeviceRecyclerViewAdapter.ViewHolder> {

    private final List<ScannedDeviceItem> mValues;

    private ScannedDeviceItemClickListener mListener;

    public ScannedDeviceRecyclerViewAdapter(List<ScannedDeviceItem> items) {
        mValues = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentScannedDeviceItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(mValues.get(position).getName());
        holder.mMacAddressView.setText(mValues.get(position).getMacAddress());
        boolean connect = true;
        switch (mValues.get(position).getState()) {
            case BluetoothProfile.STATE_DISCONNECTED:
                holder.mConnectButton.setText(R.string.connect);
                break;
            case BluetoothProfile.STATE_CONNECTED:
                connect = false;
                holder.mConnectButton.setText(R.string.disconnect);
                break;
            default:
                break;
        }
        boolean finalConnect = connect;
        holder.mConnectButton.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onConnectButtonClicked(holder.mItem, finalConnect);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues != null ? mValues.size() : 0;
    }

    public void setListener(ScannedDeviceItemClickListener listener) {
        mListener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<ScannedDeviceItem> items) {
        mValues.clear();
        if (items != null) {
            mValues.addAll(items);
        }
        notifyDataSetChanged();
    }

    public List<ScannedDeviceItem> getData() {
        return mValues;
    }

    public void appendData(ScannedDeviceItem item) {
        Optional<ScannedDeviceItem> result = mValues
                .stream()
                .filter(scannedDeviceItem -> scannedDeviceItem.getMacAddress().equalsIgnoreCase(item.getMacAddress()))
                .findFirst();
        if (result.isPresent()) {
            return;
        }

        mValues.add(item);
        notifyItemInserted(mValues.size() - 1);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final AppCompatTextView mNameView;
        public final AppCompatTextView mMacAddressView;
        public final AppCompatButton mConnectButton;
        public ScannedDeviceItem mItem;

        public ViewHolder(FragmentScannedDeviceItemBinding binding) {
            super(binding.getRoot());
            mNameView = (AppCompatTextView) binding.name;
            mMacAddressView = (AppCompatTextView) binding.macAddress;
            mConnectButton = (AppCompatButton) binding.connect;
        }

    }

    public interface ScannedDeviceItemClickListener {
        void onConnectButtonClicked(ScannedDeviceItem item, boolean connect);
    }

}