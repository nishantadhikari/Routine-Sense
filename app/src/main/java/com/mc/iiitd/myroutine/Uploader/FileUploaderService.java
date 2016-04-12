package com.mc.iiitd.myroutine.Uploader;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.mc.iiitd.myroutine.CommonThings.CommonFunctions;
import com.mc.iiitd.myroutine.CommonThings.Constants;
import com.mc.iiitd.myroutine.CommonUtils;
import com.mc.iiitd.myroutine.MainActivity;
import com.mc.iiitd.myroutine.SensingController;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
//import org.apache.http.entity.mime.MultipartEntityBuilder.*;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class FileUploaderService extends Service implements OnSharedPreferenceChangeListener {
    public static String ACCL_DATA ="accl";
    public static String GYRO_DATA ="gyro";
    public static String SOUND_DATA="sound";
    public static String WIFI_DATA="wifi";
    private static int filesize;
    public String files2DB[];
    private static int counter;
    private static String TAG = "FileUploaderService";
    String FILE_UPL ="fileupload";
    SharedPreferences sharedPref;
    @Override

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        filesize = 0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        counter =0;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.registerOnSharedPreferenceChangeListener((OnSharedPreferenceChangeListener) this);
        if(!sharedPref.getBoolean(FILE_UPL, false))
        {
            //Toast.makeText(getApplicationContext(), "FILEUP st=" + sharedPref.getBoolean(FILE_UPL, false),Toast.LENGTH_SHORT).show();
            Log.d(TAG,"FILE_UPL");
            stopSelf();
            onDestroy();
        }

        //check if the server is ready for upload
        if(!CommonFunctions.sendRequestToServer("1", "home").equals("ONLINE")){
            Log.i(TAG,"Some problem with the server");
        }
        else{
            Log.i(TAG,"Server is up for connection");
        }
        // If yes fetch the files to be uploaded and start uploading
        String[] f = CommonFunctions.listFilesToUpload();
        files2DB = new String[f.length+1];
        sharedPref= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String user = sharedPref.getString("USERID","");
        if(!user.isEmpty() && f.length>0){
            final int userid = Integer.parseInt(user);
            new UploadZipAsync(userid,f).execute();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    private boolean UploadFile(int user_id, String fileName){
        String ACCL_URL = Constants.URL;
        File externalStorageAccl = Environment.getExternalStorageDirectory();
        String AcclPath = externalStorageAccl.getAbsolutePath()+File.separator+Constants.MEDIA_DIR_NAME+File.separator+fileName;
        String AcclResult = null;
        String KEY ="";
        if(fileName.startsWith(ACCL_DATA)){
            ACCL_URL = ACCL_URL+"accl";
            KEY ="AcclFile";
        }

        else if(fileName.startsWith(GYRO_DATA)){
            ACCL_URL = ACCL_URL+"gyro";
            KEY = "GyroFile";
        }

        else if(fileName.startsWith(SOUND_DATA)){
            ACCL_URL = ACCL_URL+"sound";
            KEY = "SoundFile";
        }
        else if(fileName.startsWith(WIFI_DATA)){
            ACCL_URL = ACCL_URL+"wifi";
            KEY = "WifiFile";
        }

        try{
            Log.i(TAG,"going to upload at " + ACCL_URL);
            AcclResult = ContactMUC(AcclPath, user_id, ACCL_URL, KEY);
            if( AcclResult != null ) {
                if( AcclResult.contains("SUCCESS") ){
                    //Delete the file & Sending request to update db
                    File delfile = new File(AcclPath);
                    delfile.delete();
                    Log.i(TAG,AcclResult);
                    String temp[]= AcclResult.split(":");
                    //Sending request to server for updating db
                    String filename = temp[1];
                    filesize++;
                    Log.i(TAG,"Filename"+filename);
                    Log.i(TAG,"Filesize"+String.valueOf(filesize));
                    Log.i(TAG,"Files2DBsize"+String.valueOf(files2DB.length));
                    files2DB[filesize] = filename;
                    Log.i(TAG,"size="+String.valueOf(filesize)+filename);}
                else{
                    Log.i(TAG,"Upload is not successful");
                    return false;
                }
            }
        }
        catch ( Exception error ){
            Log.w(TAG,error.toString());
        }
        return true;
    }
    public String ContactMUC( String FilePath , int USER_ID , String CONN_URL, String Key ) {
        String Data = null;
        try {
            Log.i(TAG, "Inside ContactMUC");
            HttpParams httpParameters = new BasicHttpParams();
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(CONN_URL + "?userid=" + Integer.toString(USER_ID));
            Log.d("testing connection", CONN_URL);
            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            FileBody bin = new FileBody(new File(FilePath));
            reqEntity.addPart(Key, bin);
            httppost.setEntity(reqEntity);
            HttpResponse response = null;
            response = httpclient.execute(httppost);
            BufferedReader BuffRead = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            Data = BuffRead.readLine();
            Log.d("testing", Key + Data);
        } catch (HttpHostConnectException e) {
            Log.e("HttpHostConnectE ", e.toString());
            //Toast.makeText(context, "Unable to connect to server. Please try after sometime", Toast.LENGTH_LONG).show();
            return  null;
        } catch (Exception error) {
            Log.i("Exception", error.toString());
            return null;
        }

        return Data;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        // TODO Auto-generated method stub
        Log.i("info","shared preference changed listener!!!!");
        if(key.equalsIgnoreCase(FILE_UPL))
        {
            if(!sharedPreferences.getBoolean(key, false))
            {
                //Toast.makeText(getApplicationContext(), "FILEUP prf=" + sharedPreferences.getBoolean(FILE_UPL, false),Toast.LENGTH_SHORT).show();
                Log.d(TAG,"fileupload false");
                stopSelf();
                onDestroy();
            }

        }
    }

    public boolean RequestDBUpdate( String Filename , int USER_ID , String CONN_URL, String Key ) {

        String serverResponse = "null";
        try {
            Log.i(TAG, "Inside RequestDBUpdate");
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(CONN_URL + Key + "?userid=" + Integer.toString(USER_ID)
                    +"&filename=" + Filename);
            //Log.d("testing connection", CONN_URL);

            HttpResponse response = null;
            response = httpclient.execute(httppost);

            BufferedReader BuffRead = new BufferedReader( new InputStreamReader(response.getEntity().getContent(),"UTF-8") );
            serverResponse = BuffRead.readLine();

        } catch (HttpHostConnectException e) {
            Log.e("HttpHostConnectE", e.toString());
            //Toast.makeText(context, "Unable to connect to server. Please try after sometime", Toast.LENGTH_LONG).show();

        } catch (Exception error) {
            Log.i("Exception", error.toString());
        }
        if(serverResponse.equals("Success")){
            return true;
        }
        else{
            return false;
        }
    }


    private class LoadZip2DB extends AsyncTask<Integer, Void, Boolean> {

        protected Boolean doInBackground(Integer... urls) {
            int userid = urls[0];
            counter = 0;
            try {
                String ACCL_URL = Constants.URL;
                for(int i=0;i<files2DB.length;i++){
                    if(RequestDBUpdate(files2DB[i], userid, ACCL_URL, "updateDB")){
                        counter++;
                    }
                }
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            if(counter==files2DB.length)return true;
            return false;
        }

        protected void onPostExecute(Boolean result) {
            //Toast.makeText(getApplicationContext(), "ZIP2DB Done="+result, Toast.LENGTH_SHORT).show();
            if(result){
                CommonUtils.startTime=System.currentTimeMillis();
                sharedPref= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                Editor editor = sharedPref.edit();
                editor.putBoolean(FILE_UPL, false);
                editor.commit();
                SensingController.registerSensingAlarm(getApplicationContext());
                String user = sharedPref.getString("USERID","");
                final int userid = Integer.parseInt(user);
                MainActivity.updateRankfromService(getApplicationContext(), userid);
            }
        }

    }

    private class UploadZipAsync extends AsyncTask<Void, Void, Integer> {

        String[] f;
        int userid;

        public UploadZipAsync(int userid,String[] f){
            this.userid = userid;
            this.f = f;
        }

        protected Integer doInBackground(Void... urls) {
            //int userid = urls[0];
            try {
                counter = 0;
                for(int i=0; i< f.length;i ++){
                    //UploadFile(18768,f[i]);
                    if(UploadFile(userid, f[i])){
                        counter++;
                    }
                    Log.i(TAG,f[i]);
                }
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            if(counter==f.length)return userid;
            return -1;
        }

        protected void onPostExecute(Integer userid) {
            //Toast.makeText(getApplicationContext(), "Uploading Done="+result, Toast.LENGTH_SHORT).show();

            if(userid!=-1){
                new LoadZip2DB().execute(userid);
            }
        }

    }
}