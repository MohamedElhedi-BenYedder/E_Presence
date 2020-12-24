package tn.dev.e_presence;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class SessionDetails extends AppCompatActivity {
    ImageButton btn_rq;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_details);
        btn_rq=findViewById(R.id.btn_qr);
        btn_rq.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                Intent i = new Intent(v.getContext(),CreateSession.class);


                //
                startActivity(i);
            }
        });


    }
    }
