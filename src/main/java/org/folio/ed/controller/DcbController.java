package org.folio.ed.controller;

import lombok.RequiredArgsConstructor;
import org.folio.ed.domain.dto.RefreshShadowLocationResponse;
import org.folio.ed.domain.dto.ShadowLocationRefreshBody;
import org.folio.ed.rest.resource.DcbApi;
import org.folio.ed.service.DcbService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/dcbService/")
public class DcbController implements DcbApi {

  private final DcbService dcbService;

  @Override
  public ResponseEntity<RefreshShadowLocationResponse> refreshShadowLocation(ShadowLocationRefreshBody requestBody) {
    var response = dcbService.refreshLocations(requestBody);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}
