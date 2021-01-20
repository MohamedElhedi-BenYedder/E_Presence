package tn.dev.e_presence;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static androidx.core.content.ContextCompat.startActivity;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class CourseAdapter extends FirestoreRecyclerAdapter<Course,CourseAdapter.CourseHolder> {


    private final static int ColorList[] = {0, 1, 2, 3, 5, 6, 7};
    private final static int ColorNumber = 8;
    private final static int ImageNumber = 0;
    private final static FirebaseFirestore db=FirebaseFirestore.getInstance();

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public CourseAdapter(@NonNull FirestoreRecyclerOptions<Course> options) {
        super(options);
    }
    @Override
    protected void onBindViewHolder(@NonNull CourseAdapter.CourseHolder holder, int position, @NonNull Course model) {
        holder.tv_course_name.setText(model.getDisplayName()); }

    @NonNull
    @Override
    public CourseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View oneCourseItem=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        return new CourseHolder(oneCourseItem);
    }
    public void delete(int position,String SchoolID){
        String deletedCourseId=getSnapshots().getSnapshot(position).getId();
        DocumentReference courseRef= db.collection("School")
                .document(SchoolID).collection("Course").document(deletedCourseId);
        courseRef.delete();

    }
    @SuppressLint("RestrictedApi")
    public Intent edit(int position){
        String editedCourseId=getSnapshots().getSnapshot(position).getId();
        Intent intent= new Intent(getApplicationContext(),AddCourse.class).
                putExtra("CourseID",editedCourseId);
        return intent;
    }

    /***************Course Holder Class*********************/
    public class CourseHolder extends RecyclerView.ViewHolder {
        TextView tv_course_name;
        LinearLayout ll_bg;
        public CourseHolder(@NonNull View oneCourseItem) {
            super(oneCourseItem);
            tv_course_name = oneCourseItem.findViewById(R.id.tv_course_name);
            ll_bg = oneCourseItem.findViewById(R.id.ll_bg);


        }

    }

}
