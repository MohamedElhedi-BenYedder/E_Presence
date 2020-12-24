package tn.dev.e_presence;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TimePicker;

public class CreateSession extends AppCompatActivity {
    Button btn_ok,btn_cancel;
    EditText et_start,et_end,et_classroom,et_teacher,et_subject,et_group,et_date;

    Switch sw_presential;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_session);
        btn_ok=findViewById(R.id.btn_ok);
        et_start=findViewById(R.id.et_start);
        et_end=findViewById(R.id.et_End);
        et_classroom=findViewById(R.id.et_classroom);
        et_teacher=findViewById(R.id.et_teacher);
        et_subject=findViewById(R.id.et_subject);
        et_group=findViewById(R.id.et_group);
        et_date=findViewById(R.id.et_date);
        sw_presential=findViewById(R.id.sw_presential);


        btn_ok.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //get data
                String new_start=et_start.getText().toString();
                String new_end=et_end.getText().toString();
                String new_classroom=et_classroom.getText().toString();
                String new_teacher=et_teacher.getText().toString();
                String new_subject=et_subject.getText().toString();
                String new_group=et_group.getText().toString();
                String new_date=et_date.getText().toString();
                boolean new_presential=sw_presential.isChecked();
                // Start Dashborad Activity again
                Intent i = new Intent(v.getContext(),Dashboard.class);
                //put Data into a message for DashboradActivty
                i.putExtra("start",new_start);
                i.putExtra("end",new_end);
                i.putExtra("date",new_date);
                i.putExtra("classroom",new_classroom);
                i.putExtra("group",new_group);
                i.putExtra("teacher",new_teacher);
                i.putExtra("subject",new_subject);
                i.putExtra("presential",new_presential);

                //
                startActivity(i);
            }
        });


    }
}