package com.ashenone.dfp.collector;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.ashenone.dfp.util.ReadUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Locale;

public class VirtualAppCheck {

    private Context ctx;
    private static VirtualAppCheck INSTANCE;

    private VirtualAppCheck(Context ctx) {
        this.ctx = ctx;
    }

    public static VirtualAppCheck getInstance(Context ctx) {
        if(VirtualAppCheck.INSTANCE == null) {
            Class v1 = VirtualAppCheck.class;
            synchronized(v1) {
                if(VirtualAppCheck.INSTANCE == null) {
                    VirtualAppCheck.INSTANCE = new VirtualAppCheck(ctx);
                }

                return VirtualAppCheck.INSTANCE;
            }
        }
        return VirtualAppCheck.INSTANCE;
    }

    private int isTrue(boolean arg2) {
        return arg2 ? 1 : 0;
    }

    public String getVirtualAppInfo() {
        String result = "";
        int isVirtualApp = 1;
        try {
            String pw_name = this.get_pw_name();
            if(!TextUtils.isEmpty(pw_name)) {
                String psResult = this.execShell("ps");
                if(TextUtils.isEmpty(psResult)) {
                    return result;
                }
                String[] lines = psResult.split("\n");
                if(lines.length <= 0) {
                    return result;
                }
                ArrayList array = new ArrayList();
                int count = 0;
                for(String line:lines) {
                    if(line.contains(pw_name)) {
                        int blankIndex = line.lastIndexOf(" ");
                        String pkgName = line.substring((blankIndex > 0 ? blankIndex + 1 : 0));
                        if(!TextUtils.isEmpty(pkgName) && (new File(String.format("/data/data/%s", new Object[]{pkgName})).exists())) {
                            array.add(pkgName);
                            ++count;
                        }
                    }
                }

                Locale CHINA = Locale.CHINA;
                Object[] v6_1 = new Object[1];
                if(count <= 1) {
                    isVirtualApp = 0;
                }

                StringBuilder sb = new StringBuilder();
                v6_1[0] = isVirtualApp;
                sb.append("IsVirtualApp: " + String.format(CHINA, "%d", v6_1));
                sb.append(" , SelfPwName: " + pw_name);
                sb.append(" , AppsCountOfPwName: " + String.format(Locale.CHINA, "%d", ((int)count)));
                sb.append(" , AppListOfPwName: " + array.toString());
                result = sb.toString();
            }
        }
        catch(Exception v0) {
        }
        return result;
    }

    private String get_pw_name() {
        int uid = 0;
        try {
            String cgroup = this.execShell("cat /proc/self/cgroup");
            if(!TextUtils.isEmpty(cgroup)) {
                int v3 = cgroup.lastIndexOf("uid");
                int v1_1 = cgroup.lastIndexOf("/pid");
                if(v3 >= 0) {
                    if(v1_1 <= 0) {
                        v1_1 = cgroup.length();
                    }

                    String str = cgroup.substring(v3 + 4, v1_1).replaceAll("\n", "");
                    if(this.checkValidDigit(str)) {
                        uid = (int)Integer.valueOf(str);
                    }
                }
            }
        }
        catch(Exception v1) {
        }

        if(uid == 0) {
            if(ctx != null) {
                uid = ctx.getApplicationInfo().uid;
            }
        }

        return uid == 0 ? null : this.get_pw_name(uid);
    }

    private String get_pw_name(int uid) {
        if(Build.VERSION.SDK_INT > 27) {
            return String.format(Locale.CHINA, "u0_a%d", ((int)(uid - 10000)));
        }

        try {
            Field v1 = Class.forName("libcore.io.Libcore").getDeclaredField("os");
            if(!v1.isAccessible()) {
                v1.setAccessible(true);
            }

            Object v1_1 = v1.get(null);
            if(v1_1 != null) {
                Method getpwuid = v1_1.getClass().getMethod("getpwuid", Integer.TYPE);
                if(getpwuid != null) {
                    if(!getpwuid.isAccessible()) {
                        getpwuid.setAccessible(true);
                    }

                    Object v1_2 = getpwuid.invoke(v1_1, ((int)uid));
                    if(v1_2 != null) {
                        Field v0_1 = v1_2.getClass().getDeclaredField("pw_name");
                        if(!v0_1.isAccessible()) {
                            v0_1.setAccessible(true);
                        }

                        return (String)v0_1.get(v1_2);
                    }
                }
            }
        }
        catch(Exception v0) {
            return String.format(Locale.CHINA, "u0_a%d", ((int)(uid - 10000)));
        }

        return null;
    }

    private boolean checkValidDigit(String arg4) {
        if(arg4 == null || arg4.length() == 0) {
            return false;
        }

        int v0 = 0;
        while(v0 < arg4.length()) {
            if(Character.isDigit(((char)arg4.charAt(v0)))) {
                ++v0;
                continue;
            }

            return false;
        }

        return true;
    }

    private String execShell(String path) {
        BufferedInputStream bis = null;
        Process process = null;
        String output = null;
        try {
            process = Runtime.getRuntime().exec(path);
            bis = new BufferedInputStream(process.getInputStream());
            process.waitFor();
            output = this.readall(bis);
        }
        catch(Exception v3) {
            if(bis != null) {
                ReadUtil.close(bis);
            }
            if(process != null) {
                process.destroy();
                return null;
            }

            return output;
        }
        catch(Throwable v0_1) {
            Process v4 = process;
            if(bis != null) {
                ReadUtil.close(bis);
            }

            if(v4 != null) {
                v4.destroy();
            }

            throw v0_1;
        }

        if(bis != null) {
            ReadUtil.close(bis);
        }

        if(process != null) {
            process.destroy();
        }

        return output;
    }

    private String readall(BufferedInputStream bis) {
        if(bis == null) {
            return "";
        }

        byte[] buffer = new byte[0x200];
        StringBuilder v2 = new StringBuilder();
        try {
            do {
                int len = bis.read(buffer);
                if(len > 0) {
                    v2.append(new String(buffer, 0, len));
                }else break;
            }
            while(true);
        }
        catch(Exception v0) {
            v0.printStackTrace();
            return v2.toString();
        }

        return v2.toString();
    }

}
