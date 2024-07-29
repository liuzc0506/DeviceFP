package com.ashenone.dfp.collector;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

import com.ashenone.dfp.Common;
import com.ashenone.dfp.DeviceInfo;

import org.json.JSONException;

public class SettingsCollector {

    private Context ctx;
    private static SettingsCollector INSTANCE;

    private SettingsCollector(Context ctx){
        this.ctx = ctx;
    }

    public static SettingsCollector getInstance(Context ctx){
        if(INSTANCE == null){
            INSTANCE = new SettingsCollector(ctx);
        }
        return INSTANCE;
    }


    public void getSettings(){
        ContentResolver contentResolver = ctx.getContentResolver();
        DeviceInfo deviceInfo = DeviceInfo.getInstance();

        for(String key: Common.setting_global_keys){
            try {
                deviceInfo.SettingInfo.put(key, Settings.Global.getString(contentResolver, key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for(String key: Common.setting_secure_keys){
            try {
                deviceInfo.SettingInfo.put(key, Settings.Secure.getString(contentResolver, key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for(String key: Common.setting_system_keys){
            try {
                deviceInfo.SettingInfo.put(key, Settings.System.getString(contentResolver, key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
