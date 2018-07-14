package com.appromobile.hotel.enums;

/**
 * Created by xuan on 8/5/2016.
 */
public enum BookingType {
    Hourly(1),
    Overnight(2), Daily(3);

    private int type;
    public int getType() {
        return this.type;
    }
    BookingType(int type) {
        this.type = type;
    }

    public static BookingType toType(int type) {
        switch (type){
            case 1:
                return Hourly;
            case 2:
                return Overnight;
            case 3:
                return Daily;
        }
        return Hourly;
    }
}
