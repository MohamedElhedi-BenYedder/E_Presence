package tn.dev.e_presence;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static tn.dev.e_presence.GV.getUser;

public class AddSchool extends AppCompatActivity {
    private StorageReference mStorageRef=FirebaseStorage.getInstance().getReference();
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final String UserId = user.getUid();
    @Nullable private User modelCurrentUser = new User();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private BottomAppBar bottomAppBar;
    EditText et_display_name;
    EditText et_full_name;
    EditText et_description;
    EditText et_location;
    TextView tv_welcome_user;
    Boolean NewSchool;
    ImageButton ib_photo ;
    FloatingActionButton fab;
    private String SchoolId;
    String photo="";
    TextView tv_title;
    String TAG= AddSchool.class.getSimpleName();
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_school);
        findViews();
        if(user.isAnonymous())
            bottomAppBar.setVisibility(View.INVISIBLE);
        welcomeUser();
        SetUpBottomAppBarMenu();
        if(NewSchool)
            tv_title.setText("Add School");
        listenForIncommingMessages();
        UploadPhoto();
        setDisplay();
        addEditSchool();
        }
        void setDisplay()
        {
            if(!NewSchool)
            {
                et_display_name.setVisibility(View.GONE);
                db.document("School/" + SchoolId)
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        et_location.setText(documentSnapshot.getString("Location"));
                        et_description.setText(documentSnapshot.getString("Description"));
                        et_full_name.setText(documentSnapshot.getString("FullName"));
                        String photo=documentSnapshot.getString("Photo");
                    }
                });
            }


        }
        void UploadPhoto()
        {
            ib_photo.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //Open Galery
                    Intent OpenGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(OpenGalleryIntent,100);
                }

            });
        }
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        void addEditSchool()
        {

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                      if (!user.isAnonymous())
                     {
                    Map<String, Object> school = new HashMap<>();
                    String displayName = et_display_name.getText().toString();
                    String location = et_location.getText().toString();
                    String fullName = et_full_name.getText().toString();
                    String Description = et_description.getText().toString();
                    school.put("FullName", fullName);
                    school.put("Description", Description);
                    school.put("Location", location);
                    if (!photo.isEmpty()) school.put("Photo", photo);
                    if (NewSchool)
                    // the user is creating a new school
                    {
                        school.put("Students", new ArrayList<String>());
                        school.put("Teachers", new ArrayList<String>());
                        school.put("Admins", new ArrayList<String>());
                        List<String> admins = new ArrayList<>();
                        admins.add(user.getUid());
                        school.put("Admins", admins);
                        if ((!displayName.isEmpty()) && (displayName.length() > 4) && (fullName.length() > 4) && (Description.length() > 4) && (location.length() > 4)) {
                            school.put("DisplayName", displayName);

                            db.document("School/" + et_display_name.getText().toString())
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()) {
                                                Toast.makeText(AddSchool.this, "Choose an other Display Name", Toast.LENGTH_SHORT).show();

                                            } else {
                                                db.document("School/" + et_display_name.getText().toString())
                                                        .set(school);
                                                updateDocumentArray();

                                                Intent intent = new Intent(AddSchool.this, SchoolPage.class)
                                                        .putExtra("SchoolID", displayName)
                                                        .putExtra("first", true)
                                                        .putExtra("Priority", 3);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(AddSchool.this, "Error!", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, e.toString());
                                        }
                                    });

                        } else {
                            Toast.makeText(AddSchool.this, "fields should have more than 4 caracters", Toast.LENGTH_SHORT).show();

                        }

                    } else {
                        if ((fullName.length() > 4) && (Description.length() > 4) && (location.length() > 4)) {
                            db.document("School/" + SchoolId)
                                    .update(school)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Intent intent = new Intent(AddSchool.this, SchoolPage.class)
                                                    .putExtra("SchoolID", SchoolId)
                                                    .putExtra("NewSchool", false)
                                                    .putExtra("Priority", 3);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddSchool.this, "Error ", Toast.LENGTH_SHORT).show();

                                }
                            });


                        } else {
                            Toast.makeText(AddSchool.this, "fields should have more than 4 caracters", Toast.LENGTH_SHORT).show();
                        }

                    }

                }
                else
                {
                    final AlertDialog.Builder ananymousDialog = new AlertDialog.Builder(AddSchool.this);
                    ananymousDialog.setTitle("Sign Up ?");
                    ananymousDialog.setMessage("To access this page authentification is required !");
                    ananymousDialog.setIcon(getDrawable(R.drawable.ic8_add_key));
                    ananymousDialog.setPositiveButton("Sign Up", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AuthUI.getInstance()
                                    .signOut(AddSchool.this)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        public void onComplete(@NonNull Task<Void> task) {
                                            // user is now signed out
                                            startActivity(new Intent(AddSchool.this, Welcome.class));
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
                }

            });


        }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100)
        {
            if(resultCode==Activity.RESULT_OK)
            {
                Uri imageUri =data.getData();
                photo=uploadImageToFirebase(imageUri);

            }
        }
    }
    private String uploadImageToFirebase( Uri contentUri) {
        String name="School/"+System.currentTimeMillis()
                + "." + getFileExtension(contentUri);
         StorageReference image = mStorageRef.child(name);
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("tag", "onSuccess: Uploaded Image URl is " + uri.toString());
                        Picasso.get().load(uri).into(ib_photo);
                    }
                });

                Toast.makeText(AddSchool.this, "Image Is Uploaded.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddSchool.this, "Upload Failled.", Toast.LENGTH_SHORT).show();
            }
        });
    return name;
    }
    public void updateDocumentArray() {
        DocumentReference RF = db.collection("User").document(user.getUid());
        RF.update("adminIN", FieldValue.arrayUnion("School/"+et_display_name.getText().toString()));
    }
    void welcomeUser()
    {
        Spanned welcome = Html.fromHtml("Welcome "+"<b>"+GV.currentUserName+"<b/>");
        tv_welcome_user.setText(welcome);
        /*getUser(UserId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                tv_welcome_user.setText("Welcome "+documentSnapshot.getString("displayName"));
            }
        });*/
    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    private void SetUpBottomAppBarMenu( )
    {
        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.miHome:
                        startActivity(new Intent(getApplicationContext(),Home.class));
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
                    case R.id.miDashboard:
                        startActivity(new Intent(getApplicationContext(),Dashboard.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return true;

            }


        });
    }
    void findViews()
    {
        bottomAppBar=findViewById(R.id.bnb);
        et_display_name =findViewById(R.id.et_display_name);
        et_full_name=findViewById(R.id.et_full_name);
        et_description=findViewById(R.id.et_description);
        et_location=findViewById(R.id.et_location);
        tv_welcome_user=findViewById(R.id.tv_welcome_user);
        ib_photo = findViewById(R.id.ib_photo);
        fab=findViewById(R.id.fab);
        tv_title=findViewById(R.id.tv_title);
    }
 void listenForIncommingMessages()
 {
     Bundle incommingMessages =getIntent().getExtras();
    NewSchool=incommingMessages.getBoolean("NewSchool",false);
    SchoolId=incommingMessages.getString("SchoolID","0");
 }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(AddSchool.this,Home.class);
        startActivity(intent);
        finish();
    }

}