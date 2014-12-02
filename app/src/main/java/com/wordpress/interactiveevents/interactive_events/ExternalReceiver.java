package com.wordpress.interactiveevents.interactive_events;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class ExternalReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        if(intent!=null){
            Bundle extras = intent.getExtras();
            Log.i("SOMETHING IS COMING!", "RUUN!");
            MessageReceivingService.saveToLog(extras, context);
            //MessageReceivingService.sendToApp(extras, context);
            /*if(!AndroidMobilePushApp.inBackground){
                MessageReceivingService.sendToApp(extras, context);
            }
            else{
                MessageReceivingService.saveToLog(extras, context);
            }*/
        }
    }
}

