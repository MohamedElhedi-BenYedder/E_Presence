package tn.dev.e_presence;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.text.Line;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import static androidx.core.content.ContextCompat.startActivity;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class SchoolAdapter extends FirestoreRecyclerAdapter<School,SchoolAdapter.SchoolHolder>  {
    private OnItemClickListener listener;
    static StorageReference STORAGE_REFERENCE;
    static int count=0;
    final static int ColorList[]={0,1,3};
    final static int ColorNumber=3;
    final static int ImageList[]={R.drawable.ic_school,R.drawable.ic_school1,R.drawable.ic_school2};
    final static int ImageNumber=3;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public SchoolAdapter(@NonNull FirestoreRecyclerOptions<School> options,StorageReference s) {
        super(options);
        STORAGE_REFERENCE=s;
    }
    public boolean isAdmin(int position, String UserId)
    {
        return ((List<String>)(getSnapshots().getSnapshot(position).get("Admins"))).contains(UserId);
    }
    public void deleteItem(int position, String UserId, FirebaseFirestore db) {
            //delete School
            getSnapshots().getSnapshot(position).getReference().delete();
            //remove School from adminIN Array field in User Document
            DocumentReference UserRef = db.collection("User").document(UserId);
            UserRef.update("adminIN", FieldValue.arrayRemove(getSnapshots().getSnapshot(position).getId())); }
    @SuppressLint("RestrictedApi")
    public Intent editItem(int position, String UserId)
    {
        Intent intent=new Intent(getApplicationContext(),AddSchool.class)
                .putExtra("SchoolID",getSnapshots().getSnapshot(position).getId());
        return intent;

    }


    @Override
    protected void onBindViewHolder(@NonNull SchoolHolder holder, int position, @NonNull School model) {
        model.setSid(this.getSnapshots().getSnapshot(position).getId());
        holder.tv_display_name.setText(model.getDisplayName());
        holder.tv_location.setText(model.getLocation());

try    {     StorageReference image = STORAGE_REFERENCE.child(model.getPhoto());
    image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
        @Override
        public void onSuccess(Uri uri) {
            Log.d("tag", "onSuccess: Uploaded Image URl is " + uri.toString());
            Picasso.get().load(uri).into(holder.iv_photo);

        }
    });
}
catch (Exception e){count=count%ImageNumber;holder.iv_photo.setImageResource(ImageList[count]);count++;};

//holder.ll_bg.setBackgroundColor(ColorList[position%ColorNumber]);
    }

    @NonNull
    @Override
    public SchoolHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View oneSchoolItem=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_school, parent, false);
        return new SchoolHolder(oneSchoolItem);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();

    }


    /***************School Holder Class*********************/
    public class SchoolHolder extends RecyclerView.ViewHolder {
        TextView tv_display_name;
        TextView tv_location;
        ImageView iv_photo ;
        LinearLayout ll_bg ;
        LinearLayout ll_;


        public SchoolHolder(@NonNull View oneSchoolItem) {
            super(oneSchoolItem);
            tv_display_name= oneSchoolItem.findViewById(R.id.tv_display_name);
            tv_location = oneSchoolItem.findViewById(R.id.tv_location);
            iv_photo = oneSchoolItem.findViewById(R.id.iv_photo);
            ll_bg =oneSchoolItem.findViewById(R.id.ll_bg);
            ll_=oneSchoolItem.findViewById(R.id.ll_);
            ll_.setOnClickListener(new View.OnClickListener() {
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
    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.listener =listener;
    }

}
