package tn.dev.e_presence;
import android.annotation.SuppressLint;
import android.graphics.Color;
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
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class SessionAdapter extends FirestoreRecyclerAdapter<Session,SessionAdapter.SessionHolder> {
    private OnItemClickListener listener;
    private boolean Clickable=true;
    private static String UserId;
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
    public SessionAdapter(@NonNull FirestoreRecyclerOptions<Session> options,String userId) {
        super(options);
        this.UserId=userId;
    }
    public SessionAdapter(@NonNull FirestoreRecyclerOptions<Session> options, StorageReference s) {
        super(options);
        STORAGE_REFERENCE = s;
    }

    public boolean isClickable() {
        return Clickable;
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onBindViewHolder(@NonNull SessionHolder holder, int position, @NonNull Session model) {
        Clickable=true;
        holder.tv_start.setText(model.getStart());
        holder.tv_end.setText(model.getEnd());
        holder.tv_classroom.setText(model.getClassroom());
        holder.tv_teacher.setText(model.getTeacher());
        holder.tv_subject.setText(model.getSubject());
        holder.tv_group.setText(model.getGroup());
        if (model.isPresential()) holder.tv_presential.setText("Yes");
        else holder.tv_presential.setText("No");
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        //(model.getDate().isEmpty()||model.getStart().isEmpty()||model.getEnd().isEmpty() )){

        if (!(model.getEnd().compareTo(currentTime)>0 && model.getStart().compareTo(currentTime)<0 && model.getDate().equals(currentDate) )){


            Clickable=false;
        }
        if(!model.isPresential()){
            Clickable=false;
        }

        if(model.getListOfPresence().contains(UserId)){
            holder.ll.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.rectengular_field_scanned));
            Clickable=false;
        }

    }

    @NonNull
    @Override
    public SessionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View oneSessionItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_session, parent, false);
        return new SessionHolder(oneSessionItem);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();

    }
    /*-------------Session Holder Class----------*/
    public class SessionHolder extends RecyclerView.ViewHolder {
        TextView tv_start;
        TextView tv_end;
        TextView tv_classroom;
        TextView tv_teacher;
        TextView tv_subject;
        TextView tv_group;
        TextView tv_presential;
        LinearLayout ll ;
        ImageView iv_qrc;

        public SessionHolder(@NonNull View oneSessionItem) {
            super(oneSessionItem);
             tv_start=oneSessionItem.findViewById(R.id.tv_start);
             tv_end=oneSessionItem.findViewById(R.id.tv_end);
             tv_classroom=oneSessionItem.findViewById(R.id.tv_classroom);
             tv_teacher=oneSessionItem.findViewById(R.id.tv_teacher);
             tv_subject=oneSessionItem.findViewById(R.id.tv_subject);
             tv_group=oneSessionItem.findViewById(R.id.tv_group);
             tv_presential=oneSessionItem.findViewById(R.id.tv_presential);
             iv_qrc=oneSessionItem.findViewById(R.id.iv_qrc);
             ll =oneSessionItem.findViewById(R.id.ll);

             iv_qrc.setOnClickListener(new View.OnClickListener() {
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
    public void setOnItemClickListener(SessionAdapter.OnItemClickListener listener)
    {
        this.listener =listener;
    }
}