package tn.dev.e_presence;



public class Course {
    private String courseName;
    private String semstre;
    private String level;

    public Course() {
        // needed for fireestore
    }

    public Course(String courseName, String semstre, String level) {
        this.courseName = courseName;
        this.semstre = semstre;
        this.level = level;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getSemstre() {
        return semstre;
    }

    public void setSemstre(String semstre) {
        this.semstre = semstre;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
