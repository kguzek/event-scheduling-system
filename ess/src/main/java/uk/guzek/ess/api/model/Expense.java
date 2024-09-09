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
@Table(name="expenses")
@AllArgsConstructor
@NoArgsConstructor
public class Expense {
  @Id
  @GeneratedValue
  private Long id;
  @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event.id")
  private Event event;
  private String title;
  private int costCents;
  private Date datetime;
}