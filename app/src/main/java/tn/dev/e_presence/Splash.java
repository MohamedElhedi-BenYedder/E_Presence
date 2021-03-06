package tn.dev.e_presence;

import android.content.Intent;
import android.os.Handler;

import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class Splash extends AppCompatActivity {
    Animation topAnim,ButtomAnim;
    ImageView image ;
    TextView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        LoadLightDarkMode();// fonction to load dark mode or Light Mode . Don't delete it MohamedElhedi .
        setContentView(R.layout.activity_splash);
        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation );
        ButtomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation );

        image = findViewById(R.id.imageView2);
        logo = findViewById(R.id.tv_email);

        image.setAnimation(topAnim);
        logo.setAnimation(ButtomAnim);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                Intent i = new Intent(Splash.this, Welcome.class);
                startActivity(i);

                finish();
            }
        }, 3000);
    }
    void LoadLightDarkMode()
    {
        if(new DarkModePrefManager(this).isNightMode()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }
}