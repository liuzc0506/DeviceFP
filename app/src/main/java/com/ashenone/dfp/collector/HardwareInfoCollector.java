package com.ashenone.dfp.collector;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.util.Log;

import com.ashenone.dfp.util.ReadUtil;
import com.ashenone.dfp.util.ReflactUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

public class HardwareInfoCollector {
    private static HardwareInfoCollector INSTANCE = null;
    private String model_name;
    private String vendor_id;
    private Context ctx;
    private final FileFilter fileFilter;

    private HardwareInfoCollector(Context ctx){
        this.model_name = "";
        this.vendor_id = "";
        this.ctx = ctx;
        this.fileFilter = new FileFilter() {
            @Override
            public boolean accept(File arg5) {
                String name = arg5.getName();
                if(name.startsWith("cpu")) {
                    int index;
                    for(index = 3; index < name.length(); ++index) {
                        if(!Character.isDigit(((char)name.charAt(index)))) {
                            return false;
                        }
                    }
                    return true;
                }
                return false;
            }
        };
        initCpuInfo();
    }

    public String getExternalStorageInfo() {
        try {
            if(Build.VERSION.SDK_INT >= 18) {
                StatFs mStatFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
                StringBuilder sb = new StringBuilder();
                sb.append("AvailableBytes: "+ mStatFs.getAvailableBytes());
                sb.append(", FreeBytes: "+ mStatFs.getFreeBytes());
                sb.append(", TotalBytes: "+ mStatFs.getTotalBytes());
                return sb.toString();
            }
        }
        catch(Exception v0) {
        }
        return "";
    }

    public String getScreenResolutionAndDpi() {
        if(ctx == null) {
            return "";
        }

        try {
            DisplayMetrics v0_2 = ctx.getResources().getDisplayMetrics();
            return String.format(Locale.CHINA, "%d,%d,%d", ((int)v0_2.widthPixels), ((int)v0_2.heightPixels), ((int)v0_2.densityDpi));
        }
        catch(Exception v0_1) {
            return "0,0,0";
        }
    }

    public int get_screen_brightness() {
        if(this.ctx == null) {
            return -1;
        }

        try {
            Object[] screen_brightness = {this.ctx.getContentResolver(), "screen_brightness"};
            return (int)(((Integer) ReflactUtil.invoke("android.provider.Settings$System", "getInt", new Class[]{ContentResolver.class, String.class}, screen_brightness)));
        }
        catch(SecurityException v0_1) {
            return -1001;
        }
        catch(Exception v0) {
            return -1;
        }
    }

    public static HardwareInfoCollector getInstance(Context ctx){
        if(HardwareInfoCollector.INSTANCE == null) {
            Class v1 = HardwareInfoCollector.class;
            synchronized(v1) {
                if(HardwareInfoCollector.INSTANCE == null) {
                    HardwareInfoCollector.INSTANCE = new HardwareInfoCollector(ctx);
                }
            }
        }
        return INSTANCE;
    }

    private int readFile(String path) {
        int v0_2 = -1;
        FileInputStream fis = null;
        BufferedReader br = null;
        try {
            fis = new FileInputStream(path);
            br = new BufferedReader(new InputStreamReader(fis));
            v0_2 = this.matchs(br.readLine());
        }catch (Exception e){}
        finally {
            if(fis != null) {
                try {
                    fis.close();
                }
                catch(Exception v0) {
                }
            }
            if(br != null) {
                try {
                    br.close();
                }
                catch(Exception v0) {
                }
            }
        }
        return v0_2;
    }

    private int matchs(String arg2) {
        return arg2 != null && (arg2.matches("0-[\\d]+$")) ? ((int)Integer.valueOf(arg2.substring(2))) + 1 : -1;
    }

    public int getCpuCount() {
        int v0_2;
        if(Build.VERSION.SDK_INT <= 10) {
            return 1;
        }

        try {
            v0_2 = this.readFile("/sys/devices/system/cpu/possible");
            if(v0_2 == -1) {
                v0_2 = this.readFile("/sys/devices/system/cpu/present");
            }

            if(v0_2 == -1) {
                return this.getCpuCount2();
            }
        }
        catch(SecurityException v0_1) {
            return -1;
        }
        catch(Exception v0) {
            return -1;
        }

        return v0_2;
    }

    private void initCpuInfo() {
        try {
            for(Object line: ReadUtil.readLines("/proc/cpuinfo")) {
                String[] v0_2 = ((String)line).split(":");
                if(2 != v0_2.length) {
                    continue;
                }

                String key = v0_2[0].trim();
                String value = v0_2[1].trim();
                if(("hardware".equals(key)) || ("vendor_id".equals(key))) {
                    this.vendor_id = value;
                    continue;
                }

                if(!"Processor".equals(key) && !"model name".equals(key)) {
                    continue;
                }

                this.model_name = value;
            }
            return;
        }
        catch(Exception v0) {
        }
    }

    public String getCPUModelName() {
        return this.model_name;
    }

    public String get_vendor_id() {
        return this.vendor_id;
    }

    public long getTotalMem() {
        int v0_2;
        FileInputStream fis;
        if(this.ctx != null) {

            if(Build.VERSION.SDK_INT >= 16) {

                try {
                    ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
                    ((ActivityManager)this.ctx.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryInfo(memoryInfo);

                    return memoryInfo.totalMem;
                }
                catch(Exception v0) {
                    return 0L;
                }

            }

            try {
                fis = new FileInputStream("/proc/meminfo");
                v0_2 = this.readInt("MemTotal", fis);
            }
            catch(Throwable v0_1) {
                try {
                    ReadUtil.close(null);
                    throw v0_1;
                }
                catch(Exception v0_3) {
                    return -1L;
                }
            }

            long result = ((long)v0_2) * 0x400L;
            try {
                ReadUtil.close(fis);
            }
            catch(Exception v2) {
            }

            return result;
        }

        return 0L;
    }


    public int getCPUMHz() {
        int cpuMHz;
        FileInputStream fis1 = null;
        FileInputStream fis2 = null;
        byte[] v6;
        int result = -1;
        try {
            for(int cpuIndex =0;cpuIndex<this.getCpuCount();cpuIndex++){
                File cpuinfo_max_freq = new File("/sys/devices/system/cpu/cpu" + cpuIndex + "/cpufreq/cpuinfo_max_freq");
                if(cpuinfo_max_freq.exists()) {
                    v6 = new byte[0x80];
                    fis2 = new FileInputStream(cpuinfo_max_freq);
                    try {
                        fis2.read(v6);
                        int index;
                        for(index = 0; (Character.isDigit(v6[index])) && index < v6.length; ++index) {
                        }

                        Integer hz = (int)Integer.parseInt(new String(v6, 0, index));
                        if(((int)hz) > result) {
                            result = (int)hz;
                        }
                    }
                    catch(NumberFormatException v2) {}
                    catch(Throwable v0_2) {}
                }
            }
            if(result != -1) {
                return result;
            }
            fis1 = new FileInputStream("/proc/cpuinfo");
            cpuMHz = this.readInt("cpu MHz", fis1);
            result = cpuMHz * 1000;
            return result;
        }
        catch(Exception v0_1) {
            return -1;
        }finally {
            ReadUtil.close(fis2);
            ReadUtil.close(fis1);
        }


    }

    private int readInt(String key, FileInputStream fis) {
        byte[] buffer = new byte[0x400];
        try {
            int len = fis.read(buffer);
            int index;
            for(index = 0; index < len; ++index) {
                if(buffer[index] == 10 || index == 0) {
                    if(buffer[index] == 10) {
                        ++index;
                    }

                    int index2;
                    for(index2 = index; index2 < len; ++index2) {
                        int v4 = index2 - index;
                        if(buffer[index2] != key.charAt(v4)) {
                            break;
                        }

                        if(v4 == key.length() - 1) {
                            return this.readInt(buffer, index2);
                        }
                    }
                }
            }
        }
        catch(IOException v0_1) {
            return -1;
        }
        catch(NumberFormatException v0) {
        }

        return -1;
    }

    private int readInt(byte[] buffer, int index) {
        while(index < buffer.length && buffer[index] != 10) {
            if(Character.isDigit(buffer[index])) {
                int v0;
                for(v0 = index + 1; v0 < buffer.length && (Character.isDigit(buffer[v0])); ++v0) {
                }

                return Integer.parseInt(new String(buffer, 0, index, v0 - index));
            }

            ++index;
        }

        return -1;
    }

    private int getCpuCount2() {
        return new File("/sys/devices/system/cpu/possible").listFiles(this.fileFilter).length;
    }
}
