package org.folio.ed.client;

import org.folio.ed.client.config.OkapiFeignClientConfig;
import org.folio.ed.domain.dto.DcbTransaction;
import org.folio.ed.domain.dto.TransactionStatus;
import org.folio.ed.domain.dto.TransactionStatusResponse;
import org.folio.ed.domain.dto.TransactionStatusResponseCollection;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.OffsetDateTime;

@FeignClient(name = "transactions", configuration = OkapiFeignClientConfig.class)
public interface DcbClient {

  @GetMapping(value = "/{dcbTransactionId}/status")
  TransactionStatusResponse getDcbTransactionStatus(@PathVariable("dcbTransactionId") String dcbTransactionId);

  @PostMapping(value = "/{dcbTransactionId}")
  TransactionStatusResponse createCirculationRequest(@PathVariable("dcbTransactionId") String dcbTransactionId,
                                                                       @RequestBody DcbTransaction dcbTransaction);
  @PutMapping(value = "/{dcbTransactionId}/status")
  TransactionStatusResponse updateTransactionStatus(@PathVariable("dcbTransactionId") String dcbTransactionId,
                                                                      @RequestBody TransactionStatus transactionStatus);

  @GetMapping(value = "/status")
  TransactionStatusResponseCollection getTransactionStatusList(@RequestParam("fromDate") OffsetDateTime fromDate, @RequestParam("toDate") OffsetDateTime toDate,
                                                               @RequestParam("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize);
}
