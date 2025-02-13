package pl.papuda.ess.server.api.model;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@Table(name = "events")
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String organiserName;
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
    private Location location;
    private Frequency frequency;
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User creator;
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "event_attendance", joinColumns = @JoinColumn(name = "event_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> attendees;
    private int budgetCents;
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @OneToMany(fetch = FetchType.EAGER)
    private List<Expense> expenses;
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @OneToMany(fetch = FetchType.EAGER)
    private List<Resource> resources;
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @OneToMany(fetch = FetchType.EAGER)
    private List<Task> tasks;
    private ZonedDateTime reminderTime;
    private String feedbackMessage;
    private EventStatus status;
}
