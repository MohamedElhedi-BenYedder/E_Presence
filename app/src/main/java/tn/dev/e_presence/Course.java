package tn.dev.e_presence;



public class Course {
    private String displayName;
    private String description;
    private  String departement;


    public Course() {
        // needed for fireestore
    }

    public Course(String subject, String description, String departement) {
        this.displayName = subject;

        this.description = description;
        this.departement = departement;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }
}
