package com.wordpress.interactiveevents.interactive_events;

import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by sofie on 14-11-20.
 */
public class Storage {
    public static final String PREFS_NAME = "Tokens";
    private static SharedPreferences auth_prefs;

    // Contructor
    public static void initSharedPrefs(android.content.Context appContext){
        auth_prefs = appContext.getSharedPreferences(PREFS_NAME, appContext.MODE_PRIVATE);
    }

    static void setNewAccessToken(String access_token) {
        // Need an Editor object to make preference changes
        // All objects are from android.context.Context?
        SharedPreferences.Editor editor = auth_prefs.edit();
        Log.d("login", "setNewAccessToken="+access_token);
        editor.putString("access_token", access_token);
        // Commit the edit!
        editor.commit();
    }

    static String getAccessToken() {
        //Log.d("login", "fromSharedPrefs, access_token=" + auth_prefs.getString("access_token", "No token stored"));
        return auth_prefs.getString("access_token", "No token in storage");
    }
}
