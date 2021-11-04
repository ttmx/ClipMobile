package com.migueljteixeira.clipmobile.entities;

public class StudentClassDoc extends Entity {
    private String name, url, date, size, type;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
