package com.mc.iiitd.myroutine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Nishant on 11/2/2015.
 */
public class ListAdapterfrnds extends ArrayAdapter {

    private Context context;
    private int resource;
    private LayoutInflater inflater;

    public ListAdapterfrnds (Context context, List<CustomClass> values) { // or String[][] or whatever

        super(context, R.layout.list_frnds, values);
        this.context = context;
        this.resource = R.layout.list_frnds;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = (RelativeLayout) inflater.inflate(resource, null);

        CustomClass item = (CustomClass) getItem(position);

        TextView textviewTitle = (TextView) convertView.findViewById(R.id.title);
        TextView textviewSubtitle = (TextView) convertView.findViewById(R.id.subtitle);
        TextView tvrank=(TextView) convertView.findViewById(R.id.counter);
        TextView tvc=(TextView) convertView.findViewById(R.id.count);
        //ImageView imageview = (ImageView) convertView.findViewById(R.id.icon);

        //fill the textviews and imageview with the values
        textviewTitle.setText(item.getTitle());
        textviewSubtitle.setText(item.getSubtitle());
        tvrank.setText(item.getRank()+"");
        tvc.setText(item.getCount()+"");
        /*
        if (item.getAfbeelding() != null) {
            int imageResource = context.getResources().getIdentifier("drawable/" + item.getImage(), null, context.getPackageName());
            Drawable image = context.getResources().getDrawable(imageResource);
            Icon i=item.getIcon();
        }
        */
        //int id=context.getResources().getIdentifier(item.getIcon(),"drawable", context.getPackageName());
        //System.out.println(id+"    "+R.mipmap.menu_list);
        //imageview.setImageResource(id);
        //imageview.setImageBitmap();
        return convertView;
    }
}