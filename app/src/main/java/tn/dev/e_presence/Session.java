package tn.dev.e_presence;

import java.util.List;

public class Session {
    private String date;
    private String start;
    private String end;
    private String classroom;
    private String group;
    private String teacher;
    private String qrstring;
    private String subject;
    private boolean presential;
    private List<String> listOfPresence;


    public Session() { //needed
    }

    public Session(String date, String start, String end, String classroom, String group, String teacher, String qrstring, String subject, boolean presential, List<String> listOfPresence) {
        this.date = date;
        this.start = start;
        this.end = end;
        this.classroom = classroom;
        this.group = group;
        this.teacher = teacher;
        this.qrstring = qrstring;
        this.subject = subject;
        this.presential = presential;
        this.listOfPresence = listOfPresence;

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getQrstring() {
        return qrstring;
    }

    public void setQrstring(String qrstring) {
        this.qrstring = qrstring;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean isPresential() {
        return presential;
    }

    public void setPresential(boolean presential) {
        this.presential = presential;
    }

    public List<String> getListOfPresence() {
        return listOfPresence;
    }

    public void setListOfPresence(List<String> listOfPresence) {
        this.listOfPresence = listOfPresence;
    }
}
