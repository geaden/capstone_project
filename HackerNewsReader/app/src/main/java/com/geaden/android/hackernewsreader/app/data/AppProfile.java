package com.geaden.android.hackernewsreader.app.data;

/**
 * Simple POJO representing application profile.
 *
 * @author Gennady Denisov
 */
public class AppProfile {
    private String displayName;
    private String email;
    private String photoUrl;
    private String coverUrl;

    public AppProfile(String displayName, String email, String photoUrl, String coverUrl) {
        this.displayName = displayName;
        this.email = email;
        this.photoUrl = photoUrl;
        this.coverUrl = coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }
}
