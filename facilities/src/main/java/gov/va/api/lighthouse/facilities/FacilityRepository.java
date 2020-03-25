package gov.va.api.lighthouse.facilities;

import gov.va.api.health.autoconfig.logging.Loggable;
import java.util.Collection;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Loggable
@Transactional(isolation = Isolation.READ_UNCOMMITTED)
public interface FacilityRepository extends CrudRepository<FacilityEntity, FacilityEntity.Pk> {
  List<FacilityEntity> findByIdIn(Collection<FacilityEntity.Pk> ids);
}
