package tn.dev.e_presence;

import android.view.Display;

import java.util.List;

public class Group {
    private String Gid;
    private String DisplayName;
    private List<String> StudentList;
    private int Level;

    public Group(String gid, String displayName, List<String> studentList, int level) {
        Gid = gid;
        DisplayName = displayName;
        StudentList = studentList;
        Level = level;
    }

    public String getGid() {
        return Gid;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public List<String> getStudentList() {
        return StudentList;
    }

    public void setStudentList(List<String> studentList) {
        StudentList = studentList;
    }

    public int getLevel() {
        return Level;
    }

    public void setLevel(int level) {
        Level = level;
    }
}
