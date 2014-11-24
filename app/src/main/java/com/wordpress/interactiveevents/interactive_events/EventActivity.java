package com.wordpress.interactiveevents.interactive_events;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.octo.android.robospice.JacksonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;


public class EventActivity extends Activity{
    private Button homeBtn;
    TextView event_description;
    TextView event_title;

    ArrayList<String> moduleStringArr = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    private String eventId = "1";
    private ModulesList modulesList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_view);

        event_description = (TextView) findViewById(R.id.event_description);
        event_title = (TextView) findViewById(R.id.event_title);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            eventId = extras.getString(eventId);
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
                //performRequest("steffengy");
                performEventModulesRequest("1");
            }
        });

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, moduleStringArr);
        ListView lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(adapter);

        performEventModulesRequest(eventId);

        // see syntax for overriding methods without extending a class explicitly
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position,
                                    long id)
            {
                // parent = ListView lv, view = view for the item that was clicked on
                // position = position for view in the adapter
                // id = row id of the item that was clicked on
                Log.d("modulelist", "position="+position+" id="+id);

                // Assuming string, get value of clicked item
                String value = (String)parent.getItemAtPosition(position);
                Log.d("modulelist", value);

                //Starting a new Intent
                Intent nextScreen = new Intent(getApplicationContext(), WebViewActivity.class);

                //Sending parameters to next Activity - WebViewActivity
                nextScreen.putExtra("eventId", eventId);
                String moduleId = modulesList.get(position).getModuleId();
                nextScreen.putExtra("moduleId", moduleId);

                startActivity(nextScreen);
            }
        });
    }

    protected void onResume() {
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
}
