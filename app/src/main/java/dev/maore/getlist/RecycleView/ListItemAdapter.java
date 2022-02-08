package dev.maore.getlist.RecycleView;

import static dev.maore.getlist.Model.AddList.getOptDeleteTask;
import static dev.maore.getlist.RecycleView.ListAdapter.getGetListUidForEdit;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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

    //parameters
    public static boolean isDeleteListItem = false;
    private int pos;

    public ListItemAdapter(List<String> lists, Listener listener) {
        this.lists = lists;
        this.listener = listener;
    }

    private String getListItem(int position) {
        return lists.get(position);
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
        pos = position;
        holder.listName.setText(taskList);
        holder.MaterialCardView.setTag(position);
        holder.MaterialCardView.setOnDragListener(new DragListener(listener));
        holder.taskList = taskList;

        //checked issue
        holder.CheckedListItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()  {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //set TaskList last checked status
//                holder.CheckedListItem.
            }
        });
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

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.ListItem_Tv_TaskName)
        public MaterialTextView listName;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.List_MCV_Item_Task)
        public MaterialCardView MaterialCardView;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.ListItem_Iv_DeleteTask)
        public ShapeableImageView deleteListItem;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.List_Cb_Checked)
        public MaterialCheckBox CheckedListItem;


        ListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);


            //Delete & Checked List Item

            //Check if its edit/create mod
            if (getOptDeleteTask() == 1) {
                deleteListItem.setVisibility(View.GONE);
                CheckedListItem.setVisibility(View.GONE);
            } else {
                //Delete List Item
                deleteListItem.setOnClickListener(v -> {

                    // Delete List_Item from "LISTS" in the database
                    DatabaseReference listItemRef = database.getReference("lists").child(getGetListUidForEdit()).child("taskList").child(Integer.toString(getAdapterPosition()));
                    listItemRef.removeValue();

                    for (int i = 0; i < lists.size(); i++) {
                        if (lists.get(i).equals(taskList)) {
                            lists.remove(i);
                            notifyItemRemoved(i);
                        }
                    }

                    setIsDeleteListItem(true);
                });

                //Checked List Item
                CheckedListItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        if(CheckedListItem.isChecked()){
//                            CheckedListItem.setChecked(true);
//                        }else{
//                            CheckedListItem.setChecked(false);
//                        }
                    }
                });
            }


        }
    }

    public static boolean getIsDeleteListItem() {
        return isDeleteListItem;
    }

    public static void setIsDeleteListItem(boolean isDeleteListItem) {
        ListItemAdapter.isDeleteListItem = isDeleteListItem;
    }

}