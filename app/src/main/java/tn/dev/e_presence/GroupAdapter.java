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

public class GroupAdapter extends BaseAdapter {
    Activity mActivity;
    ListofGroups GroupList;
    final static int ColorList[]={0,1,3};
    final static int ColorNumber=3;
    final static int ImageList[]={};
    final static int ImageNumber=0;

    public GroupAdapter(Activity mActivity, ListofGroups groupList) {
        this.mActivity = mActivity;
         GroupList= groupList;
    }

    @Override
    public int getCount() {return GroupList.getGroupList().size(); }

    @Override
    public Group getItem(int position) {return GroupList.getGroupList().get(position); }

    @Override
    public long getItemId(int position) { return 0; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View oneGroupItem;
        LayoutInflater inflaytor= (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        oneGroupItem = inflaytor.inflate(R.layout.item_group,parent,false);
        TextView tv_display_name=oneGroupItem.findViewById(R.id.tv_display_name);
        TextView tv_student_number=oneGroupItem.findViewById(R.id.tv_student_number);
        ImageView iv_level =oneGroupItem.findViewById(R.id.iv_level);
        LinearLayout ll_bg =oneGroupItem.findViewById(R.id.ll_bg);

        Group G =this.getItem(position);
        tv_display_name.setText(G.getDisplayName());
        tv_student_number.setText(G.getStudentList().size());
        iv_level.setImageResource(ImageList[G.getLevel()-1]);
        ll_bg.setBackgroundColor(ColorList[G.getLevel()-1]);
        return oneGroupItem;
    }
}
