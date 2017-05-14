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
    public List<String> groupIDs = new ArrayList<String>();

    public User() {
    }

    public User(String email, String username, String Uid, List<String> groupIDs) {
        this.email = email;
        this.username = username;
        this.Uid = Uid;
        this.groupIDs = groupIDs;
    }

    public User(String email, String username, String Uid) {
        this.email = email;
        this.username = username;
        this.Uid = Uid;
    }

    public void addGroup(String groupID) {
        this.groupIDs.add(groupID);
    }
}
