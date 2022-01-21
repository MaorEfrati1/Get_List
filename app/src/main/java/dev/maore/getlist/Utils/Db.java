package dev.maore.getlist.Utils;

import java.util.ArrayList;

import dev.maore.getlist.Model.User;

public class Db {

    public ArrayList<User> lists = new ArrayList<>();

    public Db() {

    }

    public Db(ArrayList<User> lists) {
        this.lists = lists;
    }

    public ArrayList<User> getLists() {
        return lists;
    }

    public Db setLists(ArrayList<User> lists) {
        this.lists = lists;
        return this;
    }
}
