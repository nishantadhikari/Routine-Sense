package com.mc.iiitd.myroutine;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.mc.iiitd.myroutine.CommonThings.Constants;
import com.mc.iiitd.myroutine.Uploader.LockHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class WiFiScanning extends Thread{
    Context context;
    private String TAG ="WiFiScan";
    boolean wifiExplicitlyStarted = false;
    BufferedWriter wifiBuf;
    boolean isBufferWriterOpen = false;
    public WiFiScanning(Context context) {
        this.context = context;
        // Even if the alarm returns we ensure that the CPU does not goto sleep
       /* if((LockHandler.mCpuWakeLock!= null )&& (LockHandler.mCpuWakeLock.isHeld()==false))
        {
            LockHandler.acquireWakeLock(context).acquire();
            this.start();
        }
*/try {
            Log.i(TAG, "In the scanner of wifi");
            LockHandler.acquireWakeLock(context).acquire();
            start();
        }
        catch(Exception e)
        {
            Log.i(TAG, "error in scanning");
        }
    }

    public void run(){

        Log.i(TAG, "starting the Wi-Fi scanning thread");
        scanWiFi();
        LockHandler.releaseWakeLock();
        Log.i(TAG, "scanning done");
    }

    public void scanWiFi(){
        enableWifi(this.context);
        WifiManager wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        String wirelessNetworkName = wifiInfo.getSSID();
        String wirelessbssid =wifiInfo.getBSSID();
        //Log.w("myApp", wirelessNetworkName);
        //Log.w("myAppb", wirelessbssid);



        wifiManager.startScan();
        File logFile = new File(CommonUtils.getFilepath(Constants.WIFI_FILENAME));

        try {

            wifiBuf = new BufferedWriter(new FileWriter(logFile, true));

            isBufferWriterOpen = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            int i=0;
            int sleep_time = 5000;	// 5 seconds
            while(i<2){
                Thread.sleep(sleep_time);
                List<ScanResult> wifiList = wifiManager.getScanResults();
                if(wifiList!=null){
                    if(wifiList.isEmpty()){
                        Log.d(TAG,"Empty wifi list");
                        i++;


                        sleep_time = 3000;
                        continue;
                    }
                    for (ScanResult config : wifiList) {
                        int fre = config.frequency/1000;

                        String freq;
                        if(fre == 2)
                        {
                            freq="2.4";
                        }
                        else
                        {
                            freq= "5";
                        }
                        String time = CommonUtils.unixTimestampToString(System.currentTimeMillis());
                        //String ssids[]={"\"STUDENTS-M\"","\"STUDENTS-N\"","\"FACULTY-STAFF-N\"","\"GUEST-N\"","\"SENSOR\""};
                        String ssids[]={"STUDENTS-M","STUDENTS-N","FACULTY-STAFF-N","GUEST-N","SENSOR"};
                        Log.d("maa", config.SSID);
                        if(Arrays.asList(ssids).contains(config.SSID)) {
                            String msg = config.BSSID + "," + config.SSID + "," + config.level + "," + time + "," + wirelessNetworkName + "," + wirelessbssid + "," + freq;
                            wifiBuf.append(msg);
                            wifiBuf.newLine();
                        }
                        else {
                            String msg = "00:00:00:00:00:01" + "," + config.SSID + "," + config.level + "," + time + "," + "Unknown" + "," + "Unknown" + "," + freq;
                            wifiBuf.append(msg);
                            wifiBuf.newLine();
                        }


                    }
                }
                else{
                    String time = CommonUtils.unixTimestampToString(System.currentTimeMillis());
                    Log.d(TAG, "Null Wifi List in " + (i + 1) + " cycle");
                    String msg = "00:00:00:00:00:00" + "," + "nothing" + "," + "nothing" + "," + time + "," + "nothing" + "," + "nothing" + "," + "nothing";
                    wifiBuf.append(msg);
                    wifiBuf.newLine();

                    i++;
                    sleep_time = 3000;
                    continue;	// no scan results was found after 3 seconds. So try again. Do not iterate more than thrice
                }

                if(isBufferWriterOpen)
                    wifiBuf.close();
                break;
            }
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d(TAG, "Error in userInsideDLF "+e.getMessage());
        }
        disableWifi(this.context);
    }

    // enables WiFi if switched off
    public void enableWifi(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(true);
            wifiExplicitlyStarted = true;
        }
    }

    // disables WiFi if switched on and if it was started by our service
    public void disableWifi(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if(wifiManager.isWifiEnabled() && wifiExplicitlyStarted){
            wifiManager.setWifiEnabled(false);
            wifiExplicitlyStarted = false;
        }
    }
}

