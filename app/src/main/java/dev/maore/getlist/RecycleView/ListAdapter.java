package dev.maore.getlist.RecycleView;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import dev.maore.getlist.Model.AddMember;
import dev.maore.getlist.Utils.FireBaseDB;
import dev.maore.getlist.Model.List_Item;
import dev.maore.getlist.Model.ShowList;
import dev.maore.getlist.R;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder>
        implements View.OnTouchListener {
    //FireBase
    //Auth
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();

    //DataBase
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();


    //LISTS
    private final Listener listener;
    private List<List_Item> lists;
    private List<String> tasks;

    public static boolean isDeleteListItem = false;
    private static String getListUidForEdit = "";
    private boolean editAble;

    public ListAdapter(List<List_Item> lists, Listener listener) {
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
        holder.MaterialCardView.setTag(position);
        holder.MaterialCardView.setOnTouchListener(this);
        holder.MaterialCardView.setOnDragListener(new dev.maore.getlist.RecycleView.DragListener(listener));
        holder.list_item = list_item;

        //For Read Only
        //Check edit Able on DB
        String userId = fAuth.getCurrentUser().getUid();
        DatabaseReference editAbleRef = database.getReference("users").child(userId);
        editAbleRef.child("lists").child(list_item.getUid()).child("editAble").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            } else {
                if (task.getResult().getValue() != null) {
                    editAble = (boolean) task.getResult().getValue();
                    if (editAble) {
                        holder.addMemberListItem.setVisibility(View.VISIBLE);
                        holder.shareListItem.setVisibility(View.VISIBLE);
                    } else {
                        holder.shareListItem.setVisibility(View.GONE);
                        holder.addMemberListItem.setVisibility(View.GONE);
                    }
                }
            }
            });

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

    public dev.maore.getlist.RecycleView.DragListener getDragInstance() {
        if (listener != null) {
            return new dev.maore.getlist.RecycleView.DragListener(listener);
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
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.List_Iv_AddMember)
        public ShapeableImageView addMemberListItem;

        ListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            //Open List Item
            listName.setOnClickListener(v -> {
                setGetListUidForEdit(list_item.getUid());
                itemView.getContext().startActivity(new Intent(itemView.getContext(), ShowList.class));
                ((Activity) itemView.getContext()).finish();
            });


                        //Add Member to List
                        addMemberListItem.setOnClickListener(v -> {
                            setGetListUidForEdit(list_item.getUid());
                            itemView.getContext().startActivity(new Intent(itemView.getContext(), AddMember.class));
                            ((Activity) itemView.getContext()).finish();
                        });


                        //Delete List Item
                        deleteListItem.setOnClickListener(v -> {


                            // Delete List_Item from all "USERS" in the database
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
                        });

                        //Shared list
                        shareListItem.setOnClickListener(v -> FireBaseDB.getListItemTasks(database, list_item.getUid(), taskList -> {
                            tasks = taskList;
                            if (tasks != null) {
                                FireBaseDB.getUserFirstName(fAuth, database, userFirstName -> FireBaseDB.getUserLastName(fAuth, database, userLastName -> {
                                    Intent sendIntent = new Intent();
                                    sendIntent.setAction(Intent.ACTION_SEND);
                                    sendIntent.putExtra(Intent.EXTRA_TEXT, list_item.ShareListItem(userFirstName, userLastName, tasks));
                                    sendIntent.setType("text/plain");
                                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                                    itemView.getContext().startActivity(shareIntent);
                                }));
                            }
                        }));

        }
    }

    public static boolean getIsIsDeleteListItem() {
        return isDeleteListItem;
    }

    public static void setIsDeleteListItem(boolean isDeleteListItem) {
        ListAdapter.isDeleteListItem = isDeleteListItem;
    }

    public static String getGetListUidForEdit() {
        return getListUidForEdit;
    }

    public static void setGetListUidForEdit(String getListUidForEdit) {
        ListAdapter.getListUidForEdit = getListUidForEdit;
    }
}