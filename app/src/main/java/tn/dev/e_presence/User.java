package tn.dev.e_presence;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String displayName;
    private String email;
    private String phoneNumber;
    private String gender;
    private String photo;
    private List<String> adminIN;
    private List<String> studentIN;
    private List<String> teacherIN;
    private List<String> allSessions;

    public User() {
        //public no-arg constructor needed
    }

    public User(String displayName, String email, String phoneNumber, String gender, String photo, List<String> adminIN, List<String> studentIN, List<String> teacherIN,List<String> allSessions) {
        this.displayName = displayName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.photo = photo;
        this.adminIN = adminIN;
        this.studentIN = studentIN;
        this.teacherIN = teacherIN;
        this.allSessions=allSessions;
    }



    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public String getPhoto() {
        return photo;
    }

    public List<String> getAdminIN() {
        return adminIN;
    }

    public List<String> getStudentIN() {
        return studentIN;
    }

    public List<String> getTeacherIN() {
        return teacherIN;
    }

    public List<String> getAllSessions() {        return allSessions;    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setAdminIN(List<String> adminIN) {
        this.adminIN = adminIN;
    }

    public void setStudentIN(List<String> studentIN) {
        this.studentIN = studentIN;
    }

    public void setTeacherIN(List<String> teacherIN) {
        this.teacherIN = teacherIN;
    }

    public void setAllSessions(List<String> allSessions) {
        this.allSessions = allSessions;
    }

    public void Copy(User other)
    {
        other.displayName=this.displayName;
        other.email=this.email;
        other.gender=this.gender;
        other.phoneNumber=this.phoneNumber;
        other.photo=this.photo;
        other.adminIN =new ArrayList<String>(this.adminIN);
        other.studentIN =new ArrayList<String>(this.studentIN);
        other.teacherIN =new ArrayList<String>(this.teacherIN);


    }
}
