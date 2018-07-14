package com.appromobile.hotel.enums;

/**
 * Created by xuan on 9/5/2016.
 */
public enum  QAScope {
    Public(1),
    Private(2);

    private int type;
    public int getType() {
        return this.type;
    }
    QAScope(int type) {
        this.type = type;
    }

    public static QAScope toType(int type) {
        switch (type){
            case 1:
                return Public;
            case 2:
                return Private;
        }
        return Public;
    }
}
