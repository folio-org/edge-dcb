package org.folio.ed.controller;

import org.folio.ed.domain.dto.DcbTransaction;
import org.folio.ed.domain.dto.DcbUpdateTransaction;
import org.folio.ed.domain.dto.TransactionStatus;
import org.folio.ed.domain.dto.TransactionStatusResponse;
import org.folio.ed.domain.dto.TransactionStatusResponseCollection;
import org.folio.ed.rest.resource.TransactionsApi;
import org.folio.ed.service.DcbTransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/dcbService/")
public class DcbTransactionController implements TransactionsApi {

  private final DcbTransactionService dcbTransactionService;

  @Override
  public ResponseEntity<TransactionStatusResponse> renewItemLoanByTransactionId(String dcbTransactionId) {
    return ResponseEntity.status(HttpStatus.OK)
      .body(dcbTransactionService.renewLoanByTransactionId(dcbTransactionId));
  }

  @Override
  public ResponseEntity<TransactionStatusResponse> getDCBTransactionStatusById(String dcbTransactionId) {
    return ResponseEntity.status(HttpStatus.OK)
      .body(dcbTransactionService.getDcbTransactionStatusById(dcbTransactionId));
  }

  @Override
  public ResponseEntity<TransactionStatusResponse> createDCBTransaction(String dcbTransactionId, DcbTransaction dcbTransaction) {
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(dcbTransactionService.createDCBTransaction(dcbTransactionId, dcbTransaction));
  }

  @Override
  public ResponseEntity<TransactionStatusResponse> updateDCBTransactionStatus(String dcbTransactionId, TransactionStatus transactionStatus) {
    return ResponseEntity.status(HttpStatus.OK)
      .body(dcbTransactionService.updateDCBTransactionStatus(dcbTransactionId, transactionStatus));
  }

  @Override
  public ResponseEntity<TransactionStatusResponseCollection> getTransactionStatusList(OffsetDateTime fromDate, OffsetDateTime toDate,
                                                                                      Integer pageNumber,Integer pageSize) {
    return ResponseEntity.status(HttpStatus.OK)
      .body(dcbTransactionService.getTransactionStatusList(fromDate, toDate, pageNumber, pageSize));
  }

  @Override
  public ResponseEntity<Void> updateTransactionDetails(String dcbTransactionId, DcbUpdateTransaction dcbUpdateTransaction) {
    dcbTransactionService.updateTransactionDetails(dcbTransactionId, dcbUpdateTransaction);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Override
  public ResponseEntity<Void> blockItemRenewalByTransactionId(String dcbTransactionId) {
    dcbTransactionService.blockItemRenewalByTransactionId(dcbTransactionId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Override
  public ResponseEntity<Void> unblockItemRenewalByTransactionId(String dcbTransactionId) {
    dcbTransactionService.unblockItemRenewalByTransactionId(dcbTransactionId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
