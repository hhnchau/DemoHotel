package com.appromobile.hotel.clock;

import java.util.Calendar;

/**
 * Created by appro on 13/03/2018.
 */

public class HelperClock {

    static int getSystemHour() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    static int getSystemMin() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MINUTE);
    }

    static String parseString(int time) {
        String s ;
        if (time % 2 > 0) {
            time = time/2;
            s = time+":30";
        } else {
            time = time/2;
            s = time + ":00";
        }
        return s;
    }

    static boolean isEven(String number) {
        String[] s = number.split(":");

        if (s.length > 1)
            if (s[1].equals("30"))
                return false;

        return true;
    }

    static int parseTime(String time) {
        String[] s = time.split(":");

        int t = -1;
        if (s.length > 1) {
            t = Integer.parseInt(s[0]);
        }
        return t;
    }

    static int handleLimit(String time) {
        String[] s = time.split(":");
        int limit = parseTime(time) * 2;
        if (s.length > 1) {
            if (s[1].equals("30")) {
                limit++;
            }
        }
        return limit;
    }


    public static String[] handleStringOvernight(int time, int startOverNight, int endOvernight) {
        String[] t = new String[2];
        if (startOverNight > endOvernight) {
            if (time > startOverNight && time <= 24) {
                //Set startHour = 00:00
                t[0] = "00:00";
                //Set endHour = 00:00
                t[1] = "00:00";
            } else if (time < endOvernight) {
                //Set startHour = endOverNight;
                t[0] = endOvernight + ":00";
                //Set endHour = ++
                t[1] = (endOvernight + 1) + ":00";
            } else if (time < startOverNight && time >= endOvernight) {
                //Set startHour = time;
                t[0] = time + ":00";
                //Set endHour = time + 1;
                t[1] = (time + 1) + ":00";
            }
        } else if (endOvernight > startOverNight) {
            if (time > endOvernight && time <= 24) {
                //Set startHour = time;
                t[0] = time + ":00";
                //Set endHour = time + 1;
                t[1] = (time + 1) + ":00";
            } else if (time < startOverNight) {
                //Set startHour = time
                t[0] = time + ":00";
                //Set endHour = time + 1;
                t[1] = (time + 1) + ":00";
            } else if (time >= startOverNight && time <= endOvernight) {
                //Set startHour = startOvernight + 1
                t[0] = (endOvernight + 1) + ":00";
                //Set endHour = +;
                t[1] = (endOvernight + 2) + ":00";
            }
        }

        return t;
    }


    public static int checkLimitTime(int time, int startOvernight, int endOvernight, int startCineJoy) {
        int t;
        if (startOvernight > endOvernight) {
            startOvernight = endOvernight;
        }

        if (time > startOvernight) {
            t = time;
        } else {
            t = startOvernight;
        }

        if (t < startCineJoy && startCineJoy > 0) {
            t = startCineJoy;
        }

        return t;
    }

}
