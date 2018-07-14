package com.appromobile.hotel.enums;

/**
 * Created by appro on 15/05/2018.
 */

public enum RoomType {
    NORMAL(1), FLASHSALE(2), CINEJOY(3);

    private int type;
    public int getType() {
        return this.type;
    }

    RoomType(int type) {
        this.type = type;
    }

    public static RoomType toType(int type) {
        switch (type){
            case 1:
                return NORMAL;
            case 2:
                return FLASHSALE;
            case 3:
                return CINEJOY;
        }
        return NORMAL;
    }
}

