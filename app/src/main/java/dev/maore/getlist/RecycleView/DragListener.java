package dev.maore.getlist.RecycleView;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import dev.maore.getlist.Model.List_Item;
import dev.maore.getlist.R;

public class DragListener implements View.OnDragListener {
    //FireBase
    //DataBase
    private FirebaseDatabase database = FirebaseDatabase.getInstance();


    private boolean isDropped = false;
    private Listener listener;

    DragListener(Listener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public boolean onDrag(View v, DragEvent event) {
        if (event.getAction() == DragEvent.ACTION_DROP) {
            isDropped = true;
            int positionTarget = -1;

            View viewSource = (View) event.getLocalState();
            int viewId = v.getId();
            final int flItem = R.id.List_MCV_Item;
            final int tvEmptyListInProcess = R.id.Main_Tv_InProcess;
            final int tvEmptyListDone = R.id.Main_Tv_Done;
            final int rvInProcess = R.id.Main_rv_InProcess;
            final int rvDone = R.id.Main_rv_Done;

            switch (viewId) {
                case flItem:
                case tvEmptyListInProcess:
                case tvEmptyListDone:
                case rvInProcess:
                case rvDone:

                    RecyclerView target;
                    switch (viewId) {
                        case tvEmptyListInProcess:
                        case rvInProcess:
                            target = v.getRootView().findViewById(rvInProcess);
                            break;
                        case tvEmptyListDone:
                        case rvDone:
                            target = v.getRootView().findViewById(rvDone);
                            break;
                        default:
                            target = (RecyclerView) v.getParent();
                            positionTarget = (int) v.getTag();
                    }

                    if (viewSource != null) {
                        RecyclerView source = (RecyclerView) viewSource.getParent();

                        int positionSource = (int) viewSource.getTag();
                        int sourceId = source.getId();

                        //Source
                        ListAdapter adapterSource = (ListAdapter) source.getAdapter();
                        assert adapterSource != null;
                        List_Item list = adapterSource.getLists().get(positionSource);
                        List<List_Item> listSource = adapterSource.getLists();

                        listSource.remove(positionSource);
                        adapterSource.updateList(listSource);
                        adapterSource.notifyDataSetChanged();

                        //Target
                        ListAdapter adapterTarget = (ListAdapter) target.getAdapter();
                        assert adapterTarget != null;
                        List<List_Item> customListTarget = adapterTarget.getLists();
                        if (positionTarget >= 0) {
                            customListTarget.add(positionTarget, list);
                        } else {
                            customListTarget.add(list);
                        }
                        adapterTarget.updateList(customListTarget);
                        adapterTarget.notifyDataSetChanged();

                        if (sourceId == rvDone && adapterSource.getItemCount() < 1) {
                            listener.setEmptyListDone(true);
                        }
                        if (viewId == tvEmptyListDone) {
                            listener.setEmptyListDone(false);
                        }
                        if (sourceId == rvInProcess && adapterSource.getItemCount() < 1) {
                            listener.setEmptyListInProcess(true);
                        }
                        if (viewId == tvEmptyListInProcess) {
                            listener.setEmptyListInProcess(false);
                        }


                        //Update changes in List item PROCESS && POSITION in DB
                        DatabaseReference listItemRef = database.getReference("lists");

                        if (sourceId == rvInProcess && adapterTarget != adapterSource) {
                            //update List Item process
                            listItemRef.child(list.getUid()).child("process").setValue("DONE");

                            //update List Item pos
                            updatePosListsItem(listItemRef, customListTarget);
                        }

                        if (sourceId == rvDone && adapterTarget != adapterSource) {
                            //update List Item process
                            listItemRef.child(list.getUid()).child("process").setValue("IN_PROCESS");

                            //update List Item pos
                            updatePosListsItem(listItemRef, customListTarget);
                        }

                        //Update changes in the RV of List item POSITION in DB
                        if (sourceId == rvInProcess && adapterTarget == adapterSource) {
                            //update List Item pos
                            updatePosListsItem(listItemRef, customListTarget);
                        }
                        if (sourceId == rvDone && adapterTarget == adapterSource) {
                            //update List Item pos
                            updatePosListsItem(listItemRef, customListTarget);
                        }


                    }
                    break;
            }
        }

        if (!isDropped && event.getLocalState() != null) {
            ((View) event.getLocalState()).setVisibility(View.VISIBLE);
        }
        return true;
    }

    public void updatePosListsItem(DatabaseReference listItemRef, List<List_Item> customListTarget) {
        for (int i = 0; i < customListTarget.size(); i++) {
            String listSourceUid = customListTarget.get(i).getUid();
            listItemRef.child(listSourceUid).child("position").setValue(i);

        }
    }
}