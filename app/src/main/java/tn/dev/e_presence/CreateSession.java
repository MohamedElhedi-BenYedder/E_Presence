package tn.dev.e_presence;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateSession extends AppCompatActivity {
    Button btn_ok,btn_cancel;
    EditText et_classroom,et_teacher,et_subject,et_group,et_date,et_qrcode;
    TextView tv_qrlink,et_start,et_end;
    int hour_start,min_start,hour_end,min_end;;
    String time_start,time_end;

    Switch sw_presential;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_session);
        btn_ok=findViewById(R.id.btn_ok);
        btn_cancel=findViewById(R.id.btn_cancel);
        et_start=findViewById(R.id.et_start);
        et_end=findViewById(R.id.et_End);
        et_classroom=findViewById(R.id.et_classroom);
        et_teacher=findViewById(R.id.et_teacher);
        et_subject=findViewById(R.id.et_subject);
        et_group=findViewById(R.id.et_group);
        et_date=findViewById(R.id.et_date);
        et_qrcode=findViewById(R.id.et_qrcode);
        sw_presential=findViewById(R.id.sw_presential);
        tv_qrlink=findViewById(R.id.tv_qrlink);
        tv_qrlink.setText("Click to generate QR code");
        et_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        CreateSession.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                hour_start=hourOfDay;
                                min_start=minute;
                                String time = hour_start + ":" + min_start;
                                SimpleDateFormat f24Hour = new SimpleDateFormat("HH:mm");
                                SimpleDateFormat f12Hour = new SimpleDateFormat("HH:mm aa");
                                try {
                                    Date date = f24Hour.parse(time);
                                    time_start=f12Hour.format(date);
                                    et_start.setText(f12Hour.format(date));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        },12,0,false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.updateTime(hour_start,min_start);
                timePickerDialog.show();
            }
        });
        et_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        CreateSession.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                hour_end=hourOfDay;
                                min_end=minute;
                                String time = hour_end + ":" + min_end;
                                SimpleDateFormat f24Hour = new SimpleDateFormat("HH:mm");
                                SimpleDateFormat f12Hour = new SimpleDateFormat("HH:mm aa");
                                try {
                                    Date date = f24Hour.parse(time);
                                    time_end=f12Hour.format(date);
                                    et_end.setText(f12Hour.format(date));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        },12,0,false
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.updateTime(hour_end,min_end);
                timePickerDialog.show();
            }
        });
        tv_qrlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri="https://api.qrserver.com/v1/create-qr-code/?size=150x150&data="+et_qrcode.getText().toString();
                //String uri="https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=hani";
                Intent i = new Intent();
                i.setData(Uri.parse(uri));
                //Toast.makeText(CreateSession.this, "testclick", Toast.LENGTH_SHORT).show();
                startActivity(i);
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //get data
                String new_start= time_start;
                String new_end= time_end;
                String new_classroom=et_classroom.getText().toString();
                String new_teacher=et_teacher.getText().toString();
                String new_subject=et_subject.getText().toString();
                String new_group=et_group.getText().toString();
                String new_date=et_date.getText().toString();
                String new_qrcode=et_qrcode.getText().toString();
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
                i.putExtra("qrcode",new_qrcode);

                //
                startActivity(i);
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(),Dashboard.class);
                startActivity(i);

            }
        });


    }
}