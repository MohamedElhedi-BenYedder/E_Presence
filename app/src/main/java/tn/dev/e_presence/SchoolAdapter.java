package tn.dev.e_presence;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class SchoolAdapter extends FirestoreRecyclerAdapter<School,SchoolHolder> {

    final static int ColorList[]={0,1,3};
    final static int ColorNumber=3;


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public SchoolAdapter(@NonNull FirestoreRecyclerOptions<School> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull SchoolHolder holder, int position, @NonNull School model) {
        holder.tv_display_name.setText(model.getDisplayName());
        holder.tv_location.setText(model.getLocation());
        holder.iv_photo.setImageURI(Uri.parse(model.getPhoto()));
        holder.ll_bg.setBackgroundColor(ColorList[position%ColorNumber]);
    }

    @NonNull
    @Override
    public SchoolHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View oneSchoolItem=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_school, parent, false);
        return new SchoolHolder(oneSchoolItem);
    }



}
