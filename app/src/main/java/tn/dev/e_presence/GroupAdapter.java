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

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class GroupAdapter extends FirestoreRecyclerAdapter<Group,GroupAdapter.GroupHolder> {
    private OnItemClickListener listener;

    final static int ColorList[]={0,1,2,3,5,6,7};
    final static int ColorNumber=8;
    final static int ImageList[]={R.drawable.ic8_1,R.drawable.ic8_2,R.drawable.ic8_3,R.drawable.ic8_4,R.drawable.ic8_5,R.drawable.ic8_6,R.drawable.ic_group};
    final static int ImageNumber=0;
    private final static FirebaseFirestore db=FirebaseFirestore.getInstance();
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public GroupAdapter(@NonNull FirestoreRecyclerOptions<Group> options) {
        super(options);
    }
    @Override
    protected void onBindViewHolder(@NonNull GroupAdapter.GroupHolder holder, int position, @NonNull Group model) {


        holder.tv_display_name.setText(model.getDisplayName());
        holder.tv_student_number.setText("Count"+model.getNum());
       try{ switch(model.getLevel()) {
            case "Level1":
                holder.iv_level.setImageResource(ImageList[0]);
                holder.ll_bg.setBackgroundColor(ColorList[0]);
                break;
            case "Level2":
                holder.iv_level.setImageResource(ImageList[1]);
                holder.ll_bg.setBackgroundColor(ColorList[1]);
                break;
            case "Level3":
                holder.iv_level.setImageResource(ImageList[2]);
                holder.ll_bg.setBackgroundColor(ColorList[2]);
                break;
            case "Level4":
                holder.iv_level.setImageResource(ImageList[3]);
                holder.ll_bg.setBackgroundColor(ColorList[3]);
                break;
            case "Level5":
                holder.iv_level.setImageResource(ImageList[4]);
                holder.ll_bg.setBackgroundColor(ColorList[4]);
                break;
            case "Level6":
                holder.iv_level.setImageResource(ImageList[5]);
                holder.ll_bg.setBackgroundColor(ColorList[5]);
                break;
            case "Club":
                holder.iv_level.setImageResource(ImageList[6]);
                holder.ll_bg.setBackgroundColor(ColorList[6]);
                break;


        }}catch(Exception e){
           Log.e("error",e.toString()+"|"+e.getMessage());
       }


    }

    @NonNull
    @Override
    public GroupAdapter.GroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View oneGroupItem=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
        return new GroupAdapter.GroupHolder(oneGroupItem);
    }

    public void delete(int position,String SchoolID){
        String deletedGroupId=getSnapshots().getSnapshot(position).getId();
        DocumentReference groupRef= db.collection("School")
                .document(SchoolID).collection("Group").document(deletedGroupId);
        groupRef.delete();

    }
    @SuppressLint("RestrictedApi")
    public Intent edit(int position){
        String editedGroupId=getSnapshots().getSnapshot(position).getId();
        Intent intent= new Intent(getApplicationContext(),AddGroup.class).
                putExtra("GroupID",editedGroupId);
        return intent;
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
            oneGroupItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int pos=getAdapterPosition();
                    if(pos!=RecyclerView.NO_POSITION && listener!=null)
                    {
                        listener.onItemClick(getSnapshots().getSnapshot(pos),pos);
                    }
                }
            });

        }

    }
    public interface OnItemClickListener
    {
        void onItemClick(DocumentSnapshot documentSnapshot,int pos);
    }
    public void setOnItemClickListener(GroupAdapter.OnItemClickListener listener)
    {
        this.listener =listener;
    }

}


