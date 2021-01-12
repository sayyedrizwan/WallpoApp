package com.wallpo.android.getset;

public class User {
    private String userid;
    private String phonenumber;
    private String email;
    private String username;
    private String password;
    private String verified;
    private String datecreated;

    private String description;
    private String displayname;
    private String category;
    private String profilephoto;
    private String backphoto;
    private String websites;
    private String subscribed;
    private String subscribers;

    public User() {
    }

    public User(String userid) {
        this.userid = userid;
    }

    public User(String userid, String phonenumber, String email, String username, String password, String verified, String datecreated, String description, String displayname, String category, String profilephoto, String backphoto, String websites, String subscribed, String subscribers) {
        this.userid = userid;
        this.phonenumber = phonenumber;
        this.email = email;
        this.username = username;
        this.password = password;
        this.verified = verified;
        this.datecreated = datecreated;
        this.description = description;
        this.displayname = displayname;
        this.category = category;
        this.profilephoto = profilephoto;
        this.backphoto = backphoto;
        this.websites = websites;
        this.subscribed = subscribed;
        this.subscribers = subscribers;
    }


    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public String getDatecreated() {
        return datecreated;
    }

    public void setDatecreated(String datecreated) {
        this.datecreated = datecreated;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProfilephoto() {
        return profilephoto;
    }

    public void setProfilephoto(String profilephoto) {
        this.profilephoto = profilephoto;
    }

    public String getBackphoto() {
        return backphoto;
    }

    public void setBackphoto(String backphoto) {
        this.backphoto = backphoto;
    }

    public String getWebsites() {
        return websites;
    }

    public void setWebsites(String websites) {
        this.websites = websites;
    }

    public String getSubscribed() {
        return subscribed;
    }

    public void setSubscribed(String subscribed) {
        this.subscribed = subscribed;
    }

    public String getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(String subscribers) {
        this.subscribers = subscribers;
    }
}
