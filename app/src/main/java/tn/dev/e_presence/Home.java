package tn.dev.e_presence;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class Home extends AppCompatActivity {
    private BottomAppBar bottomAppBar;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference SchoolRef =db.collection("School");
    private SchoolAdapter schoolAdapter;
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final String UserId = user.getUid();
    private FloatingActionButton fab;
    private EditText searchBox;
    private RecyclerView recyclerView;
    private final int wait=500;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        findViews();
        setUpBottomAppBarMenu();
        setUpRecyclerView();
        OnSwipedItem();
        fab.setImageResource(R.drawable.ic8_addx);
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
            @SuppressLint("RestrictedApi")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (!user.isAnonymous())
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
                else   {
                    final AlertDialog.Builder ananymousDialog = new AlertDialog.Builder(Home.this);
                    ananymousDialog.setTitle("Sign Up ?");
                    ananymousDialog.setMessage("To access this page authentification is required !");
                    ananymousDialog.setIcon(getDrawable(R.drawable.ic8_add_key));
                    ananymousDialog.setPositiveButton("Sign Up", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AuthUI.getInstance()
                                    .signOut(Home.this)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        public void onComplete(@NonNull Task<Void> task) {
                                            // user is now signed out
                                            startActivity(new Intent(Home.this, Welcome.class));
                                            finish();
                                        }
                                    });


                            }});

                    ananymousDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // close the dialog
                        }
                    });

                    ananymousDialog.create().show();

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

                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    int position=viewHolder.getAdapterPosition();
                    if(schoolAdapter.isAdmin(position,UserId))
                    switch (direction)
                    {
                        case ItemTouchHelper.LEFT:
                            String Identifier=schoolAdapter.getItem(position).getDisplayName();
                            EditText ConfirmIdentifier=new EditText(Home.this);
                            final AlertDialog.Builder deleteSchoolDialog = new AlertDialog.Builder(Home.this);
                            deleteSchoolDialog.setTitle("Delete this school?");
                            String Identifier_bold = "<b>" + Identifier+ "</b>";
                            String message = "This action will result in the permanent deletion of all data for this school, including courses, sessions and groups.\n" +
                                    "<br>"+"<br>"+"Confirm the deletion of this school by entering its identifier: ";
                            Spanned spannedMessage = Html.fromHtml(message+ Identifier_bold);
                            deleteSchoolDialog.setMessage(spannedMessage);
                            deleteSchoolDialog.setView(ConfirmIdentifier);
                            deleteSchoolDialog.setIcon(getDrawable(R.drawable.ic8_delete));
                            deleteSchoolDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // verify the identifier
                                    if(Identifier.equals(ConfirmIdentifier.getText().toString()))
                                    {schoolAdapter.deleteItem(position,UserId,db);

                                        Toast.makeText(Home.this, "School is deleted.", Toast.LENGTH_SHORT).show();}

                                    else{
                                        Toast.makeText(Home.this, "Wrong identifier", Toast.LENGTH_SHORT).show();
                                        schoolAdapter.notifyItemChanged(position);
                                        GV.Wait(wait);
                                    }
                                }
                            });

                            deleteSchoolDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // close the dialog
                                    schoolAdapter.notifyItemChanged(position);
                                    GV.Wait(wait);
                                }
                            });

                            deleteSchoolDialog.create().show();

                            break;
                        case ItemTouchHelper.RIGHT:
                            Intent intent=schoolAdapter.editItem(position,UserId);
                            startActivity(intent);
                            finish();

                    }
                    schoolAdapter.notifyItemChanged(position);
                    GV.Wait(wait);


                }
                @Override
                public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                    new RecyclerViewSwipeDecorator.Builder(Home.this, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                            .addSwipeLeftActionIcon(R.drawable.ic8_delete_document_resized)
                            .addSwipeLeftLabel("Delete")
                            .setSwipeLeftLabelTextSize(1,20)
                            .addSwipeLeftBackgroundColor(getResources().getColor(R.color.color_delete))
                            .addSwipeRightActionIcon(R.drawable.ic8_edit_property_resised)
                            .addSwipeRightLabel("Edit")
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