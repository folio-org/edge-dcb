package org.folio.ed.utils;

import static org.folio.ed.domain.dto.RefreshLocationStatusType.SUCCESS;

import org.folio.ed.domain.dto.DcbAgency;
import org.folio.ed.domain.dto.DcbItem;
import org.folio.ed.domain.dto.DcbLocation;
import org.folio.ed.domain.dto.DcbPatron;
import org.folio.ed.domain.dto.DcbTransaction;
import org.folio.ed.domain.dto.DcbTransaction.RoleEnum;
import org.folio.ed.domain.dto.DcbUpdateTransaction;
import org.folio.ed.domain.dto.DcbUpdateItem;
import org.folio.ed.domain.dto.RefreshLocationStatus;
import org.folio.ed.domain.dto.RefreshLocationUnitsStatus;
import org.folio.ed.domain.dto.RefreshShadowLocationResponse;
import org.folio.ed.domain.dto.ShadowLocationRefreshBody;
import org.folio.ed.domain.dto.TransactionStatus;
import org.folio.ed.domain.dto.TransactionStatusResponse;
import org.folio.ed.domain.dto.TransactionStatusResponse.StatusEnum;

public class EntityUtils {

  public static String ITEM_ID = "5b95877d-86c0-4cb7-a0cd-7660b348ae5a";
  public static String PATRON_ID = "571b0a2c-9456-40b5-a449-d41fe6017082";

  public static DcbTransaction createDcbTransaction() {
    return DcbTransaction.builder()
      .item(createDcbItem())
      .patron(createDcbPatron())
      .role(RoleEnum.LENDER)
      .build();
  }

  public static DcbUpdateTransaction createDcbUpdateTransaction() {
    return DcbUpdateTransaction.builder()
      .item(DcbUpdateItem.builder()
          .barcode(ITEM_ID)
          .materialType("book")
          .lendingLibraryCode("KU")
          .build())
      .build();
  }

  public static TransactionStatus createTransactionStatus(TransactionStatus.StatusEnum statusEnum){
    return TransactionStatus.builder()
      .status(statusEnum)
      .build();
  }

  public static DcbItem createDcbItem() {
    return DcbItem.builder()
      .id(ITEM_ID)
      .barcode("DCB_ITEM")
      .title("ITEM")
      .lendingLibraryCode("KU")
      .materialType("book")
      .locationCode("KU/CC/DI/O")
      .build();
  }

  public static DcbPatron createDcbPatron() {
    return DcbPatron.builder()
      .id(PATRON_ID)
      .barcode("DCB_PATRON")
      .group("staff")
      .borrowingLibraryCode("E")
      .localNames("[John, Smith]")
      .build();
  }

  public static TransactionStatusResponse createTransactionStatusResponse(StatusEnum statusEnum){
    return TransactionStatusResponse.builder().status(statusEnum).build();
  }

  public static TransactionStatusResponse createTransactionStatusResponse(StatusEnum status,
    TransactionStatusResponse.RoleEnum role) {
    return TransactionStatusResponse.builder()
      .status(status)
      .role(role)
      .build();
  }

  public static ShadowLocationRefreshBody shadowLocationRefreshBody() {
    var agency = new DcbAgency().name("test-agency").code("AG");
    var location = new DcbLocation().name("test").code("T-1").agency(agency);
    return new ShadowLocationRefreshBody().addLocationsItem(location);
  }

  public static RefreshShadowLocationResponse shadowLocationRefreshResponse() {
    return new RefreshShadowLocationResponse()
      .addLocationsItem(new RefreshLocationStatus().code("T-1").status(SUCCESS))
      .locationUnits(new RefreshLocationUnitsStatus()
        .addInstitutionsItem(new RefreshLocationStatus().code("AG").status(SUCCESS))
        .addCampusesItem(new RefreshLocationStatus().code("AG").status(SUCCESS))
        .addLibrariesItem(new RefreshLocationStatus().code("AG").status(SUCCESS)));
  }
}
