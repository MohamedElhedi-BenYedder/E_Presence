package tn.dev.e_presence;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;
import static java.lang.Thread.sleep;

public class GV {

    //----COLLECTIONS
    private static final String SCHOOL_COLLECTION = "School";
    private static final String USER_COLLECTION = "User";
    private static final FirebaseFirestore db=FirebaseFirestore.getInstance();
    private static final StorageReference mStorageRef= FirebaseStorage.getInstance().getReference();
    //-----------------Current User-------------//
    public static String currentUserName;
    public static String currentUserGender;
    public static String currentUserMail;
    public static String currentUserPhoneNumber;
    public static Uri currentUserPhoto;
    public static String currentUserPhotoPath;
    public static String currentUserToken;
    //-------------------visited user----------
    public static String visitedUserName;
    public static String visitedUserGender;
    public static String visitedUserMail;
    public static String visitedUserPhoneNumber;
    public static Uri visitedUserPhoto;
    public static String visitedUserPhotoPath;

    public GV() {
    }


    public static void loadCurentUserInformations() {
        if (!FirebaseAuth.getInstance().getCurrentUser().isAnonymous())
        {
            db.collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot userDoc) {
                            currentUserName = userDoc.getString("displayName");
                            currentUserGender = userDoc.getString("gender");
                            currentUserMail = userDoc.getString("email");
                            currentUserPhoneNumber = userDoc.getString("phoneNumber");
                            currentUserPhotoPath = userDoc.getString("photo");

                            try {
                                StorageReference image = mStorageRef.child(currentUserPhotoPath);
                                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        currentUserPhoto = uri;


                                    }
                                });
                            } catch (Exception e) {
                            }
                        }
                    });
        }else GV.currentUserName="Anonymous";
    }
    //------------------User--------------------//
    // ---Get USER COLLECTION REFERENCE ---
    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(USER_COLLECTION);
    }
    // --- CREATE USER---

    public static Task<Void> createUser(String uid,String displayName, String email, String phoneNumber, String gender, String photo,
                                        List<String> adminIN, List<String> studentIN, List<String> teacherIN, List<String> allSessions) {
        User userToCreate = new User(uid,displayName, email, phoneNumber, gender, photo,  adminIN,studentIN,  teacherIN,allSessions);
        return GV.getUsersCollection().document(uid).set(userToCreate);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getUser(String uid){
        return GV.getUsersCollection().document(uid).get();
    }

    // --- UPDATE ---

    public static Task<Void> updateUsername(String username, String uid) {
        return GV.getUsersCollection().document(uid).update("username", username);
    }

    public static Task<Void> updateIsMentor(String uid, Boolean isMentor) {
        return GV.getUsersCollection().document(uid).update("isMentor", isMentor);
    }

    // --- DELETE ---

    public static Task<Void> deleteUser(String uid) {
        return GV.getUsersCollection().document(uid).delete();
    }
    //-------------School-----------------//
    // ---Get USER COLLECTION REFERENCE ---
    public static CollectionReference getSchoolsCollection(){
        return FirebaseFirestore.getInstance().collection(SCHOOL_COLLECTION);}
    // --- GET School---

    public static Task<DocumentSnapshot> getSchool(String uid){
        return GV.getSchoolsCollection().document(uid).get();}

    public static void Wait(int wait)
    {
        try {
            sleep(wait);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
