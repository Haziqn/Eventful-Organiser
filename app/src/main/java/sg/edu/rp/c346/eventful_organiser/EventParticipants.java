package sg.edu.rp.c346.eventful_organiser;

/**
 * Created by 15017523 on 8/8/2017.
 */

public class EventParticipants {
    String uid;

    public EventParticipants(){}

    public EventParticipants(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return uid;
    }
}
