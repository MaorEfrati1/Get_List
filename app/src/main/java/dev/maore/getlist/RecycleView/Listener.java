package dev.maore.getlist.RecycleView;

import dev.maore.getlist.Model.List_Item;

public interface Listener {
    void setEmptyListInProcess(boolean visibility);

    void setEmptyListDone(boolean visibility);

//    void listItemClick(List_Item list_item, int position);
}