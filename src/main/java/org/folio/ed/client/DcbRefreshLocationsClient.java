package org.folio.ed.client;

import org.folio.ed.domain.dto.RefreshShadowLocationResponse;
import org.folio.ed.domain.dto.ShadowLocationRefreshBody;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("dcb")
public interface DcbRefreshLocationsClient {

  @PostExchange("/shadow-locations/refresh")
  RefreshShadowLocationResponse refreshShadowLocations(
    @RequestBody ShadowLocationRefreshBody requestBody);
}
