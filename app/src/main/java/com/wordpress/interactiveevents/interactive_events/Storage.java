package com.wordpress.interactiveevents.interactive_events;

import android.content.SharedPreferences;
import android.util.Log;

import java.util.Date;

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

    static void setExpireDateForNewestToken(long expires_in) {
        // Need an Editor object to make preference changes
        // All objects are from android.context.Context?
        SharedPreferences.Editor editor = auth_prefs.edit();
        Log.d("login", "setExpireDateForNewestToken="+expires_in);
        editor.putLong("expires_in", expires_in);
        // Commit the edit!
        editor.commit();
    }

    static String getAccessToken() {
        //Log.d("login", "fromSharedPrefs, access_token=" + auth_prefs.getString("access_token", "No token stored"));
        String access_token = auth_prefs.getString("access_token", "");
        if (access_token.equals("")) {
            Log.d("access_token", "No access_token stored in SharedPreferences");
        }
        return access_token;
    }

    static long getExpireDateForNewestToken() {
        //Log.d("login", "fromSharedPrefs, expires_in=" + auth_prefs.getString("expires_in", "No date stored"));
        long expires_in = auth_prefs.getLong("expires_in", 0);
        if (expires_in==0) {
            Log.d("access_token", "No expire date stored in SharedPreferences");
        }
        return expires_in;
    }

    static boolean accessTokenValid() {
        // return false
        long currentTimeSinceEpoch = new Date().getTime();
        long expireTimeSinceEpoch = getExpireDateForNewestToken();
        // access_token has expired
        if (currentTimeSinceEpoch >= expireTimeSinceEpoch) {
            Log.d("access_token", "access_token has expired");
            return false;
            // access_token still valid
        } else {
            // access_token is valid for timeToGo milliseconds...
            long timeToGo = expireTimeSinceEpoch - currentTimeSinceEpoch;
            Log.d("access_token", "access_token still valid for "+timeToGo/60000+" minutes");
            return true;
        }
    }
}
