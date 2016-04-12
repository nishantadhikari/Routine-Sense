package com.mc.iiitd.myroutine;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ActivityRecognitionIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    private String TAG = "ActivityRecognitionIntentService";
    public ActivityRecognitionIntentService() {
        super("hello");
    }
    BufferedWriter activityBuf;
    boolean isBufferWriterOpen = false;
    @Override
    protected void onHandleIntent(Intent intent) {
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        String msg = CommonUtils.unixTimestampToString(result.getTime()) +","+getType(result.getMostProbableActivity().getType()) +"," + result.getMostProbableActivity().getConfidence();
        Log.i(TAG, msg);
        saveDataInFile(msg);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"ActivityRecognition onCreate");
        File logFile = new File(CommonUtils.getFilepath("activityLog.txt"));
        try {
            activityBuf = new BufferedWriter(new FileWriter(logFile, true));
            isBufferWriterOpen = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(isBufferWriterOpen) {
            try {
                activityBuf.close();
                Log.i(TAG, "BufferedWriter closed");
                isBufferWriterOpen = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int getType(int type){
        if(type == DetectedActivity.UNKNOWN)
            return 0;
        else if(type == DetectedActivity.IN_VEHICLE)
            return 1;
        else if(type == DetectedActivity.ON_BICYCLE)
            return 2;
        else if(type == DetectedActivity.ON_FOOT)
            return 3;
        else if(type == DetectedActivity.STILL)
            return 4;
        else if(type == DetectedActivity.TILTING)
            return 5;
        else
            return -1;
    }

    /**
     * Saves line in filename file
     * ***/
    public void saveDataInFile(String line){
        try{

            activityBuf.append(line);
            activityBuf.newLine();
        }
        catch (IOException e){
            Log.e(TAG, e.getLocalizedMessage());

        }
    }
}
