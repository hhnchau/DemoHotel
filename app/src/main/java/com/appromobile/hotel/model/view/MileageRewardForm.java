package com.appromobile.hotel.model.view;

/**
 * Created by appro on 28/03/2018.
 */

public class MileageRewardForm {
    private String endDate;// (string, optional),
    private String lastUpdate;// (string, optional),
    private int maxAmount;// (integer, optional),
    private String name;// (string, optional),
    private int numPoint;// (integer, optional),
    private int sn;// (integer, optional),
    private String startDate;// (string, optional),
    private boolean status;// (boolean, optional): 0: Disable, 1: Active ,
    private int type;// (integer, optional): 1: Cash, 2: Gift ,
    private int usedAmount;// (integer, optional)

    public MileageRewardForm() {
    }

    public String getEndDate() {
        return endDate;
    }

    public MileageRewardForm setEndDate(String endDate) {
        this.endDate = endDate;
        return this;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public MileageRewardForm setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
        return this;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public MileageRewardForm setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
        return this;
    }

    public String getName() {
        return name;
    }

    public MileageRewardForm setName(String name) {
        this.name = name;
        return this;
    }

    public int getNumPoint() {
        return numPoint;
    }

    public MileageRewardForm setNumPoint(int numPoint) {
        this.numPoint = numPoint;
        return this;
    }

    public int getSn() {
        return sn;
    }

    public MileageRewardForm setSn(int sn) {
        this.sn = sn;
        return this;
    }

    public String getStartDate() {
        return startDate;
    }

    public MileageRewardForm setStartDate(String startDate) {
        this.startDate = startDate;
        return this;
    }

    public boolean isStatus() {
        return status;
    }

    public MileageRewardForm setStatus(boolean status) {
        this.status = status;
        return this;
    }

    public int getType() {
        return type;
    }

    public MileageRewardForm setType(int type) {
        this.type = type;
        return this;
    }

    public int getUsedAmount() {
        return usedAmount;
    }

    public MileageRewardForm setUsedAmount(int usedAmount) {
        this.usedAmount = usedAmount;
        return this;
    }
}
