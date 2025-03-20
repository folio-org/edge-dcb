package org.folio.ed.service;

import org.folio.ed.client.DcbClient;
import org.folio.ed.domain.dto.DcbTransaction;
import org.folio.ed.domain.dto.TransactionStatus;
import org.folio.ed.domain.dto.TransactionStatusResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.folio.ed.utils.EntityUtils.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.folio.ed.domain.dto.DcbUpdateTransaction;
import static org.folio.ed.domain.dto.TransactionStatusResponse.RoleEnum.LENDER;
import static org.folio.ed.domain.dto.TransactionStatusResponse.StatusEnum.ITEM_CHECKED_OUT;

@ExtendWith(MockitoExtension.class)
class DcbTransactionServiceTest {
  @Mock
  DcbClient dcbClient;

  @InjectMocks
  DcbTransactionService dcbTransactionService;

  @Test
  void getDcbTransactionStatusById() {
    when(dcbClient.getDcbTransactionStatus(anyString())).thenReturn(createTransactionStatusResponse(TransactionStatusResponse.StatusEnum.AWAITING_PICKUP));
    dcbTransactionService.getDcbTransactionStatusById("123");
    verify(dcbClient).getDcbTransactionStatus(anyString());
  }

  @Test
  void createDcbTransactionTest() {
    Mockito.when(dcbClient.createCirculationRequest(anyString(), any(DcbTransaction.class))).thenReturn(createTransactionStatusResponse(TransactionStatusResponse.StatusEnum.CREATED));
    dcbTransactionService.createDCBTransaction("123", createDcbTransaction());
    Mockito.verify(dcbClient).createCirculationRequest(anyString(), any(DcbTransaction.class));
  }

  @Test
  void updateDcbTransactionStatusTest() {
    Mockito.when(dcbClient.updateTransactionStatus(anyString(), any(TransactionStatus.class))).thenReturn(createTransactionStatusResponse(TransactionStatusResponse.StatusEnum.CREATED));
    dcbTransactionService.updateDCBTransactionStatus("123", createTransactionStatus(TransactionStatus.StatusEnum.OPEN));
    Mockito.verify(dcbClient).updateTransactionStatus(anyString(), any(TransactionStatus.class));
  }

  @Test
  void updateDcbTransactionDetails() {
    dcbTransactionService.updateTransactionDetails("123", createDcbUpdateTransaction());
    verify(dcbClient).updateTransactionDetails(anyString(), any(DcbUpdateTransaction.class));
  }

  @Test
  void updateDcbTransactionDetailsShouldThrowAnErrorIfClientReturnsError() {
    org.folio.ed.domain.dto.DcbUpdateTransaction dcbUpdateTransaction = createDcbUpdateTransaction();
    doThrow(IllegalStateException.class).when(dcbClient).updateTransactionDetails(anyString(), any(DcbUpdateTransaction.class));
    assertThrows(IllegalStateException.class,
      () -> dcbTransactionService.updateTransactionDetails("123", dcbUpdateTransaction));
  }

  @Test
  void renewLoanByTransactionIdTest() {
    Mockito.when(dcbClient.renewLoanByTransactionId(anyString())).thenReturn(
      createTransactionStatusResponse(ITEM_CHECKED_OUT, LENDER));
    dcbTransactionService.renewLoanByTransactionId("123");
    verify(dcbClient).renewLoanByTransactionId(anyString());
  }

  @Test
  void renewLoanByTransactionIdShouldThrowAnErrorIfClientReturnsError() {
    doThrow(IllegalArgumentException.class).when(dcbClient).renewLoanByTransactionId(anyString());
    assertThrows(IllegalArgumentException.class,
      () -> dcbTransactionService.renewLoanByTransactionId("123"));
  }
}
