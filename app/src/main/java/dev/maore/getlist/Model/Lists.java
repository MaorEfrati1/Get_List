package dev.maore.getlist.Model;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Lists {

    private String lid;
    private boolean editAble;

    public Lists() {
    }

    public Lists(String uid) {
        this.lid = uid;
    }

    public String getLid() {
        return lid;
    }

    public Lists setLid(String lid) {
        this.lid = lid;
        return this;
    }

    public boolean isEditAble() {
        return editAble;
    }

    public Lists setEditAble(boolean editAble) {
        this.editAble = editAble;
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        return "Lists{" +
                "uid='" + lid + '\'' +
                ", editAble=" + editAble +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Lists)) {
            return false;
        }
        Lists other =(Lists)o;
        return editAble == other.editAble && Objects.equals(lid, other.lid);
    }

}
