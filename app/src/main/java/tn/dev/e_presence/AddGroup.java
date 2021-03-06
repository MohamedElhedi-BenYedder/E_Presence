package tn.dev.e_presence;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.RenderScript;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddGroup extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    final String TAG="AddGroup";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String UserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private Spinner sp_category;
    private static String category;
    private EditText et_gdescription,et_gname;
    private boolean NewGroup;
    private String SchoolId;
    private int priority;
    private TextView tv_title;
    private Button btn_ok;
    private Button btn_cancel;
    private String NewGroupID,GroupID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        findViews();
        listenForIncommingMessages();
        setCategory();
        setOk();
        setCancel();
        setDisplay();
    }
    void setDisplay()
    {
        if(!NewGroup)
        {

            db.collection("School").document(SchoolId).collection("Group").document(GroupID)
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    et_gdescription.setText(documentSnapshot.getString("description"));
                    et_gname.setText(documentSnapshot.getString("displayName"));
                    switch(documentSnapshot.getString("level")) {
                        case "Level1":
                            sp_category.setSelection(0);
                            break;
                        case "Level2":
                            sp_category.setSelection(1);
                        case "Level3":
                            sp_category.setSelection(2);
                        case "Level4":
                            sp_category.setSelection(3);
                        case "Level5":
                            sp_category.setSelection(4);
                        case "Level6":
                            sp_category.setSelection(5);
                            break;
                        case "Club":
                            sp_category.setSelection(6);
                            break;}

                }
            });

        }
        else
        {
            tv_title.setText("Add Group");
        }


    }
    public void setCategory(){
        List<String> Category= new ArrayList<String>( Arrays.asList("Level1", "Level2","Level3","Level4","Level5","Level6","Club") );

        ArrayAdapter<String> Category_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,Category);
        Category_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_category.setAdapter(Category_adapter);
        sp_category.setOnItemSelectedListener(this);
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        category = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { /* mata3mil chay*/ }

    public void findViews()
    {
        sp_category=findViewById(R.id.sp_category);
        btn_ok=findViewById(R.id.btn_ok);
        btn_cancel=findViewById(R.id.btn_cancel);
        et_gname=findViewById(R.id.et_gname);
        et_gdescription=findViewById(R.id.et_gdescription);
        tv_title=findViewById(R.id.tv_title);
    }
    public void setCancel(){
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(AddGroup.this,GroupList.class).
                        putExtra("SchoolID",SchoolId);
                intent.putExtra("Priority",priority);
                startActivity(intent);
                finish();
            }
        });
    }
    public void setOk()
    {
        btn_ok.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                    //get data
                    String new_Name= et_gname.getText().toString();
                    String new_Description= et_gdescription.getText().toString();
                    String new_Category=category;
                Map<String, Object> group = new HashMap<>();
                group.put("displayName", new_Name);
                group.put("level", new_Category);
                group.put("description",new_Description);
                group.put("num",0);
                group.put("Students",new ArrayList<String>());
                    if (NewGroup) {
                        { db.collection("School").document(SchoolId).collection("Group").document(NewGroupID)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {
                                            Toast.makeText(AddGroup.this, "Existing Group Name", Toast.LENGTH_SHORT).show();

                                        } else {
                                            db.collection("School").document(SchoolId).collection("Group").document(NewGroupID)
                                                    .set(group)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d(TAG, "Group successfully added!");
                                                            Toast.makeText(AddGroup.this, "Group successfully added!", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(AddGroup.this,GroupList.class)
                                                                    .putExtra("SchoolID",SchoolId)
                                                                    .putExtra("Priority", priority);
                                                            ;
                                                            startActivity(intent);
                                                            finish();

                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "Error adding Group", e);
                                                            Toast.makeText(AddGroup.this, "Error adding Group", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AddGroup.this, "Error!", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, e.toString());
                                    }
                                });
                        }
                    }
                    else
                    {
                        db.collection("School").document(SchoolId).collection("Group").document(GroupID)
                                .update(group)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Group Details successfully Edited!");
                                        Toast.makeText(AddGroup.this, "Group successfully added!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(AddGroup.this,GroupList.class)
                                                .putExtra("SchoolID",SchoolId)
                                                .putExtra("Priority", priority);
                                        ;
                                        startActivity(intent);
                                        finish();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error Editing Group", e);
                                        Toast.makeText(AddGroup.this, "Error Editing Group", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    //
            }
        });
    }
    void listenForIncommingMessages()
    {
        //listen for incoming messages
        Bundle incommingMessages =getIntent().getExtras();
        SchoolId =incommingMessages.getString("SchoolID","0");
        priority=incommingMessages.getInt("Priority",0);
        NewGroupID=incommingMessages.getString("NewGroupID","0");
        NewGroup=incommingMessages.getBoolean("NewGroup");
        GroupID=incommingMessages.getString("GroupID","0");
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent =new Intent(AddGroup.this,GroupList.class).
                putExtra("SchoolID",SchoolId);
        intent.putExtra("Priority",priority);
        startActivity(intent);
        finish();
    }

}