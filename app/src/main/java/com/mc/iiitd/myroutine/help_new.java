package com.mc.iiitd.myroutine;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ViewSwitcher;

public class help_new extends Activity {
    static ViewSwitcher switcher;
    private float lastX;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_new);
        switcher=(ViewSwitcher)findViewById(R.id.ViewSwitcher);
        switcher.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                new AnimationUtils();
                switcher.setAnimation(AnimationUtils.makeInAnimation(getBaseContext(), true));
                switcher.showNext();
                return false;
            }
            });

    }


}
