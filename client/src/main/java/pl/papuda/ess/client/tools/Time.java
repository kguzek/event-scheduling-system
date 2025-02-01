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

    public static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm");
    public static final ZoneOffset zoneOffset = OffsetDateTime.now().getOffset();

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
        return dateTime.atOffset(Time.zoneOffset).toString();
    }

    public static int getDifferenceMinutes(String date1, String date2) {
        if (date1 == null || date2 == null) {
            return 0;
        }
        Instant instant1 = Instant.parse(date1);
        Instant instant2 = Instant.parse(date2);
        Long diffSeconds = Math.abs(instant1.getEpochSecond() - instant2.getEpochSecond());
        return (int) (diffSeconds / 60);
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
        timer.schedule(task, executionTime);
    }
}
