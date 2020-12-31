package tn.dev.e_presence;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String DisplayName;
    private String Email;
    private int PhoneNumber;
    private String Gender;
    private String Photo;
    private List<String> AdminIN;
    private List<String> StudentIN;
    private List<String> TeacherIN;

    public User(String displayName, String email, int phoneNumber, String gender, String photo, List<String> adminIN, List<String> studentIN, List<String> teacherIN) {
        DisplayName = displayName;
        Email = email;
        PhoneNumber = phoneNumber;
        Gender = gender;
        Photo = photo;
        AdminIN = adminIN;
        StudentIN = studentIN;
        TeacherIN = teacherIN;
    }

    public User() {
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        this.Gender = gender;
    }

    public String getPhoto() {
        return Photo;
    }

    public void setPhoto(String photo) {
        Photo = photo;
    }

    public int getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public List<String> getAdminIN() {
        return AdminIN;
    }

    public void setAdminIN(List<String> adminIN) {
        AdminIN = adminIN;
    }

    public List<String> getStudentIN() {
        return StudentIN;
    }

    public void setStudentIN(List<String> studentIN) {
        StudentIN = studentIN;
    }

    public List<String> getTeacherIN() {
        return TeacherIN;
    }

    public void setTeacherIN(List<String> teacherIN) {
        TeacherIN = teacherIN;
    }
    public void Copy(User other)
    {
        other.DisplayName=this.DisplayName;
        other.Email=this.Email;
        other.Gender=this.Gender;
        other.PhoneNumber=this.PhoneNumber;
        other.Photo=this.Photo;
        other.AdminIN =new ArrayList<String>(this.AdminIN);
        other.StudentIN =new ArrayList<String>(this.StudentIN);
        other.TeacherIN =new ArrayList<String>(this.TeacherIN);


    }
}
