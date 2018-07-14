package com.appromobile.hotel.model.view;

/**
 * Created by appro on 14/05/2018.
 */

public class SearchHistoryForm {
    private boolean flashsale;
    private String hotelName;
    private String hotelAddress;
    private String keyword;
    private String lastUpdate;
    private int numOfRecord;
    private boolean promotion;
    private String searchTime;
    private int selectedHotelSn;
    private  int sn;
    private int hotelStatus;

    public SearchHistoryForm() {
    }

    public SearchHistoryForm(boolean flashsale, String hotelName, String hotelAddress, boolean promotion, int selectedHotelSn, int hotelStatus) {
        this.flashsale = flashsale;
        this.hotelName = hotelName;
        this.hotelAddress = hotelAddress;
        this.promotion = promotion;
        this.selectedHotelSn = selectedHotelSn;
        this.hotelStatus = hotelStatus;
    }

    public boolean isFlashsale() {
        return flashsale;
    }

    public SearchHistoryForm setFlashsale(boolean flashsale) {
        this.flashsale = flashsale;
        return this;
    }

    public String getHotelName() {
        return hotelName;
    }

    public SearchHistoryForm setHotelName(String hotelName) {
        this.hotelName = hotelName;
        return this;
    }

    public String getHotelAddress() {
        return hotelAddress;
    }

    public SearchHistoryForm setHotelAddress(String hotelAddress) {
        this.hotelAddress = hotelAddress;
        return this;
    }

    public String getKeyword() {
        return keyword;
    }

    public SearchHistoryForm setKeyword(String keyword) {
        this.keyword = keyword;
        return this;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public SearchHistoryForm setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
        return this;
    }

    public int getNumOfRecord() {
        return numOfRecord;
    }

    public SearchHistoryForm setNumOfRecord(int numOfRecord) {
        this.numOfRecord = numOfRecord;
        return this;
    }

    public boolean isPromotion() {
        return promotion;
    }

    public SearchHistoryForm setPromotion(boolean promotion) {
        this.promotion = promotion;
        return this;
    }

    public String getSearchTime() {
        return searchTime;
    }

    public SearchHistoryForm setSearchTime(String searchTime) {
        this.searchTime = searchTime;
        return this;
    }

    public int getSelectedHotelSn() {
        return selectedHotelSn;
    }

    public SearchHistoryForm setSelectedHotelSn(int selectedHotelSn) {
        this.selectedHotelSn = selectedHotelSn;
        return this;
    }

    public int getSn() {
        return sn;
    }

    public SearchHistoryForm setSn(int sn) {
        this.sn = sn;
        return this;
    }

    public int getHotelStatus() {
        return hotelStatus;
    }

    public SearchHistoryForm setHotelStatus(int hotelStatus) {
        this.hotelStatus = hotelStatus;
        return this;
    }
}
