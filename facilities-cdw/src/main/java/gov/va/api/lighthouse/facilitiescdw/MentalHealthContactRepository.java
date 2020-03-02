package gov.va.api.lighthouse.facilitiescdw;

import gov.va.api.health.autoconfig.logging.Loggable;
import org.springframework.data.repository.PagingAndSortingRepository;

@Loggable
public interface MentalHealthContactRepository
    extends PagingAndSortingRepository<MentalHealthContactEntity, String> {}
