package io.github.tuanpq.bledevice;

public class PairedDeviceItem {

    private final String mName;
    private final String mMacAddress;
    private int mState;

    public PairedDeviceItem(String name, String macAddress) {
        this.mName = name;
        this.mMacAddress = macAddress;
    }

    public String getName() {
        return mName;
    }

    public String getMacAddress() {
        return mMacAddress;
    }

    public int getState() {
        return mState;
    }

    public void setState(int state) {
        this.mState = state;
    }

}