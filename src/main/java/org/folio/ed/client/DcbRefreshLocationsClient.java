package org.folio.ed.client;

import org.folio.ed.client.config.OkapiFeignClientConfig;
import org.folio.ed.domain.dto.RefreshShadowLocationResponse;
import org.folio.ed.domain.dto.ShadowLocationRefreshBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "dcb", configuration = OkapiFeignClientConfig.class)
public interface DcbRefreshLocationsClient {

  @PostMapping(value = "/shadow-locations/refresh")
  RefreshShadowLocationResponse refreshShadowLocations(ShadowLocationRefreshBody requestBody);
}
