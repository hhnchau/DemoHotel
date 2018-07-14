package com.appromobile.hotel.model.view;

/**
 * Created by appro on 16/04/2018.
 */

public class Checkbox {
    private String title;
    private boolean select;

    public Checkbox(String title, boolean select) {
        this.title = title;
        this.select = select;
    }

    public Checkbox setTitle(String title) {
        this.title = title;
        return this;
    }

    public Checkbox setSelect(boolean select) {
        this.select = select;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public boolean isSelect() {
        return select;
    }
}
