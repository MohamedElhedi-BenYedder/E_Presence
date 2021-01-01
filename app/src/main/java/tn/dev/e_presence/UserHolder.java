package tn.dev.e_presence;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserHolder extends RecyclerView.ViewHolder {
    TextView tv_display_name;
    TextView tv_gender;
    TextView tv_email ;
    ImageView iv_photo ;
    LinearLayout ll_bg ;

    public UserHolder(@NonNull View oneUserItem) {
        super(oneUserItem);
        tv_display_name=oneUserItem.findViewById(R.id.tv_display_name);
         tv_gender=oneUserItem.findViewById(R.id.tv_gender);
         tv_email =oneUserItem.findViewById(R.id.tv_email);
         iv_photo =oneUserItem.findViewById(R.id.iv_photo);
         ll_bg =oneUserItem.findViewById(R.id.ll_bg);
    }
}


