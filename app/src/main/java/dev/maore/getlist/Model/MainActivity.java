package dev.maore.getlist.Model;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import dev.maore.getlist.R;
import dev.maore.getlist.RecycleView.ListAdapter;

import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.maore.getlist.RecycleView.Listener;

public class MainActivity extends AppCompatActivity implements Listener {
    //FireBase
    //Auth
    private FirebaseAuth fAuth;

    //DataBase
    private FirebaseDatabase database;

    //Views
    private Button LogOut;
    private Button AddList;

    private List<List_Item> inProcessList;
    private List<Lists> allLists;

    //Recycle View
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.Main_rv_InProcess)
    RecyclerView rvInProcess;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.Main_rv_Done)
    RecyclerView rvDone;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.Main_Tv_InProcess)
    TextView tvEmptyListInProcess;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.Main_Tv_Done)
    TextView tvEmptyListDone;

    //List Item
    TextView listName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Firebase
        // Initialize Firebase Auth
        fAuth = FirebaseAuth.getInstance();

        //Initialize Firebase Database
        database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("message");
//        myRef.setValue("Hello, World!");

        //Recycle View
        ButterKnife.bind(this);

        initInProcessRecyclerView();
        initDoneRecyclerView();

        tvEmptyListInProcess.setVisibility(View.GONE);
        tvEmptyListDone.setVisibility(View.GONE);

        //Views
        //Add list btn
        AddList = findViewById(R.id.Main_Btn_AddList);

        AddList.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddList.class));
            finish();
        });

        //Log out btn
        LogOut = findViewById(R.id.Main_Btn_LogOut);

        LogOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
        });

    }


    //Recycle View
    @Override
    public void setEmptyListInProcess(boolean visibility) {
        tvEmptyListInProcess.setVisibility(visibility ? View.VISIBLE : View.GONE);
        rvInProcess.setVisibility(visibility ? View.GONE : View.VISIBLE);
    }

    @Override
    public void setEmptyListDone(boolean visibility) {
        tvEmptyListDone.setVisibility(visibility ? View.VISIBLE : View.GONE);
        rvDone.setVisibility(visibility ? View.GONE : View.VISIBLE);
    }

    private void initInProcessRecyclerView() {
        rvInProcess.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false));

        inProcessList = new ArrayList<>();
        ListAdapter inProcessListAdapter = new ListAdapter(inProcessList, this);

        FireBaseDB.getLists(fAuth, database, new FireBaseDB.Callback_Lists() {
            @Override
            public void dataReady(List<Lists> lists) {
                allLists = lists;
                for (int i = 0; i < lists.size(); i++) {

                    FireBaseDB.getList_Item(database, allLists.get(i).getUid(), new FireBaseDB.Callback_ListItem() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void dataReady(List_Item list_items) {
                            inProcessList.add(list_items);
                            inProcessListAdapter.updateList(inProcessList);
                            inProcessListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

        //inProcessList.add("list A");
//        inProcessList.add("list B");


        rvInProcess.setAdapter(inProcessListAdapter);
        tvEmptyListInProcess.setOnDragListener(inProcessListAdapter.getDragInstance());
        rvInProcess.setOnDragListener(inProcessListAdapter.getDragInstance());
    }

    private void initDoneRecyclerView() {
        rvDone.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false));

        List<List_Item> doneList = new ArrayList<>();
//        doneList.add("list C");
//        doneList.add("list D");

        ListAdapter doneListAdapter = new ListAdapter(doneList, this);
        rvDone.setAdapter(doneListAdapter);
        tvEmptyListDone.setOnDragListener(doneListAdapter.getDragInstance());
        rvDone.setOnDragListener(doneListAdapter.getDragInstance());
    }
}
