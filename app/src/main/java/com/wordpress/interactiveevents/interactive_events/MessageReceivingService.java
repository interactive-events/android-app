package com.wordpress.interactiveevents.interactive_events;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;



import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/*
 * This service is designed to run in the background and receive messages from gcm. If the app is in the foreground
 * when a message is received, it will immediately be posted. If the app is not in the foreground, the message will be saved
 * and a notification is posted to the NotificationManager.
 */
public class MessageReceivingService extends Service {
    private GoogleCloudMessaging gcm;
    public static SharedPreferences savedValues;
    private static String webAddress = "http://interactive-events-web-app.s3-website-eu-west-1.amazonaws.com/";
    private String address = "http://interactive-events.elasticbeanstalk.com/users/"+Storage.getUserId()+"?access_token="+Storage.getAccessToken();
    private static final String TAG = ".MessageReceivingService";






/*
    public static void sendToApp(Bundle extras, Context context){
       // Intent newIntent = new Intent();
       // newIntent.setClass(context, AndroidMobilePushApp.class);
       // newIntent.putExtras(extras);
        Log.i("ASD KATT MEW", " : "+extras.getString("default"));
       // newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
       // context.startActivity(newIntent);
    }
*/
    public void onCreate(){
        super.onCreate();
        final String preferences = getString(R.string.preferences);
        savedValues = getSharedPreferences(preferences, Context.MODE_PRIVATE);
        // In later versions multi_process is no longer the default
        if(VERSION.SDK_INT >  9){
            savedValues = getSharedPreferences(preferences, Context.MODE_MULTI_PROCESS);
        }
        gcm = GoogleCloudMessaging.getInstance(getBaseContext());
        SharedPreferences savedValues = PreferenceManager.getDefaultSharedPreferences(this);
        if(savedValues.getBoolean(getString(R.string.first_launch), true)){
            Log.i("WAI NO REGISTER?", "DERP");
            register();
            SharedPreferences.Editor editor = savedValues.edit();
            editor.putBoolean(getString(R.string.first_launch), false);
            editor.commit();
        }
        // Let AndroidMobilePushApp know we have just initialized and there may be stored messages
        //sendToApp(new Bundle(), this);
    }

    protected static void saveToLog(Bundle extras, Context context){
        String evntId = null;
        String actId = null;
        String moduleName = null;
        String evntTitle = null;
        String urlStr = null;

        //SharedPreferences.Editor editor=savedValues.edit();
        //String numOfMissedMessages = context.getString(R.string.num_of_missed_messages);
        int linesOfMessageCount = 0;
        for(String key : extras.keySet()) {
            String line = String.format("%s=%s", key, extras.getString(key));
            Log.i(TAG, "####GETSTRING####" + line);
            if (line.contains("eventId")) {
                evntId = line.substring(line.indexOf("=") + 1);

            } else if (line.contains("activityId")) {
                actId = line.substring(line.indexOf("=") + 1);

            } else if (line.contains("name")) {
                moduleName = line.substring(line.indexOf("=") + 1);

            } else if (line.contains("eventTitle")) {
                evntTitle = line.substring(line.indexOf("=") + 1);

            } else if (line.contains("url")) {
                urlStr = line.substring(line.indexOf("=") + 1);
                urlStr = urlStr+"?access_token="+Storage.getAccessToken();
            }
        }
            Log.i("AMAGAD SO MUCH DATA", "ALL THE THINGS \n"+"evnt id "+evntId+" activityId "+actId+" Module name "+moduleName+" eventTitle "+evntTitle);
            //editor.putString("MessageLine" + linesOfMessageCount, line);
            linesOfMessageCount++;



        //editor.putInt(context.getString(R.string.lines_of_message_count), linesOfMessageCount);
        //editor.putInt(context.getString(R.string.lines_of_message_count), linesOfMessageCount);
        //editor.putInt(numOfMissedMessages, savedValues.getInt(numOfMissedMessages, 0) + 1);
        //editor.commit();
        //postNotification(new Intent(context, AndroidMobilePushApp.class), context);
        if(evntId != null && !evntId.isEmpty() && actId != null && !actId.isEmpty()){
            Log.i(TAG,"URLSTRTILLINTENT= "+urlStr);
            Intent pushIntent = new Intent(context, WebViewActivity.class);
            pushIntent.putExtra("urlStr", urlStr);
            postNotification(pushIntent, context, evntTitle, moduleName);
        } else {
            Log.i("empty urlStr", "do nothing");
        }
    }



    protected static void postNotification(Intent intentAction, Context context, String eventName, String moduleType){
        final NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentAction, PendingIntent.FLAG_ONE_SHOT);
        final Notification notification = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(eventName+" Has posted a new "+moduleType)
                .setContentText("")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .getNotification();

        mNotificationManager.notify(R.string.notification_number, notification);
    }

    private void register() {
        new AsyncTask(){
            protected Object doInBackground(final Object... params) {
                String token;
                try {
                    token = gcm.register(getString(R.string.project_number));
                    Log.i("registrationId", token);

                    //HTTP PUT GCM TOKEN TO SERVER
                    HttpClient client = new DefaultHttpClient();
                    HttpPut put = new HttpPut(address);

                    StringEntity data = null;
                    try {

                        String d = String.format("{\"gcmToken\":\"%s\"}", token);

                        data = new StringEntity(d);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    //httpPost.setHeader("Accept", "application/json");

                    data.setContentType("application/json; charset=UTF-8"); //; charset=UTF-8
                    //data.setContentEncoding("application/json; charset=UTF-8");

                    put.setEntity(data);
                    put.setHeader("Content-Type", "application/json; charset=UTF-8");
                    client.execute(put);
                    Log.i(TAG,"HTTP PUT GOING TO: "+address);
                    //END HTTP PUT TO SERVER

                }
                catch (IOException e) {
                    Log.i("Registration Error", e.getMessage());
                }
                return true;
            }
        }.execute(null, null, null);
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }

}