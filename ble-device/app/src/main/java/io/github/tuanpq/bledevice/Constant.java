package io.github.tuanpq.bledevice;

import java.util.UUID;

public class Constant {
    public static int REQUEST_BLUETOOTH_CONNECT = 1;
    public static int REQUEST_PERMISSION = 2;
    public static final UUID MICROCHIP_TRANSPARENT_UART_SERVICE_UUID = UUID.fromString("49535343-FE7D-4AE5-8FA9-9FAFD205E455");
    public static final UUID MICROCHIP_TRANSPARENT_UART_TX_CHARACTERISTIC_UUID = UUID.fromString("49535343-1E4D-4BD9-BA61-23C647249616"); // Notify, Write, Write without response
    public static final UUID MICROCHIP_TRANSPARENT_UART_RX_CHARACTERISTIC_UUID = UUID.fromString("49535343-8841-43F4-A8D4-ECBE34729BB3"); // Write, Write without response
    public static final UUID CLIENT_CHARACTERISTIC_CONFIGURATION_UUID = UUID.fromString("00002902-0000-1000-8000-00805F9B34FB");
}
