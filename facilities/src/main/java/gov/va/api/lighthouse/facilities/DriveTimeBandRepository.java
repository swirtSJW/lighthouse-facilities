package gov.va.api.lighthouse.facilities;

import gov.va.api.health.autoconfig.logging.Loggable;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Loggable
@Transactional(isolation = Isolation.READ_UNCOMMITTED)
public interface DriveTimeBandRepository
    extends CrudRepository<DriveTimeBandEntity, DriveTimeBandEntity.Pk> {

  @Query("select e.id from #{#entityName} e")
  List<DriveTimeBandEntity.Pk> findAllIds();
}
