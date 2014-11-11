package com.wordpress.interactiveevents.interactive_events;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EventActivity extends Activity{
    private Button homeBtn;
    TextView event_description;
    TextView event_title;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_view);



        event_description = (TextView) findViewById(R.id.event_description);
        event_title = (TextView) findViewById(R.id.event_title);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String title = extras.getString("event_title");
            String description = extras.getString("event_desc");
            event_description.setText(description);
            event_title.setText(title);
        }



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
