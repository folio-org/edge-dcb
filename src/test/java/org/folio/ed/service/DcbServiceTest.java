package org.folio.ed.service;

import static org.folio.ed.utils.EntityUtils.shadowLocationRefreshBody;
import static org.folio.ed.utils.EntityUtils.shadowLocationRefreshResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.folio.ed.client.DcbRefreshLocationsClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DcbServiceTest {

  @InjectMocks private DcbService dcbService;
  @Mock private DcbRefreshLocationsClient dcbRefreshLocationsClient;

  @Test
  void refreshShadowLocation_positive() {
    var requestBody = shadowLocationRefreshBody();
    var response = shadowLocationRefreshResponse();

    when(dcbRefreshLocationsClient.refreshShadowLocations(requestBody)).thenReturn(response);

    var result = dcbService.refreshLocations(requestBody);

    assertEquals(response, result);

  }
}
