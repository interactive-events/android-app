package com.wordpress.interactiveevents.interactive_events;

import android.app.Activity;
import android.app.Application;
import android.app.IntentService;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.service.RangingData;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;
import org.altbeacon.beacon.RangeNotifier;

import java.lang.reflect.Array;
import java.util.Collection;

/**
 * Created by Webbpiraten on 2014-11-10.
 */
public class BleActivity extends Application implements BootstrapNotifier, RangeNotifier{
    private static final String TAG = ".BleActivity";
    private RegionBootstrap regionBootstrap;
    private String[] BeaconMinMaj;
    private BeaconManager bm;

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "----------> Started! <----------");
        //BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
        //beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
        //beaconManager.bind(this);

        BeaconManager.getInstanceForApplication(this).getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));

        // Identifier.fromInt(1)
        Region region = new Region("com.wordpress.interactiveevents.interactive_events",Identifier.parse("B9407F30-F5F8-466E-AFF9-25556B57fE6D"), null, null);
        regionBootstrap = new RegionBootstrap(this, region);
        bm = BeaconManager.getInstanceForApplication(this);
        bm.setBackgroundScanPeriod(2000); // 2 seconds
        bm.setBackgroundBetweenScanPeriod(15000); // 15 seconds

        bm.setRangeNotifier(this);


    }

    @Override
    public void didEnterRegion(Region region) {
        Log.d(TAG, "###################################### Did enter region ######################################");
        // Important:  make sure to add android:launchMode="singleInstance" in the manifest
        // to keep multiple copies of this activity from getting created if the user has
        // already manually launched the app.
        try {
            bm.startRangingBeaconsInRegion(region);
        } catch (RemoteException e){
            e.printStackTrace();
        }
    }
    @Override
    public void didDetermineStateForRegion(int i, Region region) {
    }
    @Override
    public void didExitRegion(Region region) {
    }
    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        Intent beaconService = new Intent(getApplicationContext(), BeaconDataService.class);
        for (Beacon beacon: beacons){
            Log.i(TAG, "Beacon detected with id1: "+beacon.getId1()+" id2:"+beacon.getId2()+" id3: "+beacon.getId3());
            beaconService.putExtra("beacon_ID", beacon.getId1().toString());
            beaconService.putExtra("beacon_major", beacon.getId2().toString());
            beaconService.putExtra("beacon_minor", beacon.getId3().toString());
            startService(beaconService);
        }
    }
}