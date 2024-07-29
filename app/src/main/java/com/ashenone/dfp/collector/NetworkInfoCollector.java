package com.ashenone.dfp.collector;

import android.Manifest;
import android.net.wifi.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.ashenone.dfp.DeviceInfo;
import com.ashenone.dfp.util.ReflactUtil;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

public class NetworkInfoCollector {
    public static NetworkInfoCollector INSTANCE = null;
    private WifiManager wifiService;
    private Context ctx;
    private WifiInfo wifiInfo;

    private NetworkInfoCollector(Context ctx) {
        this.wifiService = null;
        this.ctx = null;
        this.wifiInfo = null;
        try {
            this.ctx = ctx;
            if (this.ctx != null) {
                this.wifiService = (WifiManager)ReflactUtil.invoke(this.ctx, "getSystemService", new Class[]{String.class}, new Object[]{"wifi"});
                if (this.wifiService != null) {
                    this.wifiInfo = (WifiInfo) ReflactUtil.invoke(this.wifiService, "getConnectionInfo");
                    return;
                }
            }
        } catch (Exception v0) {
            return;
        }
    }

    public static NetworkInfoCollector getInstance(Context ctx) {
        if (NetworkInfoCollector.INSTANCE == null) {
            Class v1 = NetworkInfoCollector.class;
            synchronized (v1) {
                if (NetworkInfoCollector.INSTANCE == null) {
                    NetworkInfoCollector.INSTANCE = new NetworkInfoCollector(ctx);
                }

                return NetworkInfoCollector.INSTANCE;
            }
        }
        return NetworkInfoCollector.INSTANCE;
    }

    public static String getNetworkType(int arg4) {
        switch (arg4) {
            case -101: {
                return "wifi";
            }
            case -1: {
                return "nil";
            }
            case 0: {
                return "unknown";
            }
            case 1: {
                return "2g.gprs";
            }
            case 2: {
                return "2g.edge";
            }
            case 3: {
                return "3g.umts";
            }
            case 4: {
                return "2g.cdma";
            }
            case 5: {
                return "3g.evdo_0";
            }
            case 6: {
                return "3g.evdo_a";
            }
            case 7: {
                return "2g.1xrtt";
            }
            case 8: {
                return "3g.hsdpa";
            }
            case 9: {
                return "3g.hsupa";
            }
            case 10: {
                return "3g.hspa";
            }
            case 11: {
                return "2g.iden";
            }
            case 12: {
                return "3g.evdo_b";
            }
            case 13: {
                return "4g.lte";
            }
            case 14: {
                return "3g.ehrpd";
            }
            case 15: {
                return "3g.hspap";
            }
            default: {
                return String.format("%d", ((int) arg4));
            }
        }
    }

    public String getSSID() {
        try {
            if (this.wifiInfo != null) {
                String v0_1 = (String) ReflactUtil.invoke(this.wifiInfo, "getSSID");
                return v0_1 == null ? "" : v0_1;
            }
        } catch (Exception v0) {
            return "";
        }

        return "";
    }

    public String getBSSID() {
        try {
            if (this.wifiInfo != null) {
                String v0_1 = (String) ReflactUtil.invoke(this.wifiInfo, "getBSSID");
                return v0_1 == null ? "" : v0_1;
            }
        } catch (Exception v0) {
            return "";
        }

        return "";
    }

    // android6.0以下
    public String getMacAddress() {
        try {
            if (this.wifiInfo != null) {
                String v0_1 = (String) ReflactUtil.invoke(this.wifiInfo, "getMacAddress");
                return v0_1 == null ? "" : v0_1;
            }
        } catch (Exception v0) {
            return "";
        }

        return "";
    }

    public String getIpAddress() {
        try {
            if (this.wifiInfo != null) {
                String v0_1 = Formatter.formatIpAddress(((Integer) ReflactUtil.invoke(this.wifiInfo, "getIpAddress")).intValue());
                return v0_1 == null ? "" : v0_1;
            }
        } catch (Exception v0) {
            return "";
        }

        return "";
    }

    private String replaceMaoHao(String arg2) {
        return arg2 != null && !arg2.isEmpty() ? arg2.replaceAll(":", "").toLowerCase() : "";
    }

    private String getHexString(byte[] arg6) {
        StringBuffer sb = new StringBuffer();
        int index;
        for(index = 0; index < arg6.length; ++index) {
            byte b = arg6[index];
            if(sb.length() > 0) {
                sb.append(":");
            }

            String hexString = Integer.toHexString(b & 0xFF);
            sb.append((hexString.length() == 1 ? "0" + hexString : Integer.toHexString(b & 0xFF)));
        }

        return sb.toString();
    }

    public List getWifiLocation() {
        ArrayList v2 = new ArrayList();
        try {
            PackageManager v0_1 = this.ctx.getPackageManager();
            if (v0_1.checkPermission("android.permission.ACCESS_FINE_LOCATION", this.ctx.getPackageName()) != PackageManager.PERMISSION_GRANTED && v0_1.checkPermission("android.permission.ACCESS_COARSE_LOCATION", this.ctx.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return v2;
            }

            if (this.wifiService != null) {
                for (Object o : ((List) ReflactUtil.invoke(this.wifiService, "getScanResults"))) {
                    ScanResult result = (ScanResult) o;
                    v2.add(replaceMaoHao(((String) ReflactUtil.getValue(result, "BSSID"))) + "," + ((int) (((Integer) ReflactUtil.getValue(result, "level")))));
                }
            }
        } catch (Exception v0) {
            v0.printStackTrace();
        }

        return v2;
    }

    public String getNetworkType() {
        String result = "";
        try {
            if (this.ctx != null) {
                result = this._getNetworkType();
                if (result == null) {
                    return "";
                }
            }
        } catch (Exception v1) {
            return result;
        }

        return result;
    }

    public void getNetwordIFaceAddrs() {
        try {
            DeviceInfo deviceInfo = DeviceInfo.getInstance();
            Map<String, String> tempMap = NativeCollector.getMac();
            for (Map.Entry<String, String> entry : tempMap.entrySet()) {
                String key = entry.getKey(); // 获取键
                String value = entry.getValue(); // 获取值
                Log.d("NetworkInterface_debug:   ", key + value);
                deviceInfo.NetworkInterfaceInfo.put(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }




//        try {
//            Object mNetworkInterfaces = ReflactUtil.invoke("java.net.NetworkInterface", "getNetworkInterfaces");
//            Method hasMoreElements = Enumeration.class.getDeclaredMethod("hasMoreElements");
//            hasMoreElements.setAccessible(true);
//            Method nextElement = Enumeration.class.getDeclaredMethod("nextElement");
//            nextElement.setAccessible(true);
//            while ((Boolean) hasMoreElements.invoke(mNetworkInterfaces, new Object[0])) {
//                NetworkInterface networkInterface = (NetworkInterface) nextElement.invoke(mNetworkInterfaces);
//                if (networkInterface.isLoopback()) {
//                    continue;
//                }
//
//                byte[] hardwareAddress = networkInterface.getHardwareAddress();
//                String hardwareAddressString = hardwareAddress != null && hardwareAddress.length > 0 ? getHexString(hardwareAddress) : "";
////                if ((hardwareAddressString.isEmpty()) || (hardwareAddressString.equals("000000000000"))) {
////                    Log.d("testtt",hardwareAddressString);
////                    continue;
////                }
//
//                //deviceInfo.NetworkInterfaceInfo.put(networkInterface.getDisplayName(), hardwareAddressString);
//                deviceInfo.NetworkInterfaceInfo.put("mac", hardwareAddressString);
//
//                String name = networkInterface.getName();
//                int mtu = networkInterface.getMTU();
//                boolean isLoopback = networkInterface.isLoopback();
//                boolean isP2P = networkInterface.isPointToPoint();
//                boolean isUp = networkInterface.isUp();
//                boolean isVirtual = networkInterface.isVirtual();
//                deviceInfo.NetworkInterfaceInfo.put("name", name);
//                deviceInfo.NetworkInterfaceInfo.put("mtu", mtu);
//                deviceInfo.NetworkInterfaceInfo.put("isLoopback", isLoopback);
//                deviceInfo.NetworkInterfaceInfo.put("isP2P", isP2P);
//                deviceInfo.NetworkInterfaceInfo.put("isUp", isUp);
//                deviceInfo.NetworkInterfaceInfo.put("isVirtual", isVirtual);
//            }
//        } catch (Exception v0) {
//            v0.printStackTrace();
//        }
    }


    public boolean isConnected() {
        try {
            NetworkInfo v0_1 = ((ConnectivityManager) this.ctx.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (v0_1 != null) {
                return v0_1.isConnected();
            }
        } catch (Exception v0) {
            return true;
        }

        return false;
    }

    private String _getNetworkType() {
        try {
            NetworkInfo networkInfo = ((ConnectivityManager) this.ctx.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (networkInfo != null && (networkInfo.isAvailable()) && (networkInfo.isConnected())) {
                int type = networkInfo.getType();
                if (type == 1) {
                    return "wifi";
                }

                if (ActivityCompat.checkSelfPermission(this.ctx, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return type != 0 ? "unknown" : NetworkInfoCollector.getNetworkType(((TelephonyManager) this.ctx.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkType());
                }

            }

            return "nil";
        }
        catch(Exception v0) {
            return "unknown";
        }
    }

}
