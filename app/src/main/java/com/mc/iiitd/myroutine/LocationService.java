package com.mc.iiitd.myroutine;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.mc.iiitd.myroutine.CommonThings.Constants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LocationService extends Service implements LocationListener,
        //GooglePlayServicesClient.ConnectionCallbacks,
        //GooglePlayServicesClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {
    private GoogleApiClient mGoogleApiClient;
    private String TAG = "LocationService";
    private LocationRequest mLocationRequest;
    BufferedWriter locBuf;
    boolean isBufferWriterOpen = false;
    private boolean mRunning;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mRunning = false;
        int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resp == ConnectionResult.SUCCESS){
            Log.d(TAG, "Google Play service available");
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        else{
            Log.d(TAG,"Google play service not installed");
            this.onDestroy();
        }
        File logFile = new File(CommonUtils.getFilepath("locationLog.txt"));
        try {
            locBuf = new BufferedWriter(new FileWriter(logFile, true));
            isBufferWriterOpen = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!mRunning) {
            mRunning = true;
            mGoogleApiClient.connect();

        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
        if(isBufferWriterOpen) {
            try {
                locBuf.close();
                Log.i(TAG, "BufferedWriter closed");
                isBufferWriterOpen = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG,"onConnectionSuspended");

    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5*60*1000); // Update location every 10 second
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }


    public void onDisconnected() {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, Double.toString(location.getLatitude()));
        Log.i(TAG, Double.toString(location.getLongitude()));
        //mLocationView.setText("Location received: " + location.toString());
        long timeInMillis = System.currentTimeMillis();
        String msg =  Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude())+","+ Double.toString(location.getAltitude())+ "," + CommonUtils.unixTimestampToString(timeInMillis);
        saveDataInFile(msg);

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        File logFile = new File(CommonUtils.getFilepath(Constants.WIFI_FILENAME));

        try {
            BufferedWriter wifiBuf;
            wifiBuf = new BufferedWriter(new FileWriter(logFile, true));
            String nu = "nothing";
            String mytime = CommonUtils.unixTimestampToString(System.currentTimeMillis());
            String msg = "00:00:00:00:00:00" + "," + nu + "," + nu + "," + mytime + "," + nu + "," + nu + "," + nu;
            Log.d("wifi log disconnected", msg);
            wifiBuf.append(msg);
            wifiBuf.newLine();
            wifiBuf.close();
            // Toast.makeText(getActivity().getApplicationContext(),"FILE BUFFER",
            //Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d(TAG, "Error in userInsideDLF "+e.getMessage());
        }
    }


    /**
     * Saves line in filename file
     * ***/
    public void saveDataInFile(String line){
        Log.i(TAG,"line "+ line);
        try{

            locBuf.append(line);
            locBuf.newLine();
        }
        catch (IOException e){
            Log.e(TAG, e.getLocalizedMessage());

        }
    }


}
