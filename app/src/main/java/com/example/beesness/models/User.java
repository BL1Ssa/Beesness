package com.example.beesness.models;

public class User {
    private String id;
    private String name;
    private String email;
    private String phonenum;
    private String companyName;
    private String category;

    public User(String id, String name, String email, String phonenum, String companyName, String category) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phonenum = phonenum;
        this.companyName = companyName;
        this.category = category;
    }

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
