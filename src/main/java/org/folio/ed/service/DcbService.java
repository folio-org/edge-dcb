package org.folio.ed.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.folio.ed.client.DcbRefreshLocationsClient;
import org.folio.ed.domain.dto.RefreshShadowLocationResponse;
import org.folio.ed.domain.dto.ShadowLocationRefreshBody;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class DcbService {

  private final DcbRefreshLocationsClient dcbRefreshLocationsClient;

  /**
   * Refreshes shadow locations using the provided request body.
   *
   * @param requestBody the request body containing shadow location refresh details
   * @return the response after refreshing shadow locations
   */
  public RefreshShadowLocationResponse refreshLocations(ShadowLocationRefreshBody requestBody) {
    log.info("refreshLocations:: Refreshing locations: {}",
      () -> StringUtils.trimToEmpty(requestBody.toString()).replaceAll("\\s+", " "));
    return dcbRefreshLocationsClient.refreshShadowLocations(requestBody);
  }
}
