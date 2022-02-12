package dev.maore.getlist.Model;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import dev.maore.getlist.R;
import dev.maore.getlist.RecycleView.ListAdapter;

import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.maore.getlist.RecycleView.Listener;
import dev.maore.getlist.Utils.FireBaseDB;

public class MainActivity extends AppCompatActivity implements Listener {
    //FireBase
    //Auth
    private FirebaseAuth fAuth;

    //DataBase
    private FirebaseDatabase database;

    //Views
    private Button LogOut;
    private FloatingActionButton AddList;

    //LISTS
    private List<List_Item> inProcessList = new ArrayList<>();
    private List<List_Item> doneList = new ArrayList<>();
    private List<Lists> allLists;
    private final ListAdapter doneListAdapter = new ListAdapter(doneList, this);
    private final ListAdapter inProcessListAdapter = new ListAdapter(inProcessList, this);


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

//    //List Item
//    TextView listName;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Firebase
        // Initialize Firebase Auth
        fAuth = FirebaseAuth.getInstance();

        //Initialize Firebase Database
        database = FirebaseDatabase.getInstance();


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

        //Toolbar
        ImageView LogOut = findViewById(R.id.Main_Textview_ToolBarLogOut);
        ImageView refreshDB = findViewById(R.id.Main_Textview_ToolBarRefreshDB);

        //Log out btn
        LogOut.setOnClickListener(v -> {
            final AlertDialog.Builder logOutDialog = new AlertDialog.Builder(v.getContext());
            logOutDialog.setTitle("You Sure Want To Disconnect?");
            logOutDialog.setNegativeButton("No", (dialog, which) -> {
            });
            logOutDialog.setPositiveButton("Yes", (dialog, which) -> {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, Login.class));
                finish();
            });
            logOutDialog.create().show();
        });

        //Refresh DB
        refreshDB.setOnClickListener(v -> {
            //Update RV inProcess List
            inProcessList.clear();
            getListFromDB(inProcessList, inProcessListAdapter, "IN_PROCESS");

            //Update RV done List
            doneList.clear();
            getListFromDB(doneList, doneListAdapter, "DONE");

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

        //get inProcess List From DB
        getListFromDB(inProcessList, inProcessListAdapter, "IN_PROCESS");

        rvInProcess.setAdapter(inProcessListAdapter);
        tvEmptyListInProcess.setOnDragListener(inProcessListAdapter.getDragInstance());
        rvInProcess.setOnDragListener(inProcessListAdapter.getDragInstance());
    }

    private void initDoneRecyclerView() {
        rvDone.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false));

        //get done List From DB
        getListFromDB(doneList, doneListAdapter, "DONE");

        rvDone.setAdapter(doneListAdapter);
        tvEmptyListDone.setOnDragListener(doneListAdapter.getDragInstance());
        rvDone.setOnDragListener(doneListAdapter.getDragInstance());
    }

    //get List From DB
    private void getListFromDB(List<List_Item> list, ListAdapter listAdapter, String process) {
        FireBaseDB.getLists(fAuth, database, new FireBaseDB.Callback_Lists() {
            @Override
            public void dataReady(List<Lists> lists) {
                allLists = lists;

                for (int i = 0; i < lists.size(); i++) {

                    FireBaseDB.getList_Item(database, allLists.get(i).getLid(), new FireBaseDB.Callback_ListItem() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void dataReady(List_Item list_items) {
                            if (list_items.getProcess().equals(process)) {

                                //add List item & update RV
                                list.add(list_items);

                                //Sort by pos
                                list.sort(List_Item::compareTo);

                                //Update RV List
                                listAdapter.updateList(list);
                                listAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }
        });
    }
}
