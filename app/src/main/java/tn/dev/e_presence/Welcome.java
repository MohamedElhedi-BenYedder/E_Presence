package tn.dev.e_presence;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.Nullable;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Welcome extends AppCompatActivity {
    private static final String TAG = "WelcomeActivity";
    int AUTHUI_REQUEST_CODE = 1208;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(this, Home.class));
            this.finish();
        }
    }

    public void handleLoginRegister(View view) {

        List<AuthUI.IdpConfig> providers;
        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.MicrosoftBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build()

        );

        Intent intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTosAndPrivacyPolicyUrls("https://example.com", "https://example.com")
                .setLogo(R.drawable.ic_logo)
                .setAlwaysShowSignInMethodScreen(true)
                .setIsSmartLockEnabled(false)
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
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    String UserId=user.getUid();
                    Map<String, Object> RegDoc = new HashMap<>();
                    // Create a new user with a first and last name
                    RegDoc.put("DisplayName", user.getDisplayName());
                    RegDoc.put("Email", user.getEmail());
                    RegDoc.put("PhoneNumber", user.getPhoneNumber());
                    RegDoc.put("Photo", "School/"+user.getUid());
                    RegDoc.put("Gender", "Male");
                    RegDoc.put("AdminIN", new ArrayList<String>());
                    RegDoc.put("StudentIN", new ArrayList<String>());
                    RegDoc.put("TeacherIN", new ArrayList<String>());
                    // Add a new document with a generated ID
                    db.collection("User").document(user.getUid())
                            .set(RegDoc);

                } else {
                    //This is a returning user
                }
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

}