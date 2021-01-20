package tn.dev.e_presence;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddCourse extends AppCompatActivity {

    final String TAG="AddCourse";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String UserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private EditText et_cdescription,et_cname,et_departement;
    private boolean NewCourse;
    private String SchoolId,CourseID;
    private TextView tv_title;
    private int priority;
    private Button btn_ok;
    private Button btn_cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
        findViews();
        listenForIncommingMessages();
        setOk();
        setCancel();
        setDisplay();
    }
    void setDisplay()
    {
        if(!NewCourse)
        {


            db.collection("School").document(SchoolId).collection("Course").document(CourseID)
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    et_cdescription.setText(documentSnapshot.getString("description"));
                    et_cname.setText(documentSnapshot.getString("displayName"));
                    et_departement.setText(documentSnapshot.getString("departement"));
                }
            });
        }else{
            tv_title.setText("Add Course");
        }


    }

    public void setCancel(){
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(AddCourse.this,CourseList.class).
                        putExtra("SchoolID",SchoolId);
                intent.putExtra("Priority",priority);
                startActivity(intent);
                finish();
            }
        });
    }

    public void findViews()
    {
        et_departement=findViewById(R.id.et_departement);
        btn_ok=findViewById(R.id.btn_ok);
        btn_cancel=findViewById(R.id.btn_cancel);
        et_cname=findViewById(R.id.et_cname);
        et_cdescription=findViewById(R.id.et_cdescription);
        tv_title =findViewById(R.id.tv_title);
    }
    public void setOk()
    {
        btn_ok.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //get data
                String new_Name= et_cname.getText().toString();
                String new_Description= et_cdescription.getText().toString();
                String new_Departement=et_departement.getText().toString();
                Map<String, Object> course = new HashMap<>();
                course.put("displayName", new_Name);
                course.put("description",new_Description);
                course.put("departement",new_Departement);
                // Start Dashborad Activity again
                if (NewCourse) {

                    //put Data into a message for group



                    { db.collection("School").document(SchoolId).collection("Course").document(new_Name)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        Toast.makeText(AddCourse.this, "Existing Course Name", Toast.LENGTH_SHORT).show();

                                    } else {
                                        db.collection("School").document(SchoolId).collection("Course").document(new_Name)
                                                .set(course)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(AddCourse.this, "Course successfully added!", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(AddCourse.this,CourseList.class)
                                                                .putExtra("SchoolID",SchoolId)
                                                                .putExtra("Priority", priority);
                                                        ;
                                                        startActivity(intent);
                                                        finish();

                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error adding Group", e);
                                                        Toast.makeText(AddCourse.this, "Error adding Course", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {


                                }
                            });
                    }
                }
                else
                {
                    db.collection("School").document(SchoolId).collection("Course").document(CourseID)
                            .update(course)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(AddCourse.this, "Course successfully added!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AddCourse.this,CourseList.class)
                                            .putExtra("SchoolID",SchoolId)
                                            .putExtra("Priority", priority);
                                    ;
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding Group", e);
                                    Toast.makeText(AddCourse.this, "Error adding Course", Toast.LENGTH_SHORT).show();
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
        SchoolId =incommingMessages.getString("SchoolID","0");
        priority=incommingMessages.getInt("Priority",0);
        CourseID=incommingMessages.getString("CourseID","0");
        NewCourse=incommingMessages.getBoolean("NewCourse",false);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent =new Intent(AddCourse.this,CourseList.class).
                putExtra("SchoolID",SchoolId);
        intent.putExtra("Priority",priority);
        startActivity(intent);
        finish();
    }

}