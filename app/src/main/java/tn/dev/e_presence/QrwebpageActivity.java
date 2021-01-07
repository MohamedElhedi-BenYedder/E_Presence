package tn.dev.e_presence;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class QrwebpageActivity extends AppCompatActivity {

    private WebView webView;
    private String Url,GroupID,SchoolID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrwebpage);
        webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());
        listenForIncommingMessages();
        webView.loadUrl(Url);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
            Intent i = new Intent(QrwebpageActivity.this,Dashboard.class)
                    .putExtra("SchoolID",SchoolID).putExtra("GroupID",GroupID);
            startActivity(i);
            finish();
        }
    }
    void listenForIncommingMessages()
    {
        //listen for incoming messages
        Bundle incommingMessages =getIntent().getExtras();
        Url=incommingMessages.getString("Qrurl","0");
        GroupID=incommingMessages.getString("GroupID","0");
        SchoolID=incommingMessages.getString("SchoolID","0");

    }


}