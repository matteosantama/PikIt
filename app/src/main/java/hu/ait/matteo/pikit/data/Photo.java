package hu.ait.matteo.pikit.data;

import java.util.UUID;

/**
 * Created by matteosantamaria on 5/16/17.
 */

public class Photo {

    private String photoID;
    private String creatorID;
    private String url;

    public Photo() {
        photoID = UUID.randomUUID().toString();
    }

    public Photo(String creatorID, String url) {
        this.creatorID = creatorID;
        this.url = url;
        photoID = UUID.randomUUID().toString();
    }

    public Photo(String photoID, String creatorID, String url) {
        this.photoID = photoID;
        this.creatorID = creatorID;
        this.url = url;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPhotoID() {
        return photoID;
    }

    public void setPhotoID(String photoID) {
        this.photoID = photoID;
    }
}
