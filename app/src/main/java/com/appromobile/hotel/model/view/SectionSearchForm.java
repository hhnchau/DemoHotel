package com.appromobile.hotel.model.view;

import java.util.List;

/**
 * Created by appro on 11/05/2018.
 */

public class SectionSearchForm {
    private String sectionLabel;
    private List<SearchHistoryForm> itemRecyclerView;

    public SectionSearchForm(String sectionLabel, List<SearchHistoryForm> itemRecyclerView) {
        this.sectionLabel = sectionLabel;
        this.itemRecyclerView = itemRecyclerView;
    }

    public SectionSearchForm() {
    }

    public String getSectionLabel() {
        return sectionLabel;
    }

    public SectionSearchForm setSectionLabel(String sectionLabel) {
        this.sectionLabel = sectionLabel;
        return this;
    }

    public List<SearchHistoryForm> getItemRecyclerView() {
        return itemRecyclerView;
    }

    public SectionSearchForm setItemRecyclerView(List<SearchHistoryForm> itemRecyclerView) {
        this.itemRecyclerView = itemRecyclerView;
        return this;
    }
}
