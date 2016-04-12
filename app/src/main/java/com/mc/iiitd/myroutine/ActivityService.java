package com.mc.iiitd.myroutine;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

public class ActivityService extends Service implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {
    boolean isActivityServiceRunning;
    private GoogleApiClient mGoogleApiClientActivity;
    String TAG = "ActivityService";
    Context context;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isActivityServiceRunning = false;
        Log.i(TAG,"OnCreate+++++++++++");
        context = getApplicationContext();
        int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resp == ConnectionResult.SUCCESS){
            Log.d(TAG, "Google Play service available");
            mGoogleApiClientActivity = new GoogleApiClient.Builder(this)
                    .addApi(ActivityRecognition.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        else{
            Log.d(TAG,"Google play service not installed");
            this.onDestroy();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!isActivityServiceRunning){
            isActivityServiceRunning = true;
            Log.i(TAG,"onStartCommand+++++");
            mGoogleApiClientActivity.connect();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        Intent intent = new Intent(context, ActivityRecognitionIntentService.class);
        PendingIntent  mpIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mGoogleApiClientActivity,mpIntent);
      	stopService(intent);
      	mpIntent.cancel();
      	if(mGoogleApiClientActivity.isConnected()){
      	mGoogleApiClientActivity.disconnect();
		}	
    }

    @Override
    public void onConnected(Bundle bundle) {

        Intent intent = new Intent(context, ActivityRecognitionIntentService.class);
        PendingIntent  mpIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGoogleApiClientActivity, 10*1000, mpIntent);

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG,"onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

    }
}
