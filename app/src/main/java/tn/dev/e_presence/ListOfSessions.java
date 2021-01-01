package tn.dev.e_presence;

import java.util.ArrayList;
import java.util.List;

public class ListOfSessions {
    List<Session> SessionList;

    public ListOfSessions(List<Session> sessionList) {
        SessionList = sessionList;
    }

    public ListOfSessions() {
        SessionList=new ArrayList<>();
        Session S0= new Session(1,"20-10-2010",    "10:00","12:00","A200","INDP2-F","Monsieur","transmission",true);
        Session S1= new Session(2 ,"11-10-2010",    "08:00","10:00","A200","INDP2-F","Monsieur","transmission",true);
        SessionList.add(S0);
        SessionList.add(S1);
        SessionList.add(S0);
        SessionList.add(S1);

    }

    public List<Session> getSessionList() {
        return SessionList;
    }

    public void setSessionList(List<Session> sessionList) {
        SessionList = sessionList;
    }
}
