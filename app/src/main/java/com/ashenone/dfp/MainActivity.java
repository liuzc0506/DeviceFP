package com.ashenone.dfp;

import android.Manifest;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ashenone.dfp.collector.ActivityManagerCollector;
import com.ashenone.dfp.collector.BatteryInfoCollector;
import com.ashenone.dfp.collector.BlueToothInfoCollector;
import com.ashenone.dfp.collector.CanvasCollector;
import com.ashenone.dfp.collector.DisplayInfoCollector;
import com.ashenone.dfp.collector.GpuInfoCollector;
import com.ashenone.dfp.collector.JavaSystemInfoCollector;
import com.ashenone.dfp.collector.NativeCollector;
import com.ashenone.dfp.collector.NetworkInfoCollector;
import com.ashenone.dfp.collector.PackageCollector;
import com.ashenone.dfp.collector.PropCollector;
import com.ashenone.dfp.collector.SensorInfoCollector;
import com.ashenone.dfp.collector.SettingsCollector;
import com.ashenone.dfp.collector.TelephonyManagerCollector;
import com.ashenone.dfp.collector.WifiManagerCollector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    public Context context = MainActivity.this;
    private HashMap<String,TextView> textViewHashMap = new HashMap<>();

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.refresh_btn:
                refresh();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static byte[] hex2byte(String hex) {
        if(hex.contains(":"))hex = hex.replace(":","");
        char[] arr = hex.toCharArray();
        byte[] b = new byte[hex.length() / 2];
        for (int i = 0, j = 0, l = hex.length(); i < l; i++, j++) {
            String swap = "" + arr[i++] + arr[i];
            int byteint = Integer.parseInt(swap, 16) & 0xFF;
            b[j] = new Integer(byteint).byteValue();
        }
        return b;
    }

    public void refresh(){
//            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
//            try {
//                Class clz = adapter.getClass();
//                Method setAddress = clz.getDeclaredMethod("setAddress", String.class);
//                setAddress.invoke(adapter,"Reflect Fake Address");
//            }catch (Exception e){
//                e.printStackTrace();
//            }

//        System.out.println(NativeCollector.getDrm());

//        copyDeviceFile();
//        copyFonts();
        MainActivity.this.getDeviceInfo(MainActivity.this);
//        String key = "k镪R!عدد.テ";
//        String s1 = CanvasCollector.getCanvasFeature(key);
//        String s2 = CanvasCollector.getCanvasFeature(key+"1");
//        System.out.println(s1.equals(s2));
//            Runtime runtime = Runtime.getRuntime();
//            try {
//                Process exec = runtime.exec("ps -A");
//                StringBuffer mRespBuff = new StringBuffer();
//                char[] buff = new char[1024];
//                int ch = 0;
//                BufferedReader br = new BufferedReader(new InputStreamReader(exec.getInputStream()));
//                while ((ch = br.read(buff)) != -1) {
//                    mRespBuff.append(buff, 0, ch);
//                }
//                br.close();
//                exec.destroy();
//                System.out.println(mRespBuff);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        // 文件信息和目录信息
        copyDeviceFile();
        copyFonts();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void addTitle(ViewGroup view, String title){
        TextView textView = new TextView(this);
        textView.setText(title);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0);
        layoutParams.topMargin = 3;
        textView.setLayoutParams(layoutParams);
        textView.setTextColor(Color.parseColor("#673AB7"));
        textView.setBackgroundColor(Color.parseColor("#A0A0A0"));
        textView.setTextSize(20);
        textView.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        view.addView(textView);
    }

    private TextView addRow(ViewGroup view,String key){
        LinearLayout linearLayout = new LinearLayout(MainActivity.this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0);
        layoutParams.topMargin = 5;
        linearLayout.setLayoutParams(layoutParams);

        TextView keyText = new TextView(MainActivity.this);
        keyText.setText(key);
        keyText.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        keyText.setGravity(Gravity.CENTER);
        keyText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 11));

        TextView valueText = new TextView(MainActivity.this);
        valueText.setText("UNKNOWN");
        valueText.setGravity(Gravity.CENTER);
        valueText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 4));

        View split = new View(this);
        split.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 3, 0));
        split.setBackgroundColor(Color.BLACK);

        linearLayout.addView(keyText);
        linearLayout.addView(valueText);
        view.addView(linearLayout);
        view.addView(split);

        return valueText;
    }

    private void initView(){
        LinearLayout mainView = findViewById(R.id.main_layout);
        addTitle(mainView,"UUID");
        textViewHashMap.put("UUID",addRow(mainView,"UUID:"));
        textViewHashMap.put("BOOT_ID",addRow(mainView,"BOOT_ID:"));

        addTitle(mainView,"BatteryInfo");
        textViewHashMap.put("battery_status",addRow(mainView,"Status:"));
        textViewHashMap.put("battery_plugged",addRow(mainView,"Plugged:"));
        textViewHashMap.put("battery_health",addRow(mainView,"Health:"));
        textViewHashMap.put("battery_level",addRow(mainView,"Level:"));
        textViewHashMap.put("battery_voltage",addRow(mainView,"Voltage:"));
        textViewHashMap.put("battery_temperature",addRow(mainView,"Temperature:"));
        textViewHashMap.put("battery_scale",addRow(mainView,"Scale:"));
        textViewHashMap.put("battery_present",
                addRow(mainView,"Present:"));
        textViewHashMap.put("battery_technology",addRow(mainView,"Technology:"));
        textViewHashMap.put("battery_low",addRow(mainView,"Low:"));


        addTitle(mainView,"BluetoothInfo");
        textViewHashMap.put("bluetooth_address",addRow(mainView,"Address:"));
        textViewHashMap.put("bluetooth_name",addRow(mainView,"Name:"));

        addTitle(mainView,"DisplayInfo");
        textViewHashMap.put("display_heightPixels",addRow(mainView,"HeightPixels:"));
        textViewHashMap.put("display_widthPixels",addRow(mainView,"WidthPixels:"));
        textViewHashMap.put("display_densityDpi",addRow(mainView,"DensityDpi:"));
        textViewHashMap.put("display_scaledDensity",addRow(mainView,"ScaledDensity:"));
        textViewHashMap.put("display_density",addRow(mainView,"Density:"));
        textViewHashMap.put("display_xdpi",addRow(mainView,"Xdpi:"));
        textViewHashMap.put("display_ydpi",addRow(mainView,"Ydpi:"));
        // add language
        textViewHashMap.put("display_language",addRow(mainView,"Language:"));
        // add fps
        textViewHashMap.put("display_fps",addRow(mainView,"fps:"));


        addTitle(mainView,"BuildInfo");
        for(String key:Common.build_fields){
            textViewHashMap.put("build_"+key.toLowerCase(),addRow(mainView,key+":"));
        }
        textViewHashMap.put("build_radio_version",addRow(mainView,"RadioVersion:"));
//        textViewHashMap.put("build_serial",addRow(mainView,"SERIAL:"));

        addTitle(mainView,"VersionInfo");
        for(String key:Common.version_fields){
            textViewHashMap.put("version_"+key.toLowerCase(),addRow(mainView,key+":"));
        }
//        addTitle(mainView,"SystemPropInfo");
//        for(String key:Common.prop_keys){
//            textViewHashMap.put("spi_"+key,addRow(mainView,key+":"));
//        }
//
//        addTitle(mainView,"NativeSystemPropInfo");
//        for(String key:Common.prop_keys){
//            textViewHashMap.put("nspi_"+key,addRow(mainView,key+":"));
//        }

        addTitle(mainView,"JavaSystemPropInfo");
        for(String key:Common.java_system_props){
            textViewHashMap.put("jspi_"+key,addRow(mainView,key+":"));
        }
        addTitle(mainView,"Setting$Global");
        for(String key:Common.setting_global_keys){
            textViewHashMap.put("setting_global_"+key,addRow(mainView,key+":"));
        }

        addTitle(mainView,"Setting$Secure");
        for(String key:Common.setting_secure_keys){
            textViewHashMap.put("setting_secure_"+key,addRow(mainView,key+":"));
        }

        addTitle(mainView,"Setting$System");
        for(String key:Common.setting_system_keys){
            textViewHashMap.put("setting_system_"+key,addRow(mainView,key+":"));
        }

        addTitle(mainView,"MemoryInfo");
        textViewHashMap.put("availMem",addRow(mainView,"AvailMem:"));
        textViewHashMap.put("threshold",addRow(mainView,"Threshold:"));
        textViewHashMap.put("totalMem",addRow(mainView,"TotalMem:"));
        textViewHashMap.put("foregroundAppThreshold",addRow(mainView,"ForegroundAppThreshold:"));
        textViewHashMap.put("hiddenAppThreshold",addRow(mainView,"HiddenAppThreshold:"));
        textViewHashMap.put("secondaryServerThreshold",addRow(mainView,"SecondaryServerThreshold:"));
        textViewHashMap.put("visibleAppThreshold",addRow(mainView,"VisibleAppThreshold:"));

        addTitle(mainView,"TelephonyManager");
        textViewHashMap.put("tele_sim_operator",addRow(mainView,"SimOperator:"));
        textViewHashMap.put("tele_network_operator",addRow(mainView,"NetworkOperator:"));
        textViewHashMap.put("tele_network_operator_name",addRow(mainView,"NetworkOperatorName:"));
        textViewHashMap.put("tele_network_type",addRow(mainView,"NetworkType:"));
        textViewHashMap.put("tele_phone_count",addRow(mainView,"PhoneCount:"));
        // add sim state
        textViewHashMap.put("tele_sim_state",addRow(mainView,"SimState"));
        textViewHashMap.put("tele_manufacturer_code",addRow(mainView,"ManufacturerCode:"));
        textViewHashMap.put("tele_sim_serial_number",addRow(mainView,"SimSerialNumber:"));
        textViewHashMap.put("tele_subscriber_id",addRow(mainView,"SubscriberId:"));
        textViewHashMap.put("tele_service_state",addRow(mainView,"ServiceState:"));
        textViewHashMap.put("tele_device_software_version",addRow(mainView,"DeviceSoftwareVersion:"));
        textViewHashMap.put("tele_meid",addRow(mainView,"Meid:"));
        textViewHashMap.put("tele_cell_location",addRow(mainView,"CellLocation:"));
        textViewHashMap.put("tele_all_cell_info",addRow(mainView,"AllCellInfo:"));
        textViewHashMap.put("tele_deviceid",addRow(mainView,"DeviceID:"));

        // add wifiManager
        addTitle(mainView, "WifiManager");
        textViewHashMap.put("wifi_wifi_state", addRow(mainView,"WifiState:"));
        textViewHashMap.put("wifi_scan_results", addRow(mainView,"ScanResults:"));
        textViewHashMap.put("wifi_ssid", addRow(mainView,"SSID:"));
        textViewHashMap.put("wifi_bssid", addRow(mainView,"BSSID:"));
        textViewHashMap.put("wifi_ip", addRow(mainView,"Ip:"));
        textViewHashMap.put("wifi_max_rx_link_speed", addRow(mainView,"MaxSupportedRxLinkSpeedMbps:"));
        textViewHashMap.put("wifi_rx_link_speed", addRow(mainView,"RxLinkSpeedMbps:"));
        textViewHashMap.put("wifi_max_tx_link_speed", addRow(mainView,"MaxSupportedTxLinkSpeedMbps:"));
        textViewHashMap.put("wifi_tx_link_speed", addRow(mainView,"TxLinkSpeedMbps:"));
        textViewHashMap.put("wifi_network_id", addRow(mainView,"NetworkId:"));
        textViewHashMap.put("wifi_Rssi", addRow(mainView,"Rssi:"));

        // add gpu info
        addTitle(mainView,"GpuInfo");
        textViewHashMap.put("gpu_renderer",addRow(mainView,"Renderer"));
        textViewHashMap.put("gpu_vendor",addRow(mainView,"Vendor"));

        addTitle(mainView,"NetworkInterface");
//        textViewHashMap.put("network_mac",addRow(mainView,"mac:"));
//        textViewHashMap.put("network_name",addRow(mainView,"name:"));
//        textViewHashMap.put("network_mtu",addRow(mainView,"mtu:"));
//        textViewHashMap.put("network_isLoopback",addRow(mainView,"isLoopback:"));
//        textViewHashMap.put("network_isP2P",addRow(mainView,"isP2P:"));
//        textViewHashMap.put("network_isUp",addRow(mainView,"isUp:"));
//        textViewHashMap.put("network_isVirtual",addRow(mainView,"isVirtual:"));

        textViewHashMap.put("p2p0",addRow(mainView,"p2p0:"));
        textViewHashMap.put("wlan1",addRow(mainView,"wlan1:"));
        textViewHashMap.put("wlan0",addRow(mainView,"wlan0:"));
        textViewHashMap.put("ip6tnl0",addRow(mainView,"ip6tnl0:"));
        textViewHashMap.put("sit0",addRow(mainView,"sit0:"));
        textViewHashMap.put("ip6_vti0",addRow(mainView,"ip6_vti0:"));
        textViewHashMap.put("ip_vti0",addRow(mainView,"ip_vti0:"));
        textViewHashMap.put("dummy0",addRow(mainView,"dummy0:"));
        textViewHashMap.put("bond0",addRow(mainView,"bond0:"));
        textViewHashMap.put("lo",addRow(mainView,"lo:"));

        addTitle(mainView,"UiMode");
        textViewHashMap.put("currentModeType",addRow(mainView,"currentModeType:"));

        addTitle(mainView,"PackageInfo");
        textViewHashMap.put("launcher",addRow(mainView,"launcher:"));

        addTitle(mainView,"Sensor");

    }


    private void getDeviceInfo(Context ctx){
        //动态申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                    | ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    | ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                    | ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS) | ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) |
                        ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)
                        | ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {//是否请求过该权限
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.RECEIVE_SMS,
                                    Manifest.permission.READ_SMS,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_PHONE_STATE,
                                    Manifest.permission.ACCESS_FINE_LOCATION}, 10001);
                } else {//没有则请求获取权限，示例权限是：存储权限和短信权限，需要其他权限请更改或者替换
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.RECEIVE_SMS,
                                    Manifest.permission.READ_SMS,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_PHONE_STATE,
                                    Manifest.permission.ACCESS_FINE_LOCATION}, 10001);
                }
            }

        }

        //动态申请存储权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if(!Environment.isExternalStorageManager()){
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
                return;
            }
        }else{
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        DeviceInfo deviceInfo = DeviceInfo.getInstance();
        File f = new File("/sdcard/fakeData");

        try {
            Objects.requireNonNull(textViewHashMap.get("UUID")).setText(NativeCollector.readFile("/proc/sys/kernel/random/uuid"));
            Objects.requireNonNull(textViewHashMap.get("BOOT_ID")).setText(NativeCollector.readFile("/proc/sys/kernel/random/boot_id"));
        }catch (Exception e){
            e.printStackTrace();
        }

        BatteryInfoCollector.getBatteryInfo(ctx);
        try {
            Objects.requireNonNull(textViewHashMap.get("battery_status")).setText(deviceInfo.BatteryInfo.get("status").toString());
            Objects.requireNonNull(textViewHashMap.get("battery_plugged")).setText(deviceInfo.BatteryInfo.get("plugged").toString());
            Objects.requireNonNull(textViewHashMap.get("battery_health")).setText(deviceInfo.BatteryInfo.get("health").toString());
            Objects.requireNonNull(textViewHashMap.get("battery_temperature")).setText(deviceInfo.BatteryInfo.get("temperature").toString());
            Objects.requireNonNull(textViewHashMap.get("battery_voltage")).setText(deviceInfo.BatteryInfo.get("voltage").toString());
            Objects.requireNonNull(textViewHashMap.get("battery_level")).setText(deviceInfo.BatteryInfo.get("level").toString());
            Objects.requireNonNull(textViewHashMap.get("battery_scale")).setText(deviceInfo.BatteryInfo.get("scale").toString());

            Objects.requireNonNull(textViewHashMap.get("battery_present")).setText(deviceInfo.BatteryInfo.get(BatteryManager.EXTRA_PRESENT).toString());
            Objects.requireNonNull(textViewHashMap.get("battery_technology")).setText(deviceInfo.BatteryInfo.get(BatteryManager.EXTRA_TECHNOLOGY).toString());
            Objects.requireNonNull(textViewHashMap.get("battery_low")).setText(deviceInfo.BatteryInfo.get(BatteryManager.EXTRA_BATTERY_LOW).toString());
        }catch (Exception e){
            e.printStackTrace();
        }


        BlueToothInfoCollector.getBlueToothInfo(ctx);
        try {
            Objects.requireNonNull(textViewHashMap.get("bluetooth_name")).setText(deviceInfo.BluetoothInfo.get("name").toString());
            Objects.requireNonNull(textViewHashMap.get("bluetooth_address")).setText(deviceInfo.BluetoothInfo.get("address").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        PropCollector propCollector = PropCollector.getInstance(MainActivity.this);
        propCollector.getBuildInfo();

        for(String key:Common.build_fields){
            try {
                Objects.requireNonNull(textViewHashMap.get("build_"+key.toLowerCase())).setText(deviceInfo.BuildInfo.get(key).toString());
            } catch (JSONException e) {

            }
        }
        try {
            Objects.requireNonNull(textViewHashMap.get("build_radio_version")).setText(deviceInfo.BuildInfo.get("radioVersion").toString());
            Objects.requireNonNull(textViewHashMap.get("build_serial")).setText(deviceInfo.BuildInfo.get("SERIAL").toString());
        } catch (JSONException e) {}


        for(String key:Common.version_fields){
            try {
                Objects.requireNonNull(textViewHashMap.get("version_"+key.toLowerCase())).setText(deviceInfo.VersionInfo.get(key).toString());
            } catch (JSONException e) {

            }
        }

        propCollector.getSystemProp();
//        for(String key:Common.prop_keys){
//            try {
//                Objects.requireNonNull(textViewHashMap.get("spi_"+key)).setText(deviceInfo.SystemPropInfo.get(key).toString());
//            } catch (JSONException e) {
//
//            }
//        }

        NativeCollector.getSystemProp();
//        for(String key:Common.prop_keys){
//            try {
//                Objects.requireNonNull(textViewHashMap.get("nspi_"+key)).setText(deviceInfo.NativeSystemPropInfo.get(key).toString());
//            } catch (JSONException e) {
//
//            }
//        }

        JavaSystemInfoCollector.getProperties();
        for(String key:Common.java_system_props){
            try {
                Objects.requireNonNull(textViewHashMap.get("jspi_"+key)).setText(deviceInfo.JavaSystemInfo.get(key).toString());
            } catch (JSONException e) {

            }
        }

        DisplayInfoCollector displayInfoCollector = DisplayInfoCollector.getInstance(this);
        displayInfoCollector.getDisplayInfo();
        try{
            Objects.requireNonNull(textViewHashMap.get("display_heightPixels")).setText(deviceInfo.DisplayInfo.get("heightPixels").toString());
            Objects.requireNonNull(textViewHashMap.get("display_widthPixels")).setText(deviceInfo.DisplayInfo.get("widthPixels").toString());
            Objects.requireNonNull(textViewHashMap.get("display_densityDpi")).setText(deviceInfo.DisplayInfo.get("densityDpi").toString());
            Objects.requireNonNull(textViewHashMap.get("display_scaledDensity")).setText(deviceInfo.DisplayInfo.get("scaledDensity").toString());
            Objects.requireNonNull(textViewHashMap.get("display_density")).setText(deviceInfo.DisplayInfo.get("density").toString());
            Objects.requireNonNull(textViewHashMap.get("display_xdpi")).setText(deviceInfo.DisplayInfo.get("xdpi").toString());
            Objects.requireNonNull(textViewHashMap.get("display_ydpi")).setText(deviceInfo.DisplayInfo.get("ydpi").toString());
            // add language
            Objects.requireNonNull(textViewHashMap.get("display_language")).setText(deviceInfo.DisplayInfo.get("language").toString());
            // add fps
            Objects.requireNonNull(textViewHashMap.get("display_fps")).setText(deviceInfo.DisplayInfo.get("fps").toString());
        }catch (Exception e){
            e.printStackTrace();
        }

        SettingsCollector.getInstance(this).getSettings();
        for(String key:Common.setting_global_keys){
            try {
                Objects.requireNonNull(textViewHashMap.get("setting_global_"+key)).setText(deviceInfo.SettingInfo.get(key).toString());
            } catch (JSONException e) {

            }
        }
        for(String key:Common.setting_secure_keys){
            try {
                Objects.requireNonNull(textViewHashMap.get("setting_secure_"+key)).setText(deviceInfo.SettingInfo.get(key).toString());
            } catch (JSONException e) {

            }
        }
        for(String key:Common.setting_system_keys){
            try {
                Objects.requireNonNull(textViewHashMap.get("setting_system_"+key)).setText(deviceInfo.SettingInfo.get(key).toString());
            } catch (JSONException e) {

            }
        }

        ActivityManagerCollector activityManagerCollector = ActivityManagerCollector.getInstance(this);
        activityManagerCollector.getMemoryInfo();
        try{
            Objects.requireNonNull(textViewHashMap.get("availMem")).setText(deviceInfo.MemoryInfo.get("availMem").toString());
            Objects.requireNonNull(textViewHashMap.get("threshold")).setText(deviceInfo.MemoryInfo.get("threshold").toString());
            Objects.requireNonNull(textViewHashMap.get("totalMem")).setText(deviceInfo.MemoryInfo.get("totalMem").toString());
            Objects.requireNonNull(textViewHashMap.get("foregroundAppThreshold")).setText(deviceInfo.MemoryInfo.get("foregroundAppThreshold").toString());
            Objects.requireNonNull(textViewHashMap.get("hiddenAppThreshold")).setText(deviceInfo.MemoryInfo.get("hiddenAppThreshold").toString());
            Objects.requireNonNull(textViewHashMap.get("secondaryServerThreshold")).setText(deviceInfo.MemoryInfo.get("secondaryServerThreshold").toString());
            Objects.requireNonNull(textViewHashMap.get("visibleAppThreshold")).setText(deviceInfo.MemoryInfo.get("visibleAppThreshold").toString());
        }catch (Exception e){
            e.printStackTrace();
        }

//
        TelephonyManagerCollector telephonyManagerCollector = TelephonyManagerCollector.getInstance(this);
        telephonyManagerCollector.getTelephoneManagerInfo();
        try{
            Objects.requireNonNull(textViewHashMap.get("tele_sim_operator")).setText(deviceInfo.TelephonyManagerInfo.get("simOperator").toString());
            Objects.requireNonNull(textViewHashMap.get("tele_network_operator")).setText(deviceInfo.TelephonyManagerInfo.get("networkOperator").toString());
            Objects.requireNonNull(textViewHashMap.get("tele_network_operator_name")).setText(deviceInfo.TelephonyManagerInfo.get("networkOperatorName").toString());
            Objects.requireNonNull(textViewHashMap.get("tele_network_type")).setText(deviceInfo.TelephonyManagerInfo.get("networkType").toString());
            Objects.requireNonNull(textViewHashMap.get("tele_phone_count")).setText(deviceInfo.TelephonyManagerInfo.get("phoneCount").toString());
            Objects.requireNonNull(textViewHashMap.get("tele_manufacturer_code")).setText(deviceInfo.TelephonyManagerInfo.get("manufacturerCode").toString());
            Objects.requireNonNull(textViewHashMap.get("tele_sim_serial_number")).setText(deviceInfo.TelephonyManagerInfo.get("simSerialNumber").toString());
            Objects.requireNonNull(textViewHashMap.get("tele_subscriber_id")).setText(deviceInfo.TelephonyManagerInfo.get("subscriberId").toString());
            Objects.requireNonNull(textViewHashMap.get("tele_service_state")).setText(deviceInfo.TelephonyManagerInfo.get("serviceState").toString());
            Objects.requireNonNull(textViewHashMap.get("tele_device_software_version")).setText(deviceInfo.TelephonyManagerInfo.get("deviceSoftwareVersion").toString());
            Objects.requireNonNull(textViewHashMap.get("tele_meid")).setText(deviceInfo.TelephonyManagerInfo.get("meid").toString());
            // add imei
            Objects.requireNonNull(textViewHashMap.get("tele_imei")).setText(deviceInfo.TelephonyManagerInfo.get("imei").toString());
            Objects.requireNonNull(textViewHashMap.get("tele_all_cell_info")).setText(deviceInfo.TelephonyManagerInfo.get("allCellInfo").toString());
            Objects.requireNonNull(textViewHashMap.get("tele_deviceid")).setText(deviceInfo.TelephonyManagerInfo.get("deviceId").toString());
            Objects.requireNonNull(textViewHashMap.get("tele_cell_location")).setText(deviceInfo.TelephonyManagerInfo.get("cellLocation").toString());
            //add sim state
            Objects.requireNonNull(textViewHashMap.get("tele_sim_state")).setText(deviceInfo.TelephonyManagerInfo.get("simState").toString());
        }catch (Exception e){
            e.printStackTrace();
        }

        // add wifiManager
        WifiManagerCollector wifiManagerCollector = WifiManagerCollector.getInstance(this);
        wifiManagerCollector.getWifiManagerInfo();
        try{
            Objects.requireNonNull(textViewHashMap.get("wifi_wifi_state")).setText(deviceInfo.WifiManagerInfo.get("wifiState").toString());
            Objects.requireNonNull(textViewHashMap.get("wifi_scan_results")).setText(deviceInfo.WifiManagerInfo.get("scanResults").toString());
            Objects.requireNonNull(textViewHashMap.get("wifi_ssid")).setText(deviceInfo.WifiManagerInfo.get("ssid").toString());
            Objects.requireNonNull(textViewHashMap.get("wifi_bssid")).setText(deviceInfo.WifiManagerInfo.get("bssid").toString());
            Objects.requireNonNull(textViewHashMap.get("wifi_ip")).setText(deviceInfo.WifiManagerInfo.get("ip").toString());
            Objects.requireNonNull(textViewHashMap.get("wifi_max_rx_link_speed")).setText(deviceInfo.WifiManagerInfo.get("maxSupportedRxLinkSpeedMbps").toString());
            Objects.requireNonNull(textViewHashMap.get("wifi_rx_link_speed")).setText(deviceInfo.WifiManagerInfo.get("rxLinkSpeedMbps").toString());
            Objects.requireNonNull(textViewHashMap.get("wifi_max_tx_link_speed")).setText(deviceInfo.WifiManagerInfo.get("maxSupportedTxLinkSpeedMbps").toString());
            Objects.requireNonNull(textViewHashMap.get("wifi_tx_link_speed")).setText(deviceInfo.WifiManagerInfo.get("txLinkSpeedMbps").toString());
            Objects.requireNonNull(textViewHashMap.get("wifi_network_id")).setText(deviceInfo.WifiManagerInfo.get("netWorkId").toString());
            Objects.requireNonNull(textViewHashMap.get("wifi_Rssi")).setText(deviceInfo.WifiManagerInfo.get("rssi").toString());

        } catch (Exception e){
            e.printStackTrace();
        }


        LinearLayout mainView = findViewById(R.id.main_layout);

        NetworkInfoCollector networkInfoCollector = NetworkInfoCollector.getInstance(MainActivity.this);
        networkInfoCollector.getNetwordIFaceAddrs();
        try{
            Objects.requireNonNull(textViewHashMap.get("p2p0")).setText(deviceInfo.NetworkInterfaceInfo.get("p2p0").toString());
            Objects.requireNonNull(textViewHashMap.get("wlan1")).setText(deviceInfo.NetworkInterfaceInfo.get("wlan1").toString());
            Objects.requireNonNull(textViewHashMap.get("wlan0")).setText(deviceInfo.NetworkInterfaceInfo.get("wlan0").toString());
            Objects.requireNonNull(textViewHashMap.get("ip6tnl0")).setText(deviceInfo.NetworkInterfaceInfo.get("ip6tnl0").toString());
            Objects.requireNonNull(textViewHashMap.get("sit0")).setText(deviceInfo.NetworkInterfaceInfo.get("sit0").toString());
            Objects.requireNonNull(textViewHashMap.get("ip6_vti0")).setText(deviceInfo.NetworkInterfaceInfo.get("ip6_vti0").toString());
            Objects.requireNonNull(textViewHashMap.get("ip_vti0")).setText(deviceInfo.NetworkInterfaceInfo.get("ip_vti0").toString());
            Objects.requireNonNull(textViewHashMap.get("dummy0")).setText(deviceInfo.NetworkInterfaceInfo.get("dummy0").toString());
            Objects.requireNonNull(textViewHashMap.get("bond0")).setText(deviceInfo.NetworkInterfaceInfo.get("bond0").toString());
            Objects.requireNonNull(textViewHashMap.get("lo")).setText(deviceInfo.NetworkInterfaceInfo.get("lo").toString());
//            Iterator<String> keys = deviceInfo.NetworkInterfaceInfo.keys();
//            Log.d("NetworkInterface_debug", keys.toString());
//
//            while (keys.hasNext()) {
//                Log.d("NetworkInterface_debug", "1");
//                String key = keys.next();
//                String value = (String) deviceInfo.NetworkInterfaceInfo.get(key);
//                Log.d("NetworkInterface_debug:   ", key + value);
//                textViewHashMap.get(key).setText(value);
//            }
        } catch (Exception e){
            e.printStackTrace();
        }

        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(UI_MODE_SERVICE);

        try {
            deviceInfo.UiModeManagerInfo.put("currentModeType",uiModeManager.getCurrentModeType());
            Objects.requireNonNull(textViewHashMap.get("currentModeType")).setText(deviceInfo.UiModeManagerInfo.get("currentModeType").toString());
        }catch (Exception e){
            e.printStackTrace();
        }

        PackageCollector.getInstance(this).getLauncher();
        try {
            Objects.requireNonNull(textViewHashMap.get("launcher")).setText(deviceInfo.PackageInfo.get("launcher").toString());
        }catch (Exception e){
            e.printStackTrace();
        }

        // add GPU info
        GpuInfoCollector gpuInfoCollector = GpuInfoCollector.getInstance(this);
        gpuInfoCollector.getGpuInfo();
        try{
            Objects.requireNonNull(textViewHashMap.get("gpu_renderer")).setText(deviceInfo.GpuInfo.get("gpuRenderer").toString());
            Objects.requireNonNull(textViewHashMap.get("gpu_vendor")).setText(deviceInfo.GpuInfo.get("gpuVendor").toString());
        } catch (Exception e){
            e.printStackTrace();
        }


        SensorInfoCollector sensorInfoCollector = SensorInfoCollector.getInstance(this);
        sensorInfoCollector.getSensorList();

        LinearLayout view = findViewById(R.id.main_layout);
        for (Iterator<String> it = deviceInfo.SensorListInfo.keys(); it.hasNext(); ) {
            String key = it.next();
            TextView textView = addRow(view, key + ":");
            try {
                textView.setText(deviceInfo.SensorListInfo.get(key).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

//        Log.d("TEST", "BSSID : " + networkInfoCollector.getBSSID());
//        Log.d("TEST", "SSID : " + networkInfoCollector.getSSID());
//        Log.d("TEST", "NetworkType : " + networkInfoCollector.getNetworkType());
//        Log.d("TEST", "HttpProxyInfo : " + networkInfoCollector.getHttpProxyInfo());
//        Log.d("TEST", "NetworkType : " + networkInfoCollector.getNetworkType());
//        Log.d("TEST", "IpAddress : " + networkInfoCollector.getIpAddress());
//        Log.d("TEST", "MacAddress : " + networkInfoCollector.getMacAddress());
//        Log.d("TEST", "NetwordIFaceAddrs : " + networkInfoCollector.getNetwordIFaceAddrs().toString());
//        Log.d("TEST", "WifiLocation : " + networkInfoCollector.getWifiLocation().toString());

        deviceInfo.exportData(f);
//        Log.d("TEST", "DeviceInfo : " + deviceInfo.TelephonyManagerInfo);



//        Log.d("TEST", "android_id : " + propCollector.get_android_id());
//        Log.d("TEST", "Serial : " + propCollector.getSerial());
//
//        PackageCollector packageCollector = PackageCollector.getInstance(MainActivity.this);
//        Log.d("TEST", "InstalledApps : " + packageCollector.getInstalledApps().toString());
//        Log.d("TEST", "SelfInfo : " + packageCollector.getSelfInfo());
//        Log.d("TEST", "SelfProcName : " + packageCollector.getSelfProcName());
//        Log.d("TEST", "SubjectDN : " + packageCollector.getSubjectDN());
//
//

//
//        HardwareInfoCollector mHardwareInfoCollector = HardwareInfoCollector.getInstance(MainActivity.this);
//        Log.d("TEST", "CpuCount : "+ mHardwareInfoCollector.getCpuCount());
//        Log.d("TEST", "TotalMem : "+ mHardwareInfoCollector.getTotalMem());
//        Log.d("TEST", "vendor_id : "+ mHardwareInfoCollector.get_vendor_id());
//        Log.d("TEST", "ModelName : "+ mHardwareInfoCollector.getCPUModelName());
//        Log.d("TEST", "CPUMHz : "+ mHardwareInfoCollector.getCPUMHz());
//        Log.d("TEST", "ExternalStorageInfo : "+ mHardwareInfoCollector.getExternalStorageInfo());
//        Log.d("TEST", "ScreenResolutionAndDpi : "+ mHardwareInfoCollector.getScreenResolutionAndDpi());
//        Log.d("TEST", "screen_brightness : "+ mHardwareInfoCollector.get_screen_brightness());
//
//        DeviceInfoCollector deviceInfoCollector = DeviceInfoCollector.getInstance(MainActivity.this);
//        Log.d("TEST", "LocationAndPhoneServiceName : " + deviceInfoCollector.getLocationAndPhoneServiceName());
//        Log.d("TEST", "DeviceUniqueId : " + deviceInfoCollector.getDeviceUniqueIdFromDrm());
//        Log.d("TEST", "Premisions : " + deviceInfoCollector.checkPremisions());
//        Log.d("TEST", "hasXposed : "+deviceInfoCollector.hasXposed());
//        Log.d("TEST", "AccessibilityInfo : "+deviceInfoCollector.getAccessibilityInfo().toString());
//        Log.d("TEST", "InputMethonds : "+deviceInfoCollector.getInputMethonds().toString());
//        Log.d("TEST", "XposedCache : "+deviceInfoCollector.getXposedCache().toString());
//        Log.d("TEST", "xposed : "+deviceInfoCollector.xposed().toString());
//        Log.d("TEST", "BootTime : "+deviceInfoCollector.getBootTime());
//        Log.d("TEST", "is_mock_location_enable : "+deviceInfoCollector.is_mock_location_enable());
//
//        SimInfoCollector simInfoCollector = SimInfoCollector.getInstance(MainActivity.this);
//        Log.d("TEST", "CellLocation : "+simInfoCollector.getCellLocation().toString());
//        Log.d("TEST", "DeviceId : "+ simInfoCollector.getDeviceId());
//        Log.d("TEST", "SimOperator : "+ simInfoCollector.getSimOperator());
//        Log.d("TEST", "getSimSerialNumber : "+ simInfoCollector.getSimSerialNumber());
//        Log.d("TEST", "getSubscriberId : "+ simInfoCollector.getSubscriberId());
//
//        EmulatorChecker emulatorChecker = EmulatorChecker.getInstance(MainActivity.this);
//        Log.d("TEST", "isEmulator: "+emulatorChecker.isEmulatorResult());
//
//        VirtualAppCheck virtualAppCheck = VirtualAppCheck.getInstance(MainActivity.this);
//        Log.d("TEST", "VirtualAppInfo: "+virtualAppCheck.getVirtualAppInfo());
//
//        SensorInfoCollector sensorInfoCollector = SensorInfoCollector.getInstance(MainActivity.this);
//        Log.d("TEST", "SensorsTypeAndVendor: "+sensorInfoCollector.getSensorsTypeAndVendor());



    }

    public static boolean copyFile(File oldFile, File newFile) {
        try {
            if (!oldFile.exists()) {
                Log.e("--Method--", "copyFile:  oldFile not exist.");
                return false;
            } else if (!oldFile.isFile()) {
                Log.e("--Method--", "copyFile:  oldFile not file.");
                return false;
            } else if (!oldFile.canRead()) {
                Log.e("--Method--", "copyFile:  oldFile cannot read.");
                return false;
            }

            FileInputStream fileInputStream = new FileInputStream(oldFile);    //读入原文件
            FileOutputStream fileOutputStream = new FileOutputStream(newFile);
            byte[] buffer = new byte[1024];
            int byteRead;
            while ((byteRead = fileInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            Log.d("TEST-copyFileFailed", String.valueOf(oldFile));
            e.printStackTrace();
            return false;
        }
    }


    public static void copyDeviceFile(){
        String[] filelist = {
                // 共81个文件  -6
                /*       "/dev/__properties__/u:object_r:exported_bluetooth_prop:s0",
                       "/dev/__properties__/u:object_r:hwservicemanager_prop:s0",
                       "/dev/__properties__/u:object_r:use_memfd_prop:s0",
                       "/dev/__properties__/u:object_r:vendor_default_prop:s0",
                       "/dev/__properties__/u:object_r:dhcp_prop:s0",
                       "/dev/__properties__/u:object_r:serialno_prop:s0",
               */

                /*
                "/dev/event-log-tags",
                "/dev/hwbinder",
                "/dev/ion",
                "/dev/kgsl-2d0",
                "/dev/kgsl-2d1",
                "/dev/kgsl-3d0",
                "/dev/urandom",

                 */
                "/odm/etc/build.prop",
                // "/odm/lib/hw/",
                "/proc/cpuinfo",
                "/proc/meminfo",
                "/proc/misc",
                "/proc/modules",
                "/proc/net/tcp",
                "/proc/net/unix",
                /*
                "/proc/self/fd",
                "/proc/self/maps",
                "/proc/self/status",

                 */
                "/proc/sys/kernel/osrelease",
                "/proc/sys/kernel/random/boot_id",
                "/proc/sys/kernel/random/uuid",
                "/proc/tty/drivers",
                "/proc/uptime",
                "/proc/version",
                /*
                "/sys/block/mmcblk0/device/cid",
                "/sys/block/mmcblk0/device/csd",
                "/sys/block/mmcblk0/device/date",
                "/sys/block/mmcblk0/device/name",
                "/sys/block/mmcblk0/device/raw_id",
                "/sys/block/mmcblk0/device/type",
                "/sys/bus/soc/devices/soc0/build_id",
                "/sys/bus/soc/devices/soc0/machine",
                "/sys/bus/soc/devices/soc0/raw_id",
                "/sys/bus/soc/devices/soc0/serial_number",
                "/sys/class/mmc_host/mmc0/mmc0:0001/cid",
                "/sys/class/mmc_host/mmc0/mmc0:0001/csd",
                "/sys/class/mmc_host/mmc0/mmc0:0001/date",
                "/sys/class/mmc_host/mmc0/mmc0:0001/name",
                "/sys/class/mmc_host/mmc0/mmc0:0001/raw_id",
                "/sys/class/mmc_host/mmc0/mmc0:0001/type",
                "/sys/class/net/eth0/address",
                "/sys/class/thermal/thermal_zone0/temp",
                "/sys/devices/soc0/build_id",
                "/sys/devices/soc0/family",
                "/sys/devices/soc0/machine",
                "/sys/devices/soc0/raw_id",
                "/sys/devices/soc0/serial_number",
                "/sys/devices/system/cpu",

                 */
                "/sys/devices/system/cpu/cpu0/cpu_capacity",
                "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq",
                "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq",
                "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq",
                "/sys/devices/system/cpu/cpu1/cpufreq/cpuinfo_max_freq",
                "/sys/devices/system/cpu/cpu2/cpufreq/cpuinfo_max_freq",
                "/sys/devices/system/cpu/cpu3/cpufreq/cpuinfo_max_freq",
                "/sys/devices/system/cpu/cpu4/cpufreq/cpuinfo_max_freq",
                "/sys/devices/system/cpu/cpu5/cpufreq/cpuinfo_max_freq",
                "/sys/devices/system/cpu/cpu6/cpufreq/cpuinfo_max_freq",
                "/sys/devices/system/cpu/cpu7/cpufreq/cpuinfo_max_freq",
                "/sys/devices/system/cpu/present",
                //"/sys/kernel/debug/tracing/trace_marker",
                "/sys/module",
                "/system/bin/netcfg",
                "/system/build.prop",
                //"/system/etc/event-log-tags",
                //"/system/lib/hw",
                "/system/lib/libc.so",
                //"/vendor/lib/hw/"
                "vendor/build.prop",
                "product/build.prop",


                /*
                "/proc/cpuinfo",
                "/proc/net/tcp", //
                "/proc/meminfo",
                "/system/lib/libreference-ril.so", //
                "/system/lib/libc.so",
                "/proc/sys/kernel/random/boot_id",
                "/proc/sys/kernel/random/uuid",
                "/sys/class/thermal/thermal_zone0/temp", //
                "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_cur_freq", //
                "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq",
                "/sys/devices/system/cpu/present",
                "/proc/sys/kernel/osrelease", //
                "/proc/version", //
                "/proc/uptime", //
                *?
                 */
                "/proc/misc",
                "/sys/devices/soc0/serial_number",
                "/sys/block/mmcblk0/device/cid"
        };
        String prefix = "/sdcard/fakefile";

        for(String _path:filelist){
            File ori = new File(_path);
            if(!ori.exists()){
                Log.d("TEST-fileNotExist", _path);
                continue;
            }
            if(!ori.canRead()){
                Log.d("TEST-fileCannotRead", _path);
                continue;
            }
            if(!ori.isFile()){
                Log.d("TEST-fileNotAFile", _path);
                continue;
            }

            String path = prefix + _path;
            String dirpath = path.substring(0,path.lastIndexOf("/"));
            File f = new File(path);
            if(!f.exists()){
                File dir = new File(dirpath);
                boolean mkdirs = dir.mkdirs();
                System.out.println("mkdirs : " + dirpath + ", result : " + mkdirs);
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    Log.d("TEST-createFileFailed", path);
                    e.printStackTrace();
                }
            }
            copyFile(ori,f);
        }

    }

    public static void copyFonts() {
        File dir = new File("/sdcard/fakefile");
        if (!dir.exists()) {
            boolean mkdirs = dir.mkdirs();
            System.out.println("mkdirs : " + dir + ", result : " + mkdirs);
        }

        Runtime runtime = Runtime.getRuntime();
        try {
            Process exec = runtime.exec("cp -rf /system/fonts /sdcard/fakefile/system");
            StringBuffer mRespBuff = new StringBuffer();
            char[] buff = new char[1024];
            int ch = 0;
            BufferedReader br = new BufferedReader(new InputStreamReader(exec.getInputStream()));
            while ((ch = br.read(buff)) != -1) {
                mRespBuff.append(buff, 0, ch);
            }
            br.close();
            exec.destroy();
            System.out.println(mRespBuff);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}