package com.wordpress.interactiveevents.interactive_events;



import android.app.Activity;
import android.content.Intent;
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
    private String moduleId = "1";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            eventId = extras.getString("eventId");
            moduleId = extras.getString("moduleId");
            Log.d("modulelist", "eventId="+eventId+" moduleId="+moduleId);
        }

        webView = (WebView) findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://www.aftonbladet.se");
        //webView.loadUrl("http://interactive-events-web-app.s3-website-eu-west-1.amazonaws.com/events/123/modules/1/push");
        //webView.loadUrl("http://interactive-events-web-app.s3-website-eu-west-1.amazonaws.com/events/%s/modules/%s/push", eventId, moduleId);
        //String url = String.format("http://interactive-events-web-app.s3-website-eu-west-1.amazonaws.com/events/%s/modules/%s/push", eventId, moduleId);

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
                Intent nextScreen = new Intent(getApplicationContext(), EventListActivity.class);
                startActivity(nextScreen);
            }

        });

    }
}
