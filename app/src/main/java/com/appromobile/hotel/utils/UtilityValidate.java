package com.appromobile.hotel.utils;

import java.util.regex.Pattern;

/**
 * Created by APPRO on 16-12-15.
 */
public class UtilityValidate {

    private final static Pattern hasUppercase = Pattern.compile(".*[A-Z].*");
    private final static Pattern hasLowercase = Pattern.compile(".*[a-z].*");
    private final static Pattern hasNumber = Pattern.compile(".*[0-9 ].*");
    private final static Pattern hasSpecialChar = Pattern.compile(".*[~!@#$%^&*()_+/*:;-?<> ].*");

    public static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isPasswordValid(CharSequence password) {
        if (hasNumber.matcher(password).matches() && (hasLowercase.matcher(password).matches()) || hasUppercase.matcher(password).matches()) {
            return true;
        }
        return false;
    }
}
