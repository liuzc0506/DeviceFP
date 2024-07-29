package com.ashenone.dfp.collector;

import com.ashenone.dfp.Common;
import com.ashenone.dfp.DeviceInfo;

import org.json.JSONException;

public class JavaSystemInfoCollector {
    public static void getProperties(){
        DeviceInfo deviceInfo = DeviceInfo.getInstance();

        for(String key: Common.java_system_props){
            try {
                deviceInfo.JavaSystemInfo.put(key,System.getProperty(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
