package com.ashenone.dfp.collector;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.ashenone.dfp.DeviceInfo;
import com.ashenone.dfp.util.ReflactUtil;

import javax.microedition.khronos.opengles.GL10;

public class GpuInfoCollector {
    private static GpuInfoCollector INSTANCE;
    private GL10 gl10;
    private Context ctx;

    private GpuInfoCollector(Context ctx){
        this.ctx = ctx;
    }

    public static GpuInfoCollector getInstance(Context ctx) {
        if(INSTANCE == null){
            INSTANCE = new GpuInfoCollector(ctx);
        }
        return INSTANCE;
    }

    public void getGpuInfo(){
        DeviceInfo deviceInfo = DeviceInfo.getInstance();
        try{
            String gpuRenderer = gl10.glGetString(GL10.GL_RENDERER);
            String gpuVendor = gl10.glGetString(GL10.GL_VENDOR);

            deviceInfo.GpuInfo.put("gpuRenderer", gpuRenderer);
            deviceInfo.GpuInfo.put("gpuVendor", gpuVendor);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
