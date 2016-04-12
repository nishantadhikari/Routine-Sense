package com.mc.iiitd.myroutine;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;

import com.mc.iiitd.myroutine.CommonThings.Constants;

import java.io.BufferedWriter;
import java.util.Arrays;


public class WiFiBroadcastListener extends BroadcastReceiver {
    public String TAG ="WiFiBroadcastListener";
    public BufferedWriter wifiBuf;
    private static final int ALARM_ID = 40791;
    @Override
    public void onReceive(final Context mContext, Intent intent) {
        Log.i(TAG,"wifi related broadcast received "+ intent.getAction());
        Thread sw = new WiFiScanning(mContext);
        //sw.run();
        WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        //  if (!wifi.isWifiEnabled()){
        {
            Log.i(TAG, "wifi disabled");
            WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo wifiInfo = wifiManager.getConnectionInfo();

            //Putting a timer to check if is not connected to wifi back within
            final CountDownTimer Counter1 = new CountDownTimer(14400000, 100000) {

                public void onTick(long millisUntilFinished) {
                    if (!Arrays.asList(Constants.COLLEGEWIFIAP).contains(removeQuot(wifiInfo.getSSID()))
                            && wifiInfo.getSSID() != null) {
                        //onFinish();
                        Log.i(TAG, "ticker");
                        Thread sw = new WiFiScanning(mContext);


                    }
                }

                public void onFinish() {
                    Log.i(TAG, "Onfinish called Turning Sensing OFF");

                    //        SensingController.unregisterAlarm(mContext.getApplicationContext());
                    this.start();
                }
            }.start();
//        }
        }

        //else
        if(wifi.isWifiEnabled()){
            Log.i(TAG, "Connected");
            WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);

            //NetworkInfo nwInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            //Check if is not connected to Wifi AP inside IIITD stop sensing

            boolean alarmUp = (PendingIntent.getBroadcast(mContext.getApplicationContext(),ALARM_ID,
                    new Intent(mContext.getApplicationContext(), SensingController.class),
                    PendingIntent.FLAG_NO_CREATE) != null);

            Log.i(TAG,wifiInfo.getSSID());
            Log.i(TAG, "Alarm Status = " + String.valueOf(alarmUp));
            if(Arrays.asList(Constants.COLLEGEWIFIAP).contains(removeQuot(wifiInfo.getSSID()))){
                String message = wifiInfo.getSSID()+","+ wifiInfo.getBSSID()+ "," + wifiInfo.getRssi();
                Log.d(TAG, message);

                Thread swe = new WiFiScanning(mContext);
                //sw.run();

                if(!alarmUp){
                    Log.i(TAG,"Alarm was off");
                    SensingController.registerSensingAlarm(mContext.getApplicationContext());
                }
            }

            else{
                Log.i(TAG,"Turning Sensing OFF");
                SensingController.unregisterAlarm(mContext.getApplicationContext());
            }
        }


    }



    public String removeQuot(String ssid){
        int deviceVersion= Build.VERSION.SDK_INT;

        if (deviceVersion >= 17){
            if (ssid.startsWith("\"") && ssid.endsWith("\"")){
                ssid = ssid.substring(1, ssid.length()-1);
            }
        }

        return ssid;

    }

}
