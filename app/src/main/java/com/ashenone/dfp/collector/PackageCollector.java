package com.ashenone.dfp.collector;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import com.ashenone.dfp.DeviceInfo;
import com.ashenone.dfp.util.ReflactUtil;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import android.os.Process;

import org.json.JSONException;

public class PackageCollector {

    private Context ctx;
    private static PackageCollector INSTANCE;

    private PackageCollector(Context ctx){
        this.ctx = ctx;
    }

    public static PackageCollector getInstance(Context ctx){
        if(INSTANCE==null){
            INSTANCE = new PackageCollector(ctx);
        }
        return INSTANCE;
    }

    public String getSelfInfo(){
        try {
            StringBuilder sb = new StringBuilder();
            Object packageManager = ReflactUtil.invoke(ctx, "getPackageManager");
            String PackageName = (String) ReflactUtil.invoke(ctx, "getPackageName");
            String versionName = ctx.getPackageManager().getPackageInfo(PackageName, 0).versionName;
            String loadLabel = (String)ReflactUtil.invoke(ReflactUtil.getValue(ReflactUtil.invoke(packageManager, "getPackageInfo", new Class[]{String.class, Integer.TYPE}, new Object[]{ctx.getPackageName(), ((int)0)}), "applicationInfo"), "loadLabel", new Class[]{PackageManager.class}, new Object[]{packageManager});
            int isDebuggable = isDebuggable(ctx);
            sb.append("PackageName : " + PackageName);
            sb.append(" , versionName : " + PackageName);
            sb.append(" , loadLabel : " + loadLabel);
            sb.append(" , isDebuggable : " + isDebuggable);

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getSubjectDN(){

        try {
            Object packageManager = ReflactUtil.invoke(ctx, "getPackageManager");
            Object pkgInfo = ReflactUtil.invoke(packageManager, "getPackageInfo", new Class[]{String.class, Integer.TYPE}, new Object[]{ctx.getPackageName(), ((int)0x40)});
            Object[] signatures = (Object[])ReflactUtil.getValue(pkgInfo, "signatures");
            byte[] signbytes = (byte[])ReflactUtil.invoke(signatures[0], "toByteArray");
            return ((X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(signbytes))).getSubjectDN().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public static int isDebuggable(Context ctx) {
        return (ctx.getApplicationInfo().flags & 2) <= 0 ? 0 : 1;
    }

    public List getInstalledApps(){
        ArrayList appInfoList = new ArrayList();
        Object packageManager = null;
        try {
            packageManager = ReflactUtil.invoke(ctx, "getPackageManager");

            List installedPackages = (List)ReflactUtil.invoke(packageManager, "getInstalledPackages", new Class[]{Integer.TYPE}, new Object[]{((int)0)});
            Collections.sort(installedPackages, new Comparator() {
                @Override
                public int compare(Object arg9, Object arg10) {
                    // 按照安装时间排序
                    Long firstInstallTime1 = (long)(-((PackageInfo)arg9).firstInstallTime);
                    Long firstInstallTime2 = (long)(-((PackageInfo)arg10).firstInstallTime);
                    int v3 = (((PackageInfo)arg9).applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0 && (((PackageInfo)arg9).applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) <= 0 ? 0 : 1;
                    int v2 = (((PackageInfo)arg10).applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0 && (((PackageInfo)arg10).applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) <= 0 ? 0 : 1;
                    if(v3 != 0 && v2 == 0) {
                        return 1;
                    }

                    return v3 != 0 || v2 == 0 ? firstInstallTime1.compareTo(firstInstallTime2) : -1;
                }
            });
            for(int index = 0; index < installedPackages.size();index++) {

                PackageInfo pkgInfo = (PackageInfo) installedPackages.get(index);
                String packageName = pkgInfo.packageName;
                ApplicationInfo applicationInfo = pkgInfo.applicationInfo;
                int appFlag = applicationInfo.flags;
                boolean isUsrApp = false;
                if((appFlag & 1) > 0 || (appFlag & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) > 0) {
                    isUsrApp = false;
                }else if((appFlag & 0x80) == 0) {
                    isUsrApp = true;
                }
                String loadLabel = applicationInfo.loadLabel(((PackageManager) packageManager)).toString();
                appInfoList.add("" + pkgInfo.firstInstallTime + "," + packageName + "," + loadLabel + "," + isUsrApp + "," + pkgInfo.versionCode + "," + pkgInfo.versionName + "," + pkgInfo.lastUpdateTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appInfoList;
    }

    public String getSelfProcName() {

        try {
            int selfPid = Process.myPid();
            if(this.ctx != null) {
                Iterator RunningAppProcesses = ((ActivityManager)this.ctx.getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses().iterator();
                while(RunningAppProcesses.hasNext()) {

                    Object running = RunningAppProcesses.next();
                    ActivityManager.RunningAppProcessInfo procInfo = (ActivityManager.RunningAppProcessInfo)running;
                    if(procInfo.pid == selfPid) {
                        return procInfo.processName;
                    }
                }
            }
        }
        catch(Exception v0) {
        }

        return "";

    }

    public void getLauncher(){
        DeviceInfo deviceInfo = DeviceInfo.getInstance();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        ResolveInfo resolveInfo = ctx.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        try {
            deviceInfo.PackageInfo.put("launcher",resolveInfo.activityInfo.packageName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
