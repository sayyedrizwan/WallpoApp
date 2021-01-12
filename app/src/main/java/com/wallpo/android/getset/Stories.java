package com.wallpo.android.getset;

public class Stories {

    int id , likes;
    String userid, link, caption, type, dateshowed, datecreated;

    public Stories(int id, int likes, String userid, String link, String caption, String type, String dateshowed, String datecreated) {
        this.id = id;
        this.likes = likes;
        this.userid = userid;
        this.link = link;
        this.caption = caption;
        this.type = type;
        this.dateshowed = dateshowed;
        this.datecreated = datecreated;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDateshowed() {
        return dateshowed;
    }

    public void setDateshowed(String dateshowed) {
        this.dateshowed = dateshowed;
    }

    public String getDatecreated() {
        return datecreated;
    }

    public void setDatecreated(String datecreated) {
        this.datecreated = datecreated;
    }
}
