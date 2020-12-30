package tn.dev.e_presence;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SessionAdapter extends BaseAdapter {
    Activity mActivity;
    ListOfSessions SessionsList;

    public SessionAdapter(Activity mActivity, ListOfSessions sessionsList) {
        this.mActivity = mActivity;
        SessionsList = sessionsList;
    }

    @Override
    public int getCount() {
        return SessionsList.getSessionList().size();
    }

    @Override
    public Session getItem(int position) {
        return SessionsList.getSessionList().get(position);
    }

    @Override
    public long getItemId(int position) { return 0; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View oneSessionItem;
        LayoutInflater inflaytor= (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        oneSessionItem = inflaytor.inflate(R.layout.item_session,parent,false);
        TextView tv_start=oneSessionItem.findViewById(R.id.tv_start);
        TextView tv_end=oneSessionItem.findViewById(R.id.tv_end);
        TextView tv_classroom=oneSessionItem.findViewById(R.id.tv_classroom);
        TextView tv_teacher=oneSessionItem.findViewById(R.id.tv_teacher);
        TextView tv_subject=oneSessionItem.findViewById(R.id.tv_subject);
        TextView tv_group=oneSessionItem.findViewById(R.id.tv_group);
        TextView tv_presential=oneSessionItem.findViewById(R.id.tv_presential);
        LinearLayout ll =oneSessionItem.findViewById(R.id.ll);

        Session S=this.getItem(position);
        tv_start.setText(S.getStart());
        tv_end.setText(S.getEnd());
        tv_classroom.setText(S.getClassroom());
        tv_teacher.setText(S.getTeacher());
        tv_subject.setText(S.getSubject());
        tv_group.setText(S.getGroup());
        if (S.isPresential()) tv_presential.setText("Yes");

        else tv_presential.setText("No");
        if(S.isFlag()) ll.setBackgroundColor(mActivity.getResources().getColor(R.color.colorPrimary));

            return oneSessionItem;
    }

}
