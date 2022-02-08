package dev.maore.getlist.Model;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.maore.getlist.R;
import dev.maore.getlist.RecycleView.ListItemAdapter;
import dev.maore.getlist.RecycleView.Listener;

public class AddList extends AppCompatActivity implements Listener {
    //FireBase
    //Auth
    private FirebaseAuth fAuth;

    //DataBase
    private FirebaseDatabase database;

    //LISTS
    private List<String> taskList = new ArrayList<>();
    private final ListItemAdapter inProcessListAdapter = new ListItemAdapter(taskList, this);


    //Views
    private EditText addListName;
    private EditText addTask;
    private Button AddTaskToList;
    private Button Finish;

    private static int OptDeleteTask;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AddList.this, MainActivity.class));
        finish();
    }

    //Recycle View
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.Main_rv_InProcess_Task)
    RecyclerView rvInProcess;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.Main_Tv_InProcess_Task)
    TextView tvEmptyListInProcess;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);

        //Firebase
        // Initialize Firebase Auth
        fAuth = FirebaseAuth.getInstance();

        //Initialize Firebase Database
        database = FirebaseDatabase.getInstance();

        //Recycle View
        ButterKnife.bind(this);
        initInProcessRecyclerViewTask();

        //give opt to delete task
        setOptDeleteTask(1);


        //View
        //Edit Text add Task
        addListName = findViewById(R.id.AddList_Et_AddListName);
        //Edit Text add Task
        addTask = findViewById(R.id.AddList_Et_AddTask);

        //Btn Add Task
        AddTaskToList = findViewById(R.id.AddList_Btn_AddTask);

        AddTaskToList.setOnClickListener(v -> {
            if (TextUtils.isEmpty(addTask.getText())) {
                addTask.setError("Add Task Details");
                return;
            }
            String task = addTask.getText().toString();
            taskList.add(task);
            addTask.getText().clear();

            //Update RV
            inProcessListAdapter.updateList(taskList);
            inProcessListAdapter.notifyDataSetChanged();
        });


        //Btn Finish
        Finish = findViewById(R.id.AddList_Btn_Finish);

        Finish.setOnClickListener(v -> {
            if (TextUtils.isEmpty(addListName.getText())) {
                addListName.setError("List Name Is Required");
                return;
            }
                // add List Item to DB (/lists)
                DatabaseReference listItemRef = database.getReference("lists");
                String uid = listItemRef.push().getKey();

                List_Item list_item = new List_Item(uid)
                        .setListName(addListName.getText().toString())
                        .setTaskList(taskList)
                        .setUid(uid)
                        .setPosition(-1)
                        .setProcess("IN_PROCESS");


                //Add List Item To DB
                if (uid != null) {
                    listItemRef.child(uid).setValue(list_item);
                }

                // add List to DB (/users/lists/)
                addListToUserDb(uid);

                //give opt to delete task
                setOptDeleteTask(0);
                startActivity(new Intent(AddList.this, MainActivity.class));
                finish();
        });

    }

    private void initInProcessRecyclerViewTask() {
        rvInProcess.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false));


//        FireBaseDB.getLists(fAuth, database, new FireBaseDB.Callback_Lists() {
//            @Override
//            public void dataReady(List<Lists> lists) {
//                allLists = lists;
//
//                for (int i = 0; i < lists.size(); i++) {
//
//                    FireBaseDB.getList_Item(database, allLists.get(i).getUid(), new FireBaseDB.Callback_ListItem() {
//                        @SuppressLint("NotifyDataSetChanged")
//                        @Override
//                        public void dataReady(List_Item list_items) {
//                            if (list_items.getProcess().equals("IN_PROCESS")) {
//                                FireBaseDB.getListItemTasks(database, list_items.getUid(), new FireBaseDB.Callback_ListItemTasks() {
//                                    @Override
//                                    public void dataReady(List<String> taskList) {
//                                        tasks = taskList;
        //add List item & update RV
//                                            inProcessListTask.add(list_items);

        //Sort by pos
//                                            inProcessListTask.sort(List_Item::compareTo);

        //Update RV List


//                                    }
//                                });
//                            }
//                        }
//                    });
//                }
//            }
//        });


        rvInProcess.setAdapter(inProcessListAdapter);
        tvEmptyListInProcess.setOnDragListener(inProcessListAdapter.getDragInstance());
        rvInProcess.setOnDragListener(inProcessListAdapter.getDragInstance());
    }


    public void addListToUserDb(String uid) {
        // add List to DB (/users/lists/)
        String userUid = fAuth.getCurrentUser().getUid();
        DatabaseReference listsRef = database.getReference("users");

        Lists lists = new Lists(uid)
                .setEditAble(true)
                .setUid(uid);

        //Add To Users Lists In DB
        if (uid != null) {
            listsRef.child(userUid).child("lists").child(uid).setValue(lists);
        }
    }

    @Override
    public void setEmptyListInProcess(boolean visibility) {
        tvEmptyListInProcess.setVisibility(visibility ? View.VISIBLE : View.GONE);
        rvInProcess.setVisibility(visibility ? View.GONE : View.VISIBLE);
    }

    @Override
    public void setEmptyListDone(boolean visibility) {

    }


    public static int getOptDeleteTask() {
        return OptDeleteTask;
    }

    public static void setOptDeleteTask(int isDelete) {
        AddList.OptDeleteTask = isDelete;
    }


}