package com.ashenone.dfp.collector;

import android.annotation.SuppressLint;
import android.content.Context;
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

public class TelephonyManagerCollector {
    private static TelephonyManagerCollector INSTANCE;
    private TelephonyManager telephonyManager;
    private Context ctx;

    private TelephonyManagerCollector(Context ctx) {
        this.ctx = ctx;
        try {
            this.telephonyManager = (TelephonyManager) ReflactUtil.invoke(ctx, "getSystemService", new Class[]{String.class}, new Object[]{"phone"});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static TelephonyManagerCollector getInstance(Context ctx) {
        if (TelephonyManagerCollector.INSTANCE == null) {
            Class v1 = SimInfoCollector.class;
            synchronized (v1) {
                if (TelephonyManagerCollector.INSTANCE == null) {
                    TelephonyManagerCollector.INSTANCE = new TelephonyManagerCollector(ctx);
                }
            }
        }
        return INSTANCE;
    }

    @SuppressLint({"MissingPermission", "NewApi"})
    public void getTelephoneManagerInfo() {
        DeviceInfo deviceInfo = DeviceInfo.getInstance();
        try {
            String simOperator = telephonyManager.getSimOperator();
            String networkOperator = telephonyManager.getNetworkOperator();
            String networkOperatorName = telephonyManager.getNetworkOperatorName();
            int networkType = telephonyManager.getNetworkType();
            int phoneCount = telephonyManager.getPhoneCount();
            // add sim state
            int simState = telephonyManager.getSimState();

            deviceInfo.TelephonyManagerInfo.put("simOperator", simOperator);
            deviceInfo.TelephonyManagerInfo.put("networkOperator", networkOperator);
            deviceInfo.TelephonyManagerInfo.put("networkOperatorName", networkOperatorName);
            deviceInfo.TelephonyManagerInfo.put("networkType", networkType);
            deviceInfo.TelephonyManagerInfo.put("phoneCount", phoneCount);
            //add sim state
            deviceInfo.TelephonyManagerInfo.put("simState",simState);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            String deviceSoftwareVersion = telephonyManager.getDeviceSoftwareVersion();
            deviceInfo.TelephonyManagerInfo.put("deviceSoftwareVersion",deviceSoftwareVersion);
        }catch (Exception e){
            try {
                deviceInfo.TelephonyManagerInfo.put("deviceSoftwareVersion","FAIL");
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
        try {
            String deviceId = telephonyManager.getDeviceId();
            deviceInfo.TelephonyManagerInfo.put("deviceId",deviceId);
        }catch (Exception e){
            try {
                deviceInfo.TelephonyManagerInfo.put("deviceId","FAIL");
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }

        try {
            String simSerialNumber = telephonyManager.getSimSerialNumber();
            deviceInfo.TelephonyManagerInfo.put("simSerialNumber",simSerialNumber);
        }catch (Exception e){
            try {
                deviceInfo.TelephonyManagerInfo.put("simSerialNumber","FAIL");
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }

        try {
            String subscriberId = telephonyManager.getSubscriberId();
            deviceInfo.TelephonyManagerInfo.put("subscriberId",subscriberId);
        }catch (Exception e){
            try {
                deviceInfo.TelephonyManagerInfo.put("subscriberId","FAIL");
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }

        try {
            if(Build.VERSION.SDK_INT >= 29){
                String manufacturerCode = telephonyManager.getManufacturerCode();
                deviceInfo.TelephonyManagerInfo.put("manufacturerCode",manufacturerCode);
            }
        }catch (Exception e){
            try {
                deviceInfo.TelephonyManagerInfo.put("manufacturerCode","FAIL");
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }

        try {
            ServiceState serviceState = telephonyManager.getServiceState();
            deviceInfo.TelephonyManagerInfo.put("serviceState",serviceState);
        }catch (Exception e){
            try {
                deviceInfo.TelephonyManagerInfo.put("serviceState","FAIL");
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }

        try {
            String meid = telephonyManager.getMeid();
            deviceInfo.TelephonyManagerInfo.put("meid",meid);
        }catch (Exception e){
            try {
                deviceInfo.TelephonyManagerInfo.put("meid","FAIL");
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }

        // getImei
        Class clazz = telephonyManager.getClass();
        String imei = "";
        try {
            Method getImei=clazz.getDeclaredMethod("getImei",int.class);//(int slotId)
            getImei.setAccessible(true);
            imei = (String) getImei.invoke(telephonyManager);
            deviceInfo.TelephonyManagerInfo.put("imei",imei);
        } catch (Exception e) {
            try {

                deviceInfo.TelephonyManagerInfo.put("imei","FAIL");
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
/*
        try {
            String imei = telephonyManager.getImei();
            deviceInfo.TelephonyManagerInfo.put("imei",imei);
        }catch (Exception e){
            try {
                deviceInfo.TelephonyManagerInfo.put("imei","FAIL");
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }

 */


        try {
            @SuppressLint("MissingPermission") CellLocation cellLocation = telephonyManager.getCellLocation();
            JSONObject cellLocationJson = new JSONObject();
            String type = cellLocation.getClass().getSimpleName();

            cellLocationJson.put("type", type);
            if (cellLocation instanceof android.telephony.gsm.GsmCellLocation) {
                cellLocationJson.put("Lac", ((GsmCellLocation) cellLocation).getLac());
                cellLocationJson.put("Cid", ((GsmCellLocation) cellLocation).getCid());
                cellLocationJson.put("Psc", ((GsmCellLocation) cellLocation).getPsc());
            } else {
                Log.e("TODO", "getTelephoneManagerInfo: " + type);
            }
            deviceInfo.TelephonyManagerInfo.put("cellLocation", cellLocationJson);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            List<CellInfo> allCellInfo = telephonyManager.getAllCellInfo();
            JSONArray array = new JSONArray();
            Iterator<CellInfo> iterator = allCellInfo.iterator();
            while (iterator.hasNext()){
                CellInfo next = iterator.next();
                array.put(next);
            }
            deviceInfo.TelephonyManagerInfo.put("allCellInfo",array);
        }catch (Exception e){
            e.printStackTrace();
        }


    }

}
