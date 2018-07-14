package com.appromobile.hotel.enums;

/**
 * Created by xuan on 7/14/2016.
 */
public enum SortType {
    PROMOTION(1),
    HOT(2),
    NEW(3),
    ALPHABET(4),
    DISTANCE(5),
    DEEPLINK_DISTRICT(6);

    private int type;

    SortType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public static SortType toType(int type) {
        switch (type) {
            case 1:
                return PROMOTION;
            case 2:
                return HOT;
            case 3:
                return NEW;
            case 4:
                return ALPHABET;
            case 5:
                return DISTANCE;
            case 6:
                return DEEPLINK_DISTRICT;
        }
        return PROMOTION;
    }
}
