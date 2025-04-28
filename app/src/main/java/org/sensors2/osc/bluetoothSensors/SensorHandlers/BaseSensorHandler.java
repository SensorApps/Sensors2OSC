package org.sensors2.osc.bluetoothSensors.SensorHandlers;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public abstract class BaseSensorHandler implements  SensorHandler {
    final List<BluetoothGatt> gatts = new ArrayList<>();
    @Override
    public void addGatt(BluetoothGatt gatt) {
        gatts.add(gatt);
    }

    @Override
    public void removeGatt(BluetoothGatt gatt) {
        gatts.remove(gatt);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void disconnect() {
        for(BluetoothGatt gatt :gatts){
            gatt.disconnect();
            gatt.close();
        }
        gatts.clear();
    }
}
