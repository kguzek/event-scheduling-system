package pl.papuda.ess.client.model;

import lombok.Data;

@Data
public class Event {
    private Long id;
    private String title;
    private String organiserName;
    private String startTime;
    private String endTime;
    private Location location;
    private String frequency;
    private User creator;
    private User[] attendees;
    private Integer budgetCents;
    private Expense[] expenses;
    private Resource[] resources;
    private Task[] tasks;
    private String reminderTime;
    private String feedbackMessage;
    private String status;
}
