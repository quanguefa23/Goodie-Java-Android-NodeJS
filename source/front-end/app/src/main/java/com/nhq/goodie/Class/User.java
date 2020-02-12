package com.nhq.goodie.Class;

public class User {
    String fullname;
    String dob;
    int sex;
    String mail;
    String phone;

    public User(String fullname, String dob, int sex, String mail, String phone) {
        this.fullname = fullname;
        this.dob = dob;
        this.sex = sex;
        this.mail = mail;
        this.phone = phone;
    }

    public String getFullname() {
        return fullname;
    }

    public String getDob() {
        return dob;
    }

    public int getSex() {
        return sex;
    }

    public String getMail() {
        return mail;
    }

    public String getPhone() {
        return phone;
    }

    public User getDuplicate() {
        return new User(fullname, dob, sex, mail, phone);
    }
}
