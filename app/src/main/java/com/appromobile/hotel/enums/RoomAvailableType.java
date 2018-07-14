package com.appromobile.hotel.enums;

/**
 * Created by xuan on 7/15/2016.
 */
public enum RoomAvailableType {
    Full(0),
    Available(1);

    private int type;
    public int getType() {
        return this.type;
    }
    RoomAvailableType(int type) {
        this.type = type;
    }

    public static RoomAvailableType toType(int type) {
        switch (type){
            case 0:
                return Full;
            case 1:
                return Available;
        }
        return Full;
    }
}
