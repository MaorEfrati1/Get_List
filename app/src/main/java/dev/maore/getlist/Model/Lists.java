package dev.maore.getlist.Model;

public class Lists {

    private String uid;
    private boolean editAble;

    public Lists() {
    }

    public Lists(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public Lists setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public boolean isEditAble() {
        return editAble;
    }

    public Lists setEditAble(boolean editAble) {
        this.editAble = editAble;
        return this;
    }
}
