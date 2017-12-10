package com.example.asus.pad3.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Student {
    @SerializedName("phone")
    @Expose
    String phone;
    @SerializedName("year")
    @Expose
    String year;
    @SerializedName("name")
    @Expose
    String name;
    @SerializedName("id")
    @Expose
    String id;


    public String type;

    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }

    public Student(String phone, String year, String name, String id) {
        this.phone = phone;
        this.year = year;
        this.name = name;
        this.id = id;
    }

    public Student(String type) {
        this.type = type;
    }
    public Student() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
