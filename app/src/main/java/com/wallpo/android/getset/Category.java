package com.wallpo.android.getset;

public class Category {

    String categoryid, name, imagelink, type;

    public Category(String categoryid, String name, String imagelink) {
        this.categoryid = categoryid;
        this.name = name;
        this.imagelink = imagelink;
    }

    public Category(String categoryid, String name, String imagelink, String type) {
        this.categoryid = categoryid;
        this.name = name;
        this.imagelink = imagelink;
        this.type = type;
    }

    public String getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(String categoryid) {
        this.categoryid = categoryid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagelink() {
        return imagelink;
    }

    public void setImagelink(String imagelink) {
        this.imagelink = imagelink;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
