package pl.papuda.ess.server.api.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pl.papuda.ess.server.api.model.Resource;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

}
