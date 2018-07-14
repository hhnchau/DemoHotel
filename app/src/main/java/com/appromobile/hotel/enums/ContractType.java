package com.appromobile.hotel.enums;

/**
 * Created by xuan on 7/15/2016.
 */
public enum  ContractType {
    WAITING(1),
    GENERAL(2),
    CONTRACT(3),
    TRIAL(4),
    TERMINAL(5);;

    private int type;

    public int getType() {
        return this.type;
    }
    ContractType(int type) {
        this.type = type;
    }

    public static ContractType toType(int type) {
        switch (type){
            case 1:
                return WAITING;
            case 2:
                return GENERAL;
            case 3:
                return CONTRACT;
            case 4:
                return TRIAL;
            case 5:
                return TERMINAL;
        }
        return GENERAL;
    }
}
