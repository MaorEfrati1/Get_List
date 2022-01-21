package dev.maore.getlist.RecycleView;

import android.annotation.SuppressLint;
import android.view.DragEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.maore.getlist.Model.List_Item;
import dev.maore.getlist.R;

public class DragListener implements View.OnDragListener {

    private boolean isDropped = false;
    private final Listener listener;

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

                        ListAdapter adapterSource = (ListAdapter) source.getAdapter();
                        assert adapterSource != null;
                        List_Item list = adapterSource.getLists().get(positionSource);
                        List<List_Item> listSource = adapterSource.getLists();

                        listSource.remove(positionSource);
                        adapterSource.updateList(listSource);
                        adapterSource.notifyDataSetChanged();

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
                    }
                    break;
            }
        }

        if (!isDropped && event.getLocalState() != null) {
            ((View) event.getLocalState()).setVisibility(View.VISIBLE);
        }
        return true;
    }


}