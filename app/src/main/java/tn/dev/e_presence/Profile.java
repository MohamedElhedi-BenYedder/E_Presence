package tn.dev.e_presence;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

public class Profile extends AppCompatActivity {
    private BottomAppBar bottomAppBar;
    private BottomNavigationView bottomNavigationView;
    private StorageReference mStorageRef= FirebaseStorage.getInstance().getReference();
    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    private CollectionReference UserRef =db.collection("User");
    private FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
    private TextView btn_cancel;
    TextView name,mail,phone,tv_welcome_user,gender;
    private String UserId= user.getUid();
    private CircleImageView cv_photo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        findViews();
        loadCurentUserInformations();
        // setUpBottomAppBarMenu();

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Profile.this,Home.class);
                startActivity(intent);
                finish();
            }
        });






    }

    void findViews()
    {
        gender=findViewById(R.id.tv_gender);
        tv_welcome_user=findViewById(R.id.tv_welcome_user);
        name=findViewById(R.id.name);
        phone=findViewById(R.id.phone);
        mail=findViewById(R.id.mail);
        cv_photo=findViewById(R.id.cv_photo);
        btn_cancel=findViewById(R.id.back_btn);
    }
    public void loadCurentUserInformations()
    {
        db.collection("User").document(UserId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot userDoc) {
                        tv_welcome_user.setText("Hello "+ userDoc.getString("displayName"));
                        name.setText(userDoc.getString("displayName"));
                        phone.setText(userDoc.getString("phoneNumber"));
                        mail.setText(userDoc.getString("email"));
                        gender.setText(userDoc.getString("gender"));
                        String photo=userDoc.getString("photo");
                        LoadPhoto(photo);
                        }
                });


    }
    void LoadPhoto(String photo)
    {

        /*  try    { */    StorageReference image = mStorageRef.child(photo);
        image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(cv_photo);

            }
        });
      /*  }
        catch (Exception e){};*/
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setUpBottomAppBarMenu()
    {
        //find id
       // bottomAppBar=findViewById(R.id.)

        // bottomAppBar.getMenu().getItem(0).setIconTintList(getColorStateList(R.color.c2));

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



}