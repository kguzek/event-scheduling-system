package uk.guzek.ess.server.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@Table(name="resources")
@AllArgsConstructor
@NoArgsConstructor
public class Resource {
  @Id
  @GeneratedValue
  private Long id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JsonIgnore
  private Event event;
  private String name;
  private int currentAmount;
  private int goal;
  private String imageUrl;
}