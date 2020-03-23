package gov.va.api.lighthouse.facilities.collectorapi;

import gov.va.api.lighthouse.facilities.api.collector.CollectorFacilitiesResponse;

/** This interface represents interactions with the Facilities Collector. */
public interface CollectorApi {
  /** Collect all of the facilities, e.i. invoke /collect/facilities endpoint. */
  CollectorFacilitiesResponse collectFacilities();
}
