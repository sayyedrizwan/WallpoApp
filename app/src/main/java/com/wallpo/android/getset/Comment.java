package com.wallpo.android.getset;

public class Comment {

    public  int id;
    public String  userid, comment, photoid, photosuserid, datecreated;

    public Comment(int id, String userid, String comment, String photoid, String photosuserid, String datecreated) {
        this.id = id;
        this.userid = userid;
        this.comment = comment;
        this.photoid = photoid;
        this.photosuserid = photosuserid;
        this.datecreated = datecreated;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPhotoid() {
        return photoid;
    }

    public void setPhotoid(String photoid) {
        this.photoid = photoid;
    }

    public String getPhotosuserid() {
        return photosuserid;
    }

    public void setPhotosuserid(String photosuserid) {
        this.photosuserid = photosuserid;
    }

    public String getDatecreated() {
        return datecreated;
    }

    public void setDatecreated(String datecreated) {
        this.datecreated = datecreated;
    }
}
