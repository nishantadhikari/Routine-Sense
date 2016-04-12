package com.mc.iiitd.myroutine;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;


public class AboutActivity extends Activity {
    TextView textView;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        textView=((TextView) findViewById(R.id.textView8));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(Html.fromHtml(getResources().getString(R.string.linkreview)));
    }

}
