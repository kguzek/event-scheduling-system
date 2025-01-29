package pl.papuda.ess.server.api.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@Table(name = "tasks")
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Event event;
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private User assignee;
    private Date deadline;
    private TaskStatus status;
    private String additionalInformation;
}
