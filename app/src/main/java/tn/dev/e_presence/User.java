package tn.dev.e_presence;

import android.net.Uri;

public class User {
    private String Uid;
    private String DisplayName;
    private String Name;
    private String Surname;
    private String Email;
    private String Gender;
    private Uri Photo;

    public User(String uid, String displayName, String name, String surname, String email, String gender, Uri photo) {
        Uid = uid;
        DisplayName = displayName;
        Name = name;
        Surname = surname;
        Email = email;
        Gender = gender;
        Photo = photo;
    }

    public String getUid() {
        return Uid;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSurname() {
        return Surname;
    }

    public void setSurname(String surname) {
        Surname = surname;
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

    public Uri getPhoto() {
        return Photo;
    }

    public void setPhoto(Uri photo) {
        Photo = photo;
    }
}
