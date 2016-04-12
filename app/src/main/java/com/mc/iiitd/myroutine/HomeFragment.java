package com.mc.iiitd.myroutine;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.mc.iiitd.myroutine.CommonThings.CommonFunctions;
import com.mc.iiitd.myroutine.CommonThings.Constants;
import com.mc.iiitd.myroutine.Uploader.BatteryBroadcastHandler;
import com.mc.iiitd.myroutine.Uploader.FileUploaderService;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
//import java.io.BufferedWriter;
import java.io.File;
//import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class HomeFragment extends Fragment implements View.OnClickListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 0;
    private static final String TAG = "Main2Activity";

    // Profile pic image size in pixels
    private static final int PROFILE_PIC_SIZE = 300;
    /**
     * A flag indicating that a PendingIntent is in progress and prevents us
     * from starting further intents.
     */
    private boolean mIntentInProgress;

    private boolean mSignInClicked;
    public static String URL ="http://muc.iiitd.edu.in:9060/";
    public static String Key ="activity_report";
    public static int USER_ID;
    private ConnectionResult mConnectionResult;
    private SignInButton btnSignIn;
    private Button btnSignOut,btnCheckUpdates,btnFileUpload,time,datasent,datacol;
    private ImageView imgProfilePic,imgAppName,imgAppIcon;
    private TextView txtName, txtEmail,txtDatacollected,resultp;
    private static TextView txtRanking,txtDatasent,txtTotaltime;
    private static TextView fav1;
    private LinearLayout llProfileLayout;

    Context context;

    SharedPreferences sharedPref;
    private int notifyID = 1555;
    NotificationManager mNotificationManager;
    private ProgressDialog ringProgressDialog;
    BatteryBroadcastHandler b;
    static int so;


    public static int minutes(String s)
    {
        if(s.equals("null"))
        {
            return 0;
        }
        int day = 0;
        if(s.contains("days"))
        {
            String[] total = s.split(" days, ");
            day = Integer.parseInt(total[0]);
            s=total[1];
        }
        else if(s.contains("day"))
        {
            String[] total = s.split(" day, ");
            day = Integer.parseInt(total[0]);
            s=total[1];
        }
        String[] hourMin = s.split(":");

        int hour = Integer.parseInt(hourMin[0]);
        int mins = Integer.parseInt(hourMin[1]);
        int hoursInMins = hour * 60;
        return hoursInMins + mins + day* 24*60;
    }

    public void init(){
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();

        mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        sharedPref= PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        // about = (Button)rootView.findViewById(R.id.about);
        resultp=(TextView)rootView.findViewById(R.id.Result);
        //btnSignOut = (Button) rootView.findViewById(R.id.btn_sign_out);
        btnFileUpload = (Button) rootView.findViewById(R.id.btn_fileupload);
        btnCheckUpdates = (Button) rootView.findViewById(R.id.btn_check_updates);
        time = (Button)rootView.findViewById(R.id.time);
        datasent = (Button)rootView.findViewById(R.id.datasent);
        datacol = (Button)rootView.findViewById(R.id.datacol);
        fav1 = (TextView)rootView.findViewById(R.id.fav1);

        imgProfilePic = (ImageView) rootView.findViewById(R.id.imgProfilePic);
        txtName = (TextView) rootView.findViewById(R.id.txtName);
        txtEmail = (TextView) rootView.findViewById(R.id.txtEmail);
        txtDatacollected = (TextView) rootView.findViewById(R.id.Result);
        txtDatasent = (TextView) rootView.findViewById(R.id.Result);
        txtRanking = (TextView) rootView.findViewById(R.id.txtRanking);
        txtTotaltime = (TextView) rootView.findViewById(R.id.txtTotalTime);
        llProfileLayout = (LinearLayout) rootView.findViewById(R.id.llProfile);


        imgAppName =(ImageView) rootView.findViewById(R.id.imgappname);
        imgAppIcon = (ImageView) rootView.findViewById(R.id.imgicon);
        context = getActivity().getApplicationContext();
        // .setOnClickListener(this);
        //btnSignOut.setOnClickListener(this);
        btnFileUpload.setOnClickListener(this);
        //btnCheckUpdates.setOnClickListener(this);

        time.setOnClickListener(this);
        datasent.setOnClickListener(this);
        datacol.setOnClickListener(this);
        Thread sw = new WiFiScanning(getActivity().getApplicationContext());
        init();
        try{
            if(getArguments().get("so")==1){
                Toast.makeText(getActivity().getApplicationContext(),"Please wait.....",Toast.LENGTH_SHORT);
            }
        }
        catch(Exception e){
            //Log.e("HomeFragment", e.toString());
        }
        String user = sharedPref.getString("USERID","");
        if(!user.isEmpty()) {
            final int userid = Integer.parseInt(user);
            new UpdateRankAsync(context).execute(userid);
        }
        PackageManager pm = context.getPackageManager();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Old App Found");
        alertDialog.setMessage("An older version of the same app is found please uninstall it.");
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        //Toast.makeText(getApplicationContext(),"You clicked on YES", Toast.LENGTH_SHORT).show();
                    }
                });
        try {
            pm.getPackageInfo("sense.routinenew", PackageManager.GET_ACTIVITIES);
            alertDialog.show();
        } catch (PackageManager.NameNotFoundException e) {
        }

        return rootView;
    }




    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
        mGoogleApiClient.connect();
    }
    public void onPause(){
        super.onPause();
        Log.d(TAG,"OnPause called of GooglePlusActivity");

        if (sharedPref.getString("USERID", "").isEmpty()){
            killnotification();
        }
        else{
            killnotification();
            startnotification();}
    }


    @Override
    public void onResume() {
        super.onResume();
        //  launchStartDialog();
        Log.d(TAG,"On Resume called of GooglePlusActivity");
        if(internetConnectionCheck() && !sharedPref.getString("USERID", "").isEmpty()){
            killnotification();
            startnotification();
        }

        else if (sharedPref.getString("USERID", "").isEmpty()){
            killnotification();
        }
    }

    public boolean internetConnectionCheck(){
        if(CommonUtils.isInternetAvailable(context)<0){
            Toast.makeText(getActivity().getApplicationContext(), "No internet connection. Please switch on your Wi-Fi or Mobile Data", Toast.LENGTH_SHORT).show();
            /*File logFile = new File(CommonUtils.getFilepath(Constants.WIFI_FILENAME));

            try {
                BufferedWriter wifiBuf;
                wifiBuf = new BufferedWriter(new FileWriter(logFile, true));
                String nu = "nothing";
                String mytime = CommonUtils.unixTimestampToString(System.currentTimeMillis());
                String msg = "00:00:00:00:00:00" + "," + nu + "," + nu + "," + mytime + "," + nu + "," + nu + "," + nu;
                Log.d("Disconnected wifi", msg);
                wifiBuf.append(msg);
                wifiBuf.newLine();
                wifiBuf.close();
              //  Toast.makeText(getActivity().getApplicationContext(),"FILE BUFFER",
                //        Toast.LENGTH_LONG).show();
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d(TAG, "Error in userInsideDLF "+e.getMessage());
            }*/
            return false;
        }
        else{
            return true;
        }
    }





    public void onConnected(Bundle bundle) {
        mSignInClicked = false;
        // Toast.makeText(getActivity().getApplicationContext(), "User is connected!", Toast.LENGTH_LONG).show();
        int gpi=0;
        checkuser();
        try {
            getProfileInformation(1);
        }
        catch(Exception e){
            e.printStackTrace();
            gpi=1;
        }
        try{
            if(getArguments().get("so")==1){
                Log.e("HomeFragment", "signoutcalled");
                if(gpi==1){
                    if(internetConnectionCheck())
                    {
                        requestlogout();
                        signOutFromGplus();
                        launchRingDialog();
                    }
                }
                else {
                    signout();
                }
                Log.e("HomeFragment", "signout");
            }
            else if(gpi==1){
                Toast.makeText(getActivity().getApplicationContext(),"Unable to contact to muc server.\nTry Later,Signing out....",Toast.LENGTH_LONG).show();
                if(internetConnectionCheck())
                {
                    requestlogout();
                    signOutFromGplus();
                    launchRingDialog();
                }
            }
        }
        catch(Exception e){
            Log.e("HomeFragment", e.toString());
        }
    }




    public void checkuser(){
        Person currentPerson = Plus.PeopleApi
                .getCurrentPerson(mGoogleApiClient);
        // Person currentPerson = Plus.PeopleApi.getCurrentPerson(mClient);
        if(currentPerson != null) {
            //Toast.makeText(getApplicationContext(), (CharSequence) currentPerson,Toast.LENGTH_LONG).show();
            String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
            String personName = currentPerson.getDisplayName();
            try {

                long timeInMillis = System.currentTimeMillis();

                final String PhoneModel = android.os.Build.MODEL;
                JSONObject userData = new JSONObject();
                userData.put("phoneModel", PhoneModel);
                userData.put("username", personName);
                userData.put("email", email);
                Log.d(TAG, userData.toString());

                String response=CommonFunctions.sendRequestToServer(userData.toString(), "login");
                if (response.contains("SUCCESS") || response.contains("ALREADY")) {
                    Log.d("testing", response);
                    String userid = response.subSequence(13, 17).toString();
                    //Log.d("testing", userid);
                    saveSharedString("USERID", userid);
                    initialsetup();
                }

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    public void initialsetup(){

        SensingController.registerSensingAlarm(getActivity().getApplicationContext());
        b = new BatteryBroadcastHandler();
        b.registerChargerPlugIn(context);
        enableBroadcastReceiver();
        startnotification();

        //Collecting wifidata when it is connected for the 1st time
        Thread sw = new WiFiScanning(getActivity().getApplicationContext());
        // sw.run();
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode,
                                 Intent intent) {

        if (requestCode == RC_SIGN_IN) {
            if (responseCode != getActivity().RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
        Log.i(TAG, "onConnectSuspended");
        //updateUI(false);
    }


    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),getActivity(),
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

    /**
     * Updating the UI, showing/hiding buttons and profile layout
     */


    /**
     * Sign-in into google
     */
    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
            // launchLoginDialog();
            Log.i(TAG, "you just signed in using Google plus");

        }
    }

    /**
     * Sign-out from google
     */
    public void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();

            // SensingController.unregisterAlarm(getApplicationContext());

        }
    }
    /**
     * Method to resolve any signin errors
     */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(getActivity(), RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    /**
     * �����* Fetching user's information name, email, profile pic
     * �����*
     */

    private void getProfileInformation(int x) {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                Log.e(TAG, "Name: " + personName + ", plusProfile: " + personGooglePlusProfile + ", email: " + email + ", Image: " + personPhotoUrl);

                txtName.setText(personName);
                txtEmail.setText(email);

                if(!sharedPref.contains("UID")){
                    int Applicationid = getUID();
                    saveSharedint("UID", Applicationid);}

                txtName.setText(personName);
                txtEmail.setText(email);

                long DS = getDataValue(context);
                long DC = getFolderSize() + DS;
                String DataS = l2bytes(DS,false);
                String DataC = l2bytes(DC, false);
                if(CommonUtils.startTime==0L && x==3){
                    resultp.setText("Data Collected this run :" + CommonUtils.setDuration(0L) );
                }
                else{
                    long currenttime = System.currentTimeMillis();
                    long timecollected = currenttime-CommonUtils.startTime;
                    //Toast.makeText(getApplicationContext(), Long.toString(CommonUtils.startTime) +"  "+ Long.toString(currenttime) + "  "+ Long.toString(timecollected), Toast.LENGTH_LONG).show();
                    if(x==3) {
                        resultp.setText("Data Collected this run :" + CommonUtils.setDuration(timecollected));
                    }
                }


                long hours=CommonUtils.totaltime/3600;
                long minutes=(CommonUtils.totaltime%3600)/60;
                long seconds= (long) ((CommonUtils.totaltime%3600)%60);
                if(x==2)
                {
                    resultp.setText("Data Sent :" + DataS);
                }
                else if (x==1)
                {
                    resultp.setText("Total Time Collected : " + hours + "h " + minutes + "m " + seconds + "s");
                }
                else if(x==1)
                {
                    resultp.setText(CommonUtils.Rank + " as on " + CommonUtils.lastRankTime);
                }
                //txtDatacollected.clearAnimation();

                // by default the profile url gives 50x50 px image only
                // we can replace the value with whatever dimension we want by
                // replacing sz=X
                personPhotoUrl = personPhotoUrl.substring(0, personPhotoUrl.length() - 2) + PROFILE_PIC_SIZE;
                new LoadProfileImage(imgProfilePic).execute(personPhotoUrl);
                //Use this information to login to register to RoutineSense cloud
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(x==1)
        {
            //Toast.makeText(getActivity().getApplicationContext(),"how are you",Toast.LENGTH_LONG).show();
            SharedPreferences sharedPref;
            sharedPref= PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            String user = sharedPref.getString("USERID", "");
            // System.out.println("starting");
            try{
                USER_ID = Integer.parseInt(user);
                String serverResponse = "null";
                // Toast.makeText(getActivity().getApplicationContext(),"2nd try",Toast.LENGTH_LONG).show();
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(URL + Key + "?userid=" + String.valueOf(USER_ID));
                Date dNow = new Date();
                SimpleDateFormat ft =
                        new SimpleDateFormat ("yyyy-MM-dd");
                String today = ft.format(dNow);
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, -7);
                Date yesterday = calendar.getTime();



                String weekdate = ft.format(yesterday);
                JSONObject userData = new JSONObject();

                //Toast.makeText(getActivity().getApplicationContext(), "Current Date: " + weekdate, Toast.LENGTH_LONG).show();

                userData.put("start_time", weekdate);
                userData.put("end_time", today);
                // System.out.println(userData.toString());
                httppost.setEntity(new StringEntity(userData.toString()));

                HttpResponse response = httpclient.execute(httppost);
                BufferedReader BuffRead = new BufferedReader( new InputStreamReader(response.getEntity().getContent(),"UTF-8") );
                serverResponse = BuffRead.readLine();
                //tv = (TextView)rootView.findViewById(R.id.response);
                String onfoot = "null";
                //Toast.makeText(getActivity().getApplicationContext(),onfoot,Toast.LENGTH_LONG).show();
                String still = "null";
                //Toast.makeText(getActivity().getApplicationContext(),still,Toast.LENGTH_LONG).show();
                String tilting = "null";
                //Toast.makeText(getActivity().getApplicationContext(),tilting,Toast.LENGTH_LONG).show();
                String vehicle = "null";
                String cycle = "null";
                JSONObject object = (JSONObject) new JSONTokener(serverResponse).nextValue();
                if(object.has("ON_FOOT"))
                {
                    onfoot = object.getString("ON_FOOT");
                }
                if(object.has("STILL"))
                {
                    still = object.getString("STILL");
                }
                if(object.has("TILTING"))
                {
                    tilting = object.getString("TILTING");
                }
                if(object.has("IN_VEHICLE"))
                {
                    vehicle = object.getString("IN_VEHICLE");
                }
                if(object.has("ON_BICYCLE"))
                {
                    cycle = object.getString("ON_BICYCLE");
                }

                int[] result = new int[5];

                int foot = minutes(onfoot);
                int  stil = minutes(still);
                int tilt = minutes(tilting);
                int veh =  minutes(vehicle);
                int cyc = minutes(cycle);
                result[0]=foot +cyc;
                result[1]=stil + tilt;
                result[2]=veh;
                //Toast.makeText(getActivity().getApplicationContext(),"COOL" + tilt + veh + cyc,Toast.LENGTH_LONG).show();






                String ex = "Exercise : " + timestring(result[0]) + " (hh:mm) \n";
                String id = "Idle : " + timestring(result[1]) + " (hh:mm) \n";
                String ve = "In a vehicle: " + timestring(result[2]) + " (hh:mm)  \n";
                fav1.setText(ex + id+ ve);

            } catch(HttpHostConnectException e) {
                Log.v(e.toString(), "index=" + 1);
                //tv.setText(e.toString());
                //Toast.makeText(context, "Unable to connect to server. Please try after sometime", Toast.LENGTH_LONG).show();

            } catch(Exception error) {
                // System.out.println(error.toString());
                Log.v(error.toString(), "index=" + 1);
            }

        }
    }

    /**
     * �����* Background Async task to load user profile picture from url
     * �����*
     */

    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        return super.onOptionsItemSelected(item);

    }






    public String timestring(int result)
    {
        long hours = TimeUnit.MINUTES.toHours(result);
        long remainMinute = result - TimeUnit.HOURS.toMinutes(hours);
        return(String.format("%02d", hours) + ":"
                + String.format("%02d", remainMinute));


    }

    public void startnotification(){

        int pid = android.os.Process.myPid();
        Intent notiintent = new Intent(getActivity(), MainActivity.class);

        notiintent.setAction(Long.toString(System.currentTimeMillis()));
        int requestID = (int) System.currentTimeMillis();
        PendingIntent pIntent = PendingIntent.getActivity(getActivity(), requestID, notiintent,PendingIntent.FLAG_UPDATE_CURRENT);

        // Sets an ID for the notification, so it can be updated
        mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(getActivity())
                .setContentTitle("RoutineSense")
                .setContentIntent(pIntent)
                .setOngoing(true);

        mNotifyBuilder.setSmallIcon(R.drawable.ic_launcher);

        //Log.d(TAG,"PID = " +  android.os.Process.myPid() + " " + String.valueOf(pid));

        mNotifyBuilder.setContentText("Collecting Data");

        Notification temp = mNotifyBuilder.build();
        temp.flags = Notification.FLAG_ONGOING_EVENT;
        mNotificationManager.notify(notifyID,temp);
    }

    public void killnotification(){
        NotificationManager mNotificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
    }

    public int getUID(){
        int UID = 0;

        final PackageManager pm = getActivity().getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(
                PackageManager.GET_META_DATA);

        //loop through the list of installed packages and see if the selected
        //app is in the list
        for (ApplicationInfo packageInfo : packages) {
            if(packageInfo.packageName.equals("sense.routinenew")){
                //get the UID for the selected app
                UID = packageInfo.uid;
                break; //found a match, don't need to search anymore
            }
        }
        return UID;
    }


    public void launchRingDialog() {

        final ProgressDialog ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...",	"No more SENSING now...", true);
        ringProgressDialog.setCancelable(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SensingController.unregisterAlarm(context.getApplicationContext());
                    b.unregisterChargerPlugIn(context.getApplicationContext());
                    killnotification();
                    disableBatteryBroadcastReceiver();
                    disableBroadcastReceiver();
                    Intent stopar = new Intent(getActivity().getApplicationContext(),ActivityRecognitionIntentService.class);
                    getActivity().stopService(stopar);
                    Intent stopdatacol = new Intent(getActivity().getApplicationContext(),SensorService.class);
                    getActivity().stopService(stopdatacol);

                    //BatteryBroadcastHandler b = new BatteryBroadcastHandler();
                    //b.unregisterChargerPlugIn(getApplicationContext());
                    final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.clear();
                    editor.commit();
                    Thread.sleep(3555);
                } catch (Exception e) {
                }

                ringProgressDialog.dismiss();
                Intent i = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                Log.d("logout", "Called stopping services");
                // clear top clears all activities in stack - only if we have
                // not cleared explicitly
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                getActivity().finish();
            }
        }).start();
    }

    private void saveSharedString(String key,String value){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public boolean requestlogout(){
        Person currentPerson = Plus.PeopleApi
                .getCurrentPerson(mGoogleApiClient);
        String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
        String response = null;
        try {

            JSONObject userData = new JSONObject();
            userData.put("email", email);
            Log.d(TAG, userData.toString());
            response = CommonFunctions.sendRequestToServer(userData.toString(), "logout");
            if (response.contains("DONE")) {
                return true;
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {


            case R.id.time:
                getProfileInformation(1);
                break;

            case R.id.datasent:
                getProfileInformation(2);
                break;

            case R.id.datacol:
                getProfileInformation(3);
                break;

            case R.id.btn_sign_in:
                // Signin button clicked
                if(internetConnectionCheck()){
                    Log.i(TAG, "sign in button clicked");
                    signInWithGplus();
                }
                break;



            case R.id.btn_fileupload:
                //Force File Upload
                if(internetConnectionCheck()){
                    String FILE_UPL = "fileupload";
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean(FILE_UPL, true);
                    editor.commit();
                    CommonFunctions.CompressandSend();
                    SensingController.unregisterAlarm(context.getApplicationContext());
                    final Runnable runnable = new Runnable() {
                        public void run() {
                            // TODO Auto-generated method stub
                            {
                                try {
                                    Thread.sleep(10000);
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                                Log.d(TAG,"Going to start File upload service");

                                String FILE_UPL = "fileupload";
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putBoolean(FILE_UPL, true);
                                editor.commit();
                                CommonFunctions.CompressandSend();
                                startFileUpload();
                            }
                        }
                    };
                    new Thread(runnable).start();

                    txtDatasent.setText("Uploading Data");
                    txtDatasent.setAnimation(getBlinkAnimation());
                }

                else{
                    Toast.makeText(getActivity().getApplicationContext(),"Network Not available...",
                            Toast.LENGTH_LONG).show();
/*
                    File logFile = new File(CommonUtils.getFilepath(Constants.WIFI_FILENAME));

                    try {
                        BufferedWriter wifiBuf;
                        wifiBuf = new BufferedWriter(new FileWriter(logFile, true));
                        String nu = "nothing";
                        String mytime = CommonUtils.unixTimestampToString(System.currentTimeMillis());
                        String msg = "00:00:00:00:00:00" + "," + nu + "," + nu + "," + mytime + "," + nu + "," + nu + "," + nu;
                        Log.d("Disconnected wifi", msg);
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
					*/
                }
                break;
            case R.id.btn_sign_out:
                // Signout button clicked
                signout();
                break;

            case R.id.btn_check_updates:
                // Checking for updates
                try{
                    Intent viewIntent =
                            new Intent("android.intent.action.VIEW",
                                    Uri.parse("https://play.google.com/store/apps/details?id=com.mc.iiitd.myroutine"));
                    startActivity(viewIntent);
                }

                catch(Exception e) {
                    Toast.makeText(getActivity().getApplicationContext(),"Unable to Connect Try Again...",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

                break;
        }
    }

    public void signout(){
        if(internetConnectionCheck() && requestlogout())
        {
            signOutFromGplus();
            launchRingDialog();
        }
    }
    public static long getDataValue(Context context){
        SharedPreferences sharedPref= PreferenceManager.getDefaultSharedPreferences(context);
        int UID = sharedPref.getInt("UID", 0);
        return(TrafficStats.getUidTxBytes(UID));
    }

    public static long getFolderSize() {
        File dir = new File(Environment.getExternalStorageDirectory().getPath()+"/RoutineSenApp/");
        long size = 0;
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                // System.out.println(file.getName() + " " + file.length());
                size += file.length();
            }
        }
        return size;
    }

    public static String l2bytes(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    private void saveSharedint(String key, int value) {
        // TODO Auto-generated method stub
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void enableBroadcastReceiver(){

        ComponentName receiver1 = new ComponentName(getActivity().getApplicationContext(), WiFiBroadcastListener.class);

        PackageManager pm = getActivity().getApplicationContext().getPackageManager();

        pm.setComponentEnabledSetting(receiver1,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void disableBroadcastReceiver(){

        ComponentName receiver1 = new ComponentName(getActivity().getApplicationContext(), WiFiBroadcastListener.class);

        PackageManager pm = getActivity().getApplicationContext().getPackageManager();

        pm.setComponentEnabledSetting(receiver1,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void disableBatteryBroadcastReceiver(){

        ComponentName receiver1 = new ComponentName(getActivity().getApplicationContext(),BatteryBroadcastHandler.class);

        PackageManager pm = getActivity().getApplicationContext().getPackageManager();

        pm.setComponentEnabledSetting(receiver1,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }


    private void startFileUpload() {
        Intent intent = new Intent(context, FileUploaderService.class);
        getActivity().startService(intent);
    }

    public Animation getBlinkAnimation(){
        Animation animation = new AlphaAnimation(1, 0);         // Change alpha from fully visible to invisible
        animation.setDuration(600);                             // duration - half a second
        animation.setInterpolator(new LinearInterpolator());    // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE);                            // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE);             // Reverse animation at the end so the button will fade back in

        return animation;
    }

    public static void updateRankUI(String reply,Context context){

        if(reply==null){
            txtRanking.setText("Please connect to network");
            fav1.setText("Please Connect to network");
            return;
        }
        String time[] = reply.split(",");
        long totaltime = Long.parseLong(time[0]);
        Log.v(TAG,time[0]);
        if(totaltime!=-1){
            CommonUtils.totaltime = totaltime;
            CommonUtils.Rank = (int)Float.parseFloat(time[1]);
            CommonUtils.lastRankTime = CommonUtils.unixTimestampToString(System.currentTimeMillis());
            long hours=totaltime/3600;
            long minutes=(totaltime%3600)/60;
            long seconds= (long) ((totaltime%3600)%60);
            long DS = getDataValue(context);
            String DataS = l2bytes(DS,false);

            txtRanking.setText("Rank Among all the users: " + CommonUtils.Rank +"\nData Collected till:\n" + CommonUtils.lastRankTime +"\nTotal Users: "+time[2]);
            txtDatasent.clearAnimation();
            txtDatasent.setText("");
        }
    }


    public static void updateRankfromService(Context context,int userid){
        new UpdateRankAsync(context).execute(userid);
    }
}

class UpdateRankAsync extends AsyncTask<Integer, Void, String> {
    Context context;

    public UpdateRankAsync(Context context){
        this.context = context;
    }

    protected String doInBackground(Integer... urls) {
        int userid = urls[0];
        String ACCL_URL = Constants.URL;
        String rank = null;
        rank=RankUpdate(userid, ACCL_URL, "rank");
        return rank;
    }

    protected void onPostExecute(String rank) {
        //Toast.makeText(getApplicationContext(), "Uploading Done="+result, Toast.LENGTH_SHORT).show();
        HomeFragment.updateRankUI(rank, context);
    }

    public String RankUpdate(int USER_ID , String CONN_URL, String Key ) {

        String serverResponse = null;
        try {
            Log.i("HomeFragment", "Inside RequestRankUpdate");
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(CONN_URL + Key + "?userid=" + String.valueOf(USER_ID));
            //Log.d("testing connection", CONN_URL);

            HttpResponse response = httpclient.execute(httppost);
            BufferedReader BuffRead = new BufferedReader( new InputStreamReader(response.getEntity().getContent(),"UTF-8") );
            serverResponse = BuffRead.readLine();
            Log.v("Rank",serverResponse);
        } catch (HttpHostConnectException e) {
            Log.e("HttpHostConnectE", e.toString());
            //Toast.makeText(context, "Unable to connect to server. Please try after sometime", Toast.LENGTH_LONG).show();

        } catch (Exception error) {
            Log.i("Exception", error.toString());
        }
        return serverResponse;
    }
}