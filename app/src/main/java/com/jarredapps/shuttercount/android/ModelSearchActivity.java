package com.jarredapps.shuttercount.android;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.*;
import android.view.*;

public class ModelSearchActivity extends Activity {
    WebView modelsearchWebView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modelsearch);
        modelsearchWebView = findViewById(R.id.modelsearchWebView);
        setTitle("Camera Brand Searcher");
        
        WebSettings settings = modelsearchWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setLightTouchEnabled(true);
        
        String modelToSearcb = getIntent().getStringExtra("modelToSearch");
        modelsearchWebView.setWebViewClient(new WebViewClient());
        modelsearchWebView.loadUrl("https://www.google.com/search?q=" + modelToSearcb);
    }
    
}
