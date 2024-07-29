package com.ashenone.dfp.collector;


import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.ashenone.dfp.Common;
import com.ashenone.dfp.DeviceInfo;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PropCollector {

    private Context ctx;
    private static PropCollector INSTANCE;

    private PropCollector(Context ctx){
        this.ctx = ctx;
    }

    public static PropCollector getInstance(Context ctx){
        if(INSTANCE == null){
            INSTANCE = new PropCollector(ctx);
        }
        return INSTANCE;
    }


    public void getBuildInfo(){

        DeviceInfo deviceInfo = DeviceInfo.getInstance();

        try {
            deviceInfo.BuildInfo.put("fake_enable", true);
            deviceInfo.VersionInfo.put("fake_enable", true);
            Class buildCls = Class.forName("android.os.Build");

            for(String target: Common.build_fields){
                try {
                    Field field = buildCls.getDeclaredField(target);
                    field.setAccessible(true);
                    if(field.getType()==String[].class){
                        List list = new ArrayList();
                        for(String s:(String[]) field.get(null)){
                            list.add(s);
                        }
                        deviceInfo.BuildInfo.put(target,list);
                    }else {
                        deviceInfo.BuildInfo.put(target,field.get(null));
                    }


                } catch (Exception e) {
//                    e.printStackTrace();
                }
            }

            Class versionClz = Class.forName("android.os.Build$VERSION");

            for(String target:Common.version_fields){
                try {
                    Field field = versionClz.getDeclaredField(target);
                    field.setAccessible(true);
                    if(field.getType()==String[].class){
                        List list = new ArrayList();
                        for(String s:(String[]) field.get(null)){
                            list.add(s);
                        }
                        deviceInfo.VersionInfo.put(target,list);
                    }else {
                        deviceInfo.VersionInfo.put(target,field.get(null));
                    }

                } catch (Exception e) {
//                    e.printStackTrace();
                }
            }

            deviceInfo.BuildInfo.put("radioVersion",Build.getRadioVersion());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                deviceInfo.BuildInfo.put("SERIAL",Build.getSerial());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getSystemProp(){
        DeviceInfo deviceInfo = DeviceInfo.getInstance();
        try {
            Class sysPropCls = Context.class.getClassLoader().loadClass("android.os.SystemProperties");
            Method get = sysPropCls.getMethod("get",String.class);
            get.setAccessible(true);
            for(String str:Common.prop_keys){
                String result = (String) get.invoke(sysPropCls,str);
                if(result != null && !result.isEmpty()) {
                    deviceInfo.SystemPropInfo.put(str,result);
                }
            }
            // ro.build.fingerprint长度超过100
            /*Method read_callback =  sysPropCls.getMethod("readCallback",String.class);
            read_callback.setAccessible(true);
            String result = (String) read_callback.invoke(sysPropCls,"ro.build.fingerprint");
            if(result != null && !result.isEmpty()) {
                deviceInfo.SystemPropInfo.put("ro.build.fingerprint",result);
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
