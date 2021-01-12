package com.wallpo.android.getset;

public class Albums {

    String albumid, userid, albumname, albumdesc, albumcreated, albumurl;

    public Albums(String albumid, String userid, String albumname, String albumdesc, String albumcreated, String albumurl) {
        this.albumid = albumid;
        this.userid = userid;
        this.albumname = albumname;
        this.albumdesc = albumdesc;
        this.albumcreated = albumcreated;
        this.albumurl = albumurl;
    }

    public String getAlbumid() {
        return albumid;
    }

    public void setAlbumid(String albumid) {
        this.albumid = albumid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getAlbumname() {
        return albumname;
    }

    public void setAlbumname(String albumname) {
        this.albumname = albumname;
    }

    public String getAlbumdesc() {
        return albumdesc;
    }

    public void setAlbumdesc(String albumdesc) {
        this.albumdesc = albumdesc;
    }

    public String getAlbumcreated() {
        return albumcreated;
    }

    public void setAlbumcreated(String albumcreated) {
        this.albumcreated = albumcreated;
    }

    public String getAlbumurl() {
        return albumurl;
    }

    public void setAlbumurl(String albumurl) {
        this.albumurl = albumurl;
    }
}
