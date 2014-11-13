package com.wordpress.interactiveevents.interactive_events;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

/**
 * Created by Webbpiraten on 2014-11-10.
 */
public class BleActivity extends Application implements BootstrapNotifier{
    private static final String TAG = ".BleActivity";
    private RegionBootstrap regionBootstrap;

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "----------> Started! <----------");
        //BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
        //beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
        //beaconManager.bind(this);

        BeaconManager.getInstanceForApplication(this).getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));

        Region region = new Region("com.wordpress.interactiveevents.interactive_events",Identifier.parse("B9407F30-F5F8-466E-AFF9-25556B57fE6D"), null, null);
        regionBootstrap = new RegionBootstrap(this, region);

        BeaconManager bm = BeaconManager.getInstanceForApplication(this);
        bm.setBackgroundScanPeriod(2000); // 2 seconds
        bm.setBackgroundBetweenScanPeriod(15000); // 15 seconds

    }

    @Override
    public void didEnterRegion(Region arg0) {
        Log.d(TAG, "###################################### Did enter region ######################################");
        Intent beaconService = new Intent(getApplicationContext(), BeaconDataService.class);

        //##########EL SIMONÈ LOS VARIABLOS BELOW##########
        /*
        beaconService.putExtra("beacon_ID", title);
        beaconService.putExtra("beacon_major", description);
        beaconService.putExtra("beacon_minor", description);
        */
        
        
        // Important:  make sure to add android:launchMode="singleInstance" in the manifest
        // to keep multiple copies of this activity from getting created if the user has
        // already manually launched the app.

        //old code no bueno activity-san
        //startActivity(intent);

        startService(beaconService);
    }
    @Override
    public void didDetermineStateForRegion(int i, Region region) {
    }
    @Override
    public void didExitRegion(Region region) {
    }
}