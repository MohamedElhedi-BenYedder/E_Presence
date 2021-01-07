package tn.dev.e_presence;

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
    private boolean flag;

    public Session() { //needed
    }

    public Session(String day, String start, String end, String classroom, String group, String teacher, String qrstring, String subject, boolean presential, boolean flag) {
        this.date = day;
        this.start = start;
        this.end = end;
        this.classroom = classroom;
        this.group = group;
        this.teacher = teacher;
        this.qrstring = qrstring;
        this.subject = subject;
        this.presential = presential;
        this.flag = flag;
    }

    public String getDay() {
        return date;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public String getClassroom() {
        return classroom;
    }

    public String getGroup() {
        return group;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getQrstring() {
        return qrstring;
    }

    public String getSubject() {
        return subject;
    }

    public boolean isPresential() {
        return presential;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setDay(String day) {
        this.date = day;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public void setQrstring(String qrstring) {
        this.qrstring = qrstring;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setPresential(boolean presential) {
        this.presential = presential;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
