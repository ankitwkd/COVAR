package com.example.covar.data;

public class User {
    private String fullName;
    private String age;
    private String mobileNum;

    public User(){
        //Default constructor
    }

    public User(String fullName, String age, String mobileNum) {
        this.fullName = fullName;
        this.age = age;
        this.mobileNum = mobileNum;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getMobileNum() {
        return mobileNum;
    }

    public void setMobileNum(String mobileNum) {
        this.mobileNum = mobileNum;
    }
}
