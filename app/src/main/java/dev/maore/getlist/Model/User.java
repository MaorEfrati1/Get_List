package dev.maore.getlist.Model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String firstName = "";
    private String lastName = "";
    private String email = "";
    private String uid;
    private List<Lists> lists = new ArrayList<>();

    public User() {
    }

    public User(String uid) {
        this.uid = uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public User setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public User setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getUid() {
        return uid;
    }

    public User setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public List<Lists> getLists() {
        return lists;
    }

    public User setLists(List<Lists> lists) {
        this.lists = lists;
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", uid='" + uid + '\'' +
                ", lists=" + lists +
                '}';
    }
}
