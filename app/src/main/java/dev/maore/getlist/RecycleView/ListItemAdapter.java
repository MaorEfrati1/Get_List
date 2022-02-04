package dev.maore.getlist.RecycleView;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.maore.getlist.Model.FireBaseDB;
import dev.maore.getlist.Model.List_Item;
import dev.maore.getlist.R;

public class ListItemAdapter extends RecyclerView.Adapter<ListItemAdapter.ListViewHolder>
        implements View.OnTouchListener {
    //FireBase
    //Auth
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();

    //DataBase
    private FirebaseDatabase database = FirebaseDatabase.getInstance();


    //LISTS
    private Listener listener;
    private List<List_Item> lists;
    private List<String> tasks;

    public static boolean isDeleteListItem = false;

    public ListItemAdapter(List<List_Item> lists, Listener listener) {
        this.lists = lists;
        this.listener = listener;
    }

    private List_Item getListItem(int position) {
        return lists.get(position);
    }


    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ListViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        List_Item list_item = getListItem(position);

        holder.listName.setText(list_item.getListName());
//        if (list_item.getTaskList() != null) {
//            holder.list_item.setTaskList(list_item.getTaskList());
//        }
//        holder.list_item.setUid(list_item.getUid());
//        holder.list_item.setPosition(list_item.getPosition());
//        holder.list_item.setProcess(list_item.getProcess());
        holder.MaterialCardView.setTag(position);
        holder.MaterialCardView.setOnTouchListener(this);
        holder.MaterialCardView.setOnDragListener(new DragListener(listener));
        holder.list_item = list_item;
    }


    @Override
    public int getItemCount() {
        return lists.size();
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDragAndDrop(data, shadowBuilder, v, 0);
            return true;
        }
        return false;
    }


    List<List_Item> getLists() {
        return lists;
    }

    public void updateList(List<List_Item> list) {
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
        List_Item list_item;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.List_Tv_listName)
        public MaterialTextView listName;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.List_MCV_Item)
        public MaterialCardView MaterialCardView;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.List_Iv_Delete)
        public ShapeableImageView deleteListItem;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.List_Iv_Share)
        public ShapeableImageView shareListItem;

        ListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            //Open List Item
            itemView.findViewById(R.id.List_Tv_listName).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("ptt", "onClick: ." + list_item.getListName());
                }
            });


            //Delete List Item
            deleteListItem.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Delete List_Item from "LISTS" in the database
                    DatabaseReference listItemRef = database.getReference("lists").child(list_item.getUid());
                    listItemRef.removeValue();

                    // Delete List_Item from "USERS" in the database
                    String userUid = fAuth.getCurrentUser().getUid();
                    DatabaseReference usersRef = database.getReference("users").child(userUid).child("lists").child(list_item.getUid());
                    usersRef.removeValue();

                    for (int i = 0; i < lists.size(); i++) {
                        if (lists.get(i).getUid().equals(list_item.getUid())) {
                            lists.remove(i);
                            notifyItemRemoved(i);
                        }
                    }

                    setIsDeleteListItem(true);
                }
            });

            //Shared list
            shareListItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FireBaseDB.getListItemTasks(database, list_item.getUid(), new FireBaseDB.Callback_ListItemTasks() {
                        @Override
                        public void dataReady(List<String> taskList) {
                            tasks = taskList;
                            if (tasks != null) {
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, list_item.ShareListItem("Maor", "Efrati", tasks));
                                sendIntent.setType("text/plain");
                                Intent shareIntent = Intent.createChooser(sendIntent, null);
                                itemView.getContext().startActivity(shareIntent);
                            }
                        }
                    });
                }
            });
        }
    }

    public static boolean getIsIsDeleteListItem() {
        return isDeleteListItem;
    }

    public static void setIsDeleteListItem(boolean isDeleteListItem) {
        ListItemAdapter.isDeleteListItem = isDeleteListItem;
    }
}