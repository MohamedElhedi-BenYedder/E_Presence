package tn.dev.e_presence;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class UserAdapter extends FirestoreRecyclerAdapter<User,UserHolder> {
    static StorageReference STORAGE_REFERENCE;
    static int count=0;
    final static int ColorList[]={0,1,3};
    final static int ColorNumber=3;
    final static int ImageList[]={R.drawable.ic_male,R.drawable.ic_female,R.drawable.ic_person};
    final static int ImageNumber=2;


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public UserAdapter(@NonNull FirestoreRecyclerOptions<User> options,StorageReference s) {
        super(options);
        STORAGE_REFERENCE=s;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserHolder holder, int position, @NonNull User model) {
        holder.tv_display_name.setText(model.getDisplayName());
        holder.tv_email.setText(model.getEmail());
        holder.tv_gender.setText(model.getGender());

        try    {     StorageReference image = STORAGE_REFERENCE.child(model.getPhoto());
            image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Log.d("tag", "onSuccess: Uploaded Image URl is " + uri.toString());
                    Picasso.get().load(uri).into(holder.iv_photo);

                }
            });
        }
        catch (Exception e){if (model .getGender()=="Male") holder.iv_photo.setImageResource(ImageList[0]);
        else if (model .getGender()=="female") holder.iv_photo.setImageResource(ImageList[1]);
        else holder.iv_photo.setImageResource(ImageList[2]);};

        holder.ll_bg.setBackgroundColor(ColorList[position%ColorNumber]);
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View oneUserItem=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserHolder(oneUserItem);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();

    }

}
