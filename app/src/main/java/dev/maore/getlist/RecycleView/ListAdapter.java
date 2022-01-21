package dev.maore.getlist.RecycleView;

import android.annotation.SuppressLint;
import android.content.ClipData;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.maore.getlist.Model.List_Item;
import dev.maore.getlist.R;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder>
        implements View.OnTouchListener {

    private List<List_Item> lists;
    private final Listener listener;

    public ListAdapter(List<List_Item> lists, Listener listener) {
        this.lists = lists;
        this.listener = listener;
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
        holder.listName.setText(lists.get(position).getListName());
        holder.MaterialCardView.setTag(position);
        holder.MaterialCardView.setOnTouchListener(this);
        holder.MaterialCardView.setOnDragListener(new dev.maore.getlist.RecycleView.DragListener(listener));
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

    public static class ListViewHolder extends RecyclerView.ViewHolder {
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.List_Tv_listName)
        public MaterialTextView listName;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.List_MCV_Item)
        public MaterialCardView MaterialCardView;

        ListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}