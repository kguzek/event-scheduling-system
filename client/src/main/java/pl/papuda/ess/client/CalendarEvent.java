package pl.papuda.ess.client;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import pl.papuda.ess.client.model.Event;
import pl.papuda.ess.client.model.Location;
import pl.papuda.ess.client.model.User;

public class CalendarEvent extends javax.swing.JPanel {

    private final Event event;
    private final ZoneId zone = ZoneId.systemDefault();
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("E, dd'/'MM'/'yyyy");
    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

    public CalendarEvent(Event event) {
        initComponents();
        this.event = event;
        initEvent();
    }

    public static String ordinal(int i) {
        String[] suffixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return i + "th";
            default:
                return i + suffixes[i % 10];
        }
    }

    private String getEventFrequency(LocalDate date, String frequency) {
        String dayOfMonth = ordinal(date.getDayOfMonth());
        String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        String monthName = date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        switch (frequency) {
            case "DAILY":
                return "Every day";
            case "WEEKLY":
                return "Every " + dayOfWeek;
            case "FORTNIGHTLY":
                return "Every other " + dayOfWeek;
            case "MONTHLY":
                return "On the " + dayOfMonth + " day of every month";
            case "YEARLY":
                return "Every " + dayOfMonth + " of " + monthName;
            case null:
            default:
                return "One-time event";
        }
    }

    private String formatTime(String timestamp) {
        Instant instant = Instant.parse(timestamp);
        return timeFormat.format(instant.atZone(zone));
    }

    private String formatLocation(Location location) {
        String text = String.format("%s, %s %s, %s", location.getStreet(), location.getCode(), location.getCity(),
                location.getCountry());
        String additionalInformation = location.getAdditionalInformation();
        if (additionalInformation != null) {
            text += " (" + additionalInformation + ")";
        }
        return text;
    }
    
    private class DeleteEvent extends Thread {
        @Override
        public void run() {
            delete();
        }
        
        private void showErrorDialog(String message) {
            JOptionPane.showMessageDialog(null, message, "Problem deleting event", JOptionPane.ERROR_MESSAGE);
            System.out.println("closed dialog");
        }
        
        private void delete() {
            HttpResponse response;
            Long eventId = event.getId();
            String endpoint = "/staff/event/" + eventId;
            try {
                response = Web.sendDeleteRequest(endpoint);
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                showErrorDialog("A network error occurred while deleting that event. Please try again later.");
                return;
            }
            int code = response.statusCode();
            if (code == 403) {
                showErrorDialog("You do not have permission to delete this event.");
                return;
            }
            if (code != 204) {
                String errorMessage = Web.getErrorMessage(response);
                showErrorDialog(errorMessage);
                return;
            }
            MainWindow mainWindow = (MainWindow) getTopLevelAncestor();
            mainWindow.removeEvent(eventId);
        }
    }

    private void initEvent() {
        LocalDate eventDate = Instant.parse(event.getStartTime()).atZone(zone).toLocalDate();
        String frequencyText = getEventFrequency(eventDate, event.getFrequency());
        lblEventFrequency.setText(frequencyText);
        lblEventTitle.setText(event.getTitle());
        String dateText = eventDate.format(dateFormat);
        lblEventDate.setText(dateText);
        String timeText = formatTime(event.getStartTime());
        String endTime = event.getEndTime();
        if (endTime != null) {
            timeText += " - " + formatTime(endTime);
        }
        lblEventTime.setText(timeText);
        lblEventAddress.setText(formatLocation(event.getLocation()));
        if (Web.user == null || !"STAFF".equals(Web.user.getRole())) {
            btnEventOptions.setVisible(false);
        }
        for (User participant : event.getAttendees()) {
            if (participant.getId() == Web.user.getId())
            cbxToggleParticipation.setSelected(false);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pmEventOptions = new javax.swing.JPopupMenu();
        pmiEventEdit = new javax.swing.JMenuItem();
        pmiEventDelete = new javax.swing.JMenuItem();
        pmiEventBudget = new javax.swing.JMenuItem();
        lblEventDate = new javax.swing.JLabel();
        lblEventTime = new javax.swing.JLabel();
        btnEventOptions = new javax.swing.JButton();
        lblEventTitle = new javax.swing.JLabel();
        lblEventAddress = new javax.swing.JLabel();
        lblEventFrequency = new javax.swing.JLabel();
        lblDateTimeSeparator = new javax.swing.JLabel();
        cbxToggleParticipation = new javax.swing.JCheckBox();

        pmiEventEdit.setText("Edit event");
        pmiEventEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmiEventEditActionPerformed(evt);
            }
        });
        pmEventOptions.add(pmiEventEdit);

        pmiEventDelete.setText("Delete event");
        pmiEventDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pmiEventDeleteActionPerformed(evt);
            }
        });
        pmEventOptions.add(pmiEventDelete);

        pmiEventBudget.setText("View budget");
        pmEventOptions.add(pmiEventBudget);

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        setForeground(new java.awt.Color(0, 0, 0));
        setMaximumSize(new java.awt.Dimension(32767, 120));

        lblEventDate.setForeground(new java.awt.Color(102, 102, 102));
        lblEventDate.setText("Mon, 12.09.2024");

        lblEventTime.setForeground(new java.awt.Color(102, 102, 102));
        lblEventTime.setText("08:30 - 19:00");

        btnEventOptions.setForeground(new java.awt.Color(0, 0, 0));
        btnEventOptions.setText(" ⋮ ");
        btnEventOptions.setBorder(null);
        btnEventOptions.setContentAreaFilled(false);
        btnEventOptions.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEventOptions.setFocusPainted(false);
        btnEventOptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEventOptionsActionPerformed(evt);
            }
        });

        lblEventTitle.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblEventTitle.setForeground(new java.awt.Color(51, 51, 51));
        lblEventTitle.setText("Very important meeting which I absolutely cannot miss");
        lblEventTitle.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        lblEventAddress.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        lblEventAddress.setForeground(new java.awt.Color(51, 51, 51));
        lblEventAddress.setText("123 Dave Prince's Ave.");

        lblEventFrequency.setForeground(new java.awt.Color(102, 102, 102));
        lblEventFrequency.setText("Every other Monday");

        lblDateTimeSeparator.setForeground(new java.awt.Color(132, 132, 132));
        lblDateTimeSeparator.setText("•");

        cbxToggleParticipation.setBackground(new java.awt.Color(255, 255, 255));
        cbxToggleParticipation.setForeground(new java.awt.Color(51, 51, 51));
        cbxToggleParticipation.setText("Participating");
        cbxToggleParticipation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxToggleParticipationActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblEventTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblEventAddress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblEventFrequency, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(lblEventDate)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblDateTimeSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblEventTime)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 92, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbxToggleParticipation)
                            .addComponent(btnEventOptions, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEventDate)
                    .addComponent(lblEventTime)
                    .addComponent(btnEventOptions)
                    .addComponent(lblDateTimeSeparator))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblEventTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addComponent(lblEventAddress)
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEventFrequency)
                    .addComponent(cbxToggleParticipation))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void pmiEventDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pmiEventDeleteActionPerformed
        new DeleteEvent().start();
    }//GEN-LAST:event_pmiEventDeleteActionPerformed

    private void btnEventOptionsActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnEventOptionsActionPerformed
        pmEventOptions.show(btnEventOptions, 20, 0);
    }// GEN-LAST:event_btnEventOptionsActionPerformed

    private void pmiEventEditActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_pmiEventEditActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_pmiEventEditActionPerformed

    private void cbxToggleParticipationActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jCheckBox1ActionPerformed
        HttpResponse response;
        String endpoint = "/private/event/" + event.getId() + "/attendee";
        try {
            response = cbxToggleParticipation.isSelected() ? Web.sendPostRequest(endpoint) : Web.sendDeleteRequest(endpoint);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(CalendarEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }// GEN-LAST:event_jCheckBox1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEventOptions;
    private javax.swing.JCheckBox cbxToggleParticipation;
    private javax.swing.JLabel lblDateTimeSeparator;
    private javax.swing.JLabel lblEventAddress;
    private javax.swing.JLabel lblEventDate;
    private javax.swing.JLabel lblEventFrequency;
    private javax.swing.JLabel lblEventTime;
    private javax.swing.JLabel lblEventTitle;
    private javax.swing.JPopupMenu pmEventOptions;
    private javax.swing.JMenuItem pmiEventBudget;
    private javax.swing.JMenuItem pmiEventDelete;
    private javax.swing.JMenuItem pmiEventEdit;
    // End of variables declaration//GEN-END:variables
}
