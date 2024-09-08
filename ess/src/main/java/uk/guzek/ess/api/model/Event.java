package uk.guzek.ess.api.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
  private Date datetime;
  private Location location;
  private Frequency frequency;
  @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "user.id")
  private User creator;
}
