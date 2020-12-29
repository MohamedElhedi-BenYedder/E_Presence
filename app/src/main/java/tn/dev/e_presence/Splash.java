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

public class Splash extends AppCompatActivity {
    Animation topAnim,ButtomAnim;
    ImageView image ;
    TextView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation );
        ButtomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation );

        image = findViewById(R.id.imageView2);
        logo = findViewById(R.id.textView2);

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
}