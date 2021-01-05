package tn.dev.e_presence;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.jar.Attributes;

import de.hdodenhof.circleimageview.CircleImageView;
public class Profile extends AppCompatActivity {
    TextView nom,prenom,email,tel;
    ImageView profilpic;
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    Button ChangeprofilImage;
    StorageReference storageReference;
    private BottomAppBar bottomAppBar;
    private BottomNavigationView bottomNavigationVie ;
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        SetUpBottomAppBarMenu();
        nom = findViewById(R.id.Name);
        prenom = findViewById(R.id.prenom);
        email = findViewById(R.id.email);
       fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        profilpic = findViewById(R.id.profilpic);
        ChangeprofilImage = findViewById(R.id.modifierprofil);

        ChangeprofilImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent,1000);

           }
        });





     };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==1000){
            Uri ImageUri = data.getData();
            profilpic.setImageURI(ImageUri);
            uploadImageTofirebase(ImageUri);

            };
        }




                @RequiresApi(api = Build.VERSION_CODES.O)
                private void SetUpBottomAppBarMenu () {

                    bottomAppBar = findViewById(R.id.bnb);
                    bottomAppBar.getMenu().getItem(2).setIconTintList(getColorStateList(R.color.c2));
                    bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.miDashboard:
                                    startActivity(new Intent(getApplicationContext(), Dashboard.class));
                                    overridePendingTransition(0, 0);
                                    return true;
                                case R.id.miHome:
                                    startActivity(new Intent(getApplicationContext(), Home.class));
                                    overridePendingTransition(0, 0);
                                    return true;
                                case R.id.miSettings:
                                    startActivity(new Intent(getApplicationContext(), Settings.class));
                                    overridePendingTransition(0, 0);
                                    return true;

                            }
                            return true;
                        }


                    });
                }
    private void uploadImageTofirebase (Uri ImageUri){
        StorageReference fileRef =storageReference.child("profil.png");
        fileRef.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(Profile.this, "l'image est mise à jour", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Profile.this, "Erreur", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
