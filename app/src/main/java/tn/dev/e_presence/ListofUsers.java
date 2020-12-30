package tn.dev.e_presence;

import java.util.List;

public class ListofUsers {
    private List<User> UserList;

    public ListofUsers(List<User> userList) {
        UserList = userList;
    }

    public List<User> getUserList() {
        return UserList;
    }

    public void setUserList(List<User> userList) {
        UserList = userList;
    }
}
