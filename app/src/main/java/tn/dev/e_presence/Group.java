package tn.dev.e_presence;

import android.view.Display;

import java.util.List;

public class Group {
    private String displayName;
    private int num;
    private String description;
    private List<String> studentList;
    private String level;

    public Group() {
        // needed for fireestore
    }

    public Group(String displayName, int num, String description, List<String> studentList, String level) {
        this.displayName = displayName;
        this.num = num;
        this.description = description;
        this.studentList = studentList;
        this.level = level;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<String> studentList) {
        this.studentList = studentList;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
