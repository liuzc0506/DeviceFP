package com.ashenone.dfp.collector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.ashenone.dfp.util.ReflactUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

public class SimInfoCollector {
    private static SimInfoCollector INSTANCE;
    private TelephonyManager telephonyManager;
    private Context ctx;
    private SimInfoCollector(Context ctx){
        this.ctx = ctx;
        try {
            this.telephonyManager = (TelephonyManager) ReflactUtil.invoke(ctx, "getSystemService", new Class[]{String.class}, new Object[]{"phone"});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SimInfoCollector getInstance(Context ctx){
        if(SimInfoCollector.INSTANCE == null) {
            Class v1 = SimInfoCollector.class;
            synchronized(v1) {
                if(SimInfoCollector.INSTANCE == null) {
                    SimInfoCollector.INSTANCE = new SimInfoCollector(ctx);
                }
            }
        }
        return INSTANCE;
    }


    public HashMap getCellLocation() {
        HashMap result = new HashMap();
        try {
            PackageManager pkgMng = this.ctx.getPackageManager();
            if(pkgMng.checkPermission("android.permission.ACCESS_FINE_LOCATION", this.ctx.getPackageName()) != PackageManager.PERMISSION_GRANTED &&
                    pkgMng.checkPermission("android.permission.ACCESS_COARSE_LOCATION", this.ctx.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return result;
            }

            CellLocation mCellLocation = this.getCellLocationData();
            if(mCellLocation == null && this.telephonyManager != null) {
                mCellLocation = (CellLocation)ReflactUtil.invoke(this.telephonyManager, "getCellLocation");
            }


            if(mCellLocation != null) {
                if((mCellLocation instanceof GsmCellLocation)) {
                    result.put("type", "gsm");
                    GsmCellLocation v0_1 = (GsmCellLocation)mCellLocation;
                    result.put("cid", String.valueOf(v0_1.getCid()));
                    result.put("lac", String.valueOf(v0_1.getLac()));
                    return result;
                }

                if((mCellLocation instanceof CdmaCellLocation)) {
                    result.put("type", "cdma");
                    CdmaCellLocation v0_2 = (CdmaCellLocation)mCellLocation;
                    result.put("bid", String.valueOf(v0_2.getBaseStationId()));
                    result.put("nid", String.valueOf(v0_2.getNetworkId()));
                    result.put("sid", String.valueOf(v0_2.getSystemId()));
                    result.put("lat", String.valueOf(v0_2.getBaseStationLatitude()));
                    result.put("lng", String.valueOf(v0_2.getBaseStationLongitude()));
                    return result;
                }
            }
        }
        catch(Exception v2) {
            result.put("type", "");
        }

        return result;
    }

    private int getTelephonyManagerType() {
        try {
            Class.forName("android.telephony.MSimTelephonyManager");
            return 1;
        }
        catch(Exception v0) {
            try {
                Class.forName("android.telephony.TelephonyManager2");
                return 2;
            }
            catch(SecurityException v0_2) {
                return -1001;
            }
            catch(Exception v0_1) {
                return 0;
            }
        }
    }

    private Object getSystemService(String arg7) {
        try {
            if(this.ctx != null) {
                return ReflactUtil.invoke(this.ctx.getApplicationContext(), "getSystemService", new Class[]{String.class}, new Object[]{arg7});
            }
        }
        catch(Exception v1) {
            return null;
        }

        return null;
    }

    private Object getPhoneService() {
        switch(this.getTelephonyManagerType()) {
            case 0: {
                return this.getSystemService("phone");
            }
            case 1: {
                return this.getSystemService("phone_msim");
            }
            case 2: {
                return this.getSystemService("phone2");
            }
            default: {
                return null;
            }
        }
    }

    private Class loadServiceClass() {
        String serviceName;
        ClassLoader clsLoader = ClassLoader.getSystemClassLoader();
        switch(this.getTelephonyManagerType()) {
            case 0: {
                serviceName = "android.telephony.TelephonyManager";
                break;
            }
            case 1: {
                serviceName = "android.telephony.MSimTelephonyManager";
                break;
            }
            case 2: {
                serviceName = "android.telephony.TelephonyManager2";
                break;
            }
            default: {
                serviceName = null;
            }
        }

        try {
            return clsLoader.loadClass(serviceName);
        }
        catch(Exception v0_1) {
            return null;
        }
    }

    public String getDeviceId(){
        if(this.telephonyManager!=null){
            try {
                return (String) ReflactUtil.invoke(this.telephonyManager, "getDeviceId");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public String getSimOperator() {
        String result = "";
        try {
            if(this.telephonyManager != null) {

                result = telephonyManager.getSimOperator();
                if(result == null) {
                    result = (String)ReflactUtil.invoke(this.telephonyManager, "getNetworkOperatorName");

                }

            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getSimSerialNumber() {
        String result = "";
        try {
            if(this.telephonyManager != null) {
                result = (String)ReflactUtil.invoke(this.telephonyManager, "getSimSerialNumber");
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getSubscriberId() {
        String result = "";
        try {
            if(this.telephonyManager != null) {
                result = (String)ReflactUtil.invoke(this.telephonyManager, "getSubscriberId");
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private Object invoke(Object instance, String methodName, Object[] args) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Class cls = instance.getClass();
        Class[] argsType = new Class[args.length];
        int index;
        for(index = 0; index < args.length; ++index) {
            argsType[index] = args[index].getClass();
            if(argsType[index] == Integer.class) {
                argsType[index] = Integer.TYPE;
            }
        }

        Method method = cls.getDeclaredMethod(methodName, argsType);
        if(!method.isAccessible()) {
            method.setAccessible(true);
        }

        return method.invoke(instance, args);
    }

    public CellLocation getCellLocationData() {
        List mAllCellInfo;
        CellLocation mCellLocation;
        CellLocation mCellLocationData;
        Object mPhoneService;
        try {
            mPhoneService = this.getPhoneService();
            if(mPhoneService == null) {
                return null;
            }
            Class serviceCls = this.loadServiceClass();

            if(serviceCls.isInstance(mPhoneService)) {
                mPhoneService = serviceCls.cast(mPhoneService);
                mCellLocation = (CellLocation) this.invoke(mPhoneService, "getCellLocation", new Object[0]);
                if(mCellLocation == null) {
                    mCellLocation = (CellLocation) this.invoke(mPhoneService, "getCellLocation", new Object[]{((int) 0)});
                    if (mCellLocation == null) {
                        mCellLocation = (CellLocation) this.invoke(mPhoneService, "getCellLocationGemini", new Object[]{((int) 1)});
                        if (mCellLocation == null) {
                            mAllCellInfo = (List) this.invoke(mPhoneService, "getAllCellInfo", new Object[0]);
                            mCellLocationData = this.getCellLocationData(mAllCellInfo);
                            if (mCellLocationData != null) {
                                return mCellLocationData;
                            }
                        }
                    }
                }
            }
    }catch (Exception e){}
        return null;
    }

    private CellLocation getCellLocationData(List cellInfoList) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {

        Class CellInfoCdma = null;
        Class CellInfoLte = null;
        Class CellInfoWcdma = null;
        Class CellInfoGsm = null;
        ClassLoader clsLoader = ClassLoader.getSystemClassLoader();

        if (cellInfoList == null || (cellInfoList.isEmpty())) {
            return null;
        }

        try {
            CellInfoGsm = clsLoader.loadClass("android.telephony.CellInfoGsm");
            CellInfoWcdma = clsLoader.loadClass("android.telephony.CellInfoWcdma");
            CellInfoLte = clsLoader.loadClass("android.telephony.CellInfoLte");
            CellInfoCdma = clsLoader.loadClass("android.telephony.CellInfoCdma");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        GsmCellLocation mGsmCellLocation = null;
        CdmaCellLocation mCdmaCellLocation = null;
        int v0 = 0;

        for (int index = 0; index < cellInfoList.size(); index++) {
            Object cellinfo = cellInfoList.get(index);
            Object mCellInfoGsm = null;
            if (cellinfo != null) {
                int type = 0;
                if(CellInfoGsm.isInstance(cellinfo)) {
                    type = 1;
                    mCellInfoGsm = CellInfoGsm.cast(cellinfo);
                }
                else if(CellInfoWcdma.isInstance(cellinfo)) {
                    type = 2;
                    mCellInfoGsm = CellInfoWcdma.cast(cellinfo);
                }
                else if(CellInfoLte.isInstance(cellinfo)) {
                    type = 3;
                    mCellInfoGsm = CellInfoLte.cast(cellinfo);
                }
                else if(CellInfoCdma.isInstance(cellinfo)){
                    type = 4;
                    mCellInfoGsm = CellInfoCdma.cast(cellinfo);
                }

                Object mCellIdentity = this.invoke(mCellInfoGsm, "getCellIdentity", new Object[0]);
                if(mCellIdentity == null)continue;


                CellLocation result = null;
                switch (type){
                    case 1:
                    case 2:
                        int Lac = this.invoke2(mCellIdentity, "getLac", new Object[0]);
                        int Cid = this.invoke2(mCellIdentity, "getCid", new Object[0]);
                        result = new GsmCellLocation();
                        ((GsmCellLocation)result).setLacAndCid(Lac, Cid);
                        return result;
                    case 3:
                        int Tac = this.invoke2(mCellIdentity, "getTac", new Object[0]);
                        int Ci = this.invoke2(mCellIdentity, "getCi", new Object[0]);
                        result = new GsmCellLocation();
                        ((GsmCellLocation)result).setLacAndCid(Tac, Ci);
                        return result;
                    case 4:
                        result = new CdmaCellLocation();
                        int SystemId = this.invoke2(mCellIdentity, "getSystemId", new Object[0]);
                        int NetworkId = this.invoke2(mCellIdentity, "getNetworkId", new Object[0]);
                        int BasestationId = this.invoke2(mCellIdentity, "getBasestationId", new Object[0]);
                        int Longitude = this.invoke2(mCellIdentity, "getLongitude", new Object[0]);
                        ((CdmaCellLocation)result).setCellLocationData(BasestationId, this.invoke2(mCellIdentity, "getLatitude", new Object[0]), Longitude, SystemId, NetworkId);
                        return result;
                }

            }
        }
        return new CdmaCellLocation();
    }



    private int invoke2(Object arg6, String arg7, Object[] arg8) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class v1 = arg6.getClass();
        Class[] v2 = new Class[arg8.length];
        int v0;
        for(v0 = 0; v0 < arg8.length; ++v0) {
            v2[v0] = arg8[v0].getClass();
            if(v2[v0] == Integer.class) {
                v2[v0] = Integer.TYPE;
            }
        }

        Method v0_1 = v1.getDeclaredMethod(arg7, v2);
        if(!v0_1.isAccessible()) {
            v0_1.setAccessible(true);
        }

        return (int)(((Integer)v0_1.invoke(arg6, arg8)));
    }
}
