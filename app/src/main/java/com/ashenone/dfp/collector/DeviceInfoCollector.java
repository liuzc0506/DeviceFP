package com.ashenone.dfp.collector;

import android.bluetooth.BluetoothAdapter;
import android.os.IBinder;
import android.os.Parcel;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.accessibility.AccessibilityManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Base64;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import dalvik.system.BaseDexClassLoader;

public class DeviceInfoCollector {
    private static DeviceInfoCollector INSTANCE = null;
    private Context ctx;

    private DeviceInfoCollector(Context ctx){
        this.ctx = ctx;
    }

    public static DeviceInfoCollector getInstance(Context ctx){
        if(DeviceInfoCollector.INSTANCE == null) {
            Class v1 = DeviceInfoCollector.class;
            synchronized(v1) {
                if(DeviceInfoCollector.INSTANCE == null) {
                    DeviceInfoCollector.INSTANCE = new DeviceInfoCollector(ctx);
                }
            }
        }
        return DeviceInfoCollector.INSTANCE;
    }

    public String getLocationAndPhoneServiceName() {
        StringBuilder data = new StringBuilder();
        try {
            Method getService = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
            getService.setAccessible(true);
            Object LocationService = getService.invoke(null, "location");
            Object PhoneService = getService.invoke(null, "phone");
            data.append("locateServiceName:").append(LocationService.getClass().getName()).append("|");
            data.append("phoneServiceName:").append(PhoneService.getClass().getName());
        }
        catch(Throwable v1) {
        }

        return data.toString();
    }

    private String deviceUniqueId(Constructor MediaDrm, Method getPropertyByteArray, Object UUID) {
        try {
            return Base64.encodeToString(((byte[])getPropertyByteArray.invoke(MediaDrm.newInstance(UUID), "deviceUniqueId")), 2);
        }
        catch(Throwable v0) {
            return "";
        }
    }

    public String getDeviceUniqueIdFromDrm() {
        if(Build.VERSION.SDK_INT < 23) {
            return "";
        }

        StringBuilder data = new StringBuilder();
        try {
            Class MediaDrm = Class.forName("android.media.MediaDrm");
            Constructor UUIDConstructor = UUID.class.getConstructor(Long.TYPE, Long.TYPE);
            Constructor MediaDrmConstructor = MediaDrm.getConstructor(UUID.class);
            Method getPropertyByteArray = MediaDrm.getMethod("getPropertyByteArray", String.class);
            data.append(this.deviceUniqueId(MediaDrmConstructor, getPropertyByteArray, UUIDConstructor.newInstance(((long)0xEDEF8BA979D64ACEL), ((long)-6645017420763422227L)))).append("_");
            data.append(this.deviceUniqueId(MediaDrmConstructor, getPropertyByteArray, UUIDConstructor.newInstance(((long)0x1077EFECC0B24D02L), ((long)0xACE33C1E52E2FB4BL)))).append("_");
            data.append(this.deviceUniqueId(MediaDrmConstructor, getPropertyByteArray, UUIDConstructor.newInstance(((long)0xE2719D58A985B3C9L), ((long)0x781AB030AF78D30EL)))).append("_");
            data.append(this.deviceUniqueId(MediaDrmConstructor, getPropertyByteArray, UUIDConstructor.newInstance(((long)0x9A04F07998404286L), ((long)0xAB92E65BE0885F95L))));
        }
        catch(Throwable v1) {
        }

        return data.toString();
    }

    public String checkPremisions() {
        if(ctx == null) {
            return "";
        }

        if(Build.VERSION.SDK_INT >= 23) {
            Locale CHINA = Locale.CHINA;
            Object[] v6 = new Object[]{
                    ctx.checkSelfPermission("android.permission.READ_PHONE_STATE") == PackageManager.PERMISSION_GRANTED ? 1 : 0,
                    ctx.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED ? 1 : 0,
                    ctx.checkSelfPermission("android.permission.WRITE_SETTINGS") == PackageManager.PERMISSION_GRANTED ? 1 : 0,
                    ctx.checkSelfPermission("android.permission.ACCESS_WIFI_STATE") == PackageManager.PERMISSION_GRANTED ? 1 : 0,
                    ctx.checkSelfPermission("android.permission.ACCESS_NETWORK_STATE") == PackageManager.PERMISSION_GRANTED ? 1 : 0,
                    ctx.checkSelfPermission("android.permission.ACCESS_FINE_LOCATION") == PackageManager.PERMISSION_GRANTED ? 1 : 0,
                    ctx.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == PackageManager.PERMISSION_GRANTED ? 1 : 0,
            };
            return String.format(CHINA, "%d%d%d%d%d%d%d", v6);
        }

        return "1111111";
    }

    public boolean hasXposed(String arg4) {
        try {
            ClassLoader v1 = ClassLoader.getSystemClassLoader();
            if(!this.checkXposed(v1, arg4) && !this.checkXposed(v1.getParent(), arg4)) {
                ClassLoader v1_1 = this.getClass().getClassLoader();
                if(this.checkXposed(v1_1, arg4)) {
                    return true;
                }

                boolean v1_2 = this.checkXposed(v1_1.getParent(), arg4);
                return v1_2;
            }

            return true;
        }
        catch(Exception v0) {
            return false;
        }
    }

    public boolean hasXposed() {
        try {
            return this.hasXposed("XposedBridge.jar");
        }
        catch(Exception v0) {
            return false;
        }
    }

    public int is_mock_location_enable() {
        if(ctx == null) {
            return 0;
        }
        return Settings.Secure.getInt(ctx.getContentResolver(), "mock_location", 0) == 0 ? 0 : 1;
    }

    @SuppressLint("DiscouragedPrivateApi")
    private boolean checkXposed(ClassLoader classloader, String keyword) {
        if(classloader == null || !(classloader instanceof BaseDexClassLoader)) {
            return false;
        }

        try {
            Class DexPathList = Class.forName("dalvik.system.DexPathList");
            Method toString = Class.forName("dalvik.system.DexPathList$Element").getMethod("toString", (Class<?>) null);
            Field dexElements = DexPathList.getDeclaredField("dexElements");
            dexElements.setAccessible(true);
            Field pathList = BaseDexClassLoader.class.getDeclaredField("pathList");
            pathList.setAccessible(true);
            Object[] v0_2 = (Object[])dexElements.get(pathList.get(classloader));

            for(int index = 0; index<v0_2.length; index++) {

                String v1_2 = (String)toString.invoke(v0_2[index], new Object());
                if(v1_2 != null) {
                    boolean v1_3 = v1_2.contains(keyword);
                    if(v1_3) {
                        return true;
                    }
                }
            }
        }
        catch(Throwable v0) {
            return false;
        }
        return false;
    }

    public long getBootTime() {
        try {
            ArrayList array = new ArrayList();
            int index;
            for(index = 0; index < 11; ++index) {
                array.add(System.currentTimeMillis() - SystemClock.elapsedRealtime());
            }

            Collections.sort(array);
            return (long)(((Long)array.get(5)));
        }
        catch(Exception v0) {
            return System.currentTimeMillis() - SystemClock.elapsedRealtime();
        }
    }

    public Map getAccessibilityInfo() {
        HashMap result = new HashMap();
        AccessibilityManager accessibilityService = (AccessibilityManager) this.ctx.getSystemService(Context.ACCESSIBILITY_SERVICE);
        Method isEnabled = null;
        try {
            isEnabled = accessibilityService.getClass().getDeclaredMethod("isEnabled");

            Method v4 = accessibilityService.getClass().getDeclaredMethod("getEnabledAccessibilityServiceList", Integer.TYPE);
            Object isEnabledObj = isEnabled.invoke(accessibilityService);
            ArrayList arrayList = new ArrayList();
            for(Object v6: ((List) Objects.requireNonNull(v4.invoke(accessibilityService, ((int) -1))))) {
                Object v1_1 = v6.getClass().getDeclaredMethod("getId").invoke(v6);
                if(v1_1 == null) {
                    Object v1_2 = v6.getClass().getDeclaredMethod("getResolveInfo").invoke(v6);
                    arrayList.add((v1_2 == null ? v6.toString() : v1_2.toString()));
                    continue;
                }

                arrayList.add(((String)v1_1));

            }
            result.put("enable", (((Boolean)isEnabledObj).booleanValue() ? "1" : "0"));
            result.put("service", arrayList);
            result.put("suc", "1");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public List getInputMethonds() {
        ArrayList v1 = new ArrayList();
        try {
            if(ctx == null) {
                return v1;
            }

            InputMethodManager v0_2 = (InputMethodManager)ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
            if(v0_2 == null) {
                return v1;
            }

            List v0_3 = v0_2.getInputMethodList();
            if(v0_3 == null) {
                return v1;
            }

            for(Object v0_4: v0_3) {
                v1.add(((InputMethodInfo)v0_4).toString());
            }
        }
        catch(Exception v0) {
        }

        return v1;
    }

    public Set getXposedCache() {
        HashSet result = new HashSet();
        try {
            Class xposed = ClassLoader.getSystemClassLoader().loadClass("de.robv.android.xposed.XposedHelpers");
            this.getFieldNames(xposed, "fieldCache", result);
            this.getFieldNames(xposed, "methodCache", result);
            this.getFieldNames(xposed, "constructorCache", result);
        }
        catch(Throwable v1) {
        }

        return result;
    }

    private void getFieldNames(Class cls, String fieldName, Set arg5) {
        try {
            Field field = cls.getDeclaredField(fieldName);
            field.setAccessible(true);
            arg5.addAll(((Map)field.get(null)).keySet());
        }
        catch(Throwable v0) {
        }
    }


    public Map xposed() {
        Object[] v2_4;
        HashSet v6_3;
        Class v8;
        Method v7_1;
        int v2_1 = 0;
        Field field;
        HashMap v3 = new HashMap();
        try {
            Field[] fields = ClassLoader.getSystemClassLoader().loadClass("de.robv.android.xposed.XposedBridge").getDeclaredFields();
            for(int index = 0;index < fields.length;index++) {
                field = fields[index];
                if(field == null) {
                    return v3;
                }
                field.setAccessible(true);
                if("sHookedMethodCallbacks".equals(field.getName())) {
                    v2_1 = 0;
                }
                if("hookedMethodCallbacks".equals(field.getName())) {
                    v2_1 = 1;
                }
                Map v1_1 = (Map)field.get(null);
                if(v2_1 == 0) {
                    Class clz = ClassLoader.getSystemClassLoader().loadClass("de.robv.android.xposed.XposedBridge$CopyOnWriteSortedSet");
                    Method getSnapshot = clz.getDeclaredMethod("getSnapshot");
                    getSnapshot.setAccessible(true);
                    v7_1 = getSnapshot;
                    v8 = clz;
                }
                else {
                    v7_1 = null;
                    v8 = null;
                }
                Iterator v9 = v1_1.entrySet().iterator();
                label_61:
                while(v9.hasNext()) {
                    Object v2_3 = v9.next();
                    String v6_2 = ((Map.Entry)v2_3).getKey().toString();
                    HashSet v1_2 = (HashSet) v3.get(v6_2);
                    if(v1_2 == null) {
                        HashSet v1_3 = new HashSet();
                        v3.put(v6_2, v1_3);
                        v6_3 = v1_3;
                    }
                    else {
                        v6_3 = v1_2;
                    }

                    Object v1_4 = ((Map.Entry)v2_3).getValue();
                    if(v8 != null && (v8.isInstance(v1_4))) {
                        v2_4 = (Object[])v7_1.invoke(v1_4);
                    }
                    else if(TreeSet.class.isInstance(v1_4)) {
                        Object[] v1_5 = ((TreeSet)v1_4).toArray();
                        v2_4 = v1_5;
                    }
                    else {
                        v2_4 = null;
                    }

                    if(v2_4 == null) {
                        continue;
                    }

                    int v10 = v2_4.length;
                    int v1_6 = 0;
                    while(true) {
                        if(v1_6 >= v10) {
                            continue label_61;
                        }

                        v6_3.add(v2_4[v1_6].getClass().getName());
                        ++v1_6;
                    }
                }
            }
        }catch(Exception e) {
        }

        return v3;
    }
}
