package com.mc.iiitd.myroutine;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CalendarAct extends Fragment {
    private boolean mSignInClicked;
    public static String URL ="http://muc.iiitd.edu.in:9060/";
    public static String Key ="activity_report";
    public static int USER_ID;
    CalendarView calendar;
    private  String ex;
    private  String id;
    private  String ve;
    private  String total;
    public String timestring(int result)
    {
        long hours = TimeUnit.MINUTES.toHours(result);
        long remainMinute = result - TimeUnit.HOURS.toMinutes(hours);
        return(String.format("%02d", hours) + ":"
                + String.format("%02d", remainMinute));


    }
    public CalendarAct() {
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);
		if(CommonUtils.isInternetAvailable(getActivity().getApplicationContext())<0){
            //Toast.makeText(getActivity().getApplicationContext(), "No internet connection. Please switch on your Wi-Fi or Mobile Data", Toast.LENGTH_SHORT).show();
            Toast.makeText(getActivity().getApplicationContext(), "Internet Connectivity Required: Unable to connect to server. Please try after sometime", Toast.LENGTH_LONG).show();
        }
        calendar = (CalendarView) rootView.findViewById(R.id.calendar);

        // sets whether to show the week number.
        calendar.setShowWeekNumber(false);

        // sets the first day of week according to Calendar.
        // here we set Monday as the first day of the Calendar
        calendar.setFirstDayOfWeek(2);

        //The background color for the selected week.
        calendar.setSelectedWeekBackgroundColor(getActivity().getResources().getColor(R.color.PaleGoldenrod));

        //sets the color for the dates of an unfocused month.
        calendar.setUnfocusedMonthDateColor(getActivity().getResources().getColor(R.color.MediumBlue));

        //sets the color for the separator line between weeks.
        calendar.setWeekSeparatorLineColor(getActivity().getResources().getColor(R.color.transparent));

        //sets the color for the vertical bar shown at the beginning and at the end of the selected date.
        calendar.setSelectedDateVerticalBar(R.color.PaleGoldenrod);

        //sets the listener to be notified upon selected date change.
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            //show the selected date as a toast
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int day) {


                month++;
                String date_selected = year + "-" + month + "-" + day;

                SharedPreferences sharedPref;
                sharedPref= PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                String user = sharedPref.getString("USERID", "");
                USER_ID = Integer.parseInt(user);

                String serverResponse = "null";
                // System.out.println("starting");
                try{
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(URL + Key + "?userid=" + String.valueOf(USER_ID));
                    Date dNow = new Date( );
                    SimpleDateFormat ft =
                            new SimpleDateFormat ("yyyy-MM-dd");
                    String today = ft.format(dNow);




                    JSONObject userData = new JSONObject();

                    //Toast.makeText(getActivity().getApplicationContext(), "Current Date: " + weekdate, Toast.LENGTH_LONG).show();





                    userData.put("start_time",date_selected );
                    userData.put("end_time", date_selected);
                    // System.out.println(userData.toString());
                    httppost.setEntity(new StringEntity(userData.toString()));

                    HttpResponse response = httpclient.execute(httppost);
                    BufferedReader BuffRead = new BufferedReader( new InputStreamReader(response.getEntity().getContent(),"UTF-8") );
                    serverResponse = BuffRead.readLine();
                    //tv = (TextView)rootView.findViewById(R.id.response);
                    //Toast.makeText(getActivity().getApplicationContext(),serverResponse,Toast.LENGTH_LONG).show();
                    total = "No data available on this date";
                    if(serverResponse.equals("null"))
                    {
                        //Toast.makeText(getActivity().getApplicationContext(),"if null",Toast.LENGTH_LONG).show();
                        total = "No data available on this date";
                    }
                    else
                    {
                        // Toast.makeText(getActivity().getApplicationContext(),"Not null",Toast.LENGTH_LONG).show();
                        String onfoot = "null";
                        String still = "null";
                        String tilting = "null";
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

                        int foot = HomeFragment.minutes(onfoot);
                        int stil = HomeFragment.minutes(still);
                        int tilt = HomeFragment.minutes(tilting);
                        int veh = HomeFragment.minutes(vehicle);
                        int cyc = HomeFragment.minutes(cycle);
                        int max = 0;
                        result[0] = foot + cyc;
                        result[1] = stil + tilt;
                        result[2] = veh;

                       ex = "Exercise : " + timestring(result[0]) + " (hh:mm) \n\n";
                       id = "Idle : " + timestring(result[1]) + " (hh:mm) \n\n";
                       ve = "In a vehicle: " + timestring(result[2]) + " (hh:mm)  \n\n";

                        total = ex + id+ ve;

                    }
                } catch(HttpHostConnectException e) {
                    Log.v(e.toString(), "index=" + 1);
                    //tv.setText(e.toString());
                    //Toast.makeText(context, "Unable to connect to server. Please try after sometime", Toast.LENGTH_LONG).show();

                } catch(Exception error) {
                    // System.out.println(error.toString());
                    Log.v(error.toString(), "index=" + 1);
                }


                AlertDialog.Builder build =null;
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle(date_selected);
                alertDialog.setCancelable(false);
                alertDialog.setMessage(total);
                alertDialog.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog
                                //Toast.makeText(getApplicationContext(),"You clicked on YES", Toast.LENGTH_SHORT).show();
                            }
                        });
                alertDialog.show();
                //Toast.makeText(getActivity().getApplicationContext(), day + "-" + month + "-" + year, Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Day wise statistics");
        alertDialog.setMessage("Pick a Date to know statistics of that Day");
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        //Toast.makeText(getApplicationContext(),"You clicked on YES", Toast.LENGTH_SHORT).show();
                    }
                });
        alertDialog.show();
        return rootView;
    }

}
