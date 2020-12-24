package tn.dev.e_presence;

public class Session {
    private int SID;
    private String Day;
    private String Start;
    private String End;
    private String Classroom;
    private String Group;
    private String Teacher;
    private String Subject;
    private boolean Presential;

    public Session(int sID, String day, String start, String end, String classroom,String group, String teacher, String subject, boolean presential) {
        SID = sID;
        Day = day;
        Start = start;
        End = end;
        Classroom = classroom;
        Group=group;
        Teacher = teacher;
        Subject = subject;
        Presential = presential;
    }

    public int getSID() {
        return SID;
    }

    public void setSID(int sID) { SID = sID; }

    public String getDay() { return Day; }

    public void setDay(String day) {
        Day = day;
    }

    public String getStart() {
        return Start;
    }

    public void setStart(String start) {
        Start = start;
    }

    public String getEnd() {
        return End;
    }

    public void setEnd(String end) {
        End = end;
    }

    public String getClassroom() {
        return Classroom;
    }

    public void setClassroom(String classroom) {
        Classroom = classroom;
    }

    public String getGroup() {
        return Group;
    }

    public void setGroup(String group) {
        Group = group;
    }

    public String getTeacher() {
        return Teacher;
    }

    public void setTeacher(String teacher) { Teacher = teacher; }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) { Subject = subject; }

    public boolean isPresential() {
        return Presential;
    }

    public void setPresential(boolean presential) {
        Presential = presential;
    }
}
