package uk.guzek.ess.api.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.guzek.ess.api.model.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

}
