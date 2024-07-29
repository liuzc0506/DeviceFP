package com.ashenone.dfp.collector;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import com.ashenone.dfp.DeviceInfo;

import java.util.HashMap;
import java.util.Map;

public class BatteryInfoCollector {
    public static void getBatteryInfo(Context ctx) {
        DeviceInfo deviceInfo = DeviceInfo.getInstance();

        if(ctx != null) {
            try {
                Intent v1_2 = ctx.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
                if(v1_2 != null) {
                    Log.d("TEST-battery", String.valueOf(v1_2));

                    int v2 = v1_2.getIntExtra("status", 0);
                    int plugged = v1_2.getIntExtra("plugged", 0);
                    int health = v1_2.getIntExtra("health", 1);
                    int v3 = v1_2.getIntExtra("level", 0);
                    int v4 = v1_2.getIntExtra("scale", 100);
                    int v5 = v1_2.getIntExtra("temperature", 0);
                    int v1_3 = v1_2.getIntExtra("voltage", 0);

                    //新增电池信息
                    boolean present = v1_2.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);
                    String tech = v1_2.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
                    boolean battery_low = v1_2.getBooleanExtra(BatteryManager.EXTRA_BATTERY_LOW, false);


                    deviceInfo.BatteryInfo.put("status", Integer.valueOf(v2));
                    deviceInfo.BatteryInfo.put("plugged", Integer.valueOf(plugged));
                    deviceInfo.BatteryInfo.put("health", Integer.valueOf(health));
                    deviceInfo.BatteryInfo.put("level", Integer.valueOf(v3));
                    deviceInfo.BatteryInfo.put("scale", Integer.valueOf(v4));
                    deviceInfo.BatteryInfo.put("temperature", Integer.valueOf(v5));
                    deviceInfo.BatteryInfo.put("voltage", Integer.valueOf(v1_3));

                    //
                    deviceInfo.BatteryInfo.put(BatteryManager.EXTRA_PRESENT, Boolean.valueOf(present));
                    deviceInfo.BatteryInfo.put(BatteryManager.EXTRA_TECHNOLOGY, tech);
                    deviceInfo.BatteryInfo.put(BatteryManager.EXTRA_BATTERY_LOW, battery_low);


                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
