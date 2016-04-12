package com.mc.iiitd.myroutine;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.ColorTemplate;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class WeeklyCompare extends Fragment implements OnChartGestureListener {

    public WeeklyCompare(){}

    private Typeface tf;
    private PieChart mChart;
    private BarChart mmChart;
    public static String URL ="http://muc.iiitd.edu.in:9060/";
    public static String Key ="activity_report";
    public static int USER_ID;
    public int foot;
    public int stil;
    public int unk;
    public int tilt;
    public int veh;
    public int cyc;
    public  String weekdate;
public TextView date1;
    public TextView date2;
    public TextView date3;
    public TextView date4;
    public TextView date5;
    public TextView date6;
    public TextView date7;
    //public TextView tv;
    /*
    public int minutes(String s)
    {
        if(s.equals("null"))
        {
            return 0;
        }
        int day = 0;
        if(s.contains("day"))
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
    */

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        tf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf");
        View rootView = inflater.inflate(R.layout.activity_weekcomp, container, false);
        date1=(TextView)rootView.findViewById(R.id.date1);
        date2=(TextView)rootView.findViewById(R.id.date2);
        date3=(TextView)rootView.findViewById(R.id.date3);
        date4=(TextView)rootView.findViewById(R.id.date4);
        date5=(TextView)rootView.findViewById(R.id.date5);
        date6=(TextView)rootView.findViewById(R.id.date6);
        date7=(TextView)rootView.findViewById(R.id.date7);
        if(CommonUtils.isInternetAvailable(getActivity().getApplicationContext())<0){
            //Toast.makeText(getActivity().getApplicationContext(), "No internet connection. Please switch on your Wi-Fi or Mobile Data", Toast.LENGTH_SHORT).show();
            Toast.makeText(getActivity().getApplicationContext(), "Internet Connectivity Required: Unable to connect to server. Please try after sometime", Toast.LENGTH_LONG).show();
        }

        // SERVER CONNECT
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(rootView.getContext());
        //sharedPref.registerOnSharedPreferenceChangeListener((SharedPreferences.OnSharedPreferenceChangeListener) this);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String user = sharedPref.getString("USERID", "");
        USER_ID = Integer.parseInt(user);
        JSONObject object = null;
        String serverResponse = "null";
        // System.out.println("starting");
        for (int i = 0; i < 7; i++) {
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(URL + Key + "?userid=" + String.valueOf(USER_ID));
                Date dNow = new Date();
                SimpleDateFormat ft =
                        new SimpleDateFormat("yyyy-MM-dd");
                String today = ft.format(dNow);
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, -i);
                Date yesterday = calendar.getTime();
                weekdate = ft.format(yesterday);
                JSONObject userData = new JSONObject();

                // Toast.makeText(getActivity().getApplicationContext(), "Current Date: " + weekdate, Toast.LENGTH_LONG).show();


                userData.put("start_time", weekdate);
                userData.put("end_time", weekdate);
                System.out.println(userData.toString());
                httppost.setEntity(new StringEntity(userData.toString()));

                HttpResponse response = httpclient.execute(httppost);
                BufferedReader BuffRead = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                serverResponse = BuffRead.readLine();
                //tv = (TextView)rootView.findViewById(R.id.response);
                object = null;
                foot = 0;
                stil = 0;
                unk = 0;
                tilt = 0;
                veh = 0;
                cyc = 0;



                //Toast.makeText(getActivity().getApplicationContext(),object.toString(),Toast.LENGTH_SHORT).show();
                String onfoot = "null";
                String still = "null";
                String tilting = "null";
                String vehicle = "null";
                String cycle = "null";

                object = (JSONObject) new JSONTokener(serverResponse).nextValue();

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



                foot = HomeFragment.minutes(onfoot);
                stil = HomeFragment.minutes(still);
                //unk = minutes(unknown);
                tilt = HomeFragment.minutes(tilting);
                veh = HomeFragment.minutes(vehicle);
                cyc = HomeFragment.minutes(cycle);


                foot = foot + cyc;
                stil = stil + tilt;


            } catch (HttpHostConnectException e) {
                Log.v(e.toString(), "index=" + 1);
                //tv.setText(e.toString());
                //Toast.makeText(context, "Unable to connect to server. Please try after sometime", Toast.LENGTH_LONG).show();

            } catch (Exception error) {
                // System.out.println(error.toString());
                Log.v(error.toString(), "index=" + 1);
            }


            //Generate Bar Graph
            switch (i)
            {
                case 0:
                    mmChart = (BarChart) rootView.findViewById(R.id.barChart1);
                    mmChart.setDescription(" ");
                    date1.setText(weekdate);
                    
                    break;
                case 1:
                    mmChart = (BarChart) rootView.findViewById(R.id.barChart2);
                    mmChart.setDescription(" ");
                    date2.setText(weekdate);
                    
                    break;
                case 2:
                    mmChart = (BarChart) rootView.findViewById(R.id.barChart3);
                    mmChart.setDescription(" ");
                    date3.setText(weekdate);
                    
                    break;
                case 3:
                    mmChart = (BarChart) rootView.findViewById(R.id.barChart4);
                    mmChart.setDescription(" ");
                    date4.setText(weekdate);
                    
                    break;
                case 4:
                    mmChart = (BarChart) rootView.findViewById(R.id.barChart5);
                    mmChart.setDescription(" ");
                    date5.setText(weekdate);
                    
                    break;
                case 5:
                    mmChart = (BarChart) rootView.findViewById(R.id.barChart6);
                    mmChart.setDescription(" ");
                    date6.setText(weekdate);
                    
                    break;
                case 6:
                    mmChart = (BarChart) rootView.findViewById(R.id.barChart7);
                    mmChart.setDescription(" ");
                    date7.setText(weekdate);

                    break;
                default:
                    break;
            }

            mmChart.setOnChartGestureListener(this);

            MyMarkerView mv = new MyMarkerView(getActivity(), R.layout.custom_marker_view);
            mmChart.setDrawGridBackground(false);
            mmChart.setDrawBarShadow(false);

            Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf");

            mmChart.setData(generateBarData(1, 3));

            Legend ll = mmChart.getLegend();
            ll.setTypeface(tf);

            YAxis leftAxis = mmChart.getAxisLeft();
            leftAxis.setTypeface(tf);

            mmChart.getAxisRight().setEnabled(false);

            XAxis xAxis = mmChart.getXAxis();
            xAxis.setEnabled(false);
        }
        return rootView;
    }

    protected BarData generateBarData(int dataSets, int count) {

        ArrayList<BarDataSet> sets = new ArrayList<BarDataSet>();
        float a[]=new float[3];

        a[0] = foot;
        a[1] = stil;

        a[2] = veh;


        for(int i = 0; i < dataSets; i++) {

            ArrayList<BarEntry> entries = new ArrayList<BarEntry>();

//            entries = FileUtils.loadEntriesFromAssets(getActivity().getAssets(), "stacked_bars.txt");

            for(int j = 0; j < count; j++) {
                float ti  = a[j]/60;
                if(ti>0 && ti< 1)
                {
                    ti=1;
                }
                entries.add(new BarEntry(ti, j));
            }


            BarDataSet ds = new BarDataSet(entries, getLabel(i));
            ds.setColors(ColorTemplate.VORDIPLOM_COLORS);
            sets.add(ds);
        }

        BarData d = new BarData(ChartData.generateXVals(0, count), sets);
        d.setValueTypeface(tf);
        return d;
    }
    private String[] mLabels = new String[] { "Exercise Idle Vehicle"};
//    private String[] mXVals = new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec" };

    private String getLabel(int i) {
        return mLabels[i];
    }

    @Override
    public void onChartGestureStart(MotionEvent motionEvent, ChartTouchListener.ChartGesture chartGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent motionEvent, ChartTouchListener.ChartGesture chartGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent motionEvent) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent motionEvent) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent motionEvent) {

    }

    @Override
    public void onChartFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {

    }

    @Override
    public void onChartScale(MotionEvent motionEvent, float v, float v1) {

    }

    @Override
    public void onChartTranslate(MotionEvent motionEvent, float v, float v1) {

    }
}
