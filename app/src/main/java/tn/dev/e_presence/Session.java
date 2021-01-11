package tn.dev.e_presence;

import java.util.List;

public class Session {
    private String date;
    private String start;
    private String end;
    private String classroom;
    private String group;
    private String groupId;
    private String teacher;
    private String teacherId;
    private String qrstring;
    private String schoolId;
    private String subject;
    private String subjectId;
    private boolean presential;
    private List<String> listOfPresence;


    public Session() { //needed for firestrore Database
    }


    public Session(String date, String start, String end, String classroom, String group, String groupId, String teacher, String teacherId, String qrstring, String schoolId, String subject, String subjectId, boolean presential, List<String> listOfPresence) {
        this.date = date;
        this.start = start;
        this.end = end;
        this.classroom = classroom;
        this.group = group;
        this.groupId = groupId;
        this.teacher = teacher;
        this.teacherId = teacherId;
        this.qrstring = qrstring;
        this.schoolId = schoolId;
        this.subject = subject;
        this.subjectId = subjectId;
        this.presential = presential;
        this.listOfPresence = listOfPresence;
    }

    public String getDate() {
        return date;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
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

    public void setListOfPresence(List<String> listOfPresence) { this.listOfPresence = listOfPresence; }

    public String getTeacherId() { return teacherId; }

    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }

    public String getGroupId() {  return groupId;  }

    public void setGroupId(String groupId) { this.groupId = groupId;    }

    public String getSubjectId() {  return subjectId; }

    public void setSubjectId(String subjectId) {  this.subjectId = subjectId;  }
}
