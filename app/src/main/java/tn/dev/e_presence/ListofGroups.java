package tn.dev.e_presence;

import java.util.List;

public class ListofGroups {
    private List<Group> GroupList;

    public ListofGroups(List<Group> groupList) {
        GroupList = groupList;
    }

    public List<Group> getGroupList() {
        return GroupList;
    }

    public void setGroupList(List<Group> groupList) {
        GroupList = groupList;
    }
}
