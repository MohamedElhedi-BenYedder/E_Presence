package tn.dev.e_presence;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.Nullable;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static tn.dev.e_presence.GV.*;
import static tn.dev.e_presence.GV.createUser;

public class Welcome extends AppCompatActivity {
    private static final String TAG = "WelcomeActivity";
    int AUTHUI_REQUEST_CODE = 1208;
    private StorageReference mStorageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            GV.loadCurentUserInformations();
            startActivity(new Intent(this, Home.class));
            this.finish();
        }
    }

    public void handleLoginRegister(View view) {

        List<AuthUI.IdpConfig> providers;
        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                //new AuthUI.IdpConfig.MicrosoftBuilder().build(),//
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.AnonymousBuilder().build()

        );

        Intent intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .enableAnonymousUsersAutoUpgrade()
                .setTosAndPrivacyPolicyUrls("https://htmlpreview.github.io/?https://github.com/MohamedElhedi-BenYedder/E_Presence/blob/ChangingDesign/WebPage/terms_of_services.html", "https://htmlpreview.github.io/?https://github.com/MohamedElhedi-BenYedder/E_Presence/blob/ChangingDesign/WebPage/privacy_policy.html")
                .setLogo(R.drawable.ic_logo)
                .setAlwaysShowSignInMethodScreen(true)
                .enableAnonymousUsersAutoUpgrade()
                .setIsSmartLockEnabled(true)
                .setTheme(R.style.AppThemeFirebaseAuth)
                .build();

        startActivityForResult(intent, AUTHUI_REQUEST_CODE);
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTHUI_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // We have signed in the user or we have a new user
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.d(TAG, "onActivityResult: " + user.toString());
                //Checking for User (New/Old)
                if (user.getMetadata().getCreationTimestamp() == user.getMetadata().getLastSignInTimestamp()) {
                    //This is a New User
                    createUser(user.getUid(),user.getDisplayName(), user.getEmail(), user.getPhoneNumber(), (String)null, "0",  new ArrayList<String>(),  new ArrayList<String>(),  new ArrayList<String>(),new ArrayList<String>());
                    Toast.makeText(Welcome.this, user.getDisplayName(), Toast.LENGTH_SHORT).show();
                  /*  try{createUser(user.getUid(),user.getDisplayName(), user.getEmail(), user.getPhoneNumber(), "Male", uploadImageToFirebase( user.getPhotoUrl(),user.getUid()),  new ArrayList<String>(),  new ArrayList<String>(),  new ArrayList<String>());}
                    catch (Exception e){createUser(user.getUid(),user.getDisplayName(), user.getEmail(), user.getPhoneNumber(), "Male", user.getPhotoUrl().getPath(),  new ArrayList<String>(),  new ArrayList<String>(),  new ArrayList<String>());

                }*/} else {
                    //This is a returning user
                }
                GV.loadCurentUserInformations();

                Intent intent = new Intent(this, Home.class);
                startActivity(intent);
                this.finish();

            } else {
                // Signing in failed
                IdpResponse response = IdpResponse.fromResultIntent(data);
                if (response == null) {
                    Log.d(TAG, "onActivityResult: the user has cancelled the sign in request");
                } else {
                    Log.e(TAG, "onActivityResult: ", response.getError());
                }
            }
        }
    }
  /*  private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    private String uploadImageToFirebase( Uri contentUri,String id) {
        String name="User/"+id
                + "."+getFileExtension(contentUri);
        StorageReference image = mStorageRef.child(name);
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("tag", "onSuccess: Uploaded Image URl is " + uri.toString());

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Welcome.this, "Photo Upload Failled.", Toast.LENGTH_SHORT).show();
            }
        });
        return name;}*/

}
