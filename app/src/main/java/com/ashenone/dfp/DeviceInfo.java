package com.ashenone.dfp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DeviceInfo {

    public JSONObject Switch = new JSONObject();
    public JSONObject BuildInfo = new JSONObject();
    public JSONObject VersionInfo = new JSONObject();
    public JSONObject BatteryInfo = new JSONObject();
    public JSONObject SystemPropInfo = new JSONObject();
    public JSONObject NativeSystemPropInfo = new JSONObject();
    public JSONObject SettingInfo = new JSONObject();
    public JSONObject DisplayInfo = new JSONObject();
    public JSONObject TelephonyManagerInfo = new JSONObject();
    public JSONObject WifiManagerInfo = new JSONObject();
    public JSONObject ConnectivityManagerInfo = new JSONObject();
    public JSONObject UiModeManagerInfo = new JSONObject();
    public JSONObject NetworkInterfaceInfo = new JSONObject();
    public JSONObject SensorListInfo = new JSONObject();
    public JSONObject StatFsInfo = new JSONObject();
    public JSONObject ShellInfo = new JSONObject();
    public JSONObject BluetoothInfo = new JSONObject();
    public JSONObject MemoryInfo = new JSONObject();
    public JSONObject JavaSystemInfo = new JSONObject();
    public JSONObject PackageInfo = new JSONObject();
    // add GPU info
    public JSONObject GpuInfo = new JSONObject();

    private static DeviceInfo instance = null;

    private DeviceInfo(){}

    public static DeviceInfo getInstance(){
        if(instance==null){
            instance = new DeviceInfo();
        }
        return instance;
    }

    public void importData(File file){
        JSONObject data = null;
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            byte[] buff = new byte[bis.available()];
            bis.read(buff);
            data = new JSONObject(new String(buff));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Switch = (JSONObject) data.get("Switch");
            BuildInfo = (JSONObject) data.get("Build");
            VersionInfo = (JSONObject) data.get("Version");
            BatteryInfo = (JSONObject) data.get("BatteryInfo");
            SystemPropInfo = (JSONObject) data.get("SystemProp");
            NativeSystemPropInfo = (JSONObject) data.get("NativeSystemProp");
            SettingInfo = (JSONObject) data.get("Setting");
            DisplayInfo = (JSONObject) data.get("Display");
            TelephonyManagerInfo = (JSONObject) data.get("TelephonyManager");
            WifiManagerInfo = (JSONObject) data.get("WifiManager");
            ConnectivityManagerInfo = (JSONObject) data.get("ConnectivityManager");
            UiModeManagerInfo = (JSONObject) data.get("UiModeManager");
            NetworkInterfaceInfo = (JSONObject) data.get("NetworkInterface");
            SensorListInfo = (JSONObject) data.get("SensorList");
            StatFsInfo = (JSONObject) data.get("StatFs");
            ShellInfo = (JSONObject) data.get("Shell");
            BluetoothInfo = (JSONObject) data.get("Bluetooth");
            MemoryInfo = (JSONObject) data.get("Memory");
            JavaSystemInfo = (JSONObject) data.get("JavaSystem");
            PackageInfo = (JSONObject) data.get("PackageInfo");
            // add GPU info
            GpuInfo = (JSONObject) data.get("GpuInfo");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void exportData(File file){
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.d("TEST", "Make file failed!!");
                e.printStackTrace();
            }
        }

        JSONObject data = new JSONObject();

        try {
            //init Switch
            Switch.put("NativeProp",true);
            Switch.put("NativeIO",true);
            Switch.put("NativeShell",true);
            Switch.put("Build",true);
            Switch.put("Version",true);
            Switch.put("Battery",true);
            Switch.put("SystemProp",true);
            Switch.put("Setting",true);
            Switch.put("Display",true);
            Switch.put("TelephonyManager",true);
            Switch.put("WifiManager",true);
            Switch.put("NetworkInterface",true);
            Switch.put("SensorList",true);
            Switch.put("Bluetooth",true);
            Switch.put("Memory",true);
            Switch.put("JavaSystem",true);
            Switch.put("PackageInfo",true);
            // add GPU info
            Switch.put("GpuInfo",true);


            data.put("Switch",Switch);
            data.put("Build",BuildInfo);
            data.put("Version",VersionInfo);
            data.put("Battery",BatteryInfo);
            data.put("SystemProp",SystemPropInfo);
            data.put("Setting",SettingInfo);
            data.put("Display",DisplayInfo);
            data.put("TelephonyManager",TelephonyManagerInfo);
            data.put("WifiManager",WifiManagerInfo);
            data.put("ConnectivityManager",ConnectivityManagerInfo);
            data.put("UiModeManager",UiModeManagerInfo);
            data.put("NetworkInterface",NetworkInterfaceInfo);
            data.put("SensorList",SensorListInfo);
            data.put("StatFs",StatFsInfo);
            data.put("Shell",ShellInfo);
            data.put("Bluetooth",BluetoothInfo);
            data.put("Memory",MemoryInfo);
            data.put("JavaSystem",JavaSystemInfo);
            data.put("PackageInfo",PackageInfo);
            // add GPU info
            data.put("GpuInfo",GpuInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write(data.toString().getBytes(StandardCharsets.UTF_8));
            bos.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
