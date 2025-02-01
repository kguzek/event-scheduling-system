/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pl.papuda.ess.client.tools;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.TimerTask;
import java.util.Timer;

public class Time {

    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm");
    public static final ZoneOffset ZONE_OFFSET = OffsetDateTime.now().getOffset();

    public static Date parseTimestamp(String timestamp) {
        if (timestamp == null) {
            return null;
        }
        TemporalAccessor ta = DateTimeFormatter.ISO_INSTANT.parse(timestamp);
        Instant instant = Instant.from(ta);
        Date date = Date.from(instant);
        return date;
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.atOffset(ZONE_OFFSET).toString();
    }
    
    public static int getDifferenceMinutes(Instant instant1, Instant instant2) {
        if (instant1 == null || instant2 == null) {
            return 0;
        }
        Long diffSeconds = instant1.getEpochSecond() - instant2.getEpochSecond();
        int diff = (int) (diffSeconds / 60);
        return diff;
    }

    public static int getDifferenceMinutes(String dateString1, Instant instant2) {
        if (dateString1 == null || instant2 == null) {
            return 0;
        }
        Instant instant1 = Instant.parse(dateString1);
        return getDifferenceMinutes(instant1, instant2);
    }

    public static int getDifferenceMinutes(String dateString1, String dateString2) {
        if (dateString1 == null || dateString2 == null) {
            return 0;
        }
        Instant instant1 = Instant.parse(dateString1);
        Instant instant2 = Instant.parse(dateString2);
        return getDifferenceMinutes(instant1, instant2);
    }
    
    public static Instant getCurrentInstant() {
        LocalDateTime dateTime = LocalDateTime.now();
        Instant now = dateTime.toInstant(ZONE_OFFSET);
        return now;
    }
    
    public static Date getCurrentDate() {
        return Date.from(getCurrentInstant());
    }

    public static void scheduleAt(String startTime, Runnable callback) {
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                callback.run();
            }
        };

        Date executionTime = parseTimestamp(startTime);
        System.out.println("Scheduling event reminder for " + executionTime);
        timer.schedule(task, executionTime);
    }
}
