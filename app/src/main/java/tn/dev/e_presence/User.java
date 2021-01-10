package tn.dev.e_presence;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String userID;
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

    public User(String userID, String displayName, String email, String phoneNumber, String gender, String photo, List<String> adminIN, List<String> studentIN, List<String> teacherIN, List<String> allSessions) {
        this.userID = userID;
        this.displayName = displayName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.photo = photo;
        this.adminIN = adminIN;
        this.studentIN = studentIN;
        this.teacherIN = teacherIN;
        this.allSessions = allSessions;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public List<String> getAdminIN() {
        return adminIN;
    }

    public void setAdminIN(List<String> adminIN) {
        this.adminIN = adminIN;
    }

    public List<String> getStudentIN() {
        return studentIN;
    }

    public void setStudentIN(List<String> studentIN) {
        this.studentIN = studentIN;
    }

    public List<String> getTeacherIN() {
        return teacherIN;
    }

    public void setTeacherIN(List<String> teacherIN) {
        this.teacherIN = teacherIN;
    }

    public List<String> getAllSessions() {
        return allSessions;
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
        other.userID=this.userID;


    }
}
