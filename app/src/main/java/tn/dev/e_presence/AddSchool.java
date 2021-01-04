package tn.dev.e_presence;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
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
    private StorageReference mStorageRef;
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Nullable private User modelCurrentUser = new User();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText et_display_name;
    EditText et_full_name;
    EditText et_description;
    EditText et_location;
    TextView tv_welcome_user;
    ImageButton ib_photo ;
    FloatingActionButton fab;
    String photo="";
    String TAG= AddSchool.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_school);
        //Getting all views needed
        Bundle incommingMessages =getIntent().getExtras();
         et_display_name =findViewById(R.id.et_display_name);
         et_full_name=findViewById(R.id.et_full_name);
         et_description=findViewById(R.id.et_description);
         et_location=findViewById(R.id.et_location);
         tv_welcome_user=findViewById(R.id.tv_welcome_user);

         ib_photo = findViewById(R.id.ib_photo);
         fab=findViewById(R.id.fab);

         this.getCurrentUserFromFirestore();

        ib_photo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Open Galery
                Intent OpenGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(OpenGalleryIntent,100);
            }

        });

        mStorageRef = FirebaseStorage.getInstance().getReference();
        String UserId=user.getUid();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> school = new HashMap<>();
                String displayName=et_display_name.getText().toString();
                String location =et_location.getText().toString();
                String fullName=et_full_name.getText().toString();
                String Description=et_description.getText().toString();
                school.put("DisplayName",displayName);
                school.put("FullName",fullName);
                school.put("Description",Description);
                school.put("Location",location);
                if(!photo.isEmpty())school.put("Photo",photo);
                List<String> admins =new ArrayList<>();
                admins.add(user.getUid());
                school.put("Admins",admins);
                if ((incommingMessages.getBoolean("first"))&&(!displayName.isEmpty())&&(displayName.length()>6)&&(fullName.length()>6)&&(Description.length()>6)&&(location.length()>6))
                // the user is creating a new school
               {db.document("School/"+et_display_name.getText().toString())
                   .get()
                       .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                           @Override
                           public void onSuccess(DocumentSnapshot documentSnapshot) {
                               if (documentSnapshot.exists()) {
                                   Toast.makeText(AddSchool.this, "Choose an other Display Name", Toast.LENGTH_SHORT).show();

                               } else {
                                   db.document("School/"+et_display_name.getText().toString())
                                           .set(school);
                                   updateDocumentArray();

                                   Intent intent = new Intent(AddSchool.this,SchoolPage.class);
                                   intent.putExtra("first",true);
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



                   // Add a new document with a generated ID



               // db.document("School/"+et_display_name.getText().toString()).set(school);
                  // }
               //catch (Exception e) { }
                            }
                else
                {

                }
                        }});}


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
        // [START update_document_array]
        DocumentReference RF = db.collection("User").document(user.getUid());

        // Atomically add a new region to the "regions" array field.
        RF.update("adminIN", FieldValue.arrayUnion("School/"+et_display_name.getText().toString()));

        // Atomically remove a region from the "regions" array field.
        //RF.update("AdminIN", FieldValue.arrayRemove("east_coast"));
        // [END update_document_array]
    }
    private void getCurrentUserFromFirestore(){
        getUser(user.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                modelCurrentUser = documentSnapshot.toObject(User.class);
                //tv_welcome_user.setText("Welcome "+modelCurrentUser.getDisplayName());
            }
        });
    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


}