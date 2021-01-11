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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static tn.dev.e_presence.GV.getUser;

public class GroupList extends AppCompatActivity {
    private StorageReference mStorageRef;
    private BottomAppBar bottomAppBar;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private String SchoolId;
    private String GroupId;
    private CollectionReference GroupsRef;
    private GroupAdapter groupAdapter;
    private int priority;
    String path;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        finfViews();
        listenForIncommingMessages();
        setFloatingActionButtonIcon();
        setUpBottomAppBarMenu();
        initGroupref();
        setUpRecyclerView();
        onFloatingActionButtonClick();


    }
    void finfViews()
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
                        Intent intent=new Intent(GroupList.this,AddGroup.class)
                                .putExtra("SchoolID",SchoolId)
                                .putExtra("Priority",priority);
                        startActivity(intent);
                        finish();
                    }break;
                    case 2:
                    case 1:
                    case 0:
                    {
                        Intent intent =new Intent(GroupList.this,SchoolPage.class).
                                putExtra("SchoolID",SchoolId);
                        intent.putExtra("Priority",priority);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
    }
    void setFloatingActionButtonIcon()
    {
        if (priority==3)
        {
            fab.setImageResource(R.drawable.ic_add_group);
        }
    }

    void listenForIncommingMessages()
    {
        //listen for incoming messages
        Bundle incommingMessages =getIntent().getExtras();
        SchoolId =incommingMessages.getString("SchoolID","0");
        priority=incommingMessages.getInt("Priority",0);

    }
    void initGroupref()
    {
        GroupsRef=db.collection("School").document(SchoolId).collection("Group");
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

        Query query = GroupsRef.orderBy("displayName");
        Toast.makeText(GroupList.this, "School/"+SchoolId+"/Group", Toast.LENGTH_SHORT).show();
        FirestoreRecyclerOptions<Group> options = new FirestoreRecyclerOptions.Builder<Group>()
                .setQuery(query,Group.class)
                .build();
        groupAdapter=new GroupAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.rv_group);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(groupAdapter);
        groupAdapter.setOnItemClickListener(new GroupAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
            String clickedGroupId =documentSnapshot.getId();
            Intent intent =new Intent(GroupList.this,MemberList.class)
                    .putExtra("GroupID",clickedGroupId)
                    .putExtra("key","studentIN")
                    .putExtra("Priority",priority)
                    .putExtra("SchoolID",SchoolId)
                    .putExtra("path","School/"+SchoolId+"/Group/"+clickedGroupId);

            Toast.makeText(GroupList.this, "Group Memeber List" , Toast.LENGTH_SHORT).show();
            startActivity(intent);
            finish();
        }
    });

    }

    @Override
    protected void onStart() {
        super.onStart();
        groupAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        groupAdapter.stopListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent =new Intent(GroupList.this,SchoolPage.class).
                putExtra("SchoolID",SchoolId);
        intent.putExtra("Priority",priority);
        startActivity(intent);
        finish();
    }
}