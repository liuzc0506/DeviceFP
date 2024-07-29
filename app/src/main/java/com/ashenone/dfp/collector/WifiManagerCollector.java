package com.ashenone.dfp.collector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.ashenone.dfp.DeviceInfo;
import com.ashenone.dfp.util.ReflactUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

public class WifiManagerCollector {
    private static WifiManagerCollector INSTANCE;
    private WifiManager wifiManager;
    private Context ctx;

    private WifiManagerCollector(Context ctx) {
        this.ctx = ctx;
        try {
            this.wifiManager = (WifiManager) ReflactUtil.invoke(ctx, "getSystemService", new Class[]{String.class}, new Object[]{Context.WIFI_SERVICE});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static WifiManagerCollector getInstance(Context ctx) {
        if (WifiManagerCollector.INSTANCE == null) {
            Class v1 = SimInfoCollector.class;
            synchronized (v1) {
                if (WifiManagerCollector.INSTANCE == null) {
                    WifiManagerCollector.INSTANCE = new WifiManagerCollector(ctx);
                }
            }
        }
        return INSTANCE;
    }

    @SuppressLint({"MissingPermission", "NewApi"})
    public void getWifiManagerInfo() {
        DeviceInfo deviceInfo = DeviceInfo.getInstance();
        try {
            int wifiState = wifiManager.getWifiState();
            List<ScanResult> scanResults =  wifiManager.getScanResults();

            deviceInfo.WifiManagerInfo.put("wifiState", wifiState);
            deviceInfo.WifiManagerInfo.put("scanResults",scanResults);
        } catch (Exception e) {
            e.printStackTrace();
        }


        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        try{
            String ssid = wifiInfo.getSSID();
            String bssid = wifiInfo.getBSSID();
            int ip = wifiInfo.getIpAddress();
            int maxSupportedRxLinkSpeedMbps = wifiInfo.getMaxSupportedRxLinkSpeedMbps();
            int rxLinkSpeedMbps = wifiInfo.getRxLinkSpeedMbps();
            int maxSupportedTxLinkSpeedMbps = wifiInfo.getMaxSupportedTxLinkSpeedMbps();
            int txLinkSpeedMbps = wifiInfo.getTxLinkSpeedMbps();
            int netWorkId = wifiInfo.getNetworkId();
            int rssi = wifiInfo.getRssi();

            deviceInfo.WifiManagerInfo.put("ssid", ssid);
            deviceInfo.WifiManagerInfo.put("bssid", bssid);
            deviceInfo.WifiManagerInfo.put("ip", ip);
            deviceInfo.WifiManagerInfo.put("maxSupportedRxLinkSpeedMbps", maxSupportedRxLinkSpeedMbps);
            deviceInfo.WifiManagerInfo.put("rxLinkSpeedMbps", rxLinkSpeedMbps);
            deviceInfo.WifiManagerInfo.put("maxSupportedTxLinkSpeedMbps", maxSupportedTxLinkSpeedMbps);
            deviceInfo.WifiManagerInfo.put("txLinkSpeedMbps", txLinkSpeedMbps);
            deviceInfo.WifiManagerInfo.put("netWorkId", netWorkId);
            deviceInfo.WifiManagerInfo.put("rssi", rssi);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
