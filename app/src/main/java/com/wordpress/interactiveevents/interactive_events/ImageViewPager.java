package com.wordpress.interactiveevents.interactive_events;

/**
 * Created by Webbpiraten on 2014-11-27.
 */
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageViewPager extends Activity {
    // Declare Variable
    int position;
    final Context context = this;
    boolean onboard_token = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Storage.initSharedPrefs(context);
        if (Storage.OnboardTokenValid()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Set title for the ViewPager
            setTitle("ViewPager");
            // Get the view from view_pager.xml
            setContentView(R.layout.view_pager);

            // Retrieve data from MainActivity on item click event
            //Intent p = getIntent();
            //position = p.getExtras().getInt("id");
            position = 0;

            ImageAdapter imageAdapter = new ImageAdapter(this);
            List<ImageView> images = new ArrayList<ImageView>();

            // Retrieve all the images
            for (int i = 0; i < imageAdapter.getCount(); i++) {
                ImageView imageView = new ImageView(this);
                imageView.setImageResource(imageAdapter.mThumbIds[i]);
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                images.add(imageView);
            }

            // Set the images into ViewPager
            ImagePagerAdapter pageradapter = new ImagePagerAdapter(images);

            ViewPager viewpager = (ViewPager) findViewById(R.id.pager); // R.id.pager -> view_pager.xml
            viewpager.setAdapter(pageradapter);
            // Show images following the position
            viewpager.setCurrentItem(position);

        /* MINE */
            viewpager.setOnPageChangeListener(new OnPageChangeListener() {
                ImageView vp0 = (ImageView) findViewById(R.id.indicator_0);
                ImageView vp1 = (ImageView) findViewById(R.id.indicator_1);

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }

                @Override
                public void onPageSelected(int position1) {
                    //Toast.makeText(ImageViewPager.this, "" + position1, Toast.LENGTH_SHORT).show();
                    switch (position1) {
                        case 0:
                            vp0.setImageResource(R.drawable.full_10);
                            vp1.setImageResource(R.drawable.empty_10);
                            break;
                        case 1:
                            vp0.setImageResource(R.drawable.empty_10);
                            vp1.setImageResource(R.drawable.full_10);
                            break;
                    }
                }
            });
        }

    }

    public void sendMessage(View view) {
        onboard_token = true;
        Storage.setOnboardingToken(onboard_token);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
