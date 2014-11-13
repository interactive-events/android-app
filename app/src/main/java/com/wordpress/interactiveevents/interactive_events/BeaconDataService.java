package com.wordpress.interactiveevents.interactive_events;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.Toast;

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
 * Created by lab on 2014-11-12.
 */
public class BeaconDataService extends Service{


    getBeaconEvent getBeaconEvent;

    static String API = "http://private-274c2-interactiveevents.apiary-mock.com/";





    // OM SERVICE BARA FUNGERAR EN GÅNG BYT FRÅN ONBIND TILL onStartCommand()

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("#########ONSTARTCOMMAND#########", "did i almost do it ?");
        Bundle extras = intent.getExtras();

        if (extras != null) {
            //TODO Retrieve data right way. Strings, ints ? random ?? GGG

            //int[] beacon_data = extras.getIntArray("BeaconMinMaj");
            String beaconID = extras.getString("beacon_ID");
            String beaconMajor = extras.getString("beacon_major");
            String beaconMinor = extras.getString("beacon_minor");

            Log.i("#########MAJOR#########", beaconMajor);
            Log.i("#########MINOR#########", beaconMinor);
            Log.i("#########ID#########", beaconID);

            getBeaconEvent = new getBeaconEvent(beaconID, beaconMajor, beaconMinor);
            getBeaconEvent.execute((Void) null);
            Log.i("#########ONSTARTCOMMAND#########", "did i do it ?");
        }
        return 0;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate (){
    }

  

    //KNAPPTEST2
    private void openBeaconAlert(final String eventName,final String beacID, final String eventDesc) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BeaconDataService.this);

        alertDialogBuilder.setTitle("Red or Blue pill?");
        alertDialogBuilder.setMessage("Nearby event attended! \n Event name :"+eventName+"\n  Description: "+eventDesc);
        // set positive button: Yes message
        alertDialogBuilder.setNegativeButton("Ignore.",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {

                dialog.cancel();
                Toast.makeText(getApplicationContext(), "Ignored event: " + eventName,
                        Toast.LENGTH_LONG).show();

            }
        });
        // set negative button: No message
        alertDialogBuilder.setPositiveButton("Take me to event!",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                // cancel the alert box and put a Toast to the user
                Intent positveActivity = new Intent(getApplicationContext(), EventActivity.class);
                positveActivity.putExtra("event_title", eventName);
                positveActivity.putExtra("event_desc", eventDesc);
                positveActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(positveActivity);

            }
        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        // show alert
        alertDialog.show();
    }


    //KNAPPTEST2 SLUT



    //TESTAR [BEACON sens]
    public class getBeaconEvent extends AsyncTask<Void, Void, String> {
        String beaconID,beaconMajor,beaconMinor;

        getBeaconEvent(String beaconID, String beaconMajor, String beaconMinor) {
            this.beaconID = beaconID;
            this.beaconMajor = beaconMajor;
            this.beaconMinor = beaconMinor;
        }

        @Override
        protected String doInBackground(Void... params) {
            Log.i("API", "Starting get api stuff");

            InputStream inputStream = null;

            // Making HTTP request
            try {
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(API+"events?beaconsUUID="+beaconID+"&beaconsMinor="+beaconMinor+"&beaconsMajor="+beaconMajor);
                HttpResponse httpResponse = httpClient.execute(httpGet);

                Log.i("API", "server returned status code "+httpResponse.getStatusLine());

                //random debugging
                Log.i("BEACONREQUEST", "request to server: "+httpGet.getRequestLine());
                Log.i("TESTARRANDOMSHIT", "beaconID="+beaconID);
                Log.i("TESTARRANDOMSHIT2", "beaconsMinor="+beaconMinor);
                Log.i("TESTARRANDOMSHIT3", "beaconsMajor="+beaconMajor);
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
            getBeaconEvent = null;

            if (result == null) {
                return;
            }

            JSONObject jObj;
            JSONArray beacons;
            String totalCount;
            try {
                jObj = new JSONObject(result);
                beacons = jObj.getJSONArray("events");
                JSONObject metaData = jObj.getJSONArray("_metadata").getJSONObject(0);
                totalCount = metaData.getString("totalCount");
                for (int i=0; i < 1; i++)
                {
                    JSONObject event = beacons.getJSONObject(i).getJSONObject("event");
                    final String event_title = event.getString("title");
                    final String beacon_id = event.getString("beaconId");
                    final String event_desc = event.getString("description");
                    Log.i("API", event_title+", "+beacon_id+", "+event_desc);

                    //call popup method, populate with actual beacon data
                    openBeaconAlert(event_title,beacon_id,event_desc);

                }

            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
                return;
            }

            Log.i("API", "totalCount: "+totalCount);

        }

        @Override
        protected void onCancelled() {
            getBeaconEvent = null;
            //showProgress(false);
        }
    }
    //TESTAR [BEACON sens]
}
