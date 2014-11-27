package com.wordpress.interactiveevents.interactive_events;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;

/**
 * Created by Webbpirten on 2014-11-17.
 */
public class MainActivity extends Activity{
    // Declare Variable
    int position;

    // HUVUDFÖNSTRET, HÄR SKA MAN KUNNA GÅ HÖGER/VÄNSTER
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set title for the ViewPager
        setTitle("ViewPager");
        // Get the view from view_pager.xml
        setContentView(R.layout.view_pager);

        /*
        // Retrieve data from MainActivity on item click event
        Intent p = getIntent();
        position = p.getExtras().getInt("id");

        ImageAdapter imageAdapter = new ImageAdapter(this);
        List<ImageView> images = new ArrayList<ImageView>();

        // Retrieve all the images
        for (int i = 0; i < imageAdapter.getCount(); i++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(imageAdapter.mThumbIds[i]);
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            images.add(imageView);
        }
        */
        // Set the images into ViewPager
        //ImagePagerAdapter pageradapter = new ImagePagerAdapter(images);
        //ViewPager viewpager = (ViewPager) findViewById(R.id.pager);
        //viewpager.setAdapter(pageradapter);
        // Show images following the position
        //viewpager.setCurrentItem(position);
    }
}
