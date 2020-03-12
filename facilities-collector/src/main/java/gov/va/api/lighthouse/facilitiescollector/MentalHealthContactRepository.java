package gov.va.api.lighthouse.facilitiescollector;

import gov.va.api.health.autoconfig.logging.Loggable;
import org.springframework.data.repository.PagingAndSortingRepository;

@Loggable
public interface MentalHealthContactRepository
    extends PagingAndSortingRepository<MentalHealthContactEntity, String> {}
