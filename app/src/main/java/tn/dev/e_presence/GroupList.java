package tn.dev.e_presence;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static tn.dev.e_presence.GV.getUser;

public class GroupList extends AppCompatActivity {
    private StorageReference mStorageRef;
    private BottomAppBar bottomAppBar;
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private String UserId= FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String SchoolId;
    private String GroupId;
    private CollectionReference GroupsRef;
    private GroupAdapter groupAdapter;
    private RecyclerView recyclerView;
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
                                .putExtra("Priority",priority)
                                .putExtra("NewGroup",true)
                                .putExtra("NewGroupID","Group"+System.currentTimeMillis());
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
            fab.setImageResource(R.drawable.ic8_add_user_group);
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
        recyclerView = findViewById(R.id.rv_group);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(groupAdapter);
        OnSwipedGroup();
        groupAdapter.setOnItemClickListener(new GroupAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
            String clickedGroupId =documentSnapshot.getId();
            ArrayList<String> ClickedGroupMemeberList= (ArrayList<String>) documentSnapshot.get("Students");
            Intent intent =new Intent(GroupList.this,MemberList.class)
                    .putExtra("GroupID",clickedGroupId)
                    .putExtra("key","studentIN")
                    .putExtra("Priority",priority)
                    .putExtra("SchoolID",SchoolId)
                    .putStringArrayListExtra("Students",ClickedGroupMemeberList)
                    .putExtra("path","School/"+SchoolId+"/Group/"+clickedGroupId);

            Toast.makeText(GroupList.this, "Group Memeber List" , Toast.LENGTH_SHORT).show();
            startActivity(intent);
            finish();
        }
    });

    }
    private void OnSwipedGroup()
    {
        if (priority==3){
            //---------------------------Swipe Item -------------------------//

            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT |ItemTouchHelper.RIGHT) {

                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    int position=viewHolder.getAdapterPosition();
                    switch (direction)
                    {
                        case ItemTouchHelper.LEFT:
                            String Identifier=groupAdapter.getItem(position).getDisplayName();
                            final AlertDialog.Builder deleateGroupDialog = new AlertDialog.Builder(GroupList.this);
                            deleateGroupDialog.setTitle("Delete Group?");
                            String Identifier_bold = "<b>" + Identifier+ "</b>";
                            String message = "Do you want to remove " + Identifier_bold +" from "+"<b>"+SchoolId+"</b>" +" group list?";
                            Spanned spannedMessage = Html.fromHtml(message);
                            deleateGroupDialog.setMessage(spannedMessage);
                            deleateGroupDialog.setIcon(R.drawable.ic8_delete_group);
                            deleateGroupDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // verify the identifier
                                    groupAdapter.delete(position,SchoolId);
                                }
                            });

                            deleateGroupDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // close the dialog
                                    groupAdapter.notifyItemChanged(position);
                                }
                            });

                            deleateGroupDialog.create().show();

                            break;
                        case ItemTouchHelper.RIGHT:
                            Intent intent=groupAdapter.edit(position)
                                    .putExtra("Priority",priority)
                                    .putExtra("NewGroup",false)
                                    .putExtra("SchoolID",SchoolId);
                            startActivity(intent);
                            finish();
                            break;

                    }


                }
                @Override
                public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                    new RecyclerViewSwipeDecorator.Builder(GroupList.this, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                            .addSwipeLeftActionIcon(R.drawable.ic8_delete_group_resized)
                            .addSwipeLeftLabel("Delete Group")
                            .setSwipeLeftLabelTextSize(1,20)
                            .addSwipeLeftBackgroundColor(getResources().getColor(R.color.color_delete))
                            .addSwipeRightActionIcon(R.drawable.ic8_edit_group_resized)
                            .addSwipeRightLabel("Edit Group")
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