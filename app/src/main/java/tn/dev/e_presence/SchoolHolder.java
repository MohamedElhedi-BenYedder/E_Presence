package tn.dev.e_presence;



import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SchoolHolder extends RecyclerView.ViewHolder {
    TextView tv_display_name;
    TextView tv_location;
    ImageView iv_photo ;
    LinearLayout ll_bg ;


    public SchoolHolder(@NonNull View oneSchoolItem) {
        super(oneSchoolItem);
        tv_display_name= oneSchoolItem.findViewById(R.id.tv_display_name);
        tv_location = oneSchoolItem.findViewById(R.id.tv_location);
        iv_photo = oneSchoolItem.findViewById(R.id.iv_photo);
        ll_bg =oneSchoolItem.findViewById(R.id.ll_bg);
    }
}