package com.appromobile.hotel.enums;

/**
 * Created by xuan on 8/5/2016.
 */
public enum ReasonNotCheckinType {
    NoVisit(1),
    CheckinHotel(2), NoRoom(3);

    private int type;
    public int getType() {
        return this.type;
    }
    ReasonNotCheckinType(int type) {
        this.type = type;
    }

    public static ReasonNotCheckinType toType(int type) {
        switch (type){
            case 1:
                return NoVisit;
            case 2:
                return CheckinHotel;
            case 3:
                return NoRoom;
        }
        return NoVisit;
    }
}
