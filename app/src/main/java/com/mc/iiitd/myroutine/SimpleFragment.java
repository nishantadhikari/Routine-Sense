package com.mc.iiitd.myroutine;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public abstract class SimpleFragment extends Fragment {
    
    private Typeface tf;
    
    public SimpleFragment() {
        
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        tf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public BarData generateBarData(int dataSets, float range, int count) {
        
        ArrayList<BarDataSet> sets = new ArrayList<BarDataSet>();
        
        for(int i = 0; i < dataSets; i++) {
           
            ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
            
//            entries = FileUtils.loadEntriesFromAssets(getActivity().getAssets(), "stacked_bars.txt");
            
            for(int j = 0; j < count; j++) {        
                entries.add(new BarEntry((float) (Math.random() * range) + range / 4, j));
            }
            
            BarDataSet ds = new BarDataSet(entries, getLabel(i));
            ds.setColors(ColorTemplate.VORDIPLOM_COLORS);
            sets.add(ds);
        }
        
        BarData d = new BarData(ChartData.generateXVals(0, count), sets);
        d.setValueTypeface(tf);
        return d;
    }
    

    
    /**
     * generates less data (1 DataSet, 4 values)
     * @return
     */
    public PieData generatePieData() {
        
        int count = 4;
        
        ArrayList<Entry> entries1 = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();
        
        xVals.add("Quarter 1");
        xVals.add("Quarter 2");
        xVals.add("Quarter 3");
        xVals.add("Quarter 4");
        
        for(int i = 0; i < count; i++) {
            xVals.add("entry" + (i+1));
    
            entries1.add(new Entry((float) (Math.random() * 60) + 40, i));
        }
        
        PieDataSet ds1 = new PieDataSet(entries1, "Quarterly Revenues 2015");
        ds1.setColors(ColorTemplate.VORDIPLOM_COLORS);
        ds1.setSliceSpace(2f);
        ds1.setValueTextColor(Color.WHITE);
        ds1.setValueTextSize(12f);
        
        PieData d = new PieData(xVals, ds1);
        d.setValueTypeface(tf);

        return d;
    }
    

    
    private String[] mLabels = new String[] { "Company A", "Company B", "Company C", "Company D", "Company E", "Company F" };
//    private String[] mXVals = new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec" };
    
    private String getLabel(int i) {
        return mLabels[i];
    }
}
