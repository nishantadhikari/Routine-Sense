package com.mc.iiitd.myroutine;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
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

public class MonthlyStats extends Fragment implements OnChartGestureListener {

    public MonthlyStats(){}

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
        View rootView = inflater.inflate(R.layout.fragment_week, container, false);
        mChart = (PieChart) rootView.findViewById(R.id.pieChart1);
        mChart.setDescription(" ");


        if(CommonUtils.isInternetAvailable(getActivity().getApplicationContext())<0){
            //Toast.makeText(getActivity().getApplicationContext(), "No internet connection. Please switch on your Wi-Fi or Mobile Data", Toast.LENGTH_SHORT).show();
            Toast.makeText(getActivity().getApplicationContext(), "Internet Connectivity Required: Unable to connect to server. Please try after sometime", Toast.LENGTH_LONG).show();
        }

        // SERVER CONNECT
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(rootView.getContext());
        //sharedPref.registerOnSharedPreferenceChangeListener((SharedPreferences.OnSharedPreferenceChangeListener) this);

        sharedPref= PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String user = sharedPref.getString("USERID", "");
        USER_ID = Integer.parseInt(user);

        String serverResponse = "null";
        // System.out.println("starting");
        try{


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(URL + Key + "?userid=" + String.valueOf(USER_ID));
            Date dNow = new Date();
            SimpleDateFormat ft =
                    new SimpleDateFormat("yyyy-MM-dd");
            String today = ft.format(dNow);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -30);
            Date yesterday = calendar.getTime();
            String weekdate = ft.format(yesterday);
            JSONObject userData = new JSONObject();

            //Toast.makeText(getActivity().getApplicationContext(), "Current Date: " + weekdate, Toast.LENGTH_LONG).show();


            userData.put("start_time", weekdate);
            userData.put("end_time", today);
            System.out.println(userData.toString());
            httppost.setEntity(new StringEntity(userData.toString()));

            HttpResponse response = httpclient.execute(httppost);
            BufferedReader BuffRead = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            serverResponse = BuffRead.readLine();
            //tv = (TextView)rootView.findViewById(R.id.response);

            JSONObject object = (JSONObject) new JSONTokener(serverResponse).nextValue();

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
            // unk = minutes(unknown);
            tilt = HomeFragment.minutes(tilting);
            veh = HomeFragment.minutes(vehicle);
            cyc = HomeFragment.minutes(cycle);


            foot = foot + cyc;
            stil = stil + tilt;

        } catch(HttpHostConnectException e) {
            Log.v(e.toString(), "index=" + 1);
            //tv.setText(e.toString());
            //Toast.makeText(context, "Unable to connect to server. Please try after sometime", Toast.LENGTH_LONG).show();

        } catch(Exception error) {
            // System.out.println(error.toString());
            Log.v(error.toString(), "index=" + 1);
        }
        mChart.setCenterTextTypeface(tf);
        mChart.setCenterText(generateCenterText());
        mChart.setCenterTextSize(10f);
        mChart.setCenterTextTypeface(tf);
        // radius of the center hole in percent of maximum radius
        mChart.setHoleRadius(45f);
        mChart.setTransparentCircleRadius(50f);
        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);

        mChart.setData(generatePieData());
        //Generate Bar Graph

        mmChart = (BarChart)rootView.findViewById(R.id.barChart1);
        mmChart.setDescription(" ");
        mmChart.setOnChartGestureListener(this);
        MyMarkerView mv = new MyMarkerView(getActivity(), R.layout.custom_marker_view);
        mmChart.setDrawGridBackground(false);
        mmChart.setDrawBarShadow(false);

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),"OpenSans-Light.ttf");

        mmChart.setData(generateBarData(1,3));

        Legend ll = mmChart.getLegend();
        ll.setTypeface(tf);

        YAxis leftAxis = mmChart.getAxisLeft();
        leftAxis.setTypeface(tf);

        mmChart.getAxisRight().setEnabled(false);

        XAxis xAxis = mmChart.getXAxis();
        xAxis.setEnabled(false);

        return rootView;
    }
    private SpannableString generateCenterText() {
        SpannableString s = new SpannableString("Activities\nRoutineSense 2016");
        s.setSpan(new RelativeSizeSpan(2f), 0, 10, 0);
        s.setSpan(new ForegroundColorSpan(Color.BLACK), 10, s.length(), 0);
        return s;
    }
    public PieData generatePieData() {

        ArrayList<Entry> entries1 = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();

        xVals.add("Exercise");
        xVals.add("Idle");

        xVals.add("Vehicle");


        // xVals.add("Quarter 4");


        //xVals.add("entry" + 1);

        entries1.add(new Entry((float)foot/(float)60, 1));


        //xVals.add("entry" + 2);
        entries1.add(new Entry((float)stil/(float)60, 2));

        //xVals.add("entry" + 3);
        entries1.add(new Entry((float) veh / (float) 60, 3));




        PieDataSet ds1 = new PieDataSet(entries1,"");
        ds1.setColors(ColorTemplate.VORDIPLOM_COLORS);
        ds1.setSliceSpace(2f);
        ds1.setValueTextColor(Color.BLACK);
        ds1.setValueTextSize(9f);

        PieData d = new PieData(xVals, ds1);
        d.setValueTypeface(tf);

        return d;
    }
    protected BarData generateBarData(int dataSets, int count) {

        ArrayList<BarDataSet> sets = new ArrayList<BarDataSet>();
        int a[]=new int[5];

        a[0] = foot;
        a[1] = stil;

        a[2] =veh;


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
    private String[] mLabels = new String[] {" "};
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
