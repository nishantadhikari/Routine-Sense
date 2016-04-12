package com.mc.iiitd.myroutine;


import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class HelperFragment extends Fragment {
   ImageView iv;
    ImageButton ib1;
    ImageButton ib2;
    TextView tvh;
    static int on=1;
    private static final int min=1;
    private static final int max=4;

   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_helper, container, false);
       ib1=(ImageButton)rootView.findViewById(R.id.imageButtonhr);
       ib2=(ImageButton)rootView.findViewById(R.id.imageButtonhl);
       iv=(ImageView)rootView.findViewById(R.id.imageviewhelper);
       tvh=(TextView)rootView.findViewById(R.id.texthelper);
       Drawable d;

       ib1.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               new AnimationUtils();
               iv.setAnimation(AnimationUtils.makeInAnimation(getActivity().getApplicationContext(), false));
               rclick(v);

           }
       });
       ib2.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               new AnimationUtils();
               iv.setAnimation(AnimationUtils.makeInAnimation(getActivity().getApplicationContext(), true));
               lclick(v);

           }
       });
        return rootView;
    }
    void rclick(View v){
        if(on<max){
            on++;
        }
        else{
            on=1;
        }
        update();

    }
    void lclick(View v){
        if(on>min){
            on--;
        }
        else{
            on=max;
        }
        update();

    }
    void update(){
       switch(on){
           case 1:iv.setImageResource(R.drawable.screen1);
               tvh.setText("This is Home Screen shows your profile info");
               break;
           case 2:iv.setImageResource(R.drawable.screen2);
               tvh.setText("Click on top left corner menu icon to open list of options");
               break;
           case 3:iv.setImageResource(R.drawable.screen3);
               tvh.setText("Weekly and Monthly Location Statistics");
               break;
           case 4:iv.setImageResource(R.drawable.screen4);
               tvh.setText("Weekly and Monthly Activity Statistics");
               break;
           default:iv.setImageResource(R.drawable.screen1);
               tvh.setText("This is Home Screen shows your profile info");
               break;
       }
    }
}
