package tn.dev.e_presence;

import android.net.Uri;

public class School {
    private String Sid;
    private String DisplayName;
    private String Photo;
    private String Location;

    public School() {
        Sid="121";
        DisplayName="School";
        Location="Ghazela";

    }

    public School(String displayName, String location,String photo) {
        DisplayName = displayName;
        Location = location;
        Photo = photo;
    }

    public void setSid(String sid) {
        Sid = sid;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }


    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getPhoto() {
        return Photo;
    }

    public void setPhoto(String photo) {
        Photo = photo;
    }
}
