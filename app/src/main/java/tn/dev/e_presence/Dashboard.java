package tn.dev.e_presence;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class Dashboard extends AppCompatActivity implements DatePickerListener {
    private ListView lv_Session;
    private  ArrayList<String> GroupIDs=new ArrayList<>();
    private SessionAdapter Adapter;
    private final static FirebaseFirestore db=FirebaseFirestore.getInstance();
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
    private static Boolean NewSession,Student_Teacher=true;
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
        if( new DarkModePrefManager(this).isLightMode())
            picker.setListener(this)
                .setDays(120)
                .setOffset(30)
                .setDateSelectedColor(getResources().getColor(R.color.c2))
                .setDateSelectedTextColor(Color.WHITE)
                .setMonthAndYearTextColor(getResources().getColor(R.color.c2))
                .setTodayButtonTextColor(Color.WHITE)
                .setTodayDateTextColor(Color.WHITE)
                .setTodayDateBackgroundColor(Color.BLACK)
                .setUnselectedDayTextColor(Color.BLACK)
                .setDayOfWeekTextColor(Color.BLACK)
                .showTodayButton(true)
                .init();
                else

            picker.setListener(this)
                    .setDays(120)
                    .setOffset(30)
                    .setDateSelectedColor(getResources().getColor(R.color.c2))
                    .setDateSelectedTextColor(Color.BLACK)
                    .setMonthAndYearTextColor(getResources().getColor(R.color.c2))
                    .setTodayButtonTextColor(Color.WHITE)
                    .setTodayDateTextColor(Color.WHITE)
                    .setTodayDateBackgroundColor(Color.BLACK)
                    .setUnselectedDayTextColor(getResources().getColor(R.color.ghost_white))
                    .setDayOfWeekTextColor(getResources().getColor(R.color.ghost_white))
                    .showTodayButton(true)
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


    @RequiresApi(api = Build.VERSION_CODES.O)
    private  void AddSession(View v)
    {
        if( bar)
        {
           Student_Teacher=!Student_Teacher;
            setFloatingAppButtonIcon();
            setUpRecyclerViewMenuBottomBar(day);
            sessionAdapter.startListening();

        }
        else
        switch (priority) {
            case 3:
            case 2: {
                String Schoolpath="School/"+SchoolId;
                NewSession=true;

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
                        .orderBy("start");
                if(!GroupIDs.isEmpty()) secondQuery=secondQuery.whereIn("groupId", GroupIDs);
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
                {
                    /*------------------set click--------------*/
                    /*-------Scan QR-Code--------------------*/
                    sessionAdapter.setOnItemClickListener(new SessionAdapter.OnItemClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                            String SID=sessionAdapter.getItem(position).getSchoolId();
                            int priority;
                            boolean isAdmin,isTeacher ,isStudent;
                            try{  isAdmin=((List<String>)documentSnapshot.get("Admins")).contains(UserId);}catch(Exception e){isAdmin=false;}
                            try{ isTeacher=((List<String>)documentSnapshot.get("Teachers")).contains(UserId);}catch(Exception e){isTeacher=false;}
                            try{ isStudent=((List<String>)documentSnapshot.get("Students")).contains(UserId);}catch(Exception e){isStudent=false;}
                            if (isAdmin) priority=3;
                            else if(isTeacher) priority=2;
                            else if(isStudent) priority=1;
                            else priority=0;
                            String ch=""+priority;
                            //Toast.makeText(Home.this,ch, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Dashboard.this, SchoolPage.class)
                                    .putExtra("SchoolID",SID)
                                    .putExtra("Priority",priority));
                            finish();

                        }
                    });
                }



    }
    public void setUpRecyclerViewSchoolPage(String day)
     {

            switch (priority) {
                case 3:
                //Admin
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
                    OnSwipedItem();
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
                    OnSwipedItem();
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
           if(!Student_Teacher)
           fab.setImageResource(R.drawable.ic8_change_teacher);
           else fab.setImageResource(R.drawable.ic8_change_user);

       }
       else

       {

           if (priority==1) fab.setImageResource(R.drawable.ic8_back_arrow);
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
                        Intent intent =new Intent(Dashboard.this, AddSession.class)
                                .putExtra("SchoolID",SchoolId)
                                .putExtra("GroupID",GroupId)
                                .putStringArrayListExtra("teacherIdList",new ArrayList<String>())
                                .putStringArrayListExtra("teacherNameList",new ArrayList<String>())
                                .putExtra("NewSession",NewSession)
                                .putExtra("SessionID",SessionId);
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
                        Intent intent =new Intent(Dashboard.this, AddSession.class)
                                .putExtra("SchoolID",SchoolId)
                                .putExtra("GroupID",GroupId)
                                .putExtra("Priority",priority)
                                .putStringArrayListExtra("groupIdList",new ArrayList<String>())
                                .putStringArrayListExtra("groupNameList",new ArrayList<String>())
                                .putStringArrayListExtra("teacherIdList", (ArrayList<String>) teacherIdList)
                                .putStringArrayListExtra("teacherNameList", (ArrayList<String>) teacherNameList)
                                .putExtra("NewSessionID","Session"+System.currentTimeMillis())
                                .putExtra("NewSession",NewSession)
                                .putExtra("SessionID",SessionId);
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

                            Intent intent =new Intent(Dashboard.this, AddSession.class)
                                    .putExtra("SchoolID",SchoolId)
                                    .putExtra("GroupID",GroupId)
                                    .putExtra("Priority",priority)
                                    .putStringArrayListExtra("teacherIdList", (ArrayList<String>) teacherIdList)
                                    .putStringArrayListExtra("teacherNameList", (ArrayList<String>) teacherNameList)
                                    .putStringArrayListExtra("groupIdList",(ArrayList<String>)groupIdList)
                                    .putStringArrayListExtra("groupNameList",(ArrayList<String>)groupNameList)
                                    .putStringArrayListExtra("courseNameList",CourseNameList)
                                    .putExtra("NewSessionID","Session"+System.currentTimeMillis())
                                    .putExtra("NewSession",NewSession)
                                    .putExtra("SessionID",SessionId);
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
                        Intent intent =new Intent(Dashboard.this, AddSession.class)
                                .putExtra("SchoolID",SchoolId)
                                .putExtra("GroupID",GroupId)
                                .putExtra("Priority",priority)
                                .putStringArrayListExtra("teacherIdList", (ArrayList<String>) teacherIdList)
                                .putStringArrayListExtra("teacherNameList", (ArrayList<String>) teacherNameList)
                                .putStringArrayListExtra("groupIdList",(ArrayList<String>)groupIdList)
                                .putStringArrayListExtra("groupNameList",(ArrayList<String>)groupNameList)
                                .putStringArrayListExtra("courseNameList",new ArrayList<String>())
                                .putExtra("NewSessionID","Session"+System.currentTimeMillis())
                                .putExtra("NewSession",NewSession)
                                .putExtra("SessionID",SessionId);
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
                int position=viewHolder.getAdapterPosition();
                switch (direction) {
                    case ItemTouchHelper.LEFT:
                        final AlertDialog.Builder deleateSessionDialog = new AlertDialog.Builder(Dashboard.this);
                        deleateSessionDialog.setTitle("Delete Group?");
                        String message = "Do you want to remove " + "<b>"+"this session"+"</b>" +" from "+"<b>"+SchoolId+"</b>" +" session list?";
                        Spanned spannedMessage = Html.fromHtml(message);
                        deleateSessionDialog.setMessage(spannedMessage);
                        deleateSessionDialog.setIcon(R.drawable.ic8_delete_calendar);
                        deleateSessionDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // verify the identifier
                                sessionAdapter.deleteItem(position, UserId, db, priority);
                            }
                        });

                        deleateSessionDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // close the dialog
                                sessionAdapter.notifyItemChanged(position);
                            }
                        });

                        deleateSessionDialog.create().show();
                        break;
                    case ItemTouchHelper.RIGHT:
                         SessionId=sessionAdapter.getSnapshots().getSnapshot(position).getId();
                        SchoolId=sessionAdapter.getItem(position).getSchoolId();
                        NewSession=false;
                        String Schoolpath="School/"+SchoolId;
                        getListOfTeachers_Groups_Courses(Schoolpath);
                        break;
                }


            }
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(Dashboard.this, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftActionIcon(R.drawable.ic8_delete_calendar)
                        .addSwipeLeftLabel("Delete Session")
                        .setSwipeLeftLabelTextSize(1,20)
                        .addSwipeLeftBackgroundColor(getResources().getColor(R.color.color_delete))
                        .addSwipeRightActionIcon(R.drawable.ic8_calendar)
                        .addSwipeRightLabel("Edit Session")
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
