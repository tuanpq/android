package io.github.tuanpq.bledevice;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.tuanpq.bledevice.databinding.FragmentChatDialogBinding;
import io.github.tuanpq.bledevice.databinding.FragmentChatDialogMessageFromMeItemBinding;
import io.github.tuanpq.bledevice.databinding.FragmentChatDialogMessageFromOtherSideItemBinding;

public class ChatDialogFragment extends BottomSheetDialogFragment {
    public static final String TAG = ChatDialogFragment.class.getSimpleName();
    private FragmentChatDialogBinding mBinding;
    private final ChatMessageAdapter mChatMessageAdapter = new ChatMessageAdapter();
    private static final String DEVICE_NAME_KEY = "deviceName";
    private static final String DEVICE_MAC_ADDRESS_KEY = "deviceMacAddress";

    public static ChatDialogFragment newInstance(ConnectedDeviceItem connectedDevice) {
        final ChatDialogFragment fragment = new ChatDialogFragment();
        final Bundle args = new Bundle();
        args.putString(DEVICE_NAME_KEY, connectedDevice.getName());
        args.putString(DEVICE_MAC_ADDRESS_KEY, connectedDevice.getMacAddress());
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mBinding = FragmentChatDialogBinding.inflate(inflater, container, false);
        return mBinding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            String deviceName = bundle.getString(DEVICE_NAME_KEY);
            String macAddress = bundle.getString(DEVICE_MAC_ADDRESS_KEY);
            mBinding.deviceName.setText(deviceName == null ? "" : deviceName);
            mBinding.deviceMacAddress.setText(macAddress == null ? "" : macAddress);
        }

        final RecyclerView recyclerView = view.findViewById(R.id.chat_dialog_message_list);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(mChatMessageAdapter);
        }

        mBinding.send.setOnClickListener(v -> {
            String message = mBinding.message.getText().toString();
            mBinding.message.setText("");
            mChatMessageAdapter.append(new ChatMessageItem(true, message));
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    private static class MessageFromMeViewHolder extends RecyclerView.ViewHolder {

        final TextView text;

        MessageFromMeViewHolder(FragmentChatDialogMessageFromMeItemBinding binding) {
            super(binding.getRoot());
            text = binding.text;
        }

    }

    private static class MessageFromOtherSideViewHolder extends RecyclerView.ViewHolder {

        final TextView text;

        MessageFromOtherSideViewHolder(FragmentChatDialogMessageFromOtherSideItemBinding binding) {
            super(binding.getRoot());
            text = binding.text;
        }

    }

    private static class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public static int ME_ITEM_VIEW_TYPE = 0;
        public static int OTHER_SIDE_ITEM_VIEW_TYPE = 1;

        private final List<ChatMessageItem> mValues = new ArrayList<>();

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder;
            if (viewType == ME_ITEM_VIEW_TYPE) {
                viewHolder = new MessageFromMeViewHolder(FragmentChatDialogMessageFromMeItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            } else {
                viewHolder = new MessageFromOtherSideViewHolder(FragmentChatDialogMessageFromOtherSideItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int itemViewType = getItemViewType(position);
            if (itemViewType == ME_ITEM_VIEW_TYPE) {
                ((MessageFromMeViewHolder) holder).text.setText(mValues.get(position).getMessage());
            } else {
                ((MessageFromOtherSideViewHolder) holder).text.setText(mValues.get(position).getMessage());
            }
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (Boolean.TRUE.equals(mValues.get(position).isMe())) {
                return ME_ITEM_VIEW_TYPE;
            } else {
                return OTHER_SIDE_ITEM_VIEW_TYPE;
            }
        }

        public void append(ChatMessageItem chatMessageItem) {
            mValues.add(chatMessageItem);
            notifyItemInserted(mValues.size() - 1);
        }

    }

}