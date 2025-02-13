/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package pl.papuda.ess.client.components.home.calendar;

import java.awt.Component;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.List;

import pl.papuda.ess.client.model.Event;

public class CalendarPanel extends javax.swing.JPanel {

    private int month;
    private int year;
    private List<Event> events;
    private final ZoneId zone = ZoneId.systemDefault();

    public CalendarPanel(int month, int year, List<Event> events) {
        initComponents();
        this.month = month;
        this.year = year;
        this.events = events;
        init();
    }

    private void init() {
        updateCells();
    }

    private boolean occursOn(Event event, LocalDate date) {
        LocalDate eventDate = Instant.parse(event.getStartTime()).atZone(zone).toLocalDate();
        if (date.equals(eventDate)) {
            return true;
        }
        if (eventDate.isAfter(date)) {
            return false;
        }
        switch (event.getFrequency()) {
            case "DAILY":
                return true;
            case "WEEKLY":
                return eventDate.getDayOfWeek().equals(date.getDayOfWeek());
            case "FORTNIGHTLY":
                long daysBetween = ChronoUnit.DAYS.between(eventDate, date);
                return daysBetween % 14 == 0;
            case "MONTHLY":
                return eventDate.getDayOfMonth() == date.getDayOfMonth();
            case "YEARLY":
                return eventDate.getDayOfYear() == date.getDayOfYear();
            case null:
            default:
                return false;
        }
    }

    private Long getEventId(LocalDate date) {
        if (events == null) {
            return null;
        }
        for (Event event : events) {
            if (occursOn(event, date)) {
                return event.getId();
            }
        }
        return null;
    }

    private void updateCells() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DATE, 1);
        int startDay = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7;
        calendar.add(Calendar.DATE, -startDay);
        Component[] components = getComponents();
        for (int i = 0; i < components.length; i++) {
            CalendarCell calendarCell = (CalendarCell) components[i];
            if (i < 7) {
                calendarCell.markAsTitleCell();
                continue;
            }
            int day = calendar.get(Calendar.DATE);
            LocalDate date = calendar.toInstant().atZone(zone).toLocalDate();
            String text = (day + "");
            Long eventId = getEventId(date);
            if (eventId != null) {
                calendarCell.setEventId(eventId);
            }
            calendarCell.setText(text);
            calendarCell.setDate(calendar.getTime());
            boolean isCurrentMonth = calendar.get(Calendar.MONTH) == month;
            calendarCell.updateColor(isCurrentMonth);
            if (isCurrentMonth && date.equals(LocalDate.now())) {
                calendarCell.markAsToday();
            }
            calendar.add(Calendar.DATE, 1);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        calendarCell1 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell2 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell3 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell4 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell5 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell6 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell7 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell8 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell9 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell10 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell11 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell12 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell13 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell14 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell15 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell16 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell17 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell18 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell19 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell20 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell21 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell22 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell23 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell24 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell25 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell26 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell27 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell28 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell29 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell30 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell31 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell32 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell33 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell34 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell35 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell36 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell37 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell38 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell39 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell40 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell41 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell42 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell43 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell44 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell45 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell46 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell47 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell48 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();
        calendarCell49 = new pl.papuda.ess.client.components.home.calendar.CalendarCell();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new java.awt.GridLayout(7, 7));

        calendarCell1.setBackground(new java.awt.Color(255, 255, 255));
        calendarCell1.setForeground(new java.awt.Color(51, 51, 51));
        calendarCell1.setText("Mon");
        add(calendarCell1);

        calendarCell2.setBackground(new java.awt.Color(255, 255, 255));
        calendarCell2.setForeground(new java.awt.Color(51, 51, 51));
        calendarCell2.setText("Tue");
        add(calendarCell2);

        calendarCell3.setBackground(new java.awt.Color(255, 255, 255));
        calendarCell3.setForeground(new java.awt.Color(51, 51, 51));
        calendarCell3.setText("Wed");
        add(calendarCell3);

        calendarCell4.setBackground(new java.awt.Color(255, 255, 255));
        calendarCell4.setForeground(new java.awt.Color(51, 51, 51));
        calendarCell4.setText("Thu");
        add(calendarCell4);

        calendarCell5.setBackground(new java.awt.Color(255, 255, 255));
        calendarCell5.setForeground(new java.awt.Color(51, 51, 51));
        calendarCell5.setText("Fri");
        add(calendarCell5);

        calendarCell6.setForeground(new java.awt.Color(153, 153, 153));
        calendarCell6.setText("Sat");
        add(calendarCell6);

        calendarCell7.setForeground(new java.awt.Color(255, 153, 153));
        calendarCell7.setText("Sun");
        add(calendarCell7);

        calendarCell8.setBackground(new java.awt.Color(255, 255, 255));
        calendarCell8.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell8);

        calendarCell9.setBackground(new java.awt.Color(255, 255, 255));
        calendarCell9.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell9);

        calendarCell10.setBackground(new java.awt.Color(255, 255, 255));
        calendarCell10.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell10);

        calendarCell11.setBackground(new java.awt.Color(255, 255, 255));
        calendarCell11.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell11);

        calendarCell12.setBackground(new java.awt.Color(255, 255, 255));
        calendarCell12.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell12);

        calendarCell13.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell13);

        calendarCell14.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell14);

        calendarCell15.setBackground(new java.awt.Color(255, 255, 255));
        calendarCell15.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell15);

        calendarCell16.setBackground(new java.awt.Color(255, 255, 255));
        calendarCell16.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell16);

        calendarCell17.setBackground(new java.awt.Color(255, 255, 255));
        calendarCell17.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell17);

        calendarCell18.setBackground(new java.awt.Color(255, 255, 255));
        calendarCell18.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell18);

        calendarCell19.setBackground(new java.awt.Color(255, 255, 255));
        calendarCell19.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell19);

        calendarCell20.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell20);

        calendarCell21.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell21);

        calendarCell22.setBackground(new java.awt.Color(255, 255, 255));
        calendarCell22.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell22);

        calendarCell23.setBackground(new java.awt.Color(255, 255, 255));
        calendarCell23.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell23);

        calendarCell24.setBackground(new java.awt.Color(255, 255, 255));
        calendarCell24.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell24);

        calendarCell25.setBackground(new java.awt.Color(255, 255, 255));
        calendarCell25.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell25);

        calendarCell26.setBackground(new java.awt.Color(255, 255, 255));
        calendarCell26.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell26);

        calendarCell27.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell27);

        calendarCell28.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell28);

        calendarCell29.setBackground(new java.awt.Color(255, 255, 255));
        calendarCell29.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell29);

        calendarCell30.setBackground(new java.awt.Color(255, 255, 255));
        calendarCell30.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell30);

        calendarCell31.setBackground(new java.awt.Color(255, 255, 255));
        calendarCell31.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell31);

        calendarCell32.setBackground(new java.awt.Color(255, 255, 255));
        calendarCell32.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell32);

        calendarCell33.setBackground(new java.awt.Color(255, 255, 255));
        calendarCell33.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell33);

        calendarCell34.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell34);

        calendarCell35.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell35);

        calendarCell36.setBackground(new java.awt.Color(255, 255, 255));
        calendarCell36.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell36);

        calendarCell37.setBackground(new java.awt.Color(255, 255, 255));
        calendarCell37.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell37);

        calendarCell38.setBackground(new java.awt.Color(255, 255, 255));
        calendarCell38.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell38);

        calendarCell39.setBackground(new java.awt.Color(255, 255, 255));
        calendarCell39.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell39);

        calendarCell40.setBackground(new java.awt.Color(255, 255, 255));
        calendarCell40.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell40);

        calendarCell41.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell41);

        calendarCell42.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell42);

        calendarCell43.setBackground(new java.awt.Color(255, 255, 255));
        calendarCell43.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell43);

        calendarCell44.setBackground(new java.awt.Color(255, 255, 255));
        calendarCell44.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell44);

        calendarCell45.setBackground(new java.awt.Color(255, 255, 255));
        calendarCell45.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell45);

        calendarCell46.setBackground(new java.awt.Color(255, 255, 255));
        calendarCell46.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell46);

        calendarCell47.setBackground(new java.awt.Color(255, 255, 255));
        calendarCell47.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell47);

        calendarCell48.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell48);

        calendarCell49.setForeground(new java.awt.Color(51, 51, 51));
        add(calendarCell49);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell1;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell10;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell11;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell12;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell13;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell14;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell15;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell16;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell17;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell18;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell19;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell2;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell20;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell21;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell22;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell23;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell24;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell25;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell26;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell27;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell28;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell29;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell3;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell30;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell31;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell32;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell33;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell34;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell35;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell36;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell37;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell38;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell39;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell4;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell40;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell41;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell42;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell43;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell44;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell45;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell46;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell47;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell48;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell49;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell5;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell6;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell7;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell8;
    private pl.papuda.ess.client.components.home.calendar.CalendarCell calendarCell9;
    // End of variables declaration//GEN-END:variables
}
