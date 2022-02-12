package dev.maore.getlist.RecycleView;

import static dev.maore.getlist.Model.AddList.getOptDeleteTask;
import static dev.maore.getlist.RecycleView.ListAdapter.getGetListUidForEdit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.maore.getlist.Model.ShowList;
import dev.maore.getlist.R;

public class ListItemAdapter extends RecyclerView.Adapter<ListItemAdapter.ListViewHolder> {
    //FireBase
    //Auth
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();

    //DataBase
    private FirebaseDatabase database = FirebaseDatabase.getInstance();


    //LISTS
    private Listener listener;
    private List<String> lists;
    private List<Boolean> taskListChecked;

    //parameters
    public static boolean isDeleteListItem = false;
    private int pos;
    private boolean editAble;

    public ListItemAdapter(List<String> lists, Listener listener, List<Boolean> taskListChecked) {
        this.lists = lists;
        this.listener = listener;
        this.taskListChecked = taskListChecked;
    }

    private String getListItem(int position) {
        return lists.get(position);
    }

    private Boolean getListCheckedItem(int position) {
        return taskListChecked.get(position);
    }

    public List<Boolean> getTaskListChecked() {
        return taskListChecked;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.list_item_task, parent, false);
        return new ListViewHolder(view);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(ListViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String taskList = getListItem(position);
        boolean taskChecked = getListCheckedItem(position);

        pos = position;
        holder.listName.setText(taskList);
        holder.MaterialCardView.setTag(position);
        holder.MaterialCardView.setOnDragListener(new DragListener(listener));
        holder.taskList = taskList;
        holder.taskChecked = taskChecked;

        //init checked
        if (taskChecked) {
            holder.MaterialCardView.setCardBackgroundColor(holder.itemView.getResources().getColor(R.color.ListItemChecked));
            holder.listName.setPaintFlags(holder.listName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            taskListChecked.set(position, true);
        } else {
            holder.MaterialCardView.setCardBackgroundColor(holder.itemView.getResources().getColor(R.color.ListItem));
            holder.listName.setPaintFlags(0);
            taskListChecked.set(position, false);
        }

    }


    @Override
    public int getItemCount() {
        return lists.size();
    }


    List<String> getLists() {
        return lists;
    }

    public void updateList(List<String> list) {
        this.lists = list;
    }

    public void updateListChecked(List<Boolean> listsChecked) {
        this.taskListChecked = listsChecked;

    }

    public DragListener getDragInstance() {
        if (listener != null) {
            return new DragListener(listener);
        } else {
            Log.e("ListAdapter", "Listener wasn't initialized!");
            return null;
        }
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        String taskList;
        boolean taskChecked;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.ListItem_Tv_TaskName)
        public MaterialTextView listName;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.List_MCV_Item_Task)
        public MaterialCardView MaterialCardView;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.ListItem_Iv_DeleteTask)
        public ShapeableImageView deleteListItem;


        ListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            //IsChecked

            //on click
            MaterialCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Check edit Able on DB
                    String userId = fAuth.getCurrentUser().getUid();
                    DatabaseReference editAbleRef = database.getReference("users").child(userId);
                    editAbleRef.child("lists").child(getGetListUidForEdit()).child("editAble").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Log.e("firebase", "Error getting data", task.getException());
                            } else {
                                if (task.getResult().getValue() != null) {
                                    editAble = (boolean) task.getResult().getValue();
                                }
                                if (!editAble) {
                                    deleteListItem.setVisibility(View.GONE);
                                } else {
                                    if (MaterialCardView.getCardBackgroundColor().getDefaultColor() == itemView.getResources().getColor(R.color.ListItem)) {
                                        MaterialCardView.setCardBackgroundColor(itemView.getResources().getColor(R.color.ListItemChecked));
                                        listName.setPaintFlags(listName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                        taskListChecked.set(getAdapterPosition(), true);

                                    } else {
                                        MaterialCardView.setCardBackgroundColor(itemView.getResources().getColor(R.color.ListItem));
                                        listName.setPaintFlags(0);
                                        taskListChecked.set(getAdapterPosition(), false);
                                    }
                                    addTaskCheckedToDB();
                                }
                            }
                        }
                    });
                }
            });

            //Check edit Able on DB
            String userId = fAuth.getCurrentUser().getUid();
            DatabaseReference editAbleRef = database.getReference("users").child(userId);
            editAbleRef.child("lists").child(getGetListUidForEdit()).child("editAble").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        if (task.getResult().getValue() != null) {
                            editAble = (boolean) task.getResult().getValue();
                        }

                        //Delete List Item

                        //Check if its edit/create mod
                        if (editAble) {
                            if (getOptDeleteTask() == 1) {
                                deleteListItem.setVisibility(View.GONE);
                            } else {
                                deleteListItem.setVisibility(View.VISIBLE);
                                //Delete List Item
                                deleteListItem.setOnClickListener(v -> {

                                    // Delete List_Item from "LISTS" in the database
                                    DatabaseReference listItemRef = database.getReference("lists").child(getGetListUidForEdit()).child("taskList").child(Integer.toString(getAdapterPosition()));
                                    listItemRef.removeValue();

                                    // Delete List_Item from "LISTS CHECKED" in the database
                                    DatabaseReference listCheckedItemRef = database.getReference("lists").child(getGetListUidForEdit()).child("taskListChecked").child(Integer.toString(getAdapterPosition()));
                                    listCheckedItemRef.removeValue();


                                    for (int i = 0; i < lists.size(); i++) {
                                        if (lists.get(i).equals(taskList)) {
                                            lists.remove(i);
                                            taskListChecked.remove(i);
                                            notifyItemRemoved(i);
                                        }
                                    }


                                    setIsDeleteListItem(true);
                                });
                            }
                        } else {
                            deleteListItem.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }
    }

    public static boolean getIsDeleteListItem() {
        return isDeleteListItem;
    }

    public static void setIsDeleteListItem(boolean isDeleteListItem) {
        ListItemAdapter.isDeleteListItem = isDeleteListItem;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addTaskCheckedToDB() {

        //Add User To DB
        DatabaseReference userRef = database.getReference("lists");
        userRef.child(getGetListUidForEdit()).child("taskListChecked").setValue(taskListChecked);

        //Update RV List
        updateListChecked(taskListChecked);
        notifyDataSetChanged();
    }
}