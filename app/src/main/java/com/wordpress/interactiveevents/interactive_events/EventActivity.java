package com.wordpress.interactiveevents.interactive_events;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.octo.android.robospice.JacksonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class EventActivity extends Activity {
    private ImageButton homeBtn;
    TextView event_description;
    TextView event_title;
    SeekBar seek_bar;
    long currTi = 0;
    long progressTime = 0;
    long startTi = 0;
    long endTi = 0;
    Timer timer = null;

    ArrayList<String> moduleStringArr = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    private String eventId = "1";
    private ModulesList modulesList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_view);
        currTi = System.currentTimeMillis();
        event_description = (TextView) findViewById(R.id.event_description);
        event_description.setMovementMethod(new ScrollingMovementMethod());
        event_title = (TextView) findViewById(R.id.event_title);
        seek_bar = (SeekBar) findViewById(R.id.seekBar);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            eventId = extras.getString("eventId");
            String title = extras.getString("event_title");
            String description = extras.getString("event_desc");
            startTi = extras.getLong("start_time");
            endTi = extras.getLong("stop_time");
            event_description.setText(description);
            event_title.setText(title);
            seek_bar.setEnabled(false);


            updateProgress(startTi, endTi);
        }


        homeBtn = (ImageButton) findViewById(R.id.button2);
        homeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent homeScreen = new Intent(getApplicationContext(), EventListActivity.class);
                startActivity(homeScreen);
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
            }
        });

        /*final Button reqButton = (Button) findViewById(R.id.button3);
        reqButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                //performRequest("steffengy");
                performEventModulesRequest(eventId);
            }
        });*/

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, moduleStringArr);
        ListView lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(adapter);

        performEventModulesRequest(eventId);

        // see syntax for overriding methods without extending a class explicitly
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position,
                                    long id) {
                // parent = ListView lv, view = view for the item that was clicked on
                // position = position for view in the adapter
                // id = row id of the item that was clicked on
                Log.d("modulelist", "position=" + position + " id=" + id);

                // Assuming string, get value of clicked item
                String value = (String) parent.getItemAtPosition(position);
                Log.d("modulelist", value);
                String url = modulesList.get(position).getUrl();

                //Starting a new Intent
                Intent nextScreen = new Intent(getApplicationContext(), WebViewActivity.class);

                //Sending parameters to next Activity - WebViewActivity
                nextScreen.putExtra("eventId", eventId);
                String activityId = modulesList.get(position).getActivityId();
                nextScreen.putExtra("activityId", activityId);
                nextScreen.putExtra("urlStr", url);

                startActivity(nextScreen);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                //overridePendingTransition(R.anim.anim_slide_out_right, R.anim.anim_slide_in_right);
            }
        });
    }

    class SayHello extends TimerTask {
        public void run() {
            updateProgress(startTi, endTi);
            Log.i("AIDSBULLAR","jag är här varje 5s");
        }
    }

    // And From your main() method or any other method



    public void updateProgress(long startTi, long endTi){

        currTi = System.currentTimeMillis();
        long timeFragment1 = currTi - startTi;
        long timeFragment2 = endTi - startTi;
        progressTime = (long) ((float) timeFragment1 / timeFragment2 * 100);

        Log.i("ASDASD AWTF IS HAPPENINZ", " currTi=" + currTi +" startTi=" + startTi + " endTi=" + endTi + " progressTime=" + progressTime);

        if (progressTime > 0 && progressTime <= 100) {
            Log.i("ASDASD AWTF IS HAPPENINZ", "In da if prog > 0 && prog <= 100| " + progressTime);
            seek_bar.setProgress((int) progressTime);
        } else if (progressTime < 0){
            Log.i("ASDASD AWTF IS HAPPENINZ", "In da elze prog < 0| " + progressTime);
            seek_bar.setProgress(0);
        } else if (progressTime > 100){
            Log.i("ASDASD AWTF IS HAPPENINZ", "In da elze prog > 100| " + progressTime);
            seek_bar.setProgress(100);
        }
    }


    protected void onResume() {
        timer = new Timer();
        timer.schedule(new SayHello(), 0, 5000);
        super.onResume();
        performEventModulesRequest(eventId);
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
        finish();
        timer.cancel();
    }

    //------------------------------------------------------------------------
    //---------end of block that can fit in a common base class for all activities
    //------------------------------------------------------------------------

    // request for an events associated modules
    private void performEventModulesRequest(String eventId) {
        EventActivity.this.setProgressBarIndeterminateVisibility(true);

        ModulesRequest request = new ModulesRequest(eventId);
        String lastRequestCacheKey = request.createCacheKey();

        // use DurationInMillis.ALWAYS_EXPIRED and you will never used cached values
        spiceManager.execute(request, lastRequestCacheKey, 10 * DurationInMillis.ONE_SECOND, new ListModulesRequestListener());
    }

    //inner class of your spiced Activity
    private class ListModulesRequestListener implements RequestListener<ModulesList> {

        @Override
        public void onRequestFailure(SpiceException e) {
            //update your UI
            Log.e("robospice", "Fail on request", e);
        }

        @Override
        public void onRequestSuccess(ModulesList listOfModules) {
            //update your UI
            modulesList = listOfModules;
            Log.d("robospice2", "listOfModules="+listOfModules.toString());
            for (int i=0; i < listOfModules.size(); i++) {
                Log.d("robospice2", "i="+i);
                moduleStringArr.add(i, listOfModules.get(i).toString());
                //Log.d("robospice", listOfModules.get(i).getId());
                //Log.d("robospice", listOfModules.get(i).getType());
                //Log.d("robospice", listOfModules.get(i).getCustomData().toString());
            }
            Log.d("robospice2", "moduleStringArr="+moduleStringArr.toString());
            int x = listOfModules.size();
            int times = moduleStringArr.size() - x;
            for (int j=0; j < times; j++) {
                Log.d("robospice2", "j="+j);
                moduleStringArr.remove(x);
            }
            //listOfModules.size()
            //moduleStringArr.removeRange(listOfModules.size(),);
           // moduleStringArr = (ArrayList) listOfModules;
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }
}
