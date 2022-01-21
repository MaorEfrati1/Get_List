package dev.maore.getlist.Model;

import java.util.ArrayList;

public class User {
    private String firstName = "";
    private String lastName = "";
    private String email = "";
    private String uid;
    private ArrayList<List_Item> lists = new ArrayList<>();

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

    public ArrayList<List_Item> getLists() {
        return lists;
    }

    public User setLists(ArrayList<List_Item> lists) {
        this.lists = lists;
        return this;
    }

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
