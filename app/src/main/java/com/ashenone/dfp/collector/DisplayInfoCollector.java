package com.ashenone.dfp.collector;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.ashenone.dfp.DeviceInfo;

import org.json.JSONException;

import java.util.Locale;

public class DisplayInfoCollector {
    private Context ctx;
    private static DisplayInfoCollector INSTANCE;

    private DisplayInfoCollector(Context ctx){
        this.ctx = ctx;
    }

    public static DisplayInfoCollector getInstance(Context ctx){
        if(INSTANCE == null){
            INSTANCE = new DisplayInfoCollector(ctx);
        }
        return INSTANCE;
    }

    public void getDisplayInfo(){
        DeviceInfo deviceInfo = DeviceInfo.getInstance();
        Resources res = ctx.getResources();
        DisplayMetrics displayMetrics = res.getDisplayMetrics();

        try {
            deviceInfo.DisplayInfo.put("heightPixels",displayMetrics.heightPixels);
            deviceInfo.DisplayInfo.put("widthPixels",displayMetrics.widthPixels);
            deviceInfo.DisplayInfo.put("densityDpi",displayMetrics.densityDpi);
            deviceInfo.DisplayInfo.put("scaledDensity",displayMetrics.scaledDensity);
            deviceInfo.DisplayInfo.put("density",displayMetrics.density);
            deviceInfo.DisplayInfo.put("xdpi",displayMetrics.xdpi);
            deviceInfo.DisplayInfo.put("ydpi",displayMetrics.ydpi);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // add language
        try{
            Locale locale = res.getConfiguration().locale;
            String language = locale.getLanguage();
            deviceInfo.DisplayInfo.put("language",language);
        } catch (Exception e){
            e.printStackTrace();
        }

        // add fps
        try{
            WindowManager windowManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            int fps = (int) display.getMode().getRefreshRate();
            deviceInfo.DisplayInfo.put("fps",fps);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
