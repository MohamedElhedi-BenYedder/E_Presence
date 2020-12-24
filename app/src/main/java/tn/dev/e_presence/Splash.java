package tn.dev.e_presence;

import android.content.Intent;
import android.os.Handler;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                Intent i = new Intent(Splash.this, Welcome.class);
                startActivity(i);

                finish();
            }
        }, 3000);
    }
}