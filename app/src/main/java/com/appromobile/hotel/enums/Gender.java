package com.appromobile.hotel.enums;

/**
 * Created by xuan on 8/5/2016.
 */
public enum Gender {
    Male(1),
    Female(2);

    private int type;
    public int getType() {
        return this.type;
    }
    Gender(int type) {
        this.type = type;
    }

    public static Gender toType(int type) {
        switch (type){
            case 1:
                return Male;
            case 2:
                return Female;
        }
        return Female;
    }
}
