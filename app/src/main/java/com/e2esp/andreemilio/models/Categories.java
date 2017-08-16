package com.e2esp.andreemilio.models;

/**
 * Created by Ali on 8/11/2017.
 */

public class Categories {

    private String section;
    private int count;
    private String icon;

    public Categories(String section, int count, String icon) {
        this.section = section;
        this.count = count;
        this.icon = icon;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
