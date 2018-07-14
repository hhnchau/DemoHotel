package com.appromobile.hotel.adapter;

import android.content.Context;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appromobile.hotel.R;
import com.appromobile.hotel.enums.RecyclerViewType;
import com.appromobile.hotel.model.view.SearchHistoryForm;
import com.appromobile.hotel.model.view.SectionSearchForm;

import java.util.List;

import static com.igaworks.adbrix.db.ViralCPIDAO.getActivity;

/**
 * Created by appro on 11/05/2018.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SectionViewHolder> {
    private Context context;
    private RecyclerViewType recyclerViewType;
    private List<SectionSearchForm> sectionList;

    public SearchAdapter(Context context, RecyclerViewType recyclerViewType, List<SectionSearchForm> sectionList) {
        this.context = context;
        this.recyclerViewType = recyclerViewType;
        this.sectionList = sectionList;
    }

    @Override
    public SectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_search, parent, false);
        return new SectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SectionViewHolder holder, int position) {
        //Handle Button
        holder.showAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        //Recycler view for items
        holder.itemRecyclerView.setHasFixedSize(true);
        holder.itemRecyclerView.setNestedScrollingEnabled(false);
        holder.itemRecyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        switch (recyclerViewType) {
            case LINEAR_VERTICAL:
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                holder.itemRecyclerView.setLayoutManager(linearLayoutManager);
                break;
            case LINEAR_HORIZONTAL:
                LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                holder.itemRecyclerView.setLayoutManager(linearLayoutManager1);
                break;
            case GRID:
                GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
                holder.itemRecyclerView.setLayoutManager(gridLayoutManager);
                break;
        }

        final SectionSearchForm sectionSearchForm = sectionList.get(position);
        if (sectionSearchForm != null) {
            holder.sectionLabel.setText(sectionSearchForm.getSectionLabel());

            List<SearchHistoryForm> list = sectionSearchForm.getItemRecyclerView();
            if (list != null && list.size() > 0) {
                ItemRecyclerViewAdapter adapter = new ItemRecyclerViewAdapter(context,list);
                holder.itemRecyclerView.setAdapter(adapter);
            }
        }

    }

    @Override
    public int getItemCount() {
        return sectionList.size();
    }

    class SectionViewHolder extends RecyclerView.ViewHolder {
        private TextView sectionLabel;
        private TextView showAll;
        private RecyclerView itemRecyclerView;

        SectionViewHolder(View itemView) {
            super(itemView);
            sectionLabel = itemView.findViewById(R.id.section_label);
            showAll = itemView.findViewById(R.id.section_show_all_button);
            itemRecyclerView = itemView.findViewById(R.id.item_recycler_view);
        }
    }
}
