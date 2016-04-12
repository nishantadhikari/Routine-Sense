package com.mc.iiitd.myroutine.Uploader;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.util.Log;

public class LockHandler {
    /** Wake lock to be acquired before running the manager service*/
    public static PowerManager.WakeLock mCpuWakeLock = null;

    // create a class member variable.
    public static WifiManager.WifiLock mWifiLock = null;
    /***
     * Calling this method will aquire the lock on wifi. This is avoid wifi
     * from going to sleep as long as <code>releaseWifiLock</code> method is called.
     **/
    public static WifiManager.WifiLock getWifiLock(Context mContext) {

        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);

        if( mWifiLock == null )
            mWifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, "Inside DLF");

        mWifiLock.setReferenceCounted(false);

        return mWifiLock;
    }

    /***
     * Calling this method will release if the lock is already help. After this method is called,
     * the Wifi on the device can goto sleep.
     **/
    public static void releaseWiFiLock() {
        try {
            if (mWifiLock == null)
                Log.w("WiFi Lock", "#releaseWifiLock mWifiLock was not created previously");

            if (mWifiLock != null && mWifiLock.isHeld()) {
                mWifiLock.release();
                //mWifiLock = null;
            }
        }
        catch (Exception e)
        {
            Log.w("WiFi Lock", "error in wifi locks");
        }
    }
    /** Acquires the wake lock */
    public static PowerManager.WakeLock acquireWakeLock(Context context) {

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        //mCpuWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, WAKELOCK_TAG);
        // mCpuWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE ,"Test if user is indide DLF");
        mCpuWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Test if user is inside DLF");

        return mCpuWakeLock;

    }



    /** Release the wake lock */
    public static void releaseWakeLock() {
        try {
            if (mCpuWakeLock != null) {
                if (mCpuWakeLock.isHeld()) {
                    mCpuWakeLock.release();
                }
                mCpuWakeLock = null;
            }
        }
        catch(Exception e)
        {
            Log.w("WiFi Lock", "error in wifi locks");
        }
    }

}
