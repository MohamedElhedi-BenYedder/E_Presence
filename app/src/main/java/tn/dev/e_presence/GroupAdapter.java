package tn.dev.e_presence;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class GroupAdapter extends FirestoreRecyclerAdapter<Group,GroupAdapter.GroupHolder> {
    private SchoolAdapter.OnItemClickListener listener;
    static StorageReference STORAGE_REFERENCE;
    final static int ColorList[]={0,1,3};
    final static int ColorNumber=3;
    final static int ImageList[]={};
    final static int ImageNumber=0;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public GroupAdapter(@NonNull FirestoreRecyclerOptions<Group> options, StorageReference s) {
        super(options);
        STORAGE_REFERENCE=s;
    }
    @Override
    protected void onBindViewHolder(@NonNull GroupAdapter.GroupHolder holder, int position, @NonNull Group model) {


        holder.tv_display_name.setText(model.getDisplayName());
        holder.tv_student_number.setText(model.getStudentList().size());
        holder.iv_level.setImageResource(ImageList[model.getLevel()-1]);
        holder.ll_bg.setBackgroundColor(ColorList[model.getLevel()-1]);
    }

    @NonNull
    @Override
    public GroupAdapter.GroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View oneGroupItem=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_school, parent, false);
        return new GroupAdapter.GroupHolder(oneGroupItem);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();

    }

    /***************Group Holder Class*********************/
    public class GroupHolder extends RecyclerView.ViewHolder {
        TextView tv_display_name;
        TextView tv_student_number;
        ImageView iv_level;
        LinearLayout ll_bg;


        public GroupHolder(@NonNull View oneGroupItem) {
            super(oneGroupItem);
            tv_display_name = oneGroupItem.findViewById(R.id.tv_display_name);
            tv_student_number = oneGroupItem.findViewById(R.id.tv_student_number);
            iv_level = oneGroupItem.findViewById(R.id.iv_level);
            ll_bg = oneGroupItem.findViewById(R.id.ll_bg);

        }

    }

    }


