package com.wallpo.android.getset;

public class Photos {

    private String caption;
    private String datecreated;
    private String imagepath;
    private int photoid;
    private String userid;
    private String albumid;
    private String link;
    private String categoryid;
    private String dateshowed;
    private int trendingcount;
    private String likes;
    private String type;
    private String uid;

    public Photos(String imagepath, int photoid) {
        this.imagepath = imagepath;
        this.photoid = photoid;
    }

    public Photos(String caption, String datecreated, String imagepath, int photoid, String userid, String albumid, String link, String categoryid, String dateshowed, int trendingcount, String likes, String type) {
        this.caption = caption;
        this.datecreated = datecreated;
        this.imagepath = imagepath;
        this.photoid = photoid;
        this.userid = userid;
        this.albumid = albumid;
        this.link = link;
        this.categoryid = categoryid;
        this.dateshowed = dateshowed;
        this.trendingcount = trendingcount;
        this.likes = likes;
        this.type = type;
    }

    public Photos(String caption, String datecreated, String imagepath, int photoid, String userid, String albumid, String link, String categoryid, String dateshowed, int trendingcount, String likes, String type, String uid) {
        this.caption = caption;
        this.datecreated = datecreated;
        this.imagepath = imagepath;
        this.photoid = photoid;
        this.userid = userid;
        this.albumid = albumid;
        this.link = link;
        this.categoryid = categoryid;
        this.dateshowed = dateshowed;
        this.trendingcount = trendingcount;
        this.likes = likes;
        this.type = type;
        this.uid = uid;
    }

    public Photos(String caption, String datecreated, String imagepath, int photoid, String userid, String albumid, String link, String categoryid, String dateshowed, int trendingcount, String likes) {
        this.caption = caption;
        this.datecreated = datecreated;
        this.imagepath = imagepath;
        this.photoid = photoid;
        this.userid = userid;
        this.albumid = albumid;
        this.link = link;
        this.categoryid = categoryid;
        this.dateshowed = dateshowed;
        this.trendingcount = trendingcount;
        this.likes = likes;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDatecreated() {
        return datecreated;
    }

    public void setDatecreated(String datecreated) {
        this.datecreated = datecreated;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public int getPhotoid() {
        return photoid;
    }

    public void setPhotoid(int photoid) {
        this.photoid = photoid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getAlbumid() {
        return albumid;
    }

    public void setAlbumid(String albumid) {
        this.albumid = albumid;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(String categoryid) {
        this.categoryid = categoryid;
    }

    public String getDateshowed() {
        return dateshowed;
    }

    public void setDateshowed(String dateshowed) {
        this.dateshowed = dateshowed;
    }

    public int getTrendingcount() {
        return trendingcount;
    }

    public void setTrendingcount(int trendingcount) {
        this.trendingcount = trendingcount;
    }

    public String getLikes() {
        return likes;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
