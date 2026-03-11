package io.github.tuanpq.bledevice;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.github.tuanpq.bledevice.databinding.FragmentConnectedDeviceItemBinding;

public class ConnectedDeviceRecyclerViewAdapter extends RecyclerView.Adapter<ConnectedDeviceRecyclerViewAdapter.ViewHolder> {

    private final List<ConnectedDeviceItem> mValues;

    private ConnectedDeviceItemClickListener mListener;

    public ConnectedDeviceRecyclerViewAdapter(List<ConnectedDeviceItem> items) {
        mValues = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentConnectedDeviceItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(mValues.get(position).getName());
        holder.mMacAddressView.setText(mValues.get(position).getMacAddress());
        holder.mChatButton.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onChatButtonClicked(holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues != null ? mValues.size() : 0;
    }

    public void setListener(ConnectedDeviceItemClickListener listener) {
        mListener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<ConnectedDeviceItem> items) {
        mValues.clear();
        if (items != null) {
            mValues.addAll(items);
        }
        notifyDataSetChanged();
    }

    public void append(ConnectedDeviceItem connectedDeviceItem) {
        if (mValues.stream().noneMatch(item -> item.getMacAddress().equalsIgnoreCase(connectedDeviceItem.getMacAddress()))) {
            mValues.add(connectedDeviceItem);
            notifyItemInserted(mValues.size() - 1);
        }
    }

    public void remove(String macAddress) {
        for (int i = 0; i < mValues.size(); i++) {
            if (mValues.get(i).getMacAddress().equalsIgnoreCase(macAddress)) {
                mValues.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final AppCompatTextView mNameView;
        public final AppCompatTextView mMacAddressView;
        public final AppCompatButton mChatButton;
        public ConnectedDeviceItem mItem;

        public ViewHolder(FragmentConnectedDeviceItemBinding binding) {
            super(binding.getRoot());
            mNameView = (AppCompatTextView) binding.name;
            mMacAddressView = (AppCompatTextView) binding.macAddress;
            mChatButton = (AppCompatButton) binding.chat;
        }

    }

    public interface ConnectedDeviceItemClickListener {
        void onChatButtonClicked(ConnectedDeviceItem item);
    }

}