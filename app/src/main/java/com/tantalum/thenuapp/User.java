package com.tantalum.thenuapp;

import android.widget.EditText;

public class User {
    public String fullname;
    public String phone;
    public String email;

    public User(EditText fullname, EditText phone, EditText email) {
    }

    public User (String fullname, String phone, String email) {
        this.fullname = fullname;
        this.phone = phone;
        this.email = email;
    }
}
