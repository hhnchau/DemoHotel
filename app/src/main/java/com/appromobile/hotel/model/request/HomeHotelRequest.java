package com.appromobile.hotel.model.request;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xuan on 7/14/2016.
 */
public class HomeHotelRequest implements Serializable {
    private int offset;
    private int limit;
    private String mobileUserId;
    private int typeSearch;
    private int sort=5;
    private String keyword;
    private String districtSn;

    public Map<String, Object> getMap(){
        Map<String, Object> values = new HashMap<>();
        values.put("offset", offset);
        values.put("limit", limit);
        values.put("mobileUserId",mobileUserId);
        values.put("typeSearch", typeSearch);
        values.put("sort", sort);
        if(keyword!=null) {
            values.put("keyword", keyword);
        }
        if(districtSn!=null){
            values.put("districtSn", districtSn);
        }
        return values;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getMobileUserId() {
        return mobileUserId;
    }

    public void setMobileUserId(String mobileUserId) {
        this.mobileUserId = mobileUserId;
    }

    public int getTypeSearch() {
        return typeSearch;
    }

    public void setTypeSearch(int typeSearch) {
        this.typeSearch = typeSearch;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getDistrictSn() {
        return districtSn;
    }

    public void setDistrictSn(String districtSn) {
        this.districtSn = districtSn;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
}
