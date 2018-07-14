package com.appromobile.hotel.db.search;

/**
 * Created by appro on 22/03/2018.
 */

public class PopupSql {
    private int sn;
    private int view;

    public PopupSql(int sn, int view) {
        this.sn = sn;
        this.view = view;
    }

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

}
