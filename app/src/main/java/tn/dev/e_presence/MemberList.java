package tn.dev.e_presence;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
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

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static java.lang.Thread.sleep;
import static tn.dev.e_presence.GV.getUser;

public class MemberList extends AppCompatActivity {
    private final int wait=500;
    private BottomAppBar bottomAppBar;
    private BottomNavigationView bottomNavigationView;
    private static final StorageReference mStorageRef= FirebaseStorage.getInstance().getReference();
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference UserRef =db.collection("User");
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final String UserId =user.getUid();
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
    private ArrayList<String> Students;
    private ArrayList<String> Teachers;
    private RecyclerView recyclerView;
    private TextView tv_page;
    private boolean add;
    private ItemTouchHelper itemTouchHelper1,itemTouchHelper2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_list);
        listenForIncommingMessages();
        tv_page=findViewById(R.id.tv_page);
        if(Pres)
            tv_page.setText("List Of Presence");
        else if(key.equals("studentIN"))
            tv_page.setText("Students");
        else if(key.equals("teacherIN"))
            tv_page.setText("Teachers");
        setUpBottomAppBarMenu();
        add=false;
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
        recyclerView = findViewById(R.id.rv_user);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(UserAdapter);
        searchBar(query);
        OnSwipedUserRemove();

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
            recyclerView = findViewById(R.id.rv_user);
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
            query = UserRef;
            // Add Student or teacher to school



        }
        else
        {
            //Add Student To group
             query = UserRef.whereArrayContains("studentIN","School/"+SchoolId);
            Toast.makeText(MemberList.this, "School/"+SchoolId , Toast.LENGTH_SHORT).show();

        }
        Toast.makeText(MemberList.this,Html.fromHtml("<b>Adding New Members </b>"), Toast.LENGTH_SHORT).show();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query,User.class)
                .build();
        UserAdapter=new UserAdapter(options,storageReference,true,key,path);
        recyclerView = findViewById(R.id.rv_user);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(UserAdapter);
        searchBar(query);
        onSwipedAddUser();


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
                        if(!add) {
                            add=!add;
                            setUpRecyclerViewAdmin();
                            fab.setImageResource(R.drawable.ic8_back_arrow);
                            if(key.equals("studentIN"))
                                tv_page.setText("Add Students");
                            else if(key.equals("teacherIN"))
                                tv_page.setText("Add Teachers");
                            UserAdapter.startListening();

                        }
                        else
                        {
                            add=!add;
                            fab.setImageResource(R.drawable.ic8_add_user_male);
                            if(key.equals("studentIN"))
                                tv_page.setText("Students");
                            else if(key.equals("teacherIN"))
                                tv_page.setText("Teachers");
                            setUpRecyclerView();
                            UserAdapter.startListening();


                        }
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
        Students=incommingMessages.getStringArrayList("Students");
        Teachers=incommingMessages.getStringArrayList("Teachers");

    }
    void setFloatingAppButtonIcon()
    {
        if ((priority==3)&&!Pres&&!add) fab.setImageResource(R.drawable.ic8_add_user_male);
    }

    @Override
   protected void onStart()
    {
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
    private void OnSwipedUserRemove()
    {
        //---------------------------Swipe Item -------------------------//
       try{ itemTouchHelper2.attachToRecyclerView(null);}catch (Exception e){}
        if(priority==3)
        {
        itemTouchHelper1= new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    int position = viewHolder.getAdapterPosition();
                    switch (direction) {
                        case ItemTouchHelper.LEFT:
                            String User = "";
                            String SchoolField = "";
                            if (key.equals("studentIN")) {
                                SchoolField = "Students";
                                User = "Student";
                            } else if (key.equals("teacherIN")) {
                                SchoolField = "Teachers";
                                User = "Teacher";
                            }
                            String UserName = UserAdapter.getItem(position).getDisplayName();
                            String Uid = UserAdapter.getItem(position).getUserID();
                            Toast.makeText(MemberList.this, key, Toast.LENGTH_SHORT).show();
                            final AlertDialog.Builder deleteUserDialog = new AlertDialog.Builder(MemberList.this);
                            deleteUserDialog.setTitle("Remove " + User + " ?");
                            Spanned spannedMessage = Html.fromHtml("Do you want to remove " + "<b>" + UserName + "</b> from <b>" + SchoolId + "</b> ?");
                            deleteUserDialog.setMessage(spannedMessage);
                            deleteUserDialog.setIcon(getDrawable(R.drawable.ic8_denied));
                            String finalSchoolField = SchoolField;
                            deleteUserDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String p=path;
                                    if((path.split("/").length>3)) p=path.split("/")[3];

                                    db.collection("User").document(Uid).update(key, FieldValue.arrayRemove(p));
                                    db.collection("School").document(SchoolId).update(finalSchoolField, FieldValue.arrayRemove(Uid));
                                    Toast.makeText(MemberList.this, Html.fromHtml("<b>" + UserName + "</b> is removed "), Toast.LENGTH_SHORT).show();
                                }
                            });

                            deleteUserDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // close the dialog
                                    recyclerView.setAdapter(UserAdapter);
                                }
                            });

                            deleteUserDialog.create().show();

                            break;
                        case ItemTouchHelper.RIGHT:
                            GV.visitedUserPhotoPath=UserAdapter.getItem(position).getPhoto();
                            GV.visitedUserName=UserAdapter.getItem(position).getDisplayName();
                            GV.visitedUserMail=UserAdapter.getItem(position).getEmail();
                            GV.visitedUserPhoneNumber=UserAdapter.getItem(position).getPhoneNumber();
                            startActivity(new Intent(MemberList.this,Profile.class)
                                    .putExtra("out",true));
                            break;
                    }


                }

                @Override
                public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                    String User = "";
                    if (key.equals("studentIN")) User = "Student";
                    else if (key.equals("teacherIN")) User = "Teacher";

                    new RecyclerViewSwipeDecorator.Builder(MemberList.this, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                            .addSwipeLeftActionIcon(R.drawable.ic8_denied_resized)
                            .addSwipeLeftBackgroundColor(getResources().getColor(R.color.color_delete))
                            .addSwipeLeftLabel("Remove " + User)
                            .setSwipeLeftLabelTextSize(1, 20)
                            .addSwipeRightActionIcon(R.drawable.ic8_find_user_male_resized)
                            .addSwipeRightBackgroundColor(R.color.c2)
                            .addSwipeRightLabel("Find " + User)
                            .setSwipeRightLabelTextSize(1, 20)
                            .create()
                            .decorate();

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }

            });
       try{ itemTouchHelper1.attachToRecyclerView(recyclerView);}catch (Exception e){}
        }
        else
        {
            itemTouchHelper1=new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,  ItemTouchHelper.RIGHT) {

                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    int position = viewHolder.getAdapterPosition();
                    GV.visitedUserPhotoPath=UserAdapter.getItem(position).getPhoto();
                    GV.visitedUserName=UserAdapter.getItem(position).getDisplayName();
                    GV.visitedUserMail=UserAdapter.getItem(position).getEmail();
                    GV.visitedUserPhoneNumber=UserAdapter.getItem(position).getPhoneNumber();
                    startActivity(new Intent(MemberList.this,Profile.class)
                            .putExtra("out",true));

                }


                @Override
                public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                    String User = "";
                    if (key.equals("studentIN")) User = "Student";
                    else if (key.equals("teacherIN")) User = "Teacher";

                    new RecyclerViewSwipeDecorator.Builder(MemberList.this, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                            .addSwipeRightActionIcon(R.drawable.ic8_find_user_male_resized)
                            .addSwipeRightBackgroundColor(R.color.c2)
                            .addSwipeRightLabel("Find " + User)
                            .setSwipeRightLabelTextSize(1, 20)
                            .create()
                            .decorate();

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }

            });
            itemTouchHelper1.attachToRecyclerView(recyclerView);
        }
    }
    void onSwipedAddUser()
    {
        if(priority==3)
        {
            itemTouchHelper1.attachToRecyclerView(null);
            itemTouchHelper2=new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    int position = viewHolder.getAdapterPosition();
                    switch (direction) {
                        case ItemTouchHelper.LEFT:

                                String User = "";
                                String SchoolField = "";
                                if (key.equals("studentIN")) {
                                    SchoolField = "Students";
                                    User = "Student";
                                } else if (key.equals("teacherIN")) {
                                    SchoolField = "Teachers";
                                    User = "Teacher";
                                }
                                String UserName = UserAdapter.getItem(position).getDisplayName();
                                String Uid = UserAdapter.getItem(position).getUserID();
                            if (!UserAdapter.exist(position))
                            {
                                final AlertDialog.Builder deleteUserDialog = new AlertDialog.Builder(MemberList.this);
                                deleteUserDialog.setTitle("Add " + User + " ?");
                                Spanned spannedMessage = Html.fromHtml("Do you want to add " + "<b>" + UserName + "</b> to <b>" + SchoolId + "</b> ?");
                                deleteUserDialog.setMessage(spannedMessage);
                                deleteUserDialog.setIcon(getDrawable(R.drawable.ic8_add_user_male));
                                String finalSchoolField = SchoolField;
                                deleteUserDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String p = path;
                                        if ((path.split("/").length > 3)) p = path.split("/")[3];

                                        db.collection("User").document(Uid).update(key, FieldValue.arrayUnion(p));
                                        if ((path.split("/").length > 3))
                                            db.collection("School").document(SchoolId).collection("Group").document(p).update(finalSchoolField, FieldValue.arrayUnion(Uid));
                                        else
                                            db.collection("School").document(SchoolId).update(finalSchoolField, FieldValue.arrayUnion(Uid));
                                        Toast.makeText(MemberList.this, Html.fromHtml("<b>" + UserName + "</b> is added "), Toast.LENGTH_SHORT).show();
                                        if (key.equals("studentIN"))
                                            Students.add(Uid);
                                        else if (key.equals("teacherIN"))
                                            Teachers.add(Uid);


                                    }
                                });

                                deleteUserDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // close the dialog
                                        try {
                                            sleep(wait);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        UserAdapter.notifyItemChanged(position);
                                    }
                                });

                                deleteUserDialog.create().show();
                            }
                            else
                            {
                                Toast.makeText(MemberList.this, Html.fromHtml("<b>" + UserName + "</b> already exists !!!"), Toast.LENGTH_SHORT).show();
                                Wait(position);
                            }

                            break;
                        case ItemTouchHelper.RIGHT:
                             GV.visitedUserPhotoPath=UserAdapter.getItem(position).getPhoto();
                             GV.visitedUserName=UserAdapter.getItem(position).getDisplayName();
                             GV.visitedUserMail=UserAdapter.getItem(position).getEmail();
                             GV.visitedUserPhoneNumber=UserAdapter.getItem(position).getPhoneNumber();
                            startActivity(new Intent(MemberList.this,Profile.class)
                            .putExtra("out",true));
                            break;
                    }


                }

                @Override
                public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                    String User = "";
                    if (key.equals("studentIN")) User = "Student";
                    else if (key.equals("teacherIN")) User = "Teacher";

                    new RecyclerViewSwipeDecorator.Builder(MemberList.this, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                            .addSwipeLeftActionIcon(R.drawable.ic8_add_user_male_resied)
                            .addSwipeLeftBackgroundColor(getResources().getColor(R.color.color_add))
                            .addSwipeLeftLabel("Add " + User)
                            .setSwipeLeftLabelTextSize(1, 20)
                            .addSwipeRightActionIcon(R.drawable.ic8_find_user_male_resized)
                            .addSwipeRightBackgroundColor(R.color.c2)
                            .addSwipeRightLabel("Find " + User)
                            .setSwipeRightLabelTextSize(1, 20)
                            .create()
                            .decorate();

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }

            });
            itemTouchHelper2.attachToRecyclerView(recyclerView);
        }




    }
    void Wait(int position)
    {
        try {
            sleep(wait);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        UserAdapter.notifyItemChanged(position);
    }
}