package com.wordpress.interactiveevents.interactive_events;



import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class WebViewActivity extends Activity {

    private WebView webView;
    private Button homeBtn;
    private String eventId;
    private String urlStr;
    private String activityId = "1";
    private static final String TAG = ".WebviewActivity";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            eventId = extras.getString("eventId");
            activityId = extras.getString("activityId");
            urlStr = extras.getString("urlStr");
            Log.i(TAG,"##URL STR##"+urlStr);
            Log.d("modulelist", "eventId="+eventId+" activityId="+activityId);
        }
        urlStr = urlStr+"?access_token="+Storage.getAccessToken();

        webView = (WebView) findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setWebContentsDebuggingEnabled(true);
        }

        String access_token = Storage.getAccessToken();
        String access_param = "?access_token="+access_token;
        Log.d("Web", urlStr);
        //webView.loadUrl("http://www.aftonbladet.se");
        if(urlStr != null && !urlStr.isEmpty()){
            webView.loadUrl(urlStr);
            Log.i(TAG,"### JAG LADDAR URL ### "+urlStr);
        } else {
            webView.loadUrl("http://interactive-events-web-app.s3-website-eu-west-1.amazonaws.com/events/123/modules/1/push"+access_param);
        }

        //webView.loadUrl("http://interactive-events-web-app.s3-website-eu-west-1.amazonaws.com/events/%s/modules/%s/push"+access_param, eventId, moduleId);
        //String url = String.format("http://interactive-events-web-app.s3-website-eu-west-1.amazonaws.com/events/%s/modules/%s/push"+access_param, eventId, moduleId);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                Log.d("URL:",url);
                return false;
            }
        });

        homeBtn = (Button) findViewById(R.id.eventBtn);

        homeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // Highjacking this button for test... remove later
                //webView.reload();
                //webView.loadUrl(urlStr);

                Intent nextScreen = new Intent(getApplicationContext(), EventListActivity.class);
                startActivity(nextScreen);
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
            }

        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }
}
