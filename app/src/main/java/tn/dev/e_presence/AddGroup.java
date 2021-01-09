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
    private boolean NewGroup=true;
    private String SchoolId;
    private int priority;
    private int Compteur;
    private Button btn_ok;
    private Button btn_cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        findViews();
        listenForIncommingMessages();
        setCategory();
        setOk();
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
        if (position != 0) {
            category = parent.getItemAtPosition(position).toString();


        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void findViews()
    {
        sp_category=findViewById(R.id.sp_category);
        btn_ok=findViewById(R.id.btn_ok);
        et_gname=findViewById(R.id.et_gname);
        et_gdescription=findViewById(R.id.et_gdescription);
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
                    // Start Dashborad Activity again
                    if (NewGroup) {

                        //put Data into a message for group

                        Map<String, Object> group = new HashMap<>();
                        group.put("displayName", new_Name);
                        group.put("level", new_Category);
                        group.put("description",new_Description);
                        group.put("num",0);
                        group.put("studentList",new ArrayList<String>());

                        { db.collection("School").document(SchoolId).collection("Group").document(new_Name)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {
                                            Toast.makeText(AddGroup.this, "Existing Group Name", Toast.LENGTH_SHORT).show();

                                        } else {
                                            db.collection("School").document(SchoolId).collection("Group").document(new_Name)
                                                    .set(group)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Compteur+=1;
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
    }

}