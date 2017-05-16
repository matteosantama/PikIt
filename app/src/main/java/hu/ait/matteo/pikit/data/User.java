package hu.ait.matteo.pikit.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matteosantamaria on 5/13/17.
 */

public class User {

    public String email;
    public String username;
    public String Uid;

    public List<String> groupIDs;

    public User() {
        groupIDs = new ArrayList<String>();
    }

    public User(String email, String username, String Uid, List<String> groupIDs) {
        this.email = email;
        this.username = username;
        this.Uid = Uid;
        this.groupIDs = groupIDs;
        this.groupIDs = new ArrayList<String>();
    }

    public User(String email, String username, String Uid) {
        this.email = email;
        this.username = username;
        this.Uid = Uid;
        this.groupIDs = new ArrayList<String>();
    }

    public void addGroup(String groupID) {
        this.groupIDs.add(groupID);
    }
}
