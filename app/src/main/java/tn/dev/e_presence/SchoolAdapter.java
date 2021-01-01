package tn.dev.e_presence;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class SchoolAdapter extends FirestoreRecyclerAdapter<School,SchoolHolder> {
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

    @Override
    protected void onBindViewHolder(@NonNull SchoolHolder holder, int position, @NonNull School model) {
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

holder.ll_bg.setBackgroundColor(ColorList[position%ColorNumber]);
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

}
