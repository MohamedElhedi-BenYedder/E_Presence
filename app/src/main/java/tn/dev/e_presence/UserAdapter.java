package tn.dev.e_presence;

import android.annotation.SuppressLint;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class UserAdapter extends FirestoreRecyclerAdapter<User,UserAdapter.UserHolder> {
    private UserAdapter.OnItemClickListener listener;
    static StorageReference STORAGE_REFERENCE;
    private boolean Check;
    private String Key;
    private String Path;
    static int count = 0;
    final static int ColorList[] = {0, 1, 3};
    final static int ColorNumber = 3;
    final static int ImageList[] = {R.drawable.ic8_person_male, R.drawable.ic8_person_female, R.drawable.ic_person};
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
        Check=false;
    }
    public UserAdapter(@NonNull FirestoreRecyclerOptions<User> options, StorageReference s,boolean check,String key,String path) {
        super(options);
        STORAGE_REFERENCE = s;
        Check=check;
        Key=key;
        Path=path;
        if((path.split("/").length>3))Path=path.split("/")[3];
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onBindViewHolder(@NonNull UserHolder holder, int position, @NonNull User model) {
        holder.tv_email.setText(model.getEmail());
       try{ if (model.getGender().equals("Male"))
           holder.tv_display_name.setText("Mr "+model.getDisplayName());
        else if (model.getGender().equals("Female"))
           holder.tv_display_name.setText("Mrs "+model.getDisplayName());
        else
           holder.tv_display_name.setText(model.getDisplayName());}catch (Exception e){};

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
        });
        holder.ll_bg.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.rectengular_field2));
        if (Check)
        {
            if (exist(position))
            {
                holder.ll_bg.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.rectengular_field1));

            }

        }
    }
    public boolean exist(int position)
    {
        User model=getItem(position);
        if (Key.equals("studentIN"))
            return model.getStudentIN().contains(Path);
        else if (Key.equals("teacherIN"))
            return model.getTeacherIN().contains(Path);
        else return false;
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

        TextView tv_email;
        ImageView iv_photo;
        LinearLayout ll_bg;

        public UserHolder(@NonNull View oneUserItem) {
            super(oneUserItem);
            tv_display_name = oneUserItem.findViewById(R.id.tv_display_name);
            tv_email = oneUserItem.findViewById(R.id.tv_email);
            iv_photo = oneUserItem.findViewById(R.id.iv_photo);
            ll_bg = oneUserItem.findViewById(R.id.ll_bg);
            oneUserItem.setOnClickListener(new View.OnClickListener() {
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
        void onItemClick(DocumentSnapshot documentSnapshot, int pos);
    }
    public void setOnItemClickListener(UserAdapter.OnItemClickListener listener)
    {
        this.listener =listener;
    }
}
