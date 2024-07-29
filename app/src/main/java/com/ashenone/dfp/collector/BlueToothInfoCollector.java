package com.ashenone.dfp.collector;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcel;

import androidx.core.app.ActivityCompat;

import com.ashenone.dfp.DeviceInfo;

import org.json.JSONException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class BlueToothInfoCollector {
    public static void getBlueToothInfo(Context ctx) {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        DeviceInfo deviceInfo = DeviceInfo.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ((Activity) ctx).requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT},100);

            try {
                deviceInfo.BluetoothInfo.put("name", "");
                deviceInfo.BluetoothInfo.put("address", "");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return ;
        }

        try {
            deviceInfo.BluetoothInfo.put("name", adapter.getName());
            deviceInfo.BluetoothInfo.put("address", adapter.getAddress());
            deviceInfo.BluetoothInfo.put("address", adapter.getState());
            deviceInfo.BluetoothInfo.put("address", adapter.getBluetoothLeScanner());
//            deviceInfo.BluetoothInfo.put("address", adapter.);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String getBluetoothInfo() {
        String v0_7;
        Parcel v2_1;
        Parcel v1_3;
        IBinder v0_5;
        try {
            BluetoothAdapter v0_1 = BluetoothAdapter.getDefaultAdapter();
            Field v1 = BluetoothAdapter.class.getDeclaredField("mService");
            v1.setAccessible(true);
            Object v1_1 = v1.get(v0_1);
            if(v1_1 == null) {
                throw new Exception();
            }
            Class proxy = Class.forName("android.bluetooth.IBluetooth$Stub$Proxy");
            Method getAddress = proxy.getMethod("getAddress", (Class<?>) null);
            Object v0_2 = getAddress.invoke(v1_1, (Object) null);
            if(v0_2 != null && ((v0_2 instanceof String))) {
                return (String)v0_2;
            }

            throw new Exception();

        }
        catch(Exception v0) {
            try {
                Class v0_4 = Class.forName("android.os.ServiceManager");
                Class.forName("android.bluetooth.IBluetoothManager");
                Class v1_2 = Class.forName("android.bluetooth.IBluetoothManager$Stub");
                Field v2 = v1_2.getField("FIRST_CALL_TRANSACTION");
                v0_5 = (IBinder)v0_4.getMethod("getService", String.class).invoke(null, "bluetooth_manager");
                v2.getInt(v1_2);
                v1_3 = Parcel.obtain();
                v2_1 = Parcel.obtain();
            }
            catch(Exception v0_3) {
                return "";
            }

            try {
                v1_3.writeInterfaceToken("android.bluetooth.IBluetoothManager");
                if(Build.VERSION.SDK_INT >= 21) {
                    v0_5.transact(5, v1_3, v2_1, 0);
                }
                else {
                    v0_5.transact(10, v1_3, v2_1, 0);
                }

                v2_1.readException();
                v0_7 = v2_1.readString();
                v2_1.recycle();
                v1_3.recycle();

                return v0_7 == null ? "" : v0_7;
            }
            catch(Throwable v0_6) {
            }
        }
        return "";
    }

}
