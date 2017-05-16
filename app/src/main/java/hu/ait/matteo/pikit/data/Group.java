package hu.ait.matteo.pikit.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by matteosantamaria on 5/13/17.
 */

public class Group {

    private String uniqueID;
    private String name;

    private List<String> members;
    private List<Photo> photos;

    public Group() {
        members = new ArrayList<String>();
        photos = new ArrayList<Photo>();
    }

    public Group(String name, String creatorID) {
        this.name = name;
        members = new ArrayList<String>();
        members.add(creatorID);
        uniqueID = UUID.randomUUID().toString();
        photos = new ArrayList<Photo>();
    }

    public Group(String uniqueID, String name, List<String> members, List<Photo> photos) {
        this.uniqueID = uniqueID;
        this.name = name;
        this.members = members;
        this.photos = photos;
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

    public void addUser(String Uid) {
        members.add(Uid);
    }

    public List<Photo> getPhotos() {
        return this.photos;
    }

    public void addPhoto(Photo photo) {
        photos.add(photo);
    }
}
