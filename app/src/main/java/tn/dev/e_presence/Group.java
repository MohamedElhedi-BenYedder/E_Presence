package tn.dev.e_presence;

import android.view.Display;

import java.util.List;

public class Group {
    private String displayName;
    private int num;
    private String description;
    private List<String> Students;
    private String level;

    public Group() {
        // needed for fireestore
    }

    public Group(String displayName, int num, String description, List<String> students, String level) {
        this.displayName = displayName;
        this.num = num;
        this.description = description;
        Students = students;
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

    public List<String> getStudents() {
        return Students;
    }

    public void setStudents(List<String> students) {
        Students = students;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
