package tn.dev.e_presence;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.StorageReference;

public class CourseList extends AppCompatActivity {
    private StorageReference mStorageRef;
    private BottomAppBar bottomAppBar;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private String SchoolId;
    private String CourseId;
    private CollectionReference CoursesRef;
    private CourseAdapter courseAdapter;
    private int priority;
    String path;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);
        findViews();
        listenForIncommingMessages();
        setFloatingActionButtonIcon();
        setUpBottomAppBarMenu();
        initCourseref();
        setUpRecyclerView();
        onFloatingActionButtonClick();


    }
    void findViews()
    {
        fab =(FloatingActionButton) findViewById(R.id.fab);
    }
    void  onFloatingActionButtonClick()
    {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (priority)
                {
                    case 3:
                    {
                        Intent intent=new Intent(CourseList.this,AddCourse.class)
                                .putExtra("SchoolID",SchoolId)
                                .putExtra("Priority",priority);
                        startActivity(intent);
                        finish();
                    }break;
                    case 2:
                    case 1:
                    case 0:
                    {
                        //back
                    }
                }
            }
        });
    }
    void setFloatingActionButtonIcon()
    {
        if (priority==3)
        {
            fab.setImageResource(R.drawable.ic_add_course);
        }
    }

    void listenForIncommingMessages()
    {
        //listen for incoming messages
        Bundle incommingMessages =getIntent().getExtras();
        SchoolId =incommingMessages.getString("SchoolID","0");
        priority=incommingMessages.getInt("Priority",0);

    }
    void initCourseref()
    {
        CoursesRef=db.collection("School").document(SchoolId).collection("Course");
    }
    private void setUpBottomAppBarMenu()
    {
        //find id
        bottomAppBar=findViewById(R.id.bnb);
        // bottomAppBar.getMenu().getItem(0).setIconTintList(getColorStateList(R.color.c2));

        //click event over Bottom bar menu item
        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.miDashboard:
                        startActivity(new Intent(getApplicationContext(),Dashboard.class));
                        overridePendingTransition(0,0);
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
    private void setUpRecyclerView()
    {

        Query query = CoursesRef.orderBy("displayName");
        Toast.makeText(CourseList.this, "School/"+SchoolId+"/Course", Toast.LENGTH_SHORT).show();
        FirestoreRecyclerOptions<Course> options = new FirestoreRecyclerOptions.Builder<Course>()
                .setQuery(query,Course.class)
                .build();
        courseAdapter=new CourseAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.rv_course);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(courseAdapter);
        /*courseAdapter.setOnItemClickListener(new CourseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String clickedCourseId =documentSnapshot.getId();
                Intent intent =new Intent(CourseList.this,MemberList.class)
                        .putExtra("CourseID",clickedCourseId)
                        .putExtra("key","studentIN")
                        .putExtra("Priority",priority)
                        .putExtra("SchoolID",SchoolId)
                        .putExtra("path","School/"+SchoolId+"/Course/"+clickedCourseId);

                Toast.makeText(CourseList.this, "Course Memeber List" , Toast.LENGTH_SHORT).show();
                startActivity(intent);
                finish();



            }
        });*/

    }

    @Override
    protected void onStart() {
        super.onStart();
        courseAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        courseAdapter.stopListening();
    }
}