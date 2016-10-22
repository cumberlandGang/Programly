package com.cumberlandGang;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * Created by nate on 10/22/16.
 */
public class Util {

    public static Date nowToSystemTime() {
        LocalDateTime currentDate = LocalDateTime.now();

        String dateString = ""
                + currentDate.getMonthValue()
                + "/"
                + currentDate.getDayOfMonth()
                + "/"
                + currentDate.getYear()
                + " "
                + currentDate.getHour()
                + ":"
                + currentDate.getMinute()
                + ":"
                + currentDate.getSecond();


        // M = month, d=day, y=year, k=24-hour-time hour, m=minute, s=second
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy kk:mm:ss");

        Date currentTime = null;

        try {
            currentTime = df.parse(dateString);
        } catch (ParseException e) {
            System.err.println("[Error! Util.40]: Improper date format detected");
            e.printStackTrace();
        }

        return currentTime;
    }
}
