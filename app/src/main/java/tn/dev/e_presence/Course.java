package tn.dev.e_presence;



public class Course {
    private String subject;

    private String subjectId;
    private String description;
    private  String departement;


    public Course() {
        // needed for fireestore
    }

    public Course(String subject, String subjectId, String description, String departement) {
        this.subject = subject;
        this.subjectId = subjectId;
        this.description = description;
        this.departement = departement;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
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
