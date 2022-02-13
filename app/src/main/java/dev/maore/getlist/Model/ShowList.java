package dev.maore.getlist.Model;


import static dev.maore.getlist.Model.AddList.setOptDeleteTask;
import static dev.maore.getlist.RecycleView.ListAdapter.getGetListUidForEdit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.google.android.material.button.MaterialButton;
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
import dev.maore.getlist.Utils.FireBaseDB;

public class ShowList extends AppCompatActivity implements Listener {
    //FireBase
    //Auth
    private FirebaseAuth fAuth;

    //DataBase
    private FirebaseDatabase database;

    //LISTS
    private List<String> taskList = new ArrayList<>();
    private List<Boolean> taskListChecked = new ArrayList<>();
    private final ListItemAdapter inProcessListAdapter = new ListItemAdapter(taskList, this, taskListChecked);


    //Views
    private TextView listName;
    private TextView enterTask;
    private EditText addTask;
    private MaterialButton addTaskToList;

    //parameters
    private boolean editAble;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ShowList.this, MainActivity.class));
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
        setContentView(R.layout.activity_show_list);

        //Check if its edit/create mod
        setOptDeleteTask(0);

        //Firebase
        // Initialize Firebase Auth
        fAuth = FirebaseAuth.getInstance();

        //Initialize Firebase Database
        database = FirebaseDatabase.getInstance();

        //Recycle View
        ButterKnife.bind(this);
        initInProcessRecyclerViewTask();

        //View
        //Text view add Task
        listName = findViewById(R.id.ShowList_Tv_AddTask);//Edit Text add Task
        getListItemName();

        //Text view enter Task
        enterTask = findViewById(R.id.ShowList_Tv_EnterTask);

        //Edit Text add Task
        addTask = findViewById(R.id.ShowList_Et_AddTask);

        //Btn Add Task
        addTaskToList = findViewById(R.id.ShowList_Btn_AddTask);

        //if read only
        addTask.setVisibility(View.GONE);
        enterTask.setVisibility(View.GONE);
        addTaskToList.setVisibility(View.GONE);

        //Check edit Able on DB
        String userId = fAuth.getCurrentUser().getUid();
        DatabaseReference editAbleRef = database.getReference("users").child(userId);
        editAbleRef.child("lists").child(getGetListUidForEdit()).child("editAble").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                if (task.getResult().getValue() != null) {
                    editAble = (boolean) task.getResult().getValue();
                }

                if (editAble) {
                    addTask.setVisibility(View.VISIBLE);
                    enterTask.setVisibility(View.VISIBLE);
                    addTaskToList.setVisibility(View.VISIBLE);

                    listName.setOnClickListener(v -> {
                        final EditText editText_ChangeListName = new EditText(v.getContext());
                        final AlertDialog.Builder ChangeListNameDialog = new AlertDialog.Builder(v.getContext());
                        ChangeListNameDialog.setTitle("Change List Name");
                        ChangeListNameDialog.setMessage("Enter The New List Name");
                        ChangeListNameDialog.setView(editText_ChangeListName);

                        ChangeListNameDialog.setPositiveButton("Finish", (dialog, which) -> {
                            //Change List name  in UI
                            String newListName = editText_ChangeListName.getText().toString();
                            listName.setText(newListName);

                            //Change List name in DB
                            DatabaseReference listItemRef = database.getReference("lists");
                            listItemRef.child(getGetListUidForEdit()).child("listName").setValue(newListName);
                        });
                        ChangeListNameDialog.create().show();
                    });


                    addTaskToList.setOnClickListener(v -> {
                        if (TextUtils.isEmpty(addTask.getText())) {
                            addTask.setError("Add Task Details");
                            return;
                        }
                        String task1 = addTask.getText().toString();
                        taskList.add(task1);
                        taskListChecked.add(false);
                        addTaskToDB();
                        addTaskCheckedToDB();
                        addTask.getText().clear();

                        //Update RV
                        inProcessListAdapter.updateList(taskList);
                        inProcessListAdapter.updateListChecked(taskListChecked);
                        inProcessListAdapter.notifyDataSetChanged();
                    });

                }
            }
        });
    }

    private void initInProcessRecyclerViewTask() {
        rvInProcess.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false));

        //get tasks fro DB
        getListItemTasksChecked();
        getListItemTasks();

        rvInProcess.setAdapter(inProcessListAdapter);
        tvEmptyListInProcess.setOnDragListener(inProcessListAdapter.getDragInstance());
        rvInProcess.setOnDragListener(inProcessListAdapter.getDragInstance());
    }


    @Override
    public void setEmptyListInProcess(boolean visibility) {
        tvEmptyListInProcess.setVisibility(visibility ? View.VISIBLE : View.GONE);
        rvInProcess.setVisibility(visibility ? View.GONE : View.VISIBLE);
    }

    @Override
    public void setEmptyListDone(boolean visibility) {

    }

    @SuppressLint("NotifyDataSetChanged")
    public void getListItemTasks() {
        FireBaseDB.getListItemTasks(database, getGetListUidForEdit(), tasks -> {
            //add tasks to RV
            taskList = tasks;

            // Update RV List
            inProcessListAdapter.updateList(taskList);
            inProcessListAdapter.notifyDataSetChanged();
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    public void getListItemTasksChecked() {
        FireBaseDB.getListItemTasksChecked(database, getGetListUidForEdit(), taskChecked -> {
            //is tasks checked to RV
            taskListChecked = taskChecked;

            // Update RV List
            inProcessListAdapter.updateListChecked(taskListChecked);
            inProcessListAdapter.notifyDataSetChanged();
        });

    }

    public void getListItemName() {
        FireBaseDB.getList_Item(database, getGetListUidForEdit(), list_items -> listName.setText(list_items.getListName()));
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addTaskToDB() {

        //Add User To DB
        DatabaseReference userRef = database.getReference("lists");
        userRef.child(getGetListUidForEdit()).child("taskList").setValue(taskList);

        //Update RV List
        inProcessListAdapter.updateList(taskList);
        inProcessListAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addTaskCheckedToDB() {

        //Add User To DB
        DatabaseReference userRef = database.getReference("lists");
        userRef.child(getGetListUidForEdit()).child("taskListChecked").setValue(taskListChecked);

        //Update RV List
        inProcessListAdapter.updateListChecked(taskListChecked);
        inProcessListAdapter.notifyDataSetChanged();
    }


}