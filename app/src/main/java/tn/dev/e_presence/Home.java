package tn.dev.e_presence;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Home extends AppCompatActivity {
    private StorageReference mStorageRef;
    private BottomAppBar bottomAppBar;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference SchoolRef =db.collection("School");
    private SchoolAdapter schoolAdapter;
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final String UserId = user.getUid();
    private FloatingActionButton fab;
    private EditText searchBox;
    private RecyclerView recyclerView;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        findViews();
        setUpBottomAppBarMenu();
        setUpRecyclerView();
        OnSwipedItem();
        fab.setOnClickListener(this::AddSchool);
        searchBar();


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setUpBottomAppBarMenu( )
    {
        //find id

        //bottomAppBar.getMenu().getItem(0).setIconTintList(getColorStateList(R.color.c2));

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
        private void searchBar()
        {
            searchBox.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    Query query;
                    if (s.toString().isEmpty())
                    {
                        query = SchoolRef.orderBy("DisplayName");
                        StorageReference path = FirebaseStorage.getInstance().getReference();
                        FirestoreRecyclerOptions<School> options = new FirestoreRecyclerOptions.Builder<School>()
                                .setQuery(query,School.class)
                                .build();
                        schoolAdapter.updateOptions(options);
                    }
                    else {
                        query = SchoolRef.orderBy("DisplayName").startAt(s.toString()).endAt(s.toString()+'\uf8ff');
                        StorageReference path = FirebaseStorage.getInstance().getReference();
                        FirestoreRecyclerOptions<School> options = new FirestoreRecyclerOptions.Builder<School>()
                                .setQuery(query,School.class)
                                .build();
                        schoolAdapter.updateOptions(options);}
                }
            });
        }
        private void setUpRecyclerView()
        {
            Query query = SchoolRef.orderBy("DisplayName");
            StorageReference path = FirebaseStorage.getInstance().getReference();
            FirestoreRecyclerOptions<School> options = new FirestoreRecyclerOptions.Builder<School>()
                    .setQuery(query,School.class)
                    .build();
            schoolAdapter=new SchoolAdapter(options,path);

            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(schoolAdapter);
            //----------------------setOnClickItem------------------------//
            schoolAdapter.setOnItemClickListener(new SchoolAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                    String SID = documentSnapshot.getId();
                    int priority;
                    boolean isAdmin=((List<String>)documentSnapshot.get("Admins")).contains(UserId);
                    boolean isTeacher=((List<String>)documentSnapshot.get("Teachers")).contains(UserId);
                    boolean isStudent=((List<String>)documentSnapshot.get("Students")).contains(UserId);
                    if (isAdmin) priority=3;
                    else if(isTeacher) priority=2;
                    else if(isStudent) priority=1;
                    else priority=0;
                    String ch=""+priority;
                    Toast.makeText(Home.this,ch, Toast.LENGTH_SHORT).show();
                   startActivity(new Intent(Home.this, SchoolPage.class)
                           .putExtra("SchoolID",SID)
                           .putExtra("Priority",priority));
                    finish();
                }
            });


        }
        private  void AddSchool(View v)
        {

            Intent intent =new Intent(Home.this,AddSchool.class);
            intent.putExtra("NewSchool",true);
            startActivity(intent);
            finish();
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
                    schoolAdapter.deleteItem(viewHolder.getAdapterPosition(),UserId,db);
                    recyclerView.setAdapter(schoolAdapter);

                }

            }).attachToRecyclerView(recyclerView);
            {

            }
        }
        private void findViews()
        {
            recyclerView = findViewById(R.id.rv_school);
            fab =(FloatingActionButton) findViewById(R.id.fab);
            searchBox = findViewById(R.id.searchBox);
            bottomAppBar=findViewById(R.id.bnb);
        }
    @Override
    protected void onStart() {
        super.onStart();
        schoolAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        schoolAdapter.stopListening();
    }


}