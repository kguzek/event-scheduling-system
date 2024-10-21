package pl.papuda.ess.client.home;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.CardLayout;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.papuda.ess.client.MainWindow;
import pl.papuda.ess.client.Web;
import pl.papuda.ess.client.model.Event;
import pl.papuda.ess.client.model.Location;
import pl.papuda.ess.client.model.PartialEvent;

public class EventsList extends javax.swing.JPanel {
    
    private final ZoneOffset zoneOffset = OffsetDateTime.now().getOffset();

    private final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm");
    
    private final String[] eventFrequencies = new String[] {
        null,
        "DAILY",
        "WEEKLY",
        "FORTNIGHTLY",
        "MONTHLY",
        "YEARLY"
    };
    
    private final Integer[] reminderTimesMinutes = new Integer[] {
        null,
        5,
        15,
        30,
        60,
        60 * 2,
        60 * 24
    };
    
    private Long eventId = null;
    
    /**
     * Creates new form EventsList
     */
    public EventsList() {
        initComponents();
    }
    
    public void updateEventsList(List<Event> events) {
        pnlEventsList.removeAll();
        for (Event event : events) {
            System.out.println("Adding event " + event.getTitle());
            pnlEventsList.add(new EventListItem(event, this::editEvent));
        }
        repaint();
        revalidate();
    }
    
    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.atOffset(zoneOffset).toString();
    }
    
    int getDifferenceMinutes(String date1, String date2) {
        if (date1 == null || date2 == null) return 0;
        Instant instant1 = Instant.parse(date1);
        Instant instant2 = Instant.parse(date2);
        Long diffSeconds = Math.abs(instant1.getEpochSecond() - instant2.getEpochSecond());
        return (int) (diffSeconds / 60);
    }
    
    Date parseTimestamp(String timestamp) {
        if (timestamp == null) {
            return null;
        }
        TemporalAccessor ta = DateTimeFormatter.ISO_INSTANT.parse(timestamp);
        Instant instant = Instant.from(ta);
        Date date = Date.from(instant);
        return date;
    }
    
    String orEmptyString(String s) {
        return s == null ? "" : s;
    }
    
    void editEvent(Event event) {
        this.eventId = event.getId();
        txtEventCreateError.setText("");
        btnCreateEvent.setText("EDIT");
        btnToggleEventView.setText("Show events list");
        showLayoutCard("eventEditor");
        iptEventTitle.setText(event.getTitle());
        iptEventFeedbackMessage.setText(event.getFeedbackMessage());
        iptEventDate.setValue(parseTimestamp(event.getStartTime()));
        iptEventStartTime.setValue(parseTimestamp(event.getStartTime()));
        iptEventEndTime.setValue(parseTimestamp(event.getEndTime()));
        Location location = event.getLocation();
        iptEventStreet.setText(location.getStreet());
        iptEventCode.setText(location.getCode());
        iptEventCity.setText(location.getCity());
        iptEventCountry.setText(location.getCountry());
        iptEventInformation.setText(orEmptyString(location.getAdditionalInformation()));
        Integer budget = event.getBudgetCents();
        iptEventBudget.setValue(budget == null ? 0 : (budget / 100.0));
        int diffMinutes = getDifferenceMinutes(event.getStartTime(), event.getReminderTime());
        int selectedIndex = 0;
        if (diffMinutes != 0) {
            selectedIndex = Arrays.asList(reminderTimesMinutes).indexOf(diffMinutes);
        }
        cbbEventReminderTime.setSelectedIndex(Math.max(selectedIndex, 0));
        selectedIndex = Arrays.asList(eventFrequencies).indexOf(event.getFrequency());
        cbbEventFrequency.setSelectedIndex(Math.max(selectedIndex, 0));
    }
    
    String getEventJson() throws Exception {
        String eventDate = iptEventDate.getText();
        LocalDateTime eventStart, eventEnd = null;
        try {
            eventStart = LocalDateTime.parse(eventDate + "T" + iptEventStartTime.getText(), dateTimeFormat);
            if (cbxEventEndTime.isSelected()) {
                eventEnd = LocalDateTime.parse(eventDate + "T" + iptEventEndTime.getText(), dateTimeFormat);
            }
        } catch (DateTimeParseException ex) {
            throw new Exception("Invalid event start or end date");
        }
        String additionalInformation = iptEventInformation.getText();
        if (additionalInformation.equals("")) {
            additionalInformation = null;
        }
        String feedbackMessage = iptEventFeedbackMessage.getText();
        if (feedbackMessage.equals("")) {
            feedbackMessage = null;
        }
        String frequency = eventFrequencies[cbbEventFrequency.getSelectedIndex()];
        Integer reminderDelayMinutes = reminderTimesMinutes[cbbEventReminderTime.getSelectedIndex()];
        LocalDateTime eventReminder = null;
        if (reminderDelayMinutes != null) {
            eventReminder = eventStart.minusMinutes(reminderDelayMinutes);
        }
        var budget = (Number) iptEventBudget.getValue();
        Integer budgetCents = null;
        System.out.println("Budget value: " + budget);
        if (budget != null) {
            if (budget instanceof Long) {
                System.out.println("Budget is long");
                budgetCents = budget.intValue() * 100;
            } else if (budget instanceof Double d) {
                System.out.println("System is double");
                budgetCents = ((Double) (d * 100.0)).intValue();
            }
        }
        System.out.println("budgetCents: " + budgetCents);
        Location location = Location.builder().street(iptEventStreet.getText()).city(iptEventCity.getText())
                .code(iptEventCode.getText()).country(iptEventCountry.getText()).additionalInformation(additionalInformation).build();
        PartialEvent event = PartialEvent.builder().title(iptEventTitle.getText())
            .organiserName("John Doe").startTime(formatDateTime(eventStart)).endTime(formatDateTime(eventEnd))
            .location(location).frequency(frequency).feedbackMessage(feedbackMessage)
            .reminderTime(formatDateTime(eventReminder)).budgetCents(budgetCents).build();
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(event);   
        return json;
    }
    
    private void sendEventRequest() throws Exception {
        HttpResponse<String> response;
        String json = getEventJson();
        try {
            response = eventId == null ? Web.sendPostRequest("/staff/event", json) : Web.sendPutRequest("/staff/event/" + eventId, json);
        } catch (IOException | InterruptedException ex) {
            txtEventCreateError.setText("A network error occurred while creating the event. Please try again later.");
            return;
        }
        int code = response.statusCode();
        if (code != 200) {
            txtEventCreateError.setText(Web.getErrorMessage(response));
            return;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        Event createdEvent;
        try {
            createdEvent = objectMapper.readValue(response.body(), Event.class);
        } catch (JsonProcessingException ex) {
            txtEventCreateError.setText("200 response with unparsable body");
            return;
        }
        showLayoutCard("eventsList");
        MainWindow mainWindow = (MainWindow) getTopLevelAncestor();
        if (eventId == null) {
            mainWindow.addEvent(createdEvent);
        } else {
            btnCreateEvent.setText("CREATE");
            mainWindow.updateEvent(createdEvent);
        }
        btnToggleEventViewActionPerformed(null);
    }
    
    private class CreateEvent extends Thread {
        @Override
        public void run() {
            btnCreateEvent.setEnabled(false);
            txtEventCreateError.setText("");
            try {
                sendEventRequest();
            } catch (Exception ex) {
                txtEventCreateError.setText(ex.getMessage());
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                btnCreateEvent.setEnabled(true);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnToggleEventView = new javax.swing.JButton();
        btnSendMessage = new javax.swing.JButton();
        pnlEventsContainer = new javax.swing.JPanel();
        scpEventsList = new javax.swing.JScrollPane();
        pnlEventsList = new javax.swing.JPanel();
        pnlEventEditorContainer = new javax.swing.JPanel();
        pnlCreateEvent = new javax.swing.JPanel();
        lblEventTitle = new javax.swing.JLabel();
        iptEventTitle = new javax.swing.JTextField();
        lblEventFrequency = new javax.swing.JLabel();
        cbbEventFrequency = new javax.swing.JComboBox<>();
        lblEventLocation = new javax.swing.JLabel();
        pnlEventAddress = new javax.swing.JPanel();
        lblEventStreet = new javax.swing.JLabel();
        iptEventStreet = new javax.swing.JTextField();
        lblEventCode = new javax.swing.JLabel();
        iptEventCode = new javax.swing.JTextField();
        lblEventCity = new javax.swing.JLabel();
        iptEventCity = new javax.swing.JTextField();
        lblEventCountry = new javax.swing.JLabel();
        iptEventCountry = new javax.swing.JTextField();
        lblEventInformation = new javax.swing.JLabel();
        iptEventInformation = new javax.swing.JTextField();
        lblEventFeedbackMessage = new javax.swing.JLabel();
        iptEventFeedbackMessage = new javax.swing.JTextField();
        btnCreateEvent = new javax.swing.JButton();
        lblEventDate = new javax.swing.JLabel();
        iptEventDate = new javax.swing.JFormattedTextField();
        lblEventStartTime = new javax.swing.JLabel();
        iptEventStartTime = new javax.swing.JFormattedTextField();
        iptEventEndTime = new javax.swing.JFormattedTextField();
        cbxEventEndTime = new javax.swing.JCheckBox();
        cbbEventReminderTime = new javax.swing.JComboBox<>();
        lblEventReminderTime = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtEventCreateError = new javax.swing.JTextArea();
        lblEventBudget = new javax.swing.JLabel();
        iptEventBudget = new javax.swing.JFormattedTextField();

        btnToggleEventView.setText("Create event");
        btnToggleEventView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnToggleEventViewActionPerformed(evt);
            }
        });

        btnSendMessage.setText("Send message");

        pnlEventsContainer.setBackground(new java.awt.Color(204, 204, 204));
        pnlEventsContainer.setLayout(new java.awt.CardLayout());

        scpEventsList.setBackground(new java.awt.Color(204, 204, 204));
        scpEventsList.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scpEventsList.setViewportView(null);

        pnlEventsList.setBackground(new java.awt.Color(204, 204, 204));
        pnlEventsList.setLayout(new javax.swing.BoxLayout(pnlEventsList, javax.swing.BoxLayout.Y_AXIS));
        scpEventsList.setViewportView(pnlEventsList);

        pnlEventsContainer.add(scpEventsList, "eventsList");

        pnlEventEditorContainer.setBackground(new java.awt.Color(204, 204, 204));
        pnlEventEditorContainer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        pnlEventEditorContainer.setToolTipText("");

        pnlCreateEvent.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        lblEventTitle.setText("* Event title");

        lblEventFrequency.setText("* Event frequency");

        cbbEventFrequency.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "One-time event", "Daily", "Weekly", "Fortnightly", "Monthly", "Yearly" }));

        lblEventLocation.setText("Event location");

        pnlEventAddress.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lblEventStreet.setText("* Street and building number");

        lblEventCode.setText("* Post code");

        lblEventCity.setText("* City/Town/Municipality");

        lblEventCountry.setText("* Country");

        lblEventInformation.setText("Additional information (floor, room etc.)");

        javax.swing.GroupLayout pnlEventAddressLayout = new javax.swing.GroupLayout(pnlEventAddress);
        pnlEventAddress.setLayout(pnlEventAddressLayout);
        pnlEventAddressLayout.setHorizontalGroup(
            pnlEventAddressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEventAddressLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlEventAddressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(iptEventStreet)
                    .addComponent(iptEventCountry)
                    .addComponent(iptEventInformation)
                    .addGroup(pnlEventAddressLayout.createSequentialGroup()
                        .addGroup(pnlEventAddressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblEventStreet)
                            .addComponent(lblEventCountry)
                            .addComponent(lblEventInformation))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnlEventAddressLayout.createSequentialGroup()
                        .addComponent(iptEventCode, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(iptEventCity))
                    .addGroup(pnlEventAddressLayout.createSequentialGroup()
                        .addComponent(lblEventCode)
                        .addGap(41, 41, 41)
                        .addComponent(lblEventCity, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlEventAddressLayout.setVerticalGroup(
            pnlEventAddressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEventAddressLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblEventStreet)
                .addGap(0, 0, 0)
                .addComponent(iptEventStreet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlEventAddressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblEventCity)
                    .addComponent(lblEventCode))
                .addGap(0, 0, 0)
                .addGroup(pnlEventAddressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(iptEventCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(iptEventCity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblEventCountry)
                .addGap(0, 0, 0)
                .addComponent(iptEventCountry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblEventInformation)
                .addGap(0, 0, 0)
                .addComponent(iptEventInformation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblEventFeedbackMessage.setText("Feedback message (sent to participants after event ends)");

        btnCreateEvent.setText("CREATE");
        btnCreateEvent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateEventActionPerformed(evt);
            }
        });

        lblEventDate.setText("* Date (dd/mm/yyyy)");

        iptEventDate.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT))));

        lblEventStartTime.setText("* Start time (hh:mm)");

        iptEventStartTime.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT))));
        iptEventStartTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                iptEventStartTimeActionPerformed(evt);
            }
        });

        iptEventEndTime.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT))));
        iptEventEndTime.setEnabled(false);
        iptEventEndTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                iptEventEndTimeActionPerformed(evt);
            }
        });

        cbxEventEndTime.setText("End time (hh:mm)");
        cbxEventEndTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxEventEndTimeActionPerformed(evt);
            }
        });

        cbbEventReminderTime.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No reminder", "5 minutes before", "15 minutes before", "30 minutes before", "1 hour before", "2 hours before", "1 day before" }));

        lblEventReminderTime.setText("Event reminder (send to participants before event starts)");

        jScrollPane1.setBorder(null);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        txtEventCreateError.setEditable(false);
        txtEventCreateError.setColumns(20);
        txtEventCreateError.setForeground(new java.awt.Color(255, 51, 51));
        txtEventCreateError.setRows(5);
        txtEventCreateError.setText("A network error occurred while creating the event. Try again later.\n");
        txtEventCreateError.setOpaque(false);
        jScrollPane1.setViewportView(txtEventCreateError);

        lblEventBudget.setText("Event budget");

        iptEventBudget.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("¤#,##0.00"))));
        iptEventBudget.setText("£100");

        javax.swing.GroupLayout pnlCreateEventLayout = new javax.swing.GroupLayout(pnlCreateEvent);
        pnlCreateEvent.setLayout(pnlCreateEventLayout);
        pnlCreateEventLayout.setHorizontalGroup(
            pnlCreateEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCreateEventLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlCreateEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(iptEventTitle)
                    .addComponent(iptEventFeedbackMessage)
                    .addComponent(btnCreateEvent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cbbEventFrequency, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cbbEventReminderTime, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlEventAddress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addGroup(pnlCreateEventLayout.createSequentialGroup()
                        .addGroup(pnlCreateEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlCreateEventLayout.createSequentialGroup()
                                .addGroup(pnlCreateEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(iptEventDate, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblEventDate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(pnlCreateEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lblEventStartTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(iptEventStartTime))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(pnlCreateEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(cbxEventEndTime, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(iptEventEndTime)))
                            .addComponent(lblEventTitle)
                            .addComponent(lblEventFrequency)
                            .addComponent(lblEventLocation)
                            .addComponent(lblEventReminderTime)
                            .addComponent(lblEventFeedbackMessage)
                            .addComponent(lblEventBudget))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(iptEventBudget))
                .addContainerGap())
        );
        pnlCreateEventLayout.setVerticalGroup(
            pnlCreateEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCreateEventLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblEventTitle)
                .addGap(0, 0, 0)
                .addComponent(iptEventTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlCreateEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEventDate)
                    .addComponent(lblEventStartTime)
                    .addComponent(cbxEventEndTime))
                .addGap(0, 0, 0)
                .addGroup(pnlCreateEventLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(iptEventDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(iptEventStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(iptEventEndTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblEventFrequency)
                .addGap(0, 0, 0)
                .addComponent(cbbEventFrequency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblEventLocation)
                .addGap(4, 4, 4)
                .addComponent(pnlEventAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblEventReminderTime)
                .addGap(0, 0, 0)
                .addComponent(cbbEventReminderTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblEventFeedbackMessage)
                .addGap(0, 0, 0)
                .addComponent(iptEventFeedbackMessage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblEventBudget)
                .addGap(0, 0, 0)
                .addComponent(iptEventBudget, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(btnCreateEvent)
                .addContainerGap())
        );

        javax.swing.GroupLayout pnlEventEditorContainerLayout = new javax.swing.GroupLayout(pnlEventEditorContainer);
        pnlEventEditorContainer.setLayout(pnlEventEditorContainerLayout);
        pnlEventEditorContainerLayout.setHorizontalGroup(
            pnlEventEditorContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEventEditorContainerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlCreateEvent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlEventEditorContainerLayout.setVerticalGroup(
            pnlEventEditorContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlEventEditorContainerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlCreateEvent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pnlEventsContainer.add(pnlEventEditorContainer, "eventEditor");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnToggleEventView)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSendMessage)
                .addContainerGap(187, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(pnlEventsContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnToggleEventView)
                    .addComponent(btnSendMessage))
                .addContainerGap(752, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addGap(39, 39, 39)
                    .addComponent(pnlEventsContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
    }// </editor-fold>//GEN-END:initComponents

    
    private void showLayoutCard(String cardName) {
        CardLayout layout = (CardLayout) pnlEventsContainer.getLayout();
        layout.show(pnlEventsContainer, cardName);
    }
    
    private void btnToggleEventViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnToggleEventViewActionPerformed
        txtEventCreateError.setText("");
        eventId = null;
        btnCreateEvent.setText("CREATE");
        if (btnToggleEventView.getText().equals("Show events list")) {
            showLayoutCard("eventsList");
            btnToggleEventView.setText("Create event");
        } else {
            showLayoutCard("eventEditor");
            btnToggleEventView.setText("Show events list");
        }
        
    }//GEN-LAST:event_btnToggleEventViewActionPerformed

    private void iptEventEndTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iptEventEndTimeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_iptEventEndTimeActionPerformed

    private void iptEventStartTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iptEventStartTimeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_iptEventStartTimeActionPerformed

    private void cbxEventEndTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxEventEndTimeActionPerformed
        iptEventEndTime.setEnabled(cbxEventEndTime.isSelected());
    }//GEN-LAST:event_cbxEventEndTimeActionPerformed

    private void btnCreateEventActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateEventActionPerformed
        new CreateEvent().start();
    }//GEN-LAST:event_btnCreateEventActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCreateEvent;
    private javax.swing.JButton btnSendMessage;
    private javax.swing.JButton btnToggleEventView;
    private javax.swing.JComboBox<String> cbbEventFrequency;
    private javax.swing.JComboBox<String> cbbEventReminderTime;
    private javax.swing.JCheckBox cbxEventEndTime;
    private javax.swing.JFormattedTextField iptEventBudget;
    private javax.swing.JTextField iptEventCity;
    private javax.swing.JTextField iptEventCode;
    private javax.swing.JTextField iptEventCountry;
    private javax.swing.JFormattedTextField iptEventDate;
    private javax.swing.JFormattedTextField iptEventEndTime;
    private javax.swing.JTextField iptEventFeedbackMessage;
    private javax.swing.JTextField iptEventInformation;
    private javax.swing.JFormattedTextField iptEventStartTime;
    private javax.swing.JTextField iptEventStreet;
    private javax.swing.JTextField iptEventTitle;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblEventBudget;
    private javax.swing.JLabel lblEventCity;
    private javax.swing.JLabel lblEventCode;
    private javax.swing.JLabel lblEventCountry;
    private javax.swing.JLabel lblEventDate;
    private javax.swing.JLabel lblEventFeedbackMessage;
    private javax.swing.JLabel lblEventFrequency;
    private javax.swing.JLabel lblEventInformation;
    private javax.swing.JLabel lblEventLocation;
    private javax.swing.JLabel lblEventReminderTime;
    private javax.swing.JLabel lblEventStartTime;
    private javax.swing.JLabel lblEventStreet;
    private javax.swing.JLabel lblEventTitle;
    private javax.swing.JPanel pnlCreateEvent;
    private javax.swing.JPanel pnlEventAddress;
    private javax.swing.JPanel pnlEventEditorContainer;
    private javax.swing.JPanel pnlEventsContainer;
    private javax.swing.JPanel pnlEventsList;
    private javax.swing.JScrollPane scpEventsList;
    private javax.swing.JTextArea txtEventCreateError;
    // End of variables declaration//GEN-END:variables
}
