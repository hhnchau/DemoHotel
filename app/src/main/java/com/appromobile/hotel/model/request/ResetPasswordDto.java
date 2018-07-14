package com.appromobile.hotel.model.request;

/**
 * Created by xuan on 8/15/2016.
 */
public class ResetPasswordDto {
    private String email;

    public ResetPasswordDto(String email){
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
