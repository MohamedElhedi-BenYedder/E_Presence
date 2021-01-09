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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener;
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Date;
import java.util.List;

public class Dashboard extends AppCompatActivity implements DatePickerListener {
    private ListView lv_Session;
    private SessionAdapter Adapter;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final String UserId = user.getUid();
    private CollectionReference SessionRef ;
    private CollectionReference UserRef =db.collection("User");
    private CollectionReference GroupRef;
    private static SessionAdapter sessionAdapter;
    private HorizontalPicker picker;
    private BottomAppBar bottomAppBar;
    private BottomNavigationView bottomNavigationView;
    private int REQUEST_CAMERA = 1;
    private String TAG="Dashbord";
    private static int Cur_pos;
    private String Scane_res = "chaine";
    private static String SchoolId;
    private static String GroupId,SessionId;
    private String day;
    private static String Qrdb;
    private RecyclerView recyclerView;
    private int priority;
    private static boolean Scan_verdict;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        listenForIncommingMessages();
        initSessionAndGroupRef();
        SetUpBottomAppBarMenu();
        SetDatePicker();
        setUpRecyclerView(getTodayDate());
        FloatingActionButton fab =(FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this::AddSession);



    }
    void listenForIncommingMessages()
    {
        //listen for incoming messages
        Bundle incommingMessages =getIntent().getExtras();
        SchoolId =incommingMessages.getString("SchoolID","0");
        GroupId=incommingMessages.getString("GroupID","0");
        priority=incommingMessages.getInt("Priority",0);
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
        //Toast.makeText(this, dateSelected.toString("dd/MM/yyyy"), Toast.LENGTH_SHORT).show();
        setUpRecyclerView(dateSelected.toString("dd/MM/yyyy"));
        sessionAdapter.startListening();

    }

    private  void AddSession(View v)
    {
        String path="Schoo/"+SchoolId;
        ArrayList<String> teacherNameList=new ArrayList<String>();
        UserRef.whereArrayContains("teacherIN","School/"+SchoolId)
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

                            Intent intent =new Intent(Dashboard.this,CreateSession.class)
                                    .putExtra("SchoolID",SchoolId)
                                    .putExtra("GroupID",GroupId)
                                    .putStringArrayListExtra("teacherIdList",teacherIdList)
                                    .putStringArrayListExtra("teacherNameList",teacherNameList);
                            //
                            // Toast.makeText(SchoolPage.this, GID, Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();
                        }
                    }
                });

        /*ArrayList<String> teacherIdList=(ArrayList<String>)db.collection("School").document(SchoolId).get().getResult().get("Teachers");
        for (String id :teacherIdList)
        {
            teacherIdList.add((String) db.collection("User").document(id).get().getResult().get("displayName"));
        }
        Intent intent =new Intent(Dashboard.this,CreateSession.class)
                .putExtra("SchoolID",SchoolId)
                .putExtra("GroupID",GroupId)
                .putStringArrayListExtra("teacherIdList",teacherIdList)
                .putStringArrayListExtra("teacherNameList",teacherNameList);
        startActivity(intent);
        finish();*/
    }
   /* protected void onListItemClick(ListView l, View v, int position, long id) {
        startActivity(new Intent(getApplicationContext(),ScanActivity.class));

    }*/
    public void setUpRecyclerView(String day)
     {
        switch (priority)
        {
            case 3://Admin
                {
                Query query = SessionRef.whereEqualTo("date", day);
                StorageReference path = FirebaseStorage.getInstance().getReference();
                FirestoreRecyclerOptions<Session> options = new FirestoreRecyclerOptions.Builder<Session>()
                        .setQuery(query, Session.class)
                        .build();
                sessionAdapter = new SessionAdapter(options, UserId);
                recyclerView = findViewById(R.id.rv_session);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(Dashboard.this));
                recyclerView.setAdapter(sessionAdapter);
            }
            break;
            case 2://Teacher
                {
                Query query = SessionRef.whereEqualTo("date", day).whereEqualTo("teacherId",UserId);
                StorageReference path = FirebaseStorage.getInstance().getReference();
                FirestoreRecyclerOptions<Session> options = new FirestoreRecyclerOptions.Builder<Session>()
                        .setQuery(query, Session.class)
                        .build();
                sessionAdapter = new SessionAdapter(options, UserId);
                recyclerView = findViewById(R.id.rv_session);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(Dashboard.this));
                recyclerView.setAdapter(sessionAdapter);
                }
            break;


            case 1://Student
                {
                Query query = SessionRef.whereEqualTo("group", GroupId).whereEqualTo("date", day);
                StorageReference path = FirebaseStorage.getInstance().getReference();
                FirestoreRecyclerOptions<Session> options = new FirestoreRecyclerOptions.Builder<Session>()
                        .setQuery(query, Session.class)
                        .build();
                sessionAdapter = new SessionAdapter(options, UserId);
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
                        Toast.makeText(Dashboard.this, ""+sessionAdapter.isClickable(), Toast.LENGTH_SHORT).show();
                        if(sessionAdapter.isClickable()){
                        Cur_pos=position;
                        Log.d("qrtesting",""+Cur_pos);
                        String SID = documentSnapshot.getId();
                        SessionId=SID;
                        String db_QR_Code=documentSnapshot.getString("qrcode");
                        Qrdb=documentSnapshot.getString("qrcode");
                        Log.d("qrtesting",Qrdb);
                        if (ActivityCompat.checkSelfPermission(Dashboard.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            Intent intent = new Intent(Dashboard.this,ScanActivity.class).putExtra("SchoolID",SchoolId)
                            .putExtra("GroupID",GroupId).putExtra("SessionID",SID).putExtra("Qrdb",db_QR_Code);
                            startActivityForResult(intent, 0);
                        } else {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                                Toast.makeText(Dashboard.this,
                                        "Camera permission is needed",
                                        Toast.LENGTH_SHORT).show();
                            }
                            ActivityCompat.requestPermissions(Dashboard.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                        }

                }}
            });}
                break;


         }
    }



    void initSessionAndGroupRef()
    {

        SessionRef=db.collection("School").document(SchoolId).collection("Session");
        GroupRef=db.collection("School").document(SchoolId).collection("Group");
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

                    Toast.makeText(this, Qrdb, Toast.LENGTH_SHORT).show();
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

}
