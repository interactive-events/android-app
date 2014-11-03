package com.wordpress.interactiveevents.interactive_events;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EventActivity extends Activity{
    private Button homeBtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_view);



        homeBtn = (Button) findViewById(R.id.button2);
        homeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent homeScreen = new Intent(getApplicationContext(), EventListActivity.class);
                startActivity(homeScreen);
            }
        });
    }
}
