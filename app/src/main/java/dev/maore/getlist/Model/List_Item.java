package dev.maore.getlist.Model;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class List_Item  implements Comparable<List_Item>{

    private String uid;
    private String listName;
    private int position;
    private String process;
    private List<String> TaskList = new ArrayList<>();
    private List<Boolean> TaskListChecked = new ArrayList<>();

    public List_Item() {
    }

    public List_Item(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public List_Item setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public String getListName() {
        return listName;
    }

    public List_Item setListName(String listName) {
        this.listName = listName;
        return this;
    }

    public int getPosition() {
        return position;
    }

    public List_Item setPosition(int position) {
        this.position = position;
        return this;
    }

    public String getProcess() {
        return process;
    }

    public List_Item setProcess(String process) {
        this.process = process;
        return this;
    }

    public List<String> getTaskList() {
        return TaskList;
    }

    public List_Item setTaskList(List<String> taskList) {
        TaskList = taskList;
        return this;
    }

    public List<Boolean> getTaskListChecked() {
        return TaskListChecked;
    }

    public List_Item setTaskListChecked(List<Boolean> taskListChecked) {
        TaskListChecked = taskListChecked;
        return this;
    }

    public int compareTo(List_Item list) {
        int comparePosition = ((List_Item) list).getPosition();

        //ascending order
        return this.position - comparePosition;
    }

    @NonNull
    @Override
    public String toString() {
        return "List_Item{" +
                "uid='" + uid + '\'' +
                ", listName='" + listName + '\'' +
                ", position=" + position +
                ", process='" + process + '\'' +
                ", TaskList=" + TaskList +
                ", TaskListChecked=" + TaskListChecked +
                '}';
    }

    public String ShareListItem(String userFirstName, String userLastName, List<String> TaskList) {
        String share;
        StringBuilder temp= new StringBuilder();
        for (int i=0;i<TaskList.size();i++){
            temp.append(i+1).append(") ").append(TaskList.get(i)).append("\n");
        }

        share ="Hi,\n\n" +userFirstName +" "+userLastName+" share List with you:\n"+
                "\nList Name: " +listName+
                "\n Tasks: \n" + temp;

        Log.d("listItemTasks",toString());
        return share;
    }


}
