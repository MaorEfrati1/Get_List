package dev.maore.getlist.Utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dev.maore.getlist.Model.List_Item;
import dev.maore.getlist.Model.Lists;

public class FireBaseDB {


    //Get user first name
    public interface Callback_UserFirstName {
        void dataReady(String userFirstName);
    }

    public static void getUserFirstName(FirebaseAuth fAuth, FirebaseDatabase database, Callback_UserFirstName callback_userFirstName) {

        // Read user from the database
        String userUid = fAuth.getCurrentUser().getUid();
        DatabaseReference userRef = database.getReference("users").child(userUid);
        userRef.child("firstName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userData = dataSnapshot.getValue(String.class);
                if (callback_userFirstName != null) {
                    callback_userFirstName.dataReady(userData);
                    Log.d("user", "Get user " + userData);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    //Get user Last name

    public interface Callback_UserLastName {
        void dataReady(String userLastName);
    }

    public static void getUserLastName(FirebaseAuth fAuth, FirebaseDatabase database, Callback_UserLastName callbackUserLastName) {

        // Read user from the database
        String userUid = fAuth.getCurrentUser().getUid();
        DatabaseReference userRef = database.getReference("users").child(userUid);
        userRef.child("lastName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userData = dataSnapshot.getValue(String.class);
                if (callbackUserLastName != null) {
                    callbackUserLastName.dataReady(userData);
                    Log.d("user", "Get user Last name " + userData);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }


    //Get Lists
    public interface Callback_Lists {
        void dataReady(List<Lists> lists);
    }

    public static void getLists(FirebaseAuth fAuth, FirebaseDatabase database, Callback_Lists callback_lists) {

        // Read Lists from the database
        String userUid = fAuth.getCurrentUser().getUid();
        DatabaseReference listsRef = database.getReference("users");
        listsRef.child(userUid).child("lists").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Lists> lists = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    try {
                        Log.d("lists", "val" + child.getValue());
                        lists.add(child.getValue(Lists.class));
                    } catch (Exception e) {
                        Log.d("lists", "Get Lists - ERROR :" + e.getMessage());
                    }
                }
                if (callback_lists != null) {
                    callback_lists.dataReady(lists);
                    Log.d("lists", "Get Lists " + lists);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    //Get List_Item
    public interface Callback_ListItem {
        void dataReady(List_Item list_items);
    }

    public static void getList_Item(FirebaseDatabase database, String ListUid, Callback_ListItem callback_listItem) {

        // Read List_Item from the database
        DatabaseReference listItemRef = database.getReference("lists");
        Log.d("listItem", "Get List Item UID" + ListUid);

        listItemRef.child(ListUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List_Item list_item = dataSnapshot.getValue(List_Item.class);
                if (callback_listItem != null) {
                    callback_listItem.dataReady(list_item);
                    Log.d("listItem", "Get List Item " + list_item);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    //Get List_Item tasks
    public interface Callback_ListItemTasks {
        void dataReady(List<String> taskList);
    }

    public static void getListItemTasks(FirebaseDatabase database, String ListUid, Callback_ListItemTasks callback_listItemTasks) {

        // Read List_Item from the database
        DatabaseReference listItemRef = database.getReference("lists");

        listItemRef.child(ListUid).child("taskList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> tasks = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    try {
                        Log.d("lists", "val" + child.getValue());
                        tasks.add(child.getValue(String.class));
                    } catch (Exception e) {
                        Log.d("lists", "Get Lists - ERROR :" + e.getMessage());
                    }
                }
                if (callback_listItemTasks != null) {
                    callback_listItemTasks.dataReady(tasks);
                    Log.d("lists", "Get Lists " + tasks);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }



    //Get List_Item tasks
    public interface Callback_ListItemTasksChecked {
        void dataReady(List<Boolean> taskListChecked);
    }

    public static void getListItemTasksChecked(FirebaseDatabase database, String ListUid, Callback_ListItemTasksChecked callback_listItemTasksChecked) {

        // Read List_Item from the database
        DatabaseReference listItemRef = database.getReference("lists");

        listItemRef.child(ListUid).child("taskListChecked").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Boolean> tasksChecked = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    try {
                        Log.d("lists", "val" + child.getValue());
                        tasksChecked.add(child.getValue(Boolean.class));
                    } catch (Exception e) {
                        Log.d("lists", "Get Lists - ERROR :" + e.getMessage());
                    }
                }
                if (callback_listItemTasksChecked != null) {
                    callback_listItemTasksChecked.dataReady(tasksChecked);
                    Log.d("lists", "Get Lists " + tasksChecked);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }


}

