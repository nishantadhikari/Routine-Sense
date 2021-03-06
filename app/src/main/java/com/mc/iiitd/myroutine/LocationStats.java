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

public class LocationStats extends Fragment implements OnChartGestureListener {

    public LocationStats(){}

    private Typeface tf;
    private PieChart mChart;
    private BarChart mmChart;
    public static String URL ="http://muc.iiitd.edu.in:9060/";
    public static String Key ="location_report";
    public static int USER_ID;
    public int acd;
    public int din;
    public int unk;
    public int lec;
    public int hos;
    public int lib;
    public int resi;
    public int ser;

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
        View rootView = inflater.inflate(R.layout.activity_location, container, false);
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
            calendar.add(Calendar.DATE, -7);
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

            String acad = "null";
            String dine = "null";
            String lect = "null";
            String bhostel = "null";
            String ghostel = "null";
            String ohostel = "null";
            String library = "null";
            String reside = "null";
            String service = "null";

            object = (JSONObject) new JSONTokener(serverResponse).nextValue();
            //Toast.makeText(getActivity().getApplicationContext(),object.toString(),Toast.LENGTH_LONG).show();
            if(object.has("AC"))
            {
                acad = object.getString("AC");
            }
            if(object.has("DB"))
            {
                dine = object.getString("DB");
            }
            if(object.has("LC"))
            {
                lect = object.getString("LC");
            }
            if(object.has("BH"))
            {
                bhostel = object.getString("BH");
            }
            if(object.has("GH"))
            {
                ghostel = object.getString("GH");
            }
            if(object.has("OU"))
            {
                ohostel = object.getString("OU");
            }
            if(object.has("LB"))
            {
                library = object.getString("LB");
            }
            if(object.has("RE"))
            {
                reside = object.getString("RE");
            }
            if(object.has("SR"))
            {
                service = object.getString("SR");
            }


            acd = HomeFragment.minutes(acad);
            //Toast.makeText(getActivity().getApplicationContext(),acad+dine+lect+library+bhostel+reside+service,Toast.LENGTH_LONG).show();
            din = HomeFragment.minutes(dine);
            // unk = minutes(unknown);
            lec = HomeFragment.minutes(lect);
            hos = HomeFragment.minutes(bhostel)+ HomeFragment.minutes(ghostel)+HomeFragment.minutes(ohostel);
            lib = HomeFragment.minutes(library);
            resi = HomeFragment.minutes(reside);
            ser = HomeFragment.minutes(service);




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
        l.setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);

        mChart.setData(generatePieData());

        //Generate Bar Graph

        mmChart = (BarChart)rootView.findViewById(R.id.barChart1);

        mmChart.setOnChartGestureListener(this);

        MyMarkerView mv = new MyMarkerView(getActivity(), R.layout.custom_marker_view);
        mmChart.setDrawGridBackground(false);
        mmChart.setDrawBarShadow(false);

        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),"OpenSans-Light.ttf");

        mmChart.setData(generateBarData(1,7));
        mmChart.setDescription(" ");
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
        SpannableString s = new SpannableString("Locations\nRoutineSense 2016");
        s.setSpan(new RelativeSizeSpan(2f), 0, 10, 0);
        s.setSpan(new ForegroundColorSpan(Color.BLACK), 10, s.length(), 0);
        return s;
    }
    public PieData generatePieData() {

        int i =1;
        ArrayList<Entry> entries1 = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();
        if(acd!=0)
            xVals.add("Acad");

        if(din!=0)
        xVals.add("Mess");

        if(lec!=0)
        xVals.add("Lecture");

        if(hos!=0)
        xVals.add("Hostel");

        if(lib!=0)
        xVals.add("Library");

        if(resi!=0)
        xVals.add("Home");

        if(ser!=0)
        xVals.add("Service");


        // xVals.add("Quarter 4");


        //   xVals.add("entry" + 1);
        if(acd!=0)
        {
            float ti;

            ti = (float)acd/(float)60;
            if(ti>0 && ti< 1)
            {
                ti=1;
            }
            entries1.add(new Entry(ti, i));
            i++;
        }



        //  xVals.add("entry" + 2);
        if(din!=0)
        {
            float ti;
            ti = (float)din/(float)60;
            if(ti>0 && ti< 1)
            {
                ti=1;
            }
            entries1.add(new Entry(ti, i));
            i++;
        }


        //xVals.add("entry" + 3);
        if(lec!=0)
        {
            float ti;
            ti = (float)lec/(float)60;
            if(ti>0 && ti< 1)
            {
                ti=1;
            }
            entries1.add(new Entry(ti, i));
            i++;      }

        // xVals.add("entry" + 4);
        if(hos!=0)
        {
            float ti;
            ti = (float)hos/(float)60;
            if(ti>0 && ti< 1)
            {
                ti=1;
            }
            entries1.add(new Entry(ti, i));
            i++;
        }

        //xVals.add("entry" + 5);
        if(lib!=0)
        {
            float ti;
            ti = (float)lib/(float)60;
            if(ti>0 && ti< 1)
            {
                ti=1;
            }
            entries1.add(new Entry(ti, i));
            i++;
        }

        //xVals.add("entry" + 6);
        if(resi!=0)
        {
            float ti;
            ti = (float)resi/(float)60;
            if(ti>0 && ti< 1)
            {
                ti=1;
            }
            entries1.add(new Entry(ti, i));
            i++;
        }



        //xVals.add("entry" + 7);
        if(ser!=0)
        {
            float ti;
            ti = (float)ser/(float)60;
            if(ti>0 && ti< 1)
            {
                ti=1;
            }
            entries1.add(new Entry(ti, i));
            i++;
        }

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        PieDataSet ds1 = new PieDataSet(entries1, "");
        ds1.setColors(colors);
        ds1.setSliceSpace(2f);
        ds1.setValueTextColor(Color.BLACK);
        ds1.setValueTextSize(9f);

        PieData d = new PieData(xVals, ds1);
        d.setValueTypeface(tf);

        return d;
    }
    protected BarData generateBarData(int dataSets, int count) {

        ArrayList<BarDataSet> sets = new ArrayList<>();
        float a[]=new float[9];
        int i = 0;
        if(acd!=0)
        {
            a[i] = acd;
            i++;
        }
        if(din!=0) {
            a[i] = din;
            i++;
        }

        if(lec!=0)
        {
            a[i] = lec;
            i++;
        }
        if(hos!=0) {
            a[i] = hos;
            i++;
        }
        if(lib!=0) {
            a[i] = lib;
            i++;
        }
        if(resi!=0) {
            a[i] = resi;
            i++;
        }
        if(ser!=0) {
            a[i] = ser;
            i++;
        }


        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();

//            entries = FileUtils.loadEntriesFromAssets(getActivity().getAssets(), "stacked_bars.txt");

        for(int j = 0; j < i; j++) {
            //Toast.makeText(getActivity().getApplicationContext(),Float.toString(a[j]/60),Toast.LENGTH_LONG).show();
           float ti  = a[j]/60;
            if(ti>0 && ti< 1)
            {
                ti=1;
            }
            entries.add(new BarEntry(ti, j));
        }
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);


        BarDataSet ds = new BarDataSet(entries, getLabel(0));
        ds.setColors(colors);
        sets.add(ds);


        BarData d = new BarData(ChartData.generateXVals(0, i), sets);
        d.setValueTypeface(tf);
        return d;
    }

    private String[] mLabels = new String[] { " "};
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
