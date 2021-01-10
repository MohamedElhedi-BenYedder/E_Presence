package tn.dev.e_presence;

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
import com.google.firebase.firestore.DocumentSnapshot;

public class CourseAdapter extends FirestoreRecyclerAdapter<Course,CourseAdapter.CourseHolder> {


    final static int ColorList[] = {0, 1, 2, 3, 5, 6, 7};
    final static int ColorNumber = 8;
    final static int ImageNumber = 0;

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
