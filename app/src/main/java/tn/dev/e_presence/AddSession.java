package tn.dev.e_presence;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class AddSession extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private Button btn_ok,btn_cancel;
    private EditText et_classroom,et_qrcode;
    private TextView tv_qrlink,et_start,et_end;
    private int hour_start,min_start,hour_end,min_end;;
    private String time_start="",time_end="",date_sess="";
    private static String group_sess,teacher_sess,cours_sess,teacher_sess_id,group_sess_id;
    boolean NewSession;
    private String NewSessionID,SessionID;
    private String SchoolId;
    private ArrayAdapter<String> group_adapter;
    private String GroupId,Uri;
    private TextView et_date;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    final String TAG="CreateNewSession";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String UserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private TextView tv_title;
    private Spinner spinner_group,spinner_teacher,spinner_cours;
    private ArrayList<String> teacherIdList,teacherNameList;
    private ArrayList<String> groupIdList,groupNameList;
    private ArrayList<String> courseNameList;
    private int priority;

    Switch sw_presential;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_session);
        listenForIncommingMessages();
        findViews();
        setDate();
        setStart();
        setEnd();
        setGroup();
        setTeacher();
        setCourse();
        tv_qrlink.setText("Click to generate QR code");
        setLink();
        setCancel();
        setOk();
        setDisplay();
    }
    void setDisplay()
    {
        if(!NewSession)
        {

            db.collection("School").document(SchoolId).collection("Session").document(SessionID)
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    time_start=documentSnapshot.getString("start");
                    et_start.setText(time_start);
                    time_end=documentSnapshot.getString("end");
                    et_end.setText(time_end);
                    et_classroom.setText(documentSnapshot.getString("classroom"));
                    date_sess=documentSnapshot.getString("date");
                    et_date.setText(date_sess);
                    sw_presential.setChecked(documentSnapshot.getBoolean("presential"));
                    et_qrcode.setText(documentSnapshot.getString("qrcode"));
                    int teacherId_pos=teacherIdList.indexOf(documentSnapshot.getString("teacherId"));
                    int groupId_pos=groupIdList.indexOf(documentSnapshot.getString("groupId"));
                    int course_pos=courseNameList.indexOf(documentSnapshot.getString("subject"));
                    spinner_teacher.setSelection(teacherId_pos+1);
                    spinner_cours.setSelection(course_pos+1);
                    spinner_group.setSelection(groupId_pos+1);
                }
            });
        }
        else tv_title.setText("Add Session");


    }
    public void setGroup(){
        List<String> groupestatique= new ArrayList<String>();groupestatique.add("Select a Group");
        groupestatique.addAll(groupNameList);
        ArrayAdapter<String> group_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,groupestatique);
        group_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_group.setAdapter(group_adapter);
        spinner_group.setOnItemSelectedListener(this);
        group_sess=spinner_group.getSelectedItem().toString();
    }
    public void setTeacher(){
        List<String> teacherstatique= new ArrayList<String>();teacherstatique.add("Select a Teacher");
        teacherstatique.addAll(teacherNameList);
        ArrayAdapter<String> teacher_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,teacherstatique);
        teacher_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_teacher.setAdapter(teacher_adapter);
        spinner_teacher.setOnItemSelectedListener(this);
        teacher_sess=spinner_group.getSelectedItem().toString();

    }
    public void setCourse(){
        List<String> coursstatique=new ArrayList<String>();coursstatique.add("Select a Course");
        coursstatique.addAll( courseNameList);
        ArrayAdapter<String> cours_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,coursstatique);
        cours_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_cours.setAdapter(cours_adapter);
        spinner_cours.setOnItemSelectedListener(this);
        cours_sess=spinner_group.getSelectedItem().toString();
    }
    public void setDate(){

        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        AddSession.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String Sday,smonth,date;
                if (day<10) Sday="0"+day;
                else Sday=""+day;
                if (month<10) smonth="0"+month;
                else smonth=""+month;
                date = Sday+"/"+smonth+"/" + year;
                date_sess=date;
                et_date.setText(date);
            }
        };
    }
    public void findViews()
    {
        btn_ok=findViewById(R.id.btn_ok);
        btn_cancel=findViewById(R.id.btn_cancel);
        et_start=findViewById(R.id.et_start);
        et_end=findViewById(R.id.et_End);
        et_classroom=findViewById(R.id.et_classroom);
        spinner_cours=findViewById(R.id.sp_cours);
        spinner_teacher=findViewById(R.id.sp_teacher);
        et_date = (TextView) findViewById(R.id.et_date);
        et_qrcode=findViewById(R.id.et_qrcode);
        sw_presential=findViewById(R.id.sw_presential);
        tv_qrlink=findViewById(R.id.tv_qrlink);
        spinner_group=findViewById(R.id.sp_group);
        tv_title=findViewById(R.id.tv_title);
    }
    public void setStart()
    {
        et_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        AddSession.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
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
                        AddSession.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
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
                Intent i = new Intent(AddSession.this, WebPage.class).putExtra("Qrurl",Uri)
                        .putExtra("SchoolID",SchoolId).putExtra("GroupID",GroupId)
                        .putExtra("Priority",priority)
                        .putStringArrayListExtra("groupIdList",groupIdList)
                        .putStringArrayListExtra("groupNameList",groupNameList)
                        .putStringArrayListExtra("teacherIdList", teacherIdList)
                        .putStringArrayListExtra("teacherNameList",  teacherNameList)
                        .putExtra("NewSessionID",NewSessionID);

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
                Intent i = new Intent(AddSession.this,Dashboard.class)
                        .putExtra("SchoolID",SchoolId)
                        .putExtra("GroupID",GroupId)
                        .putExtra("Priority",priority)
                        .putStringArrayListExtra("groupIdList",groupIdList)
                        .putStringArrayListExtra("groupNameList",groupNameList)
                        .putStringArrayListExtra("teacherIdList", teacherIdList)
                        .putStringArrayListExtra("teacherNameList",  teacherNameList)
                        .putExtra("NewSessionID",NewSessionID);


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
                //get data
                String new_start= time_start;
                String new_end= time_end;
                String new_classroom=et_classroom.getText().toString();
                String new_teacher=teacher_sess;
                String new_teacherId=teacher_sess_id;
                String new_subject=cours_sess;
                String new_group=group_sess;
                String new_groupId=group_sess_id;
                String new_date=date_sess;
                String new_qrcode=et_qrcode.getText().toString();
                boolean new_presential=sw_presential.isChecked();
                Intent i = new Intent(AddSession.this, WebPage.class)
                        .putExtra("SchoolID",SchoolId)
                        .putExtra("GroupID",GroupId)
                        .putExtra("Priority",priority);
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
                session.put("teacherId",new_teacherId);
                session.put("schoolId",SchoolId);
                session.put("groupId",new_groupId);
                // Start Dashborad Activity again
                if (NewSession) {


                    db.collection("School")
                            .document(SchoolId)
                            .collection("Session")
                            .document(NewSessionID)
                            .get().
                            addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists())
                                    {
                                        Toast.makeText(AddSession.this, "Loading . . .", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        db.collection("School").document(SchoolId).collection("Session").document(NewSessionID)
                                                .set(session)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "Session successfully added!");
                                                        Toast.makeText(AddSession.this, "Session successfully added!", Toast.LENGTH_SHORT).show();
                                                        Uri="https://api.qrserver.com/v1/create-qr-code/?size=250x250&data="+et_qrcode.getText().toString();
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
                                                        Toast.makeText(AddSession.this, "Error adding session", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }

                                }
                            });

                }
                else
                {
                    db.collection("School").document(SchoolId).collection("Session").document(SessionID)
                            .update(session)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Session successfully added!");
                                    Toast.makeText(AddSession.this, "Session successfully added!", Toast.LENGTH_SHORT).show();
                                    Uri="https://api.qrserver.com/v1/create-qr-code/?size=250x250&data="+et_qrcode.getText().toString();

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
                                    Toast.makeText(AddSession.this, "Error adding session", Toast.LENGTH_SHORT).show();
                                }
                            });
                }

                //


            }
        });
    }
    void listenForIncommingMessages()
    {
        //listen for incoming messages
        Bundle incommingMessages =getIntent().getExtras();
        NewSession =incommingMessages.getBoolean("NewSession",true);
        SchoolId =incommingMessages.getString("SchoolID","0");
        NewSessionID=incommingMessages.getString("NewSessionID","0");
        GroupId=incommingMessages.getString("GroupID","0");
        teacherIdList=incommingMessages.getStringArrayList("teacherIdList");
        teacherNameList=incommingMessages.getStringArrayList("teacherNameList");
        groupIdList=incommingMessages.getStringArrayList("groupIdList");
        groupNameList=incommingMessages.getStringArrayList("groupNameList");
        courseNameList=incommingMessages.getStringArrayList("courseNameList");
        priority=incommingMessages.getInt("Priority",0);
        SessionID =incommingMessages.getString("SessionID","0");


    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent i = new Intent(AddSession.this,Dashboard.class)
                .putExtra("SchoolID",SchoolId)
                .putExtra("GroupID",GroupId)
                .putExtra("Priority",priority)
                .putStringArrayListExtra("groupIdList",groupIdList)
                .putStringArrayListExtra("groupNameList",groupNameList)
                .putStringArrayListExtra("teacherIdList", teacherIdList)
                .putStringArrayListExtra("teacherNameList",  teacherNameList)
                .putExtra("NewSessionID",NewSessionID);


        startActivity(i);
        finish();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
      if(position!=0) {

          switch(parent.getId()){
              case R.id.sp_group:
                  group_sess=parent.getItemAtPosition(position).toString();
                  group_sess_id=groupIdList.get(position-1);
                  break;
              case R.id.sp_teacher:
                  teacher_sess=parent.getItemAtPosition(position).toString();
                  teacher_sess_id=teacherIdList.get(position-1);
                  break;
              case R.id.sp_cours:
                  cours_sess=parent.getItemAtPosition(position).toString();
                  break;
          }
      }


    }



    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    private void UpdateToken(){
        Map<String,Object> tokenMap=new HashMap<String, Object>();
        String refreshToken= FirebaseInstanceId.getInstance().getToken();
        tokenMap.put("token",refreshToken);
        db.collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(tokenMap);
    }




}