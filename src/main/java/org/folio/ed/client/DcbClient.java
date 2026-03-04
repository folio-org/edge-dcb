package org.folio.ed.client;

import org.folio.ed.domain.dto.DcbTransaction;
import org.folio.ed.domain.dto.TransactionStatus;
import org.folio.ed.domain.dto.TransactionStatusResponse;
import org.folio.ed.domain.dto.TransactionStatusResponseCollection;
import org.folio.ed.domain.dto.DcbUpdateTransaction;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

@HttpExchange(value = "transactions")
public interface DcbClient {

  @GetExchange(value = "/{dcbTransactionId}/status")
  TransactionStatusResponse getDcbTransactionStatus(@PathVariable String dcbTransactionId);

  @PostExchange(value = "/{dcbTransactionId}")
  TransactionStatusResponse createCirculationRequest(
    @PathVariable String dcbTransactionId,
    @RequestBody DcbTransaction dcbTransaction);

  @PutExchange(value = "/{dcbTransactionId}/status")
  TransactionStatusResponse updateTransactionStatus(
    @PathVariable String dcbTransactionId,
    @RequestBody TransactionStatus transactionStatus);

  @GetExchange(value = "/status")
  TransactionStatusResponseCollection getTransactionStatusList(
    @RequestParam("fromDate") String fromDate,
    @RequestParam("toDate") String toDate,
    @RequestParam("pageNumber") int pageNumber,
    @RequestParam("pageSize") int pageSize);

  @PutExchange(value = "/{dcbTransactionId}")
  void updateTransactionDetails(
    @PathVariable String dcbTransactionId,
    @RequestBody DcbUpdateTransaction dcbUpdateTransaction);

  @PutExchange(value = "/{dcbTransactionId}/renew")
  TransactionStatusResponse renewLoanByTransactionId(@PathVariable String dcbTransactionId);

  @PutExchange("/{dcbTransactionId}/block-renewal")
  void blockItemRenewalByTransactionId(@PathVariable String dcbTransactionId);

  @PutExchange("/{dcbTransactionId}/unblock-renewal")
  void unblockItemRenewalByTransactionId(@PathVariable String dcbTransactionId);
}
