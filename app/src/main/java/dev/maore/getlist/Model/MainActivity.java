package dev.maore.getlist.Model;

import static dev.maore.getlist.RecycleView.ListAdapter.getIsIsDeleteListItem;
import static dev.maore.getlist.RecycleView.ListAdapter.setIsDeleteListItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import dev.maore.getlist.R;
import dev.maore.getlist.RecycleView.ListAdapter;

import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

    //LISTS
    private List<List_Item> inProcessList = new ArrayList<>();
    private List<List_Item> doneList = new ArrayList<>();
    private List<Lists> allLists;
    private final ListAdapter doneListAdapter = new ListAdapter(doneList, this);
    private final ListAdapter inProcessListAdapter = new ListAdapter(inProcessList, this);

    private Timer timer;
    private static final int DELAY = 0;

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


        FireBaseDB.getLists(fAuth, database, new FireBaseDB.Callback_Lists() {
            @Override
            public void dataReady(List<Lists> lists) {
                allLists = lists;

                for (int i = 0; i < lists.size(); i++) {

                    FireBaseDB.getList_Item(database, allLists.get(i).getUid(), new FireBaseDB.Callback_ListItem() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void dataReady(List_Item list_items) {
                            if (list_items.getProcess().equals("IN_PROCESS")) {

                                //add List item & update RV
                                inProcessList.add(list_items);

                                //Sort by pos
                                inProcessList.sort(List_Item::compareTo);

                                //Update RV List
                                inProcessListAdapter.updateList(inProcessList);
                                inProcessListAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }
        });


        rvInProcess.setAdapter(inProcessListAdapter);
        tvEmptyListInProcess.setOnDragListener(inProcessListAdapter.getDragInstance());
        rvInProcess.setOnDragListener(inProcessListAdapter.getDragInstance());
    }

    private void initDoneRecyclerView() {
        rvDone.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false));

        FireBaseDB.getLists(fAuth, database, new FireBaseDB.Callback_Lists() {
            @Override
            public void dataReady(List<Lists> lists) {
                allLists = lists;
                for (int i = 0; i < lists.size(); i++) {

                    FireBaseDB.getList_Item(database, allLists.get(i).getUid(), new FireBaseDB.Callback_ListItem() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void dataReady(List_Item list_items) {
                            if (list_items.getProcess().equals("DONE")) {

                                //add List item & update RV
                                doneList.add(list_items);

                                //Sort by pos
                                doneList.sort(List_Item::compareTo);

                                //Update RV List
                                doneListAdapter.updateList(doneList);
                                doneListAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }

            }
        });
        rvDone.setAdapter(doneListAdapter);
        tvEmptyListDone.setOnDragListener(doneListAdapter.getDragInstance());
        rvDone.setOnDragListener(doneListAdapter.getDragInstance());
    }

}
