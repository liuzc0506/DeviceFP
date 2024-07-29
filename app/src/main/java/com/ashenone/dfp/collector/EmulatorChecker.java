package com.ashenone.dfp.collector;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.ashenone.dfp.util.ReadUtil;

import java.io.File;
import java.io.FileInputStream;
import java.util.Locale;

public class EmulatorChecker {
    private static EmulatorChecker INSTANCE;
    private String[] firmware = new String[]{"/dev/socket/qemud", "/dev/qemu_pipe"};
    private String[] hardware = new String[]{"goldfish"};
    private String[] firmware2 = new String[]{"/sys/qemu_trace", "/system/bin/qemu-props"};
    private String[] deviceID = new String[]{"000000000000000"};
    private String[] unknown1 = new String[]{"310260000000000"};
    private String[] unknown2;
    private Context ctx;

    private int isTrue(boolean arg2) {
        return arg2 ? 1 : 0;
    }


    private EmulatorChecker(Context ctx){
        this.ctx = ctx;
    }

    public static EmulatorChecker getInstance(Context ctx) {
        if(EmulatorChecker.INSTANCE == null) {
            Class v1 = EmulatorChecker.class;
            synchronized(v1) {
                if(EmulatorChecker.INSTANCE == null) {
                    EmulatorChecker.INSTANCE = new EmulatorChecker(ctx);
                }

                return EmulatorChecker.INSTANCE;
            }
        }
        return EmulatorChecker.INSTANCE;
    }

    private boolean isFirmwareExists() {
        try {
            for(String path:this.firmware){
                if(new File(path).exists())return true;
            }
        }
        catch(Exception v1_1) {
        }
        return false;
    }

    private boolean isFirmware2Exists() {
        try {
            for(String path:this.firmware2){
                if(new File(path).exists())return true;
            }
        }
        catch(Exception v1_1) {
        }
        return false;
    }

    private boolean isGoldfishExist() {

        FileInputStream fis = null;
        byte[] buffer;
        File file;
        try {
            file = new File("/proc/tty/drivers");
            if(!file.exists() || !file.canRead()) {
                return false;
            }

            buffer = new byte[((int)file.length())];
        }
        catch(Throwable v0) {
            return false;
        }
        try {
            fis = new FileInputStream(file);
        }
        catch(Exception v2) {
        }
        try {
            fis.read(buffer);
            String str = new String(buffer);
            for(String key:this.hardware){
                if(str.indexOf(key)>0){
                    ReadUtil.close(fis);
                    return true;
                }
            }
        }
        catch(Exception v0_2) {
        }

        ReadUtil.close(fis);
        return false;
    }

    private boolean isEmptyDeviceID() {
        try {
            for(String id:this.deviceID) {
                boolean v5 = id.equalsIgnoreCase("");
                if(v5) {
                    return true;
                }
            }
        }
        catch(Exception v1) {
            return false;
        }
        return false;
    }

    private boolean BuildPropCheck() {
        return ("unknown".equals(Build.BOARD)) ||
                ("unknown".equals(Build.BOOTLOADER)) ||
                ("generic".equals(Build.BRAND)) ||
                ("generic".equals(Build.DEVICE)) ||
                ("sdk".equals(Build.MODEL)) ||
                ("sdk".equals(Build.PRODUCT)) ||
                ("goldfish".equals(Build.HARDWARE));
    }

    private boolean SimOperatorCheck() {
        return SimInfoCollector.getInstance(ctx).getSimOperator().equals("android");
    }

    public String isEmulatorResult() {
        return String.format(Locale.CHINA,
                "%d%d%d%d%d%d%d",
                ((int)this.isTrue(this.isFirmwareExists())),
                ((int)this.isTrue(this.isGoldfishExist())),
                ((int)this.isTrue(this.isFirmware2Exists())),
                ((int)this.isTrue(false)),
                ((int)this.isTrue(this.isEmptyDeviceID())),
                ((int)this.isTrue(this.BuildPropCheck())),
                ((int)this.isTrue(this.SimOperatorCheck())));
    }
}
