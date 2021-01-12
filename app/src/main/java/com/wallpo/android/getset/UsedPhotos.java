package com.wallpo.android.getset;

public class UsedPhotos {

    String id, type, useddate, photoid, caption, categoryid, albumid, datecreated, dateshowed, link, userid, imagepath, likes, trendingcount;

    public UsedPhotos(String id, String type, String useddate, String photoid, String caption, String categoryid, String albumid, String datecreated, String dateshowed, String link, String userid, String imagepath, String likes, String trendingcount) {
        this.id = id;
        this.type = type;
        this.useddate = useddate;
        this.photoid = photoid;
        this.caption = caption;
        this.categoryid = categoryid;
        this.albumid = albumid;
        this.datecreated = datecreated;
        this.dateshowed = dateshowed;
        this.link = link;
        this.userid = userid;
        this.imagepath = imagepath;
        this.likes = likes;
        this.trendingcount = trendingcount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUseddate() {
        return useddate;
    }

    public void setUseddate(String useddate) {
        this.useddate = useddate;
    }

    public String getPhotoid() {
        return photoid;
    }

    public void setPhotoid(String photoid) {
        this.photoid = photoid;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(String categoryid) {
        this.categoryid = categoryid;
    }

    public String getAlbumid() {
        return albumid;
    }

    public void setAlbumid(String albumid) {
        this.albumid = albumid;
    }

    public String getDatecreated() {
        return datecreated;
    }

    public void setDatecreated(String datecreated) {
        this.datecreated = datecreated;
    }

    public String getDateshowed() {
        return dateshowed;
    }

    public void setDateshowed(String dateshowed) {
        this.dateshowed = dateshowed;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getTrendingcount() {
        return trendingcount;
    }

    public void setTrendingcount(String trendingcount) {
        this.trendingcount = trendingcount;
    }
}
