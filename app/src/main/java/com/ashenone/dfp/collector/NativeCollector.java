package com.ashenone.dfp.collector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.ashenone.dfp.Common;
import com.ashenone.dfp.DeviceInfo;

import java.lang.reflect.Method;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class NativeCollector {
    static {
        System.loadLibrary("dfp");
    }

    public static void getSystemProp(){
        Map<String, String> macMap = getMac();
        Log.d("NativeCollector", macMap.toString());
        DeviceInfo deviceInfo = DeviceInfo.getInstance();
        try {
            for(String str: Common. prop_keys){
                String result = getProp(str);
                if(result != null && !result.isEmpty()) {
                    deviceInfo.NativeSystemPropInfo.put(str,result);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    public static void getDrmString(){
        byte[] drm = getDrm();
        String s = Base64.getEncoder().encodeToString(drm);
        System.out.println(s);
    }

    public static native String getProp(String key);

    public static native byte[] getDrm();

    public static native void test();

    public static native String getProcessName();

    public static native String getData(String key);

    public static native void setData(String key,String value);

    public static native String readFile(String path);

    public static native String readDir(String path);

    public static native String testShell(String cmd);

    public static native Map<String, String> getMac();
}
