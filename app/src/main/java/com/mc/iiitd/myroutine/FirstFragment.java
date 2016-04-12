package com.mc.iiitd.myroutine;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by iiitd on 25-09-2015.
 */
public class FirstFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_1st, container, false);
        //FONT SETTING
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fontawesome-webfont.ttf");
        Button button = (Button)rootView.findViewById( R.id.checkupdates );
        button.setTypeface(font);
       Button makedb = (Button) rootView.findViewById(R.id.makedb);
        makedb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View rootView) {
                HOTSPOT entry = new HOTSPOT(getActivity());
                entry.open();
                DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
                Calendar cal = Calendar.getInstance();
                int date = Integer.parseInt(dateFormat.format(cal.getTime()));
                entry.createEntry(date, "Naman", "Mussoorie", "12", "23");
                entry.createEntry(date, "Namn", "ssoorie", "1", "13");
                entry.createEntry(date, "Naan", "Moorie", "19", "23");
                entry.createEntry(date, "Nman", "Moorie", "12", "03");
                entry.createEntry(date, "aman", "Mssoorie", "22", "9");
                entry.createEntry(date, "Naman", "Moorie", "16", "7");
                entry.close();
            }
        });
        Button readdb = (Button) rootView.findViewById(R.id.readdb);
        readdb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View rootView) {
                HOTSPOT entry = new HOTSPOT(getActivity());
                entry.open();

                DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
                Calendar cal = Calendar.getInstance();
                int currdate = Integer.parseInt(dateFormat.format(cal.getTime()));
                Toast.makeText(getActivity().getApplicationContext(),entry.reader(currdate,"Naman"),Toast.LENGTH_LONG).show();

                entry.close();
            }
        });
        return rootView;
    }



}