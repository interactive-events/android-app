package com.wordpress.interactiveevents.interactive_events;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by raxo on 03/11/14.
 */
public class EventListActivity extends Activity {

    static int TIMEOUT_MILLISEC = 3000000;
    static String API = "http://private-274c2-interactiveevents.apiary-mock.com/";
    TableLayout eventsTable;
    GetEventsTask getEventsTask;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_list);

        eventsTable = (TableLayout) findViewById(R.id.eventsTable);

        updateEventsTable();

    }

    public void updateEventsTable() {
        //showProgress(true);

        getEventsTask = new GetEventsTask(eventsTable, this);
        getEventsTask.execute((Void) null);


    }


    /**
     * Shows the progress UI and hides the event table.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            eventsTable.setVisibility(show ? View.GONE : View.VISIBLE);
            eventsTable.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    eventsTable.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            eventsTable.setVisibility(show ? View.VISIBLE : View.GONE);
            eventsTable.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    eventsTable.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            eventsTable.setVisibility(show ? View.VISIBLE : View.GONE);
            eventsTable.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class GetEventsTask extends AsyncTask<Void, Void, String> {

        TableLayout table;
        Context context;

        GetEventsTask(TableLayout table, Context context) {
            this.table = table;
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... params) {
            Log.i("API", "Starting get api stuff");

            InputStream inputStream = null;

            // Making HTTP request
            try {
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(API+"events");

                HttpResponse httpResponse = httpClient.execute(httpGet);

                Log.i("API", "server returned status code "+httpResponse.getStatusLine());
                HttpEntity httpEntity = httpResponse.getEntity();
                inputStream = httpEntity.getContent();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String result = null;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                result = sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                try{
                    if(inputStream != null) {
                        inputStream.close();
                    }
                }catch(Exception squish){

                }
            }

            if(result != null) {
                Log.i("API", result);
            } else {
                Log.e("API", "failed server connections or something!");
                return null;
            }
            return result;
        }


        @Override
        protected void onPostExecute(final String result) {
            getEventsTask = null;
            //showProgress(false);

            if (result == null) {
                //eventsTable.setError(getString(R.string.error_connection_problem));
                eventsTable.requestFocus();
                return;
            }

            JSONObject jObj;
            JSONArray events;
            String totalCount;
            try {
                jObj = new JSONObject(result);
                events = jObj.getJSONArray("events");
                JSONObject metaData = jObj.getJSONArray("_metadata").getJSONObject(0);
                totalCount = metaData.getString("totalCount");
                for (int i=0; i < events.length(); i++)
                {
                    JSONObject event = events.getJSONObject(i).getJSONObject("event");
                    final String title = event.getString("title");
                    final String description = event.getString("description");
                    Log.i("API", title+", "+description);

                    TableRow tr = new TableRow(context);
                    tr.setLayoutParams(new TableRow.LayoutParams(
                            TableRow.LayoutParams.FILL_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT));

                    // Create a TextView to add date
                    TextView labelTitle = new TextView(context);
                    labelTitle.setText(title);
                    labelTitle.setPadding(2, 0, 5, 0);
                    labelTitle.setTextColor(Color.BLACK);
                    tr.addView(labelTitle);

                    TextView labelDesc = new TextView(context);
                    labelDesc.setText(description);
                    labelDesc.setTextColor(Color.BLACK);
                    tr.addView(labelDesc);

                    Button eventButton = new Button(context);
                    eventButton.setText("View");
                    tr.addView(eventButton);

                    //testar redir

                    eventButton.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            Intent eventScreen = new Intent(getApplicationContext(), EventActivity.class);
                            eventScreen.putExtra("event_title", title);
                            eventScreen.putExtra("event_desc", description);
                            startActivity(eventScreen);
                        }

                    });

                    //slut test

                    table.addView(tr, new TableLayout.LayoutParams(
                            TableRow.LayoutParams.FILL_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT));
                }
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
                return;
            }
            Log.i("API", "totalCount: "+totalCount);
        }

        @Override
        protected void onCancelled() {
            getEventsTask = null;
            //showProgress(false);
        }
    }
}
