package com.example.covar.data;

public class User {
    private String fullName;
    private String age;
    private String mobileNum;
    private String vaccineName;
    private String dose;
    private String vaccinationDate1;

    private String vaccinationDate2;

    public String getVaccineName() {
        return vaccineName;
    }

    public void setVaccineName(String vaccineName) {
        this.vaccineName = vaccineName;
    }

    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public String getVaccinationDate1() {
        return vaccinationDate1;
    }

    public void setVaccinationDate1(String vaccinationDate1) {
        this.vaccinationDate1 = vaccinationDate1;
    }

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

    public String getVaccinationDate2() {
        return vaccinationDate2;
    }

    public void setVaccinationDate2(String vaccinationDate2) {
        this.vaccinationDate2 = vaccinationDate2;
    }
}
