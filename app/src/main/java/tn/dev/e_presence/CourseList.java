package tn.dev.e_presence;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class CourseList extends AppCompatActivity {
    private StorageReference mStorageRef;
    private BottomAppBar bottomAppBar;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private String UserId= FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String SchoolId;
    private String CourseId;
    private CollectionReference CoursesRef;
    private CourseAdapter courseAdapter;
    private int priority;
    private RecyclerView recyclerView;
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
    private void OnSwipedCourse()
    {
        if (priority==3){
            //---------------------------Swipe Item -------------------------//

            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT |ItemTouchHelper.RIGHT) {

                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    int position=viewHolder.getAdapterPosition();
                    switch (direction)
                    {
                        case ItemTouchHelper.LEFT:
                            String Identifier=courseAdapter.getItem(position).getDisplayName();

                            final AlertDialog.Builder deleateCourseDialog = new AlertDialog.Builder(CourseList.this);
                            deleateCourseDialog.setTitle("Delete Course?");
                            String Identifier_bold = "<b>" + Identifier+ "</b>";
                            String message = "Do you want to remove " + Identifier_bold +" from "+"<b>"+SchoolId+"</b>" +" course list?";
                            Spanned spannedMessage = Html.fromHtml(message);
                            deleateCourseDialog.setMessage(spannedMessage);
                            deleateCourseDialog.setIcon(R.drawable.ic8_delete_file);
                            deleateCourseDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // verify the identifier
                                    courseAdapter.delete(position,SchoolId);
                                }
                            });

                            deleateCourseDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // close the dialog
                                    courseAdapter.notifyItemChanged(position);
                                }
                            });

                            deleateCourseDialog.create().show();

                            break;
                        case ItemTouchHelper.RIGHT:
                            Intent intent=courseAdapter.edit(position)
                                    .putExtra("Priority",priority)
                                    .putExtra("NewCourse",false)
                                    .putExtra("SchoolID",SchoolId);
                            startActivity(intent);
                            finish();
                            break;

                    }


                }
                @Override
                public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                    new RecyclerViewSwipeDecorator.Builder(CourseList.this, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                            .addSwipeLeftActionIcon(R.drawable.ic8_delete_file_resized)
                            .addSwipeLeftLabel("Delete Course")
                            .setSwipeLeftLabelTextSize(1,20)
                            .addSwipeLeftBackgroundColor(getResources().getColor(R.color.color_delete))
                            .addSwipeRightActionIcon(R.drawable.ic8_edit_file_resized)
                            .addSwipeRightLabel("Edit Course")
                            .setSwipeRightLabelTextSize(1,20)
                            .addSwipeRightBackgroundColor(R.color.c2)
                            .create()
                            .decorate();

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }

            }).attachToRecyclerView(recyclerView);
            {

            }
        }

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
                                .putExtra("Priority",priority)
                                .putExtra("NewCourse",true);
                        startActivity(intent);
                        finish();
                    }break;
                    case 2:
                    case 1:
                    case 0:
                    {
                        Intent intent =new Intent(CourseList.this,SchoolPage.class).
                                putExtra("SchoolID",SchoolId);
                        intent.putExtra("Priority",priority);
                        startActivity(intent);
                        finish();
                    }break;
                }
            }
        });
    }
    void setFloatingActionButtonIcon()
    {
        if (priority==3)
        {
            fab.setImageResource(R.drawable.ic8_add_course);
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
    private void setUpRecyclerView()
    {

        Query query = CoursesRef.orderBy("displayName");
        //Toast.makeText(CourseList.this, "School/"+SchoolId+"/Course", Toast.LENGTH_SHORT).show();
        FirestoreRecyclerOptions<Course> options = new FirestoreRecyclerOptions.Builder<Course>()
                .setQuery(query,Course.class)
                .build();
        courseAdapter=new CourseAdapter(options);
        recyclerView = findViewById(R.id.rv_course);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(courseAdapter);
        OnSwipedCourse();

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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent =new Intent(CourseList.this,SchoolPage.class).
                putExtra("SchoolID",SchoolId);
        intent.putExtra("Priority",priority);
        startActivity(intent);
        finish();
    }
}