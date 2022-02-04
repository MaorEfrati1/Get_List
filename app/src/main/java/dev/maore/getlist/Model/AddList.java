package dev.maore.getlist.Model;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import dev.maore.getlist.R;

public class AddList extends AppCompatActivity {
    //FireBase
    //Auth
    private FirebaseAuth fAuth;

    //DataBase
    private FirebaseDatabase database;

    //List Item
    private List<String> TaskList = new ArrayList<>();


    //Views
    private EditText addListName;
    private EditText addTask;
    private Button AddTaskToList;
    private Button Finish;

//    //Recycle View
//    @SuppressLint("NonConstantResourceId")
//    @BindView(R.id.Main_rv_InProcess)
//    RecyclerView rvInProcess;
//    @SuppressLint("NonConstantResourceId")
//    @BindView(R.id.Main_rv_Done)
//    RecyclerView rvDone;
//    @SuppressLint("NonConstantResourceId")
//    @BindView(R.id.Main_Tv_InProcess)
//    TextView tvEmptyListInProcess;
//    @SuppressLint("NonConstantResourceId")
//    @BindView(R.id.Main_Tv_Done)
//    TextView tvEmptyListDone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);

        //Firebase
        // Initialize Firebase Auth
        fAuth = FirebaseAuth.getInstance();

        //Initialize Firebase Database
        database = FirebaseDatabase.getInstance();

//        //Recycle View
//        ButterKnife.bind(this);
//
//        initInProcessRecyclerView();


        //View

        //Edit Text add Task
        addListName = findViewById(R.id.AddList_Et_AddListName);
        //Edit Text add Task
        addTask = findViewById(R.id.AddList_Et_AddTask);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //Btn Add Task
        AddTaskToList = findViewById(R.id.AddList_Btn_AddTask);

        AddTaskToList.setOnClickListener(v -> {
            String task = addTask.getText().toString();
            TaskList.add(task);
            addTask.getText().clear();
        });


        //Btn Finish
        Finish = findViewById(R.id.AddList_Btn_Finish);

        Finish.setOnClickListener(v -> {

            // add List Item to DB (/lists)
            DatabaseReference listItemRef = database.getReference("lists");
            String uid = listItemRef.push().getKey();

            List_Item list_item = new List_Item(uid)
                    .setListName(addListName.getText().toString())
                    .setTaskList(TaskList)
                    .setUid(uid)
                    .setPosition(-1)
                    .setProcess("IN_PROCESS");


            //Add List Item To DB
            if (uid != null) {
                listItemRef.child(uid).setValue(list_item);
            }

            // add List to DB (/users/lists/)
            addListToUserDb(uid);


            startActivity(new Intent(AddList.this, MainActivity.class));
            finish();
        });

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

}