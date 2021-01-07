package tn.dev.e_presence;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateSession extends AppCompatActivity {
    Button btn_ok,btn_cancel;
    EditText et_classroom,et_teacher,et_subject,et_group,et_date,et_qrcode;
    TextView tv_qrlink,et_start,et_end;
    int hour_start,min_start,hour_end,min_end;;
    String time_start,time_end;
    boolean NewSession;
    String SchoolId;
    String GroupId,Uri;
    final String TAG="CreateNewSession";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String UserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private  int Compteur=0;

    Switch sw_presential;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_session);
        listenForIncommingMessages();
        findViews();
        setStart();
        setEnd();
        tv_qrlink.setText("Click to generate QR code");
        setLink();
        setCancel();
        setOk();
    }

    public void findViews()
    {
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
    }
    public void setStart()
    {
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
                                //SimpleDateFormat f12Hour = new SimpleDateFormat("HH:mm aa");
                                try {
                                    Date date = f24Hour.parse(time);
                                    time_start=f24Hour.format(date);
                                    et_start.setText(f24Hour.format(date));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        },12,0,true
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.updateTime(hour_start,min_start);
                timePickerDialog.show();
            }
        });
    }
    public void setEnd()
    {
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
                                //SimpleDateFormat f12Hour = new SimpleDateFormat("HH:mm aa");
                                try {
                                    Date date = f24Hour.parse(time);
                                    time_end=f24Hour.format(date);
                                    et_end.setText(f24Hour.format(date));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        },12,0,true
                );
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.updateTime(hour_end,min_end);
                timePickerDialog.show();
            }
        });
    }
    public void setLink()
    {
        tv_qrlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Uri="https://api.qrserver.com/v1/create-qr-code/?size=150x150&data="+et_qrcode.getText().toString();
                //String uri="https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=hani";
                Intent i = new Intent(CreateSession.this,QrwebpageActivity.class).putExtra("Qrurl",Uri)
                        .putExtra("SchoolID",SchoolId).putExtra("GroupID",GroupId);

                startActivity(i);
                finish();
            }
        });
    }
    public void setCancel()
    {
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(),Dashboard.class).putExtra("SchoolID",SchoolId).putExtra("GroupID",GroupId);

                startActivity(i);
                finish();

            }
        });

    }
    public void setOk()
    {
        btn_ok.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(Compteur==0){
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
                if (NewSession) {
                    Intent i = new Intent(CreateSession.this,QrwebpageActivity.class);
                    //put Data into a message for DashboradActivty

                    Map<String, Object> session = new HashMap<>();
                    session.put("start", new_start);
                    session.put("end", new_end);
                    session.put("date", new_date);
                    session.put("classroom", new_classroom);
                    session.put("group", new_group);
                    session.put("teacher", new_teacher);
                    session.put("subject", new_subject);
                    session.put("presential", new_presential);
                    session.put("qrcode", new_qrcode);
                    session.put("listOfPresence",new ArrayList<String>());
                    String timeC=""+System.currentTimeMillis();

                    db.collection("School").document(SchoolId).collection("Session").document(timeC)
                            .set(session)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Compteur+=1;
                                    Log.d(TAG, "Session successfully added!");
                                    Toast.makeText(CreateSession.this, "Session successfully added!", Toast.LENGTH_SHORT).show();
                                    Uri="https://api.qrserver.com/v1/create-qr-code/?size=150x150&data="+et_qrcode.getText().toString();

                                     i.putExtra("Qrurl",Uri)
                                            .putExtra("SchoolID",SchoolId).putExtra("GroupID",GroupId);

                                    startActivity(i);
                                    finish();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding session", e);
                                    Toast.makeText(CreateSession.this, "Error adding session", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else
                {

                }

                //

            }else{
                    Toast.makeText(CreateSession.this, "Loading...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    void listenForIncommingMessages()
    {
        //listen for incoming messages
        Bundle incommingMessages =getIntent().getExtras();
        NewSession =incommingMessages.getBoolean("NewSession",true);
        SchoolId =incommingMessages.getString("SchoolID","0");
        GroupId=incommingMessages.getString("GroupID","0");

    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent i = new Intent(CreateSession.this,Dashboard.class).putExtra("SchoolID",SchoolId).putExtra("GroupID",GroupId);
        startActivity(i);
        finish();
    }






}