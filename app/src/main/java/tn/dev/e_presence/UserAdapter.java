package tn.dev.e_presence;

import android.net.Uri;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class UserAdapter extends FirestoreRecyclerAdapter<User,UserAdapter.UserHolder> {
    static StorageReference STORAGE_REFERENCE;
    static int count = 0;
    final static int ColorList[] = {0, 1, 3};
    final static int ColorNumber = 3;
    final static int ImageList[] = {R.drawable.ic_male, R.drawable.ic_female, R.drawable.ic_person};
    final static int ImageNumber = 3;


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public UserAdapter(@NonNull FirestoreRecyclerOptions<User> options, StorageReference s) {
        super(options);
        STORAGE_REFERENCE = s;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserHolder holder, int position, @NonNull User model) {
        holder.tv_display_name.setText(model.getDisplayName());
        holder.tv_email.setText(model.getEmail());
       try{ if (model.getGender().equals("Male"))
            holder.tv_gender.setText("Mr");
        else if (model.getGender().equals("Female"))
            holder.tv_gender.setText("Mrs");
        else
            holder.tv_gender.setText("");}catch (Exception e){};

        StorageReference image = STORAGE_REFERENCE.child(model.getPhoto());
        image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d("tag", "onSuccess: Uploaded Image URl is " + uri.toString());
                Picasso.get().load(uri).into(holder.iv_photo);

            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
               try{ if (model.getGender().equals("Male"))
                    holder.iv_photo.setImageResource(ImageList[0]);
                else if (model.getGender().equals("Female"))
                    holder.iv_photo.setImageResource(ImageList[1]);}
               catch(Exception e1){}
            }
        })
        ;

        holder.ll_bg.setBackgroundColor(ColorList[position % ColorNumber]);
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View oneUserItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserHolder(oneUserItem);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();

    }

    public class UserHolder extends RecyclerView.ViewHolder {
        TextView tv_display_name;
        TextView tv_gender;
        TextView tv_email;
        ImageView iv_photo;
        LinearLayout ll_bg;

        public UserHolder(@NonNull View oneUserItem) {
            super(oneUserItem);
            tv_display_name = oneUserItem.findViewById(R.id.tv_display_name);
            tv_gender = oneUserItem.findViewById(R.id.tv_gender);
            tv_email = oneUserItem.findViewById(R.id.tv_email);
            iv_photo = oneUserItem.findViewById(R.id.iv_photo);
            ll_bg = oneUserItem.findViewById(R.id.ll_bg);
        }

    }
}
