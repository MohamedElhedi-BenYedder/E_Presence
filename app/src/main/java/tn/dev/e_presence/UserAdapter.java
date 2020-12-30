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

public class UserAdapter extends BaseAdapter {
    Activity mActivity;
    ListofUsers UserList;
    final static int[] ColorList={0,1,3};
    final static int ColorNumber=3;

    public UserAdapter(Activity mActivity, ListofUsers userList) {
        this.mActivity = mActivity;
        UserList = userList;
    }

    @Override
    public int getCount() {return UserList.getUserList().size(); }

    @Override
    public User getItem(int position) {return UserList.getUserList().get(position); }

    @Override
    public long getItemId(int position) { return 0; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View oneUserItem;
        LayoutInflater inflaytor= (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        oneUserItem = inflaytor.inflate(R.layout.item_user,parent,false);
        TextView tv_display_name=oneUserItem.findViewById(R.id.tv_display_name);
        TextView tv_gender=oneUserItem.findViewById(R.id.tv_gender);
        TextView tv_email =oneUserItem.findViewById(R.id.tv_email);
        ImageView iv_photo =oneUserItem.findViewById(R.id.iv_photo);
        LinearLayout ll_bg =oneUserItem.findViewById(R.id.ll_bg);

        User U =this.getItem(position);
        tv_display_name.setText(U.getDisplayName());
        tv_gender.setText(U.getGender());
        tv_email.setText(U.getEmail());
        iv_photo.setImageURI(U.getPhoto());
        ll_bg.setBackgroundColor(ColorList[position%ColorNumber]);
        return oneUserItem;
    }
}
