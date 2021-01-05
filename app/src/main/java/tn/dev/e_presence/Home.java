package tn.dev.e_presence;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

public class Home extends AppCompatActivity {
    private StorageReference mStorageRef;
    private BottomAppBar bottomAppBar;
    private BottomNavigationView bottomNavigationView;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference SchoolRef =db.collection("School");
    private SchoolAdapter schoolAdapter;

    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setUpBottomAppBarMenu();
        setUpRecyclerView();
        FloatingActionButton fab =(FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this::AddSchool);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setUpBottomAppBarMenu( )
    {
        //find id
        bottomAppBar=findViewById(R.id.bnb);
        bottomAppBar.getMenu().getItem(0).setIconTintList(getColorStateList(R.color.c2));

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
            Query query = SchoolRef.orderBy("DisplayName");
            StorageReference path = FirebaseStorage.getInstance().getReference();
            FirestoreRecyclerOptions<School> options = new FirestoreRecyclerOptions.Builder<School>()
                    .setQuery(query,School.class)
                    .build();
            schoolAdapter=new SchoolAdapter(options,path);
            RecyclerView recyclerView = findViewById(R.id.rv_school);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(schoolAdapter);
            schoolAdapter.setOnItemClickListener(new SchoolAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                    String SID = documentSnapshot.getId();
                   startActivity(new Intent(Home.this, SchoolPage.class).putExtra("ID",SID));

                }
            });

        }
        private  void AddSchool(View v)
        {

            Intent intent =new Intent(Home.this,AddSchool.class);
            intent.putExtra("first",true);
            startActivity(intent);
            finish();
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