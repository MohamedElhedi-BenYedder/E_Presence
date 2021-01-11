package tn.dev.e_presence;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;

public class QrwebpageActivity extends AppCompatActivity {

    private WebView webView;
    private String Url,GroupID,SchoolID;
    private ArrayList<String> teacherIdList,teacherNameList;
    private ArrayList<String> groupIdList,groupNameList;
    private String NewSessionID;
    private int priority;

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
                    .putExtra("SchoolID",SchoolID)
                    .putExtra("GroupID",GroupID)
                    .putExtra("Priority",priority)
                    .putStringArrayListExtra("groupIdList",groupIdList)
                    .putStringArrayListExtra("groupNameList",groupNameList)
                    .putStringArrayListExtra("teacherIdList", teacherIdList)
                    .putStringArrayListExtra("teacherNameList",  teacherNameList)
                    .putExtra("NewSessionID",NewSessionID);
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
        NewSessionID=incommingMessages.getString("NewSessionID","0");
        teacherIdList=incommingMessages.getStringArrayList("teacherIdList");
        teacherNameList=incommingMessages.getStringArrayList("teacherNameList");
        groupIdList=incommingMessages.getStringArrayList("groupIdList");
        groupNameList=incommingMessages.getStringArrayList("groupNameList");
        priority=incommingMessages.getInt("Priority",0);

    }



}