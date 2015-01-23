package com.sakurafish.common.lib;

import java.util.Calendar;

/**
 * Created by sakura on 2014/12/28.
 */
public class TimeUtils {

    public static int getAge(int year, int month, int day) {
        Calendar birthdayCal = Calendar.getInstance();
        birthdayCal.set(year, month, day);
        Calendar todayCal = Calendar.getInstance();
        int yearDiff = todayCal.get(Calendar.YEAR)
                - birthdayCal.get(Calendar.YEAR);
        if (todayCal.get(Calendar.MONTH) < birthdayCal.get(Calendar.MONTH)) {
            yearDiff--;
        } else if (todayCal.get(Calendar.MONTH) == birthdayCal
                .get(Calendar.MONTH)) {
            if (todayCal.get(Calendar.DAY_OF_MONTH) < birthdayCal
                    .get(Calendar.DAY_OF_MONTH)) {
                yearDiff--;
            }
        }
        int gene = (int) Math.floor(yearDiff / 10);
        return gene;
    }

    private TimeUtils(){}
}
