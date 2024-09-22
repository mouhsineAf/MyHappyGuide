package com.devm22.happyguide.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.devm22.happyguide.R;
import com.devm22.happyguide.utils.Shared;

public class PrivacyActivity extends AppCompatActivity {
    private static final String TAG = "PrivacyActivity";


    ImageView btnBack;
    TextView textPrivacyName;
    WebView webViewContent;
    Shared shared;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        btnBack = findViewById(R.id.btn_back);
        textPrivacyName = findViewById(R.id.privacy_name);
        webViewContent = findViewById(R.id.webViewContent);


        btnBack.setOnClickListener(view -> {
            back();
        });

        shared = new Shared(PrivacyActivity.this);

        getData();



    }


    private void getData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            String name = bundle.getString("KEY_NAME");
            String content = bundle.getString("KEY_CONTENT");

            textPrivacyName.setText(name);

            // Enable JavaScript if your HTML file requires it
            webViewContent.getSettings().setJavaScriptEnabled(true);

            // Load the HTML file
            webViewContent.loadUrl(content);

            // Set a WebViewClient to handle page navigation
            webViewContent.setWebViewClient(new WebViewClient());

        }

    }



    private void back() {
        finish();
    }
}