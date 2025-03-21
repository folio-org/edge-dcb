package org.folio.ed.service;

import org.folio.ed.client.DcbClient;
import org.folio.ed.domain.dto.DcbTransaction;
import org.folio.ed.domain.dto.TransactionStatus;
import org.folio.ed.domain.dto.TransactionStatusResponse;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class DcbTransactionService {

  private final DcbClient dcbClient;

  public TransactionStatusResponse getDcbTransactionStatusById(String dcbTransactionId) {
    log.info("getDcbTransactionStatusById:: Getting transaction status for id: {}", dcbTransactionId);
    return dcbClient.getDcbTransactionStatus(dcbTransactionId);
  }

  public TransactionStatusResponse createDCBTransaction(String dcbTransactionId, DcbTransaction dcbTransaction) {
    log.info("createDCBTransaction:: Creating transaction for id: {}", dcbTransactionId);
    return dcbClient.createCirculationRequest(dcbTransactionId, dcbTransaction);
  }

  public TransactionStatusResponse updateDCBTransactionStatus(String dcbTransactionId, TransactionStatus transactionStatus) {
    log.info("updateDCBTransactionStatus:: Updating status transaction for id: {} to {}", dcbTransactionId, transactionStatus.getStatus());
    return dcbClient.updateTransactionStatus(dcbTransactionId, transactionStatus);
  }

  public void updateTransactionDetails(String dcbTransactionId, org.folio.ed.domain.dto.DcbUpdateTransaction dcbUpdateTransaction) {
    log.info("updateTransactionDetails:: Updating transaction item details  for id: {} to {}", dcbTransactionId, dcbUpdateTransaction.getItem());
    dcbClient.updateTransactionDetails(dcbTransactionId, dcbUpdateTransaction);
  }

  public TransactionStatusResponse renewLoanByTransactionId(String dcbTransactionId) {
    log.info("renewLoanByTransactionId:: Renewing loan for transaction id: {}", dcbTransactionId);
    return dcbClient.renewLoanByTransactionId(dcbTransactionId);
  }

}
