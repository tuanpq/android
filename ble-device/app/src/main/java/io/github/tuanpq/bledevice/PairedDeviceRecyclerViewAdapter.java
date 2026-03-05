package io.github.tuanpq.bledevice;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothProfile;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import io.github.tuanpq.bledevice.databinding.FragmentPairedDeviceItemBinding;

import java.util.List;

public class PairedDeviceRecyclerViewAdapter extends RecyclerView.Adapter<PairedDeviceRecyclerViewAdapter.ViewHolder> {

    private final List<PairedDeviceItem> mValues;

    private PairedDeviceItemClickListener mListener;

    public PairedDeviceRecyclerViewAdapter(List<PairedDeviceItem> items) {
        mValues = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentPairedDeviceItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

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

    public void setListener(PairedDeviceItemClickListener listener) {
        mListener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<PairedDeviceItem> items) {
        mValues.clear();
        if (items != null) {
            mValues.addAll(items);
        }
        notifyDataSetChanged();
    }

    public List<PairedDeviceItem> getData() {
        return mValues;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final AppCompatTextView mNameView;
        public final AppCompatTextView mMacAddressView;
        public final AppCompatButton mConnectButton;
        public PairedDeviceItem mItem;

        public ViewHolder(FragmentPairedDeviceItemBinding binding) {
            super(binding.getRoot());
            mNameView = (AppCompatTextView) binding.name;
            mMacAddressView = (AppCompatTextView) binding.macAddress;
            mConnectButton = (AppCompatButton) binding.connect;
        }

    }

    public interface PairedDeviceItemClickListener {
        void onConnectButtonClicked(PairedDeviceItem item, boolean connect);
    }

}