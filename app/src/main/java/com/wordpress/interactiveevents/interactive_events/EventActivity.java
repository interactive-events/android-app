package com.wordpress.interactiveevents.interactive_events;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;

import com.octo.android.robospice.JacksonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;


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

        final Button reqButton = (Button) findViewById(R.id.button3);
        reqButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                performRequest("steffengy");
            }
        });
    }

    //------------------------------------------------------------------------
    //this block can be pushed up into a common base class for all activities
    //------------------------------------------------------------------------

    protected SpiceManager spiceManager = new SpiceManager(JacksonSpringAndroidSpiceService.class);

    @Override
    protected void onStart() {
        super.onStart();
        spiceManager.start(this);
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    //------------------------------------------------------------------------
    //---------end of block that can fit in a common base class for all activities
    //------------------------------------------------------------------------

    private void performRequest(String user) {
        EventActivity.this.setProgressBarIndeterminateVisibility(true);

        FollowersRequest request = new FollowersRequest(user);
        String lastRequestCacheKey = request.createCacheKey();

        // use DurationInMillis.ALWAYS_EXPIRED and you will never used cached values
        spiceManager.execute(request, lastRequestCacheKey, DurationInMillis.ONE_MINUTE, new ListFollowersRequestListener());
    }

    //inner class of your spiced Activity
    private class ListFollowersRequestListener implements RequestListener<FollowerList> {

        @Override
        public void onRequestFailure(SpiceException e) {
            //update your UI
            Log.e("robospice", "Fail on request", e);
        }

        @Override
        public void onRequestSuccess(FollowerList listFollowers) {
            //update your UI
            Log.d("robospice", listFollowers.toString());
            for (Follower f : listFollowers) {
                System.out.println(f.getLogin());
                Log.d("robospice", f.getLogin());
            }
        }
    }
}
