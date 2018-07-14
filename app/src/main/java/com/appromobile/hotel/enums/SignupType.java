package com.appromobile.hotel.enums;

/**
 * Created by xuan on 8/5/2016.
 */
public enum SignupType {
    Manual(1),
    Facebook(2),
    GooglePlus(3);

    private int type;
    public int getType() {
        return this.type;
    }
    SignupType(int type) {
        this.type = type;
    }

    public static SignupType toType(int type) {
        switch (type){
            case 1:
                return Manual;
            case 2:
                return Facebook;
            case 3:
                return GooglePlus;
        }
        return Manual;
    }
}
