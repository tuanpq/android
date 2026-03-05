package io.github.tuanpq.bledevice;

public class ConnectedDeviceItem {

    private final String mName;
    private final String mMacAddress;

    public ConnectedDeviceItem(String name, String macAddress) {
        this.mName = name;
        this.mMacAddress = macAddress;
    }

    public String getName() {
        return mName;
    }

    public String getMacAddress() {
        return mMacAddress;
    }

}