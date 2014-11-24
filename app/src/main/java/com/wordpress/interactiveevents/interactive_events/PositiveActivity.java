package com.wordpress.interactiveevents.interactive_events;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class PositiveActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.positive_view);
        Toast.makeText(this, "DERP!", Toast.LENGTH_LONG).show();
    }

}
