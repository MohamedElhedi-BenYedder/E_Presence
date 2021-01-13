package tn.dev.e_presence;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.protobuf.Empty;

import java.util.ArrayList;
import java.util.List;

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
    private ArrayList<String> groupIdList,groupNameList;
    private String key;
    private static String TAG;
    private boolean all;
    private static String SchoolId;
    private FloatingActionButton fab;
    private static int priority;
    private ArrayList<String> listOfPresence;
    private ArrayList<String> teacherIdList,teacherNameList;
    private static String GroupId;
    private boolean Pres;
    private String NewSessionID;
    private boolean listenAdapter=true;
    private EditText searchBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_list);
        listenForIncommingMessages();
        setUpBottomAppBarMenu();
        if (Pres) setUpRecyclerViewPres();
        else  setUpRecyclerView();
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
        String p=path;
        if(path.split("/").length>3) p=path.split("/")[3];
        Query query = UserRef.whereArrayContains(key,p);
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
        searchBar(query);
    }
    private void setUpRecyclerViewPres()
    {
        try
        {
        Query query = UserRef.whereIn("userID", listOfPresence);
            Toast.makeText(MemberList.this, SchoolId, Toast.LENGTH_SHORT).show();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                    .setQuery(query, User.class)
                    .build();
            UserAdapter = new UserAdapter(options, storageReference);
            RecyclerView recyclerView = findViewById(R.id.rv_user);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(UserAdapter);
            searchBar(query);
        }
        catch(Exception e)
        {
            listenAdapter=false;
            Toast.makeText(MemberList.this, "Empty List Of Presence", Toast.LENGTH_SHORT).show();
        }

    }
    void setUpRecyclerViewAdmin()
    {
        Query query;
        if((path.split("/").length<3))
        {
            // Add Student or teacher to school
             query = UserRef.orderBy("displayName");
        }
        else
        {
            //Add Student To group
             query = UserRef.whereArrayContains("studentIN","School/"+SchoolId);
            Toast.makeText(MemberList.this, "School/"+SchoolId , Toast.LENGTH_SHORT).show();

        }

        Toast.makeText(MemberList.this, "Adding New Members" , Toast.LENGTH_SHORT).show();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query,User.class)
                .build();
        UserAdapter=new UserAdapter(options,storageReference);
        RecyclerView recyclerView = findViewById(R.id.rv_user);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(UserAdapter);
        searchBar(query);
        UserAdapter.setOnItemClickListener(new UserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String clickedUserId =documentSnapshot.getId();
                String ClickedUserName=documentSnapshot.getString("displayName");
                DocumentReference clickedUserDocRef=db.collection("User").document(clickedUserId);
                DocumentReference SchoolDocRef=db.document(path);
                if(key.equals("studentIN"))
                {
                    String p=path;
                    if((path.split("/").length>3)) p=path.split("/")[3];
                    clickedUserDocRef.update("studentIN", FieldValue.arrayUnion(p));

                    SchoolDocRef.update("Students",FieldValue.arrayUnion(clickedUserId));
                }
                else if(key.equals("teacherIN"))
                {
                    clickedUserDocRef.update("teacherIN", FieldValue.arrayUnion("School/"+SchoolId));
                    SchoolDocRef.update("Teachers",FieldValue.arrayUnion(clickedUserId));
                }
                Toast.makeText(MemberList.this, ClickedUserName+" is added" , Toast.LENGTH_SHORT).show();



            }
        });



    }
    private void floatingActionButtonClick()
    {

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Pres) {
                    //admin
                    if (priority == 3) {
                        // add User;
                        setUpRecyclerViewAdmin();
                        UserAdapter.startListening();
                        Intent intent = new Intent(getApplicationContext(), MemberList.class)
                                .putExtra("path", path)
                                .putExtra("key", key)
                                .putExtra("all", all)
                                .putExtra("SchoolID", SchoolId)
                                .putExtra("Priority", priority)
                                .putExtra("TAG", "MemberList");
                    }
                    //Student or Teacher
                    else {
                        Intent intent = new Intent(getApplicationContext(), SchoolPage.class)
                                .putExtra("path", path)
                                .putExtra("key", key)
                                .putExtra("all", all)
                                .putExtra("SchoolID", SchoolId)
                                .putExtra("Priority", priority);
                        startActivity(intent);
                        finish();
                    }

                }else{
                    Intent intent=new Intent(MemberList.this,Dashboard.class)
                            .putExtra("SchoolID",SchoolId)
                            .putExtra("Priority",priority)
                            .putExtra("GroupID",GroupId)
                            .putExtra("NewSessionID","Session"+System.currentTimeMillis())
                            .putStringArrayListExtra("groupIdList",new ArrayList<String>())
                            .putStringArrayListExtra("groupNameList",new ArrayList<String>())
                            .putStringArrayListExtra("teacherIdList", teacherIdList)
                            .putStringArrayListExtra("teacherNameList",  teacherNameList);

                    startActivity(intent);
                    finish();

                }
            }
        });}




        /*Intent intent =new Intent(MemberList.this, AddMember.class);
        intent.putExtra("first",true);
        startActivity(intent);
        finish();*/


    void AddNewPerson() {

    }

    void listenForIncommingMessages()
    {
        //listen for incoming messages
        Bundle incommingMessages =getIntent().getExtras();
        path=incommingMessages.getString("path","0");
        key =incommingMessages.getString("key","0");
        all=incommingMessages.getBoolean("all",false);
        SchoolId =incommingMessages.getString("SchoolID","0");
        priority=incommingMessages.getInt("Priority",0);
        Pres=incommingMessages.getBoolean("Pres",false);
        listOfPresence=incommingMessages.getStringArrayList("listOfPresence");
        TAG=incommingMessages.getString("TAG","0");
        GroupId=incommingMessages.getString("GroupID","0");
        teacherIdList=incommingMessages.getStringArrayList("teacherIdList");
        teacherNameList=incommingMessages.getStringArrayList("teacherNameList");
        groupIdList=incommingMessages.getStringArrayList("groupIdList");
        groupNameList=incommingMessages.getStringArrayList("groupNameList");
        NewSessionID=incommingMessages.getString("NewSessionID","0");

    }
    void setFloatingAppButtonIcon()
    {
        if ((priority==3)&&!Pres) fab.setImageResource(R.drawable.ic_add_person);
    }

    @Override
   protected void onStart() {
        super.onStart();
        if(listenAdapter) UserAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
       try{ UserAdapter.stopListening();}catch (Exception e){}
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(MemberList.this, TAG+priority, Toast.LENGTH_SHORT).show();
        if(TAG.equals("Dashbord")){
            Intent intent=new Intent(MemberList.this,Dashboard.class)
                    .putExtra("SchoolID",SchoolId)
                    .putExtra("Priority",priority)
                    .putExtra("GroupID",GroupId)
                    .putExtra("NewSessionID","Session"+System.currentTimeMillis())
                    .putStringArrayListExtra("groupIdList",new ArrayList<String>())
                    .putStringArrayListExtra("groupNameList",new ArrayList<String>())
                    .putStringArrayListExtra("teacherIdList", teacherIdList)
                    .putStringArrayListExtra("teacherNameList",  teacherNameList);

            startActivity(intent);
            finish();//
        }
        if(TAG.equals("SchoolPage")){
            Intent intent=new Intent(MemberList.this,SchoolPage.class)
                    .putExtra("path",path)
                    .putExtra("key",key)
                    .putExtra("all",all)
                    .putExtra("SchoolID",SchoolId)
                    .putExtra("Priority",priority);
            startActivity(intent);
            finish();
        }
        if(TAG.equals("MemberList")){
            Intent intent=new Intent(MemberList.this,MemberList.class)
                    .putExtra("path",path)
                    .putExtra("key",key)
                    .putExtra("all",all)
                    .putExtra("SchoolID",SchoolId)
                    .putExtra("Priority",priority);
            startActivity(intent);
            finish();
        }
       /* Intent intent=new Intent(MemberList.this,)
                .putExtra("path",path)
                .putExtra("key",key)
                .putExtra("all",all)
                .putExtra("SchoolID",SchoolId)
                .putExtra("Priority",priority);
        startActivity(intent);
        finish();*/


    }
    private void searchBar(Query Q)

    {
        searchBox=findViewById(R.id.searchBox);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().isEmpty())
                {
                    StorageReference path = FirebaseStorage.getInstance().getReference();
                    FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                            .setQuery(Q,User.class)
                            .build();
                    UserAdapter.updateOptions(options);
                }
                else {
                    Query query;
                    query = Q.orderBy("displayName").startAt(s.toString()).endAt(s.toString()+'\uf8ff');
                    StorageReference path = FirebaseStorage.getInstance().getReference();
                    FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                            .setQuery(query,User.class)
                            .build();
                    UserAdapter.updateOptions(options);}
            }
        });
    }
}