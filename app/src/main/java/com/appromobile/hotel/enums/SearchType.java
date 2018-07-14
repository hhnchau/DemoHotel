package com.appromobile.hotel.enums;

/**
 * Created by xuan on 10/13/2016.
 */

public enum  SearchType {
    AREA(1),
    KEYWORD(2);

    private int type;

    SearchType(int type) {
        this.type = type;
    }
    public int getType() {
        return this.type;
    }

    public static SearchType toType(int type) {
        switch (type){
            case 1:
                return AREA;
            case 2:
                return KEYWORD;
        }
        return AREA;
    }
}
