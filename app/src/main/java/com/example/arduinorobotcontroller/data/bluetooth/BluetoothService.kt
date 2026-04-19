import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothService {
    private static final String TAG = "BluetoothService";
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;
    private InputStream inputStream;
    private OutputStream outputStream;

    public BluetoothService(BluetoothAdapter adapter) {
        this.bluetoothAdapter = adapter;
    }

    public void pairDevice(BluetoothDevice device) throws BluetoothException {
        try {
            device.createBond();
            this.bluetoothDevice = device;
        } catch (Exception e) {
            throw new BluetoothException("Failed to pair with device", e);
        }
    }

    public void connectToDevice(BluetoothDevice device) throws BluetoothException {
        try {
            this.bluetoothDevice = device;
            UUID uuid = device.getUuids()[0].getUuid();
            this.bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();
            inputStream = bluetoothSocket.getInputStream();
            outputStream = bluetoothSocket.getOutputStream();
        } catch (IOException e) {
            throw new BluetoothException("Could not connect to device", e);
        }
    }

    public void sendData(byte[] data) throws BluetoothException {
        try {
            outputStream.write(data);
        } catch (IOException e) {
            throw new BluetoothException("Error sending data", e);
        }
    }

    public byte[] receiveData() throws BluetoothException {
        byte[] buffer = new byte[1024];
        int bytes;
        try {
            bytes = inputStream.read(buffer);
            return buffer;
        } catch (IOException e) {
            throw new BluetoothException("Error receiving data", e);
        }
    }

    public void disconnect() throws BluetoothException {
        try {
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
            }
        } catch (IOException e) {
            throw new BluetoothException("Error disconnecting from device", e);
        }
    }

    public enum BluetoothConnectionState {
        CONNECTED,
        DISCONNECTED,
        CONNECTING,
        DISCONNECTING
    }

    public static class BluetoothException extends Exception {
        public BluetoothException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}