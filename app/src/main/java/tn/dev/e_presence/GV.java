package tn.dev.e_presence;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class GV {

    //----COLLECTIONS
    private static final String SCHOOL_COLLECTION = "School";
    private static final String USER_COLLECTION = "User";
    //------------------User--------------------//
    // ---Get USER COLLECTION REFERENCE ---
    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(USER_COLLECTION);
    }
    // --- CREATE USER---

    public static Task<Void> createUser(String uid,String displayName, String email, String phoneNumber, String gender, String photo,
                                        List<String> adminIN, List<String> studentIN, List<String> teacherIN, List<String> allSessions) {
        User userToCreate = new User(displayName, email, phoneNumber, gender, photo,  adminIN,studentIN,  teacherIN,allSessions);
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


}
