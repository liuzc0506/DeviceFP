package com.ashenone.dfp.collector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.text.TextUtils;
import android.util.Log;

import com.ashenone.dfp.DeviceInfo;
import com.ashenone.dfp.util.TaskExecutor;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SensorInfoCollector {

    private SensorManager mSensorManager;
    private Context ctx;
    private static SensorInfoCollector INSTANCE;

    private SensorInfoCollector(Context ctx) {
        this.mSensorManager = null;
        this.ctx = ctx;
        if(ctx != null) {
            this.mSensorManager = (SensorManager)ctx.getSystemService(Context.SENSOR_SERVICE);
        }
    }


    @SuppressLint("NewApi")
    public void getSensorList() {
        DeviceInfo deviceInfo = DeviceInfo.getInstance();
        try {
            for(Object item: this.mSensorManager.getSensorList(-1)) {
                Sensor sensor = (Sensor)item;
                JSONObject tmp = new JSONObject();
                tmp.put("name",sensor.getName());
                tmp.put("vendor",sensor.getVendor());
                tmp.put("version",sensor.getVersion());
                tmp.put("maxRange",sensor.getMaximumRange());
                tmp.put("resolution",sensor.getResolution());
                tmp.put("power",sensor.getPower());
                tmp.put("minDelay",sensor.getMinDelay());
                tmp.put("fifoReservedEventCount",sensor.getFifoReservedEventCount());
                tmp.put("fifoMaxEventCount",sensor.getFifoMaxEventCount());
                tmp.put("stringType",sensor.getStringType());
                tmp.put("maxDelay",sensor.getMaxDelay());
                tmp.put("id",sensor.getId());
                deviceInfo.SensorListInfo.put(sensor.getType()+"",tmp);
            }
        }
        catch(Exception v0) {
        }
    }

    public static SensorInfoCollector getInstance(Context ctx){
        if(SensorInfoCollector.INSTANCE == null) {
            Class v1 = SensorInfoCollector.class;
            synchronized(v1) {
                if(SensorInfoCollector.INSTANCE == null) {
                    SensorInfoCollector.INSTANCE = new SensorInfoCollector(ctx);
                }
            }
        }
        return INSTANCE;
    }

}
