package com.ashenone.dfp.collector;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.telephony.TelephonyManager;

import com.ashenone.dfp.DeviceInfo;
import com.ashenone.dfp.util.ReflactUtil;

import org.json.JSONException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class ActivityManagerCollector {
    private Context ctx;
    private ActivityManager activityManager;
    private static ActivityManagerCollector INSTANCE;

    private ActivityManagerCollector(Context ctx){
        this.ctx = ctx;
        try {
            this.activityManager = (ActivityManager) ReflactUtil.invoke(ctx, "getSystemService", new Class[]{String.class}, new Object[]{"activity"});
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static ActivityManagerCollector getInstance(Context ctx){
        if(INSTANCE == null){
            INSTANCE = new ActivityManagerCollector(ctx);
        }
        return INSTANCE;
    }

    public void getMemoryInfo(){
        DeviceInfo deviceInfo = DeviceInfo.getInstance();
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);

        try {
            deviceInfo.MemoryInfo.put("availMem",memoryInfo.availMem);
            deviceInfo.MemoryInfo.put("threshold",memoryInfo.threshold);
            deviceInfo.MemoryInfo.put("totalMem",memoryInfo.totalMem);
        }catch (JSONException e){}

        Class clz = memoryInfo.getClass();
        try {
            @SuppressLint("SoonBlockedPrivateApi") Field foregroundAppThreshold = clz.getDeclaredField("foregroundAppThreshold");
            foregroundAppThreshold.setAccessible(true);
            deviceInfo.MemoryInfo.put("foregroundAppThreshold",foregroundAppThreshold.get(memoryInfo));

            @SuppressLint("SoonBlockedPrivateApi") Field hiddenAppThreshold = clz.getDeclaredField("hiddenAppThreshold");
            hiddenAppThreshold.setAccessible(true);
            deviceInfo.MemoryInfo.put("hiddenAppThreshold",hiddenAppThreshold.get(memoryInfo));

            @SuppressLint("SoonBlockedPrivateApi") Field secondaryServerThreshold = clz.getDeclaredField("secondaryServerThreshold");
            secondaryServerThreshold.setAccessible(true);
            deviceInfo.MemoryInfo.put("secondaryServerThreshold",secondaryServerThreshold.get(memoryInfo));

            @SuppressLint("SoonBlockedPrivateApi") Field visibleAppThreshold = clz.getDeclaredField("visibleAppThreshold");
            visibleAppThreshold.setAccessible(true);
            deviceInfo.MemoryInfo.put("visibleAppThreshold",visibleAppThreshold.get(memoryInfo));
        } catch (NoSuchFieldException |IllegalAccessException| JSONException e) {
            e.printStackTrace();
        }

    }

}
