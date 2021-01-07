package tn.dev.e_presence;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static tn.dev.e_presence.GV.getUser;

public class MemberList extends AppCompatActivity {
    private StorageReference mStorageRef;
    private BottomAppBar bottomAppBar;
    private BottomNavigationView bottomNavigationView;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference UserRef =db.collection("User");
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final String uid =user.getUid();
    private UserAdapter UserAdapter;
    private String path;
    private String key;
    private boolean all;
    private String SchoolId;
    private FloatingActionButton fab;
    private int priority;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_list);
        listenForIncommingMessages();
        setUpBottomAppBarMenu();


        setUpRecyclerView();
        fab =(FloatingActionButton) findViewById(R.id.fab);
        setFloatingAppButtonIcon();
        floatingActionButtonClick();
    }
    private void setUpBottomAppBarMenu( )
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
                        startActivity(new Intent(getApplicationContext(), Dashboard.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.miProfile:
                        startActivity(new Intent(getApplicationContext(), Profile.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.miSettings:
                        startActivity(new Intent(getApplicationContext(), Settings.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.miHome:
                        startActivity(new Intent(getApplicationContext(), Home.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return true;
            }


        });
    }
    private void setUpRecyclerView()
    {
        /*Query query = UserRef.orderBy("DisplayName");

         */
        Query query = UserRef.whereArrayContains(key,path);
        Toast.makeText(MemberList.this, key+"/"+path, Toast.LENGTH_SHORT).show();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query,User.class)
                .build();
        UserAdapter=new UserAdapter(options,storageReference);
        RecyclerView recyclerView = findViewById(R.id.rv_user);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(UserAdapter);
    }
    private void floatingActionButtonClick()
    {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext(),SchoolPage.class).putExtra("ID",SchoolId);
                startActivity(intent);
                finish();

                /*getUser(uid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                                                  {
                                                      @Override
                                                      public void onSuccess(DocumentSnapshot documentSnapshot)
                                                      {
                                                          User modelCurrentUser = documentSnapshot.toObject(User.class);
                                                          boolean isAdmin=modelCurrentUser.getAdminIN().contains(path);
                                                          boolean isTeacher=modelCurrentUser.getTeacherIN().contains(path);
                                                          boolean isStudent=modelCurrentUser.getStudentIN().contains(path);
                                                          if(isAdmin)
                                                          {
                                                              //Admin

                                                          }
                                                          else if(isTeacher)
                                                          {
                                                              //Teacher
                                                              Intent intent =new Intent(getApplicationContext(),SchoolPage.class);
                                                              startActivity(intent);
                                                              finish();
                                                          }
                                                          else if(isStudent)
                                                          {
                                                              //Student
                                                              Intent intent =new Intent(getApplicationContext(),SchoolPage.class);
                                                              startActivity(intent);
                                                              finish();
                                                          }
                                                          else
                                                          {
                                                              //Public
                                                              Intent intent =new Intent(getApplicationContext(),SchoolPage.class);
                                                              startActivity(intent);
                                                              finish();
                                                          }

                                                      }
                                                  }
                );*/
            }
        });




        /*Intent intent =new Intent(MemberList.this, AddMember.class);
        intent.putExtra("first",true);
        startActivity(intent);
        finish();*/
    }

    void AddNewPerson() {

    }
    void listenForIncommingMessages()
    {
        //listen for incoming messages
        Bundle incommingMessages =getIntent().getExtras();
        path=incommingMessages.getString("path","0");
        key =incommingMessages.getString("key","0");
        all=incommingMessages.getBoolean("all",false);
        SchoolId =incommingMessages.getString("ID","0");
        priority=incommingMessages.getInt("Priority",0);
    }
    void setFloatingAppButtonIcon()
    {
        if (priority==3) fab.setImageResource(R.drawable.ic_add_person);
    }

    @Override
   protected void onStart() {
        super.onStart();
        UserAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        UserAdapter.stopListening();
    }

}