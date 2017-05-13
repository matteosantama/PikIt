package hu.ait.matteo.pikit.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by matteosantamaria on 5/13/17.
 */

public class Group {

    private String uniqueID;
    public String name;

    public List<String> members = new ArrayList<>();

    public Group() {
    }

    public Group(String name, String creatorID) {
        this.name = name;
        members.add(creatorID);
        uniqueID = UUID.randomUUID().toString();
    }

    public String getUniqueID() {
        return this.uniqueID;
    }

    public String getName() {
        return this.name;
    }

    public List<String> getMembers() {
        return this.members;
    }
}
