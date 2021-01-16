package tn.dev.e_presence;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener;
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.vision.barcode.Barcode;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Dashboard extends AppCompatActivity implements DatePickerListener {
    private ListView lv_Session;
    private  ArrayList<String> GroupIDs=new ArrayList<>();
    private SessionAdapter Adapter;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final String UserId = user.getUid();
    private CollectionReference SessionRef ;
    private CollectionReference SchoolRef;
    private CollectionReference UserRef =db.collection("User");
    private CollectionReference GroupRef;
    private CollectionReference CourseRef;
    private static SessionAdapter sessionAdapter;
    private HorizontalPicker picker;
    private BottomAppBar bottomAppBar;
    private BottomNavigationView bottomNavigationView;
    private int REQUEST_CAMERA = 1;
    private String TAG="Dashbord";
    private String TAG_rec;
    private static int Cur_pos;
    private String Scane_res = "chaine";
    private static String SchoolId;
    private static String GroupId,SessionId;
    private static String day;
    private static String Qrdb;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private int priority;
    private static Boolean Student_Teacher=true;
    private static boolean Scan_verdict;
    private boolean bar ;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        GroupIDs.add("Group1610569650940");
        listenForIncommingMessages();
        initSessionAndGroupRef();
        setFloatingAppButtonIcon();
        SetUpBottomAppBarMenu();
        SetDatePicker();
        day=getTodayDate();
        setUpRecyclerView(day);
        OnSwipedItem();
        fab.setOnClickListener(this::AddSession);



    }
    void listenForIncommingMessages()
    {
        //listen for incoming messages
        Bundle incommingMessages =getIntent().getExtras();
       SchoolId =incommingMessages.getString("SchoolID","0");
       GroupId=incommingMessages.getString("GroupID","0");
        priority=incommingMessages.getInt("Priority",0);
        TAG_rec=incommingMessages.getString("TAG","0");
        bar=incommingMessages.getBoolean("bar",false);
        GroupIDs=incommingMessages.getStringArrayList("GroupIDs");
        //Qrdb =incommingMessages.getString("Qrdb","0");

    }
    private void SetDatePicker() {
        picker = (HorizontalPicker) findViewById(R.id.datePicker);
        picker.setListener(this)
                .setDays(120)
                .setOffset(7)
                .setDateSelectedColor(Color.BLACK)
                .setDateSelectedTextColor(Color.WHITE)
                .setMonthAndYearTextColor(Color.BLACK)
                .setTodayButtonTextColor(Color.WHITE)
                .setTodayDateTextColor(Color.WHITE)
                .setTodayDateBackgroundColor(Color.GRAY)
                .setUnselectedDayTextColor(Color.BLACK)
                .setDayOfWeekTextColor(Color.BLACK)
                .setUnselectedDayTextColor(getResources().getColor(R.color.primaryTextColor))
                .showTodayButton(false)
                .init();
        picker.setBackgroundColor(getResources().getColor(R.color.contentBodyColor));
        picker.setDate(new DateTime());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void SetUpBottomAppBarMenu() {
        //find id
        bottomAppBar = findViewById(R.id.bnb);
        //bottomAppBar.getMenu().getItem(1).setIconTintList(getColorStateList(R.color.c2));
        //set bottom bar to Action bar as it is similar like Toolbar
        //bottomAppBar.replaceMenu(R.menu.bottom_app_bar_secondary_menu);
        //click event over Bottom bar menu item
        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.miHome:
                        startActivity(new Intent(getApplicationContext(), Home.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.miProfile:
                        startActivity(new Intent(getApplicationContext(), Profile.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.miSettings:
                        startActivity(new Intent(getApplicationContext(), Settings.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return true;

            }


        });
    }


    //Camera Permission Function
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent intent = new Intent(this, ScanActivity.class);

                startActivityForResult(intent, 0);
            } else {
                Toast.makeText(this, "Permission was not granted", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
    }

    @Override
    public void onDateSelected(DateTime dateSelected) {
        day=dateSelected.toString("dd/MM/yyyy");

        setUpRecyclerView(dateSelected.toString("dd/MM/yyyy"));
      try {
          sessionAdapter.startListening();
      }catch (Exception e){}
      }


    private  void AddSession(View v)
    {
        if( bar)
        {
           Student_Teacher=!Student_Teacher;
           setUpRecyclerViewMenuBottomBar(day);
           sessionAdapter.startListening();

        }
        else
        switch (priority) {
            case 3:
            case 2: {
                String Schoolpath="School/"+SchoolId;

                getListOfTeachers_Groups_Courses(Schoolpath);
                }
            break;
            case 1:
            case 0:
            {

            }
                break;
        }
    }


    public void setUpRecyclerViewMenuBottomBar(String day)
    {
                Query query;
                Query firstQuery = db
                        .collectionGroup("Session")
                        .whereEqualTo("date",day)
                        .whereEqualTo("teacherId",user.getUid())
                        .orderBy("start");


                Query secondQuery = db
                        .collectionGroup("Session")
                        .whereEqualTo("date",day)
                        .whereIn("groupId", GroupIDs)
                        .orderBy("start");
               if(Student_Teacher)query=firstQuery; else query=secondQuery;
                        StorageReference sr = FirebaseStorage.getInstance().getReference();
                FirestoreRecyclerOptions<Session> options = new FirestoreRecyclerOptions.Builder<Session>()
                        .setQuery(query, Session.class)
                        .build();
                sessionAdapter = new SessionAdapter(options, UserId,db,sr);
                recyclerView = findViewById(R.id.rv_session);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(Dashboard.this));
                recyclerView.setAdapter(sessionAdapter);



    }
    public void setUpRecyclerViewSchoolPage(String day)
     {

            switch (priority) {
                case 3://Admin
                {
                    Query query = SessionRef.whereEqualTo("date", day);
                    StorageReference sr = FirebaseStorage.getInstance().getReference();
                    FirestoreRecyclerOptions<Session> options = new FirestoreRecyclerOptions.Builder<Session>()
                            .setQuery(query, Session.class)
                            .build();
                    sessionAdapter = new SessionAdapter(options, UserId,db,sr);
                    recyclerView = findViewById(R.id.rv_session);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(Dashboard.this));
                    recyclerView.setAdapter(sessionAdapter);
                    sessionAdapter.setOnItemClickListener(new SessionAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(DocumentSnapshot documentSnapshot, int pos) {
                            String SessionId =documentSnapshot.getId();
                            ArrayList<String> listOfPresence= (ArrayList<String>)documentSnapshot.get("listOfPresence");
                            Intent intent = new Intent(Dashboard.this,MemberList.class)
                                    .putStringArrayListExtra("listOfPresence",listOfPresence)
                                    .putExtra("Pres",true)
                                    .putExtra("TAG",TAG)
                                    .putExtra("SchoolID",SchoolId)
                                    .putExtra("GroupID",GroupId)
                                    .putExtra("Priority",priority)
                                    .putExtra("NewSessionID","Session"+System.currentTimeMillis());
                            startActivity(intent);
                            finish();




                        }
                    });
                }
                break;
                case 2://Teacher
                {
                    Query query = SessionRef.whereEqualTo("date", day).whereEqualTo("teacherId", UserId);
                    StorageReference sr = FirebaseStorage.getInstance().getReference();
                    FirestoreRecyclerOptions<Session> options = new FirestoreRecyclerOptions.Builder<Session>()
                            .setQuery(query, Session.class)
                            .build();
                    sessionAdapter = new SessionAdapter(options, UserId,db,sr);
                    recyclerView = findViewById(R.id.rv_session);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(Dashboard.this));
                    recyclerView.setAdapter(sessionAdapter);
                }
                break;


                case 1://Student
                {
                    Query query = SessionRef.whereIn("groupId", GroupIDs).whereEqualTo("date", day);
                    StorageReference sr = FirebaseStorage.getInstance().getReference();
                    FirestoreRecyclerOptions<Session> options = new FirestoreRecyclerOptions.Builder<Session>()
                            .setQuery(query, Session.class)
                            .build();
                    sessionAdapter = new SessionAdapter(options, UserId,db,sr);
                    recyclerView = findViewById(R.id.rv_session);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(Dashboard.this));
                    recyclerView.setAdapter(sessionAdapter);

                    /*------------------set click--------------*/
                    /*-------Scan QR-Code--------------------*/
                    sessionAdapter.setOnItemClickListener(new SessionAdapter.OnItemClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                            Toast.makeText(Dashboard.this, "" + sessionAdapter.isClickable(position,UserId,priority), Toast.LENGTH_SHORT).show();
                            if (sessionAdapter.isClickable(position,UserId,priority)) {
                                Cur_pos = position;
                                Log.d("qrtesting", "" + Cur_pos);
                                String SID = documentSnapshot.getId();
                                SessionId = SID;
                                String db_QR_Code = documentSnapshot.getString("qrcode");
                                Qrdb = documentSnapshot.getString("qrcode");
                                Log.d("qrtesting", Qrdb);
                                if (ActivityCompat.checkSelfPermission(Dashboard.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                    Intent intent = new Intent(Dashboard.this, ScanActivity.class).putExtra("SchoolID", SchoolId)
                                            .putExtra("GroupID", GroupId).putExtra("SessionID", SID).putExtra("Qrdb", db_QR_Code);
                                    startActivityForResult(intent, 0);
                                } else {
                                    if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                                        Toast.makeText(Dashboard.this,
                                                "Camera permission is needed",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    ActivityCompat.requestPermissions(Dashboard.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                                }postponeEnterTransition();

                            }
                        }
                    });
                }
                break;



        }
    }
    void setUpRecyclerView(String day)
    {
        if(bar)setUpRecyclerViewMenuBottomBar(day);

        else setUpRecyclerViewSchoolPage(day);
       }




    void initSessionAndGroupRef()
    {

        SessionRef=db.collection("School").document(SchoolId).collection("Session");
        GroupRef=db.collection("School").document(SchoolId).collection("Group");
        CourseRef=db.collection("School").document(SchoolId).collection("Course");
        //Toast.makeText(this, SchoolId, Toast.LENGTH_SHORT).show();

    }
    String getTodayDate()
    {
        SimpleDateFormat dateFor = new SimpleDateFormat("dd/MM/yyyy");
        String stringDate= dateFor.format(new Date());
        return stringDate;
    }

    @Override
    protected void onStart() {
        super.onStart();
       sessionAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        sessionAdapter.stopListening();
    }
    //Scan Fonction
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 0) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Log.d("curpos",""+Cur_pos);
                    //Log.d("curpost",sessionAdapter.getItem(Cur_pos).getQrstring());
                    Barcode barcode = data.getParcelableExtra("barcode");
                    Scane_res = barcode.displayValue;

                   // Toast.makeText(this, Qrdb, Toast.LENGTH_SHORT).show();
                    if (Scane_res.equals(Qrdb)) {
                        Toast.makeText(this, "verified", Toast.LENGTH_SHORT).show();

                        DocumentReference SessionDocRef=db.collection("School").document(SchoolId).
                                collection("Session").document(SessionId);
                        SessionDocRef.update("listOfPresence", FieldValue.arrayUnion(UserId));
                    }
                    else  Toast.makeText(this, "qrcode invalide", Toast.LENGTH_SHORT).show();

                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
   @RequiresApi(api = Build.VERSION_CODES.O)
   void setFloatingAppButtonIcon()
   {
       fab=(FloatingActionButton) findViewById(R.id.fab);
      if( bar)
       {
           fab.setImageResource(R.drawable.ic8_change_user);
           if(Student_Teacher) fab.setTooltipText("Student");
           else fab.setTooltipText("Teacher");
       }
       else

       {

           if (priority==1) fab.setImageResource(R.drawable.ic_school);
       }

   }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(Dashboard.this,SchoolPage.class)
                .putExtra("SchoolID",SchoolId)
                .putExtra("Priority",priority);
        startActivity(intent);
        finish();
    }
    void getListOfTeachers_Groups_Courses(String path)
    {
        UserRef.whereArrayContains("teacherIN",path)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.getDocuments().isEmpty())
                        {
                            ArrayList<String> teacherNameList=new ArrayList<String>();
                            ArrayList<String> teacherIdList=new ArrayList<String>();
                            List<DocumentSnapshot> GID=queryDocumentSnapshots.getDocuments();
                            for(DocumentSnapshot doc: GID)
                            {
                                teacherNameList.add(doc.getString("displayName"));
                                teacherIdList.add(doc.getId());
                            }

                            getListOfGroups_Coures(teacherIdList,teacherNameList);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Intent intent =new Intent(Dashboard.this,CreateSession.class)
                                .putExtra("SchoolID",SchoolId)
                                .putExtra("GroupID",GroupId)
                                .putStringArrayListExtra("teacherIdList",new ArrayList<String>())
                                .putStringArrayListExtra("teacherNameList",new ArrayList<String>());
                    }
                })
        ;
    }
    void getListOfGroups_Coures(List<String>teacherIdList,List<String>teacherNameList)
    {
        GroupRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.getDocuments().isEmpty())
                        {
                            ArrayList<String> GroupNameList=new ArrayList<String>();
                            ArrayList<String> GroupIdList=new ArrayList<String>();
                            List<DocumentSnapshot> GID=queryDocumentSnapshots.getDocuments();
                            for(DocumentSnapshot doc: GID)
                            {
                                GroupNameList.add(doc.getString("displayName"));
                                GroupIdList.add(doc.getId());
                            }
                            getListOfCoures(teacherIdList,teacherNameList,GroupIdList,GroupNameList);

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Intent intent =new Intent(Dashboard.this,CreateSession.class)
                                .putExtra("SchoolID",SchoolId)
                                .putExtra("GroupID",GroupId)
                                .putExtra("Priority",priority)
                                .putStringArrayListExtra("groupIdList",new ArrayList<String>())
                                .putStringArrayListExtra("groupNameList",new ArrayList<String>())
                                .putStringArrayListExtra("teacherIdList", (ArrayList<String>) teacherIdList)
                                .putStringArrayListExtra("teacherNameList", (ArrayList<String>) teacherNameList)
                                .putExtra("NewSessionID","Session"+System.currentTimeMillis());
                    }
                })
        ;
    }
    void getListOfCoures(List<String>teacherIdList,List<String>teacherNameList,List<String>groupIdList,List<String>groupNameList)
    {
        CourseRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.getDocuments().isEmpty())
                        {
                            ArrayList<String> CourseNameList=new ArrayList<String>();
                            List<DocumentSnapshot> GID=queryDocumentSnapshots.getDocuments();
                            for(DocumentSnapshot doc: GID)
                            {

                                CourseNameList.add(doc.getId());
                            }

                            Intent intent =new Intent(Dashboard.this,CreateSession.class)
                                    .putExtra("SchoolID",SchoolId)
                                    .putExtra("GroupID",GroupId)
                                    .putExtra("Priority",priority)
                                    .putStringArrayListExtra("teacherIdList", (ArrayList<String>) teacherIdList)
                                    .putStringArrayListExtra("teacherNameList", (ArrayList<String>) teacherNameList)
                                    .putStringArrayListExtra("groupIdList",(ArrayList<String>)groupIdList)
                                    .putStringArrayListExtra("groupNameList",(ArrayList<String>)groupNameList)
                                    .putStringArrayListExtra("courseNameList",CourseNameList)
                                    .putExtra("NewSessionID","Session"+System.currentTimeMillis());
                            ;
                            //
                            // Toast.makeText(SchoolPage.this, GID, Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Intent intent =new Intent(Dashboard.this,CreateSession.class)
                                .putExtra("SchoolID",SchoolId)
                                .putExtra("GroupID",GroupId)
                                .putExtra("Priority",priority)
                                .putStringArrayListExtra("teacherIdList", (ArrayList<String>) teacherIdList)
                                .putStringArrayListExtra("teacherNameList", (ArrayList<String>) teacherNameList)
                                .putStringArrayListExtra("groupIdList",(ArrayList<String>)groupIdList)
                                .putStringArrayListExtra("groupNameList",(ArrayList<String>)groupNameList)
                                .putStringArrayListExtra("courseNameList",new ArrayList<String>())
                                .putExtra("NewSessionID","Session"+System.currentTimeMillis());
                    }
                });

    }
    private void OnSwipedItem()
    {
        //---------------------------Swipe Item -------------------------//
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT |ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                sessionAdapter.deleteItem(viewHolder.getAdapterPosition(),UserId,db,priority);
                recyclerView.setAdapter(sessionAdapter);

            }

        }).attachToRecyclerView(recyclerView);
        {

        }
    }
}
