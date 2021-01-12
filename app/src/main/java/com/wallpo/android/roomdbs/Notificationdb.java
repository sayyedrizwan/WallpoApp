package com.wallpo.android.roomdbs;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "notification_db")
public class Notificationdb implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "username")
    public String username;

    @ColumnInfo(name = "displayname")
    public String displayname;

    @ColumnInfo(name = "postid")
    public String postid;

    @ColumnInfo(name = "caption")
    public String caption;

    @ColumnInfo(name = "imagepath")
    public String imagepath;

    @ColumnInfo(name = "type")
    public String type;

    @ColumnInfo(name = "theuserid")
    public String theuserid;

    @ColumnInfo(name = "timestamp")
    public String timestamp;

    public Notificationdb(int uid, String username, String displayname, String postid, String caption, String imagepath, String type, String theuserid, String timestamp) {
        this.uid = uid;
        this.username = username;
        this.displayname = displayname;
        this.postid = postid;
        this.caption = caption;
        this.imagepath = imagepath;
        this.type = type;
        this.theuserid = theuserid;
        this.timestamp = timestamp;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTheuserid() {
        return theuserid;
    }

    public void setTheuserid(String theuserid) {
        this.theuserid = theuserid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
