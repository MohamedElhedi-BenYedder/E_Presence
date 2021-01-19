package tn.dev.e_presence;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static tn.dev.e_presence.GV.currentUserPhotoPath;

public class EditProfile extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner gender;
    private TextView btn_ok;
    private StorageReference mStorageRef= FirebaseStorage.getInstance().getReference();
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String UserId= user.getUid();
    String photo=currentUserPhotoPath;
    private static Uri imageUri;
    private TextView tv_welcome_user;
    private EditText name,phone,mail;
    private CircleImageView cv_photo;
    private String selected_gender;
    private String cur_name,cur_gender,cur_phone,cur_mail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        findViews();
        setGenderSpinner();
        loadCurentUserInformations();
        selectPhotoFromGalery();
        saveUpdatedInformations();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selected_gender=parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    void findViews()
    {
        btn_ok=findViewById(R.id.save_btn);
        gender=findViewById(R.id.gender);
        tv_welcome_user=findViewById(R.id.tv_welcome_user);
        name=findViewById(R.id.name);
        phone=findViewById(R.id.phone);
        mail=findViewById(R.id.mail);
        cv_photo=findViewById(R.id.cv_photo);
    }
    public void loadCurentUserInformations()
    {
        tv_welcome_user.setText("Hello "+GV.currentUserName );
        name.setText(GV.currentUserName);
        phone.setText(GV.currentUserPhoneNumber);
        mail.setText(GV.currentUserMail);
        if ((GV.currentUserGender).equals("Male")) gender.setSelection(0);
        else if ((GV.currentUserGender).equals("Female")) gender.setSelection(1);
        selected_gender= GV.currentUserGender;
        if(!GV.currentUserPhoto.equals("0"))
        Picasso.get().load(GV.currentUserPhoto).into(cv_photo);
    }

    void selectPhotoFromGalery()
    {
        cv_photo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Open Galery
                Intent OpenGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(OpenGalleryIntent,100);
            }

        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100)
        {
            if(resultCode== Activity.RESULT_OK)
            {
                imageUri =data.getData();


            }
        }
    }
    private String uploadImageToFirebase( Uri contentUri) {
        String name;
        if (currentUserPhotoPath.equals("0"))
            name="User/"+System.currentTimeMillis();
        else name=currentUserPhotoPath;
        StorageReference image = mStorageRef.child(name);
      try{ image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("tag", "onSuccess: Uploaded Image URl is " + uri.toString());
                        Picasso.get().load(uri).into(cv_photo);
                    }
                });

                Toast.makeText(EditProfile.this, "Image Is Uploaded.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfile.this, "Upload Failled.", Toast.LENGTH_SHORT).show();
            }
        });}catch (Exception e){}
        return name;
    }
    void setGenderSpinner()
    {
        List<String> groupestatique= new ArrayList<String>();
        groupestatique.add("Male");
        groupestatique.add("Female");

        ArrayAdapter<String> group_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,groupestatique);
        group_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(group_adapter);
        gender.setOnItemSelectedListener(this);
    }
    void saveUpdatedInformations()
    {
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> userUpdatedInformation=new HashMap<String,Object>();
                GV.currentUserName=name.getText().toString();
                GV.currentUserPhoneNumber=phone.getText().toString();
                GV.currentUserMail=mail.getText().toString();
                GV.currentUserGender=selected_gender;
                if(imageUri!=null)
                {
                    currentUserPhotoPath=uploadImageToFirebase(imageUri);
                    userUpdatedInformation.put("photo",currentUserPhotoPath);
                    GV.currentUserPhoto=imageUri;
                }

                userUpdatedInformation.put("displayName",GV.currentUserName);
                userUpdatedInformation.put("email",GV.currentUserMail);
                userUpdatedInformation.put("phoneNumber",GV.currentUserPhoneNumber);
                userUpdatedInformation.put("gender",GV.currentUserGender);

                db.collection("User").document(UserId)
                        .update(userUpdatedInformation);
                Intent intent=new Intent(EditProfile.this,Home.class);
                startActivity(intent);
                finish();
            }

        });
    }


}