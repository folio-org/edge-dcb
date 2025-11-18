package org.folio.ed.service;

import java.util.UUID;
import org.folio.ed.client.DcbClient;
import org.folio.ed.domain.dto.DcbTransaction;
import org.folio.ed.domain.dto.DcbUpdateTransaction;
import org.folio.ed.domain.dto.TransactionStatus;
import org.folio.ed.domain.dto.TransactionStatusResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.OffsetDateTime;

import static org.folio.ed.domain.dto.TransactionStatusResponse.RoleEnum.LENDER;
import static org.folio.ed.domain.dto.TransactionStatusResponse.StatusEnum.ITEM_CHECKED_OUT;
import static org.folio.ed.utils.EntityUtils.createDcbTransaction;
import static org.folio.ed.utils.EntityUtils.createDcbUpdateTransaction;
import static org.folio.ed.utils.EntityUtils.createTransactionStatus;
import static org.folio.ed.utils.EntityUtils.createTransactionStatusResponse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


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
    verify(dcbClient).createCirculationRequest(anyString(), any(DcbTransaction.class));
  }

  @Test
  void updateDcbTransactionDetails() {
    dcbTransactionService.updateTransactionDetails("123", createDcbUpdateTransaction());
    verify(dcbClient).updateTransactionDetails(anyString(), any(DcbUpdateTransaction.class));
  }

  @Test
  void updateDcbTransactionDetailsShouldThrowAnErrorIfClientReturnsError() {
    DcbUpdateTransaction dcbUpdateTransaction = createDcbUpdateTransaction();
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

  @Test
  void updateDcbTransactionStatusTest() {
    Mockito.when(dcbClient.updateTransactionStatus(anyString(), any(TransactionStatus.class))).thenReturn(createTransactionStatusResponse(TransactionStatusResponse.StatusEnum.CREATED));
    dcbTransactionService.updateDCBTransactionStatus("123", createTransactionStatus(TransactionStatus.StatusEnum.OPEN));
    verify(dcbClient).updateTransactionStatus(anyString(), any(TransactionStatus.class));
  }

  @Test
  void getTransactionStatusListTest() {
    var startDate = OffsetDateTime.now().minusDays(1);
    var endDate = OffsetDateTime.now();
    dcbTransactionService.getTransactionStatusList(startDate, endDate, 0, 100);
    verify(dcbClient).getTransactionStatusList(startDate.toString(), endDate.toString(), 0, 100);
  }

  @Test
  void blockItemRenewalByTransactionIdTest() {
    var transactionId = UUID.randomUUID().toString();
    dcbTransactionService.blockItemRenewalByTransactionId(transactionId);
    verify(dcbClient).blockItemRenewalByTransactionId(transactionId);
  }

  @Test
  void unblockItemRenewalByTransactionIdTest() {
    var transactionId = UUID.randomUUID().toString();
    dcbTransactionService.unblockItemRenewalByTransactionId(transactionId);
    verify(dcbClient).unblockItemRenewalByTransactionId(transactionId);
  }
}
