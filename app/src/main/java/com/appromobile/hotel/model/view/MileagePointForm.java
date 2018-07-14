package com.appromobile.hotel.model.view;

/**
 * Created by appro on 27/03/2018.
 */

public class MileagePointForm {
    private int mileageAmount;
    private int mileageEarned;
    private int mileageExpired;
    private int mileageUsed;

    public MileagePointForm() {
    }

    public int getMileageAmount() {
        return mileageAmount;
    }

    public MileagePointForm setMileageAmount(int mileageAmount) {
        this.mileageAmount = mileageAmount;
        return this;
    }

    public int getMileageEarned() {
        return mileageEarned;
    }

    public MileagePointForm setMileageEarned(int mileageEarned) {
        this.mileageEarned = mileageEarned;
        return this;
    }

    public int getMileageExpired() {
        return mileageExpired;
    }

    public MileagePointForm setMileageExpired(int mileageExpired) {
        this.mileageExpired = mileageExpired;
        return this;
    }

    public int getMileageUsed() {
        return mileageUsed;
    }

    public MileagePointForm setMileageUsed(int mileageUsed) {
        this.mileageUsed = mileageUsed;
        return this;
    }
}
