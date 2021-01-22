package tn.dev.e_presence;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static tn.dev.e_presence.GV.getSchool;

public class SchoolPage extends AppCompatActivity {
    private BottomAppBar bottomAppBar;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private final String UserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private int priority;
    private CollectionReference GroupRef;
    private String SchoolId;
    private String TAG="SchoolPage";
    private TextView tv_display_name;
    private TextView tv_description;
    private TextView tv_full_name;
    private TextView tv_welcome;
    private TextView tv_location;
    private ImageView iv_teacher;
    private ImageView iv_student;
    private ImageView iv_group;
    private ImageView iv_session;
    private ImageView iv_course;
    private ImageView iv_more;
    private LinearLayout ll_information;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_page);
        findViews();
        welcomeUser();
        listenForIncommingMessages();
        initReferences();
        displaySchoolInformations();
        setUpBottomAppBarMenu();
        onInfromationLongClick();
        onStudentClick();
        onTeacherClick();
        onGroupClick();
        onCourseClick();
        onSessionClick();
        onMoreClick();

    }

    private void setUpBottomAppBarMenu()
    {
        //find id

        // bottomAppBar.getMenu().getItem(0).setIconTintList(getColorStateList(R.color.c2));

        //click event over Bottom bar menu item
        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.miDashboard:
                        db.collection("User").document(UserId).
                                get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Query query;
                                ArrayList<String> GroupIDs = (ArrayList<String>) documentSnapshot.get("studentIN");
                                startActivity(new Intent(getApplicationContext(), Dashboard.class)
                                        .putExtra("bar", true)
                                        .putStringArrayListExtra("GroupIDs", GroupIDs));
                                overridePendingTransition(0,0);

                            }});
                        return true;
                    case R.id.miProfile:
                        startActivity(new Intent(getApplicationContext(),Profile.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.miSettings:
                        startActivity(new Intent(getApplicationContext(),Settings.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.miHome:
                        startActivity(new Intent(getApplicationContext(),Home.class));
                        overridePendingTransition(0,0);
                        return true;

                }
                return true;
            }


        });
    }
    void welcomeUser()
    {
        Spanned welcome = Html.fromHtml("Welcome "+"<b>"+GV.currentUserName+"<b/>");
        tv_welcome.setText(welcome);
        /*getUser(UserId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                tv_welcome_user.setText("Welcome "+documentSnapshot.getString("displayName"));
            }
        });*/
    }

    void onStudentClick() {


            iv_student.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (priority > 0) {
                   db.collection("School")
                           .document(SchoolId)
                           .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                       @Override
                       public void onSuccess(DocumentSnapshot documentSnapshot) {
                           ArrayList<String> Students=(ArrayList<String>)documentSnapshot.get("Students");
                               Intent intent = new Intent(getApplicationContext(), MemberList.class)
                                       .putExtra("SchoolID", SchoolId)
                                       .putStringArrayListExtra("Students", Students)
                                       .putExtra("key", "studentIN")
                                       .putExtra("path", "School/" + SchoolId)
                                       .putExtra("TAG", TAG)
                                       .putExtra("Priority", priority);
                               startActivity(intent);
                               finish();


                       }
                   }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {

                       }
                   });
                    }
                    else Toast.makeText(SchoolPage.this, "Access denied!", Toast.LENGTH_SHORT).show();
                }
            });
        }

    void onTeacherClick() {

            iv_teacher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (priority > 0) {
                        db.collection("School")
                                .document(SchoolId)
                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                ArrayList<String> Teachers=(ArrayList<String>)documentSnapshot.get("Teachers");
                                Intent intent = new Intent(getApplicationContext(), MemberList.class)
                                        .putExtra("SchoolID", SchoolId)
                                        .putStringArrayListExtra("Teachers",Teachers)
                                        .putExtra("key", "teacherIN")
                                        .putExtra("path", "School/" + SchoolId)
                                        .putExtra("TAG", TAG)
                                        .putExtra("Priority", priority);
                                startActivity(intent);
                                finish();


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                    else  Toast.makeText(SchoolPage.this, "Access denied!", Toast.LENGTH_SHORT).show();
                }
            });
        }

    void onGroupClick() {

        iv_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (priority > 0) {
                    Intent intent = new Intent(getApplicationContext(), GroupList.class)
                            .putExtra("SchoolID", SchoolId)
                            .putExtra("path", "School/" + SchoolId + "Group")
                            .putExtra("Priority",priority);
                    startActivity(intent);
                    finish();
                } else Toast.makeText(SchoolPage.this, "Access denied!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    void onCourseClick() {

        iv_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CourseList.class)
                        .putExtra("SchoolID", SchoolId)
                        .putExtra("path", "School/" + SchoolId + "Course")
                        .putExtra("Priority",priority);
                startActivity(intent);
                finish();
            }
        });
    }

    private void onSessionClick()
    {
        iv_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(priority>0)
                {

                GroupRef.whereArrayContains("Students",UserId)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if(!queryDocumentSnapshots.getDocuments().isEmpty())
                                {
                                    ArrayList<String> GroupIds=new ArrayList<String>();
                                    for(DocumentSnapshot doc :queryDocumentSnapshots.getDocuments() )
                                    {
                                        GroupIds.add(doc.getId());
                                    }
                                    Intent intent=new Intent(getApplicationContext(),Dashboard.class)
                                            .putExtra("SchoolID",SchoolId)
                                            .putStringArrayListExtra("GroupIDs",GroupIds)
                                            .putExtra("Priority",priority);
                                    //
                                    // Toast.makeText(SchoolPage.this,GroupIds.toString(), Toast.LENGTH_SHORT).show();
                                    startActivity(intent);
                                    finish();
                                }else
                                {
                                    Intent intent=new Intent(getApplicationContext(),Dashboard.class)
                                            .putExtra("SchoolID",SchoolId)
                                            .putExtra("Priority",priority);

                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });


            }
                else Toast.makeText(SchoolPage.this, "Access denied!", Toast.LENGTH_SHORT).show();
            }
        });

    }
    void onMoreClick()
    {
        iv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SchoolPage.this, WebPage.class).putExtra("Qrurl",tv_description.getText().toString())
                        .putExtra("SchoolID",SchoolId)
                        .putExtra("Priority",priority)
                        .putExtra("more",true);

                startActivity(i);
                finish();
            }
        });

    }
    void onInfromationLongClick()
    {
        if(priority==3)
        {
        ll_information.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent=new Intent(SchoolPage.this,AddSchool.class)
                        .putExtra("NewSchool",false)
                        .putExtra("SchoolID",SchoolId);
                startActivity(intent);
                finish();
                return false;
            }
        });}
    }
    void listenForIncommingMessages()
    {
        //listen for incoming messages
        Bundle incommingMessages =getIntent().getExtras();
       SchoolId =incommingMessages.getString("SchoolID","0");
       priority=incommingMessages.getInt("Priority",0);
    }
    void displaySchoolInformations()
    {
        getSchool(SchoolId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                tv_description.setText(documentSnapshot.getString("Description"));
                //tv_display_name.setText(documentSnapshot.getString("DisplayName"));
                tv_full_name.setText(documentSnapshot.getString("FullName"));
                tv_location.setText(documentSnapshot.getString("Location"));
            }
        });
    }
    void findViews()
    {
        bottomAppBar=findViewById(R.id.bnb);
        tv_description=findViewById(R.id.tv_description);
       // tv_display_name=findViewById(R.id.tv_display_name);
        tv_full_name=findViewById(R.id.tv_full_name);
        tv_welcome=findViewById(R.id.tv_welcome_user);
        tv_location=findViewById(R.id.tv_location);
        iv_teacher=findViewById(R.id.iv_teacher);
        iv_student=findViewById(R.id.iv_student);
        iv_group=findViewById(R.id.iv_group);
        iv_session=findViewById(R.id.iv_session);
        ll_information=findViewById(R.id.ll_information);
        iv_course=findViewById(R.id.iv_course);
        iv_more=findViewById(R.id.iv_more);
    }
    void initReferences()
    {
        try{
        GroupRef=db.collection("School").document(SchoolId).collection("Group");}
        catch (Exception e){}
        //Toast.makeText(this, SchoolId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(SchoolPage.this,Home.class);
        startActivity(intent);
        finish();

    }
    public void backHome(View v)
    {
        onBackPressed();
    }
    

}
