package org.folio.ed.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.requestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.folio.ed.domain.dto.TransactionStatus.StatusEnum.AWAITING_PICKUP;
import static org.folio.ed.support.wiremock.WiremockContainerExtension.WM_URL_PROPERTY;
import static org.folio.ed.utils.EntityUtils.createDcbTransaction;
import static org.folio.ed.utils.EntityUtils.createDcbUpdateTransaction;
import static org.folio.ed.utils.EntityUtils.createTransactionStatus;
import static org.folio.ed.utils.EntityUtils.shadowLocationRefreshBody;
import static org.folio.ed.utils.EntityUtils.shadowLocationRefreshResponse;
import static org.folio.edge.core.utils.ApiKeyUtils.generateApiKey;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.not;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.tomakehurst.wiremock.client.WireMock;

import org.folio.common.utils.OkapiHeaders;
import org.folio.ed.domain.dto.DcbItem;
import org.folio.ed.domain.dto.TransactionStatusResponse;
import org.folio.ed.support.wiremock.WireMockStub;
import org.folio.ed.support.wiremock.WiremockContainerExtension;
import org.folio.ed.support.wiremock.WithWiremockContainer;
import org.folio.edge.core.utils.ApiKeyUtils;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

@ActiveProfiles("it")
@AutoConfigureMockMvc
@WithWiremockContainer
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DcbEdgeRequestHandlingTest {

  private static final String TENANT = "test_tenant";
  private static final String USERNAME = "user";
  private static final String TOKEN = "edgeDcbTestAccessToken";
  private static final String TRANSACTION_ID = "3cedc460-9b08-41fb-b995-d568fad95e13";

  protected static final JsonMapper JSON_MAPPER = JsonMapper.builder()
    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
    .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
    .build();

  protected static WireMock wiremock;
  @Autowired private MockMvc mockMvc;

  @DynamicPropertySource
  static void registerProperties(DynamicPropertyRegistry registry) {
    registry.add("folio.client.okapiUrl", () -> System.getProperty(WM_URL_PROPERTY));
  }

  @BeforeEach
  void setUp() {
    DcbEdgeRequestHandlingTest.wiremock = WiremockContainerExtension.getWireMockClient();
  }

  @Test
  @WireMockStub({
    "/stubs/authn/201-post(system_login).json",
    "/stubs/mod-dcb/200-get(tx-status).json",
  })
  void proxyTransactionStatusRequest_positive() throws Exception {
    var apiKey = generateApiKey(10, TENANT, USERNAME);
    mockMvc.perform(get("/dcbService/transactions/{transactionId}/status", TRANSACTION_ID)
        .queryParam("apiKey", apiKey)
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.role").value("LENDER"))
      .andExpect(jsonPath("$.status").value("ITEM_CHECKED_OUT"));

    verifyExchangeRequestHeaders(GET, "/transactions/%s/status".formatted(TRANSACTION_ID));
  }

  @Test
  @WireMockStub({
    "/stubs/authn/201-post(system_login).json",
    "/stubs/mod-dcb/200-get(tx-status-expired).json",
  })
  void proxyTransactionStatusRequest_positive_expiredStatus() throws Exception {
    var response = mockMvc.perform(
        get("/dcbService/transactions/{transactionId}/status", TRANSACTION_ID)
          .queryParam("apiKey", generateApiKey(10, TENANT, USERNAME))
          .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.role").value("LENDER"))
      .andExpect(jsonPath("$.status").value("EXPIRED"))
      .andReturn().getResponse();

    var body = JSON_MAPPER.readValue(response.getContentAsString(), TransactionStatusResponse.class);
    assertThat(body.getStatus()).isEqualTo(TransactionStatusResponse.StatusEnum.EXPIRED);

    verifyExchangeRequestHeaders(GET, "/transactions/%s/status".formatted(TRANSACTION_ID));
  }

  @Test
  @WireMockStub({
    "/stubs/authn/201-post(system_login).json",
    "/stubs/mod-dcb/201-post(new-transaction).json",
  })
  void shouldConvertApiKeyToHeadersForPost() throws Exception {
    mockMvc.perform(post("/dcbService/transactions/{transactionId}", TRANSACTION_ID)
        .queryParam("apiKey", ApiKeyUtils.generateApiKey(10, TENANT, USERNAME))
        .content(asJsonString(createDcbTransaction()))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isCreated())
      .andExpect(content().string(not(emptyString())));

    verifyExchangeRequestHeaders(POST, "/transactions/%s".formatted(TRANSACTION_ID));
  }

  @Test
  @WireMockStub({
    "/stubs/authn/201-post(system_login).json",
    "/stubs/mod-dcb/204-put(renew-item).json",
  })
  void shouldConvertApiKeyToHeadersForRenewItemLoanByTransactionId() throws Exception{
    mockMvc.perform(put("/dcbService/transactions/{transactionId}/renew", TRANSACTION_ID)
        .queryParam("apiKey", ApiKeyUtils.generateApiKey(10, TENANT, USERNAME))
        .content(asJsonString(createDcbUpdateTransaction()))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().string(emptyString()));

    verifyExchangeRequestHeaders(PUT, "/transactions/%s/renew".formatted(TRANSACTION_ID));
  }

  @Test
  @WireMockStub({
    "/stubs/authn/201-post(system_login).json",
    "/stubs/mod-dcb/200-put(update-tx-details).json",
  })
  void shouldConvertApiKeyToHeadersForPutTransactionDetails() throws Exception {
    mockMvc.perform(put("/dcbService/transactions/{transactionId}", TRANSACTION_ID)
        .queryParam("apiKey", ApiKeyUtils.generateApiKey(10, TENANT, USERNAME))
        .content(asJsonString(createDcbUpdateTransaction()))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNoContent());

    verifyExchangeRequestHeaders(PUT, "/transactions/%s".formatted(TRANSACTION_ID));
  }

  @Test
  @WireMockStub({
    "/stubs/authn/201-post(system_login).json",
    "/stubs/mod-dcb/200-put(update-tx-status).json",
  })
  void shouldConvertApiKeyToHeadersForPut() throws Exception {
    mockMvc.perform(put("/dcbService/transactions/{transactionId}/status", TRANSACTION_ID)
        .queryParam("apiKey", ApiKeyUtils.generateApiKey(10, TENANT, USERNAME))
        .content(asJsonString(createTransactionStatus(AWAITING_PICKUP)))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().string(asJsonString(createTransactionStatus(AWAITING_PICKUP))));
    ;

    verifyExchangeRequestHeaders(PUT, "/transactions/%s/status".formatted(TRANSACTION_ID));
  }

  @Test
  @WireMockStub("/stubs/authn/201-post(system_login).json")
  void shouldThrowErrorForInvalidRole() throws Exception {
    mockMvc.perform(post("/dcbService/transactions/{transactionId}", TRANSACTION_ID)
        .queryParam("apiKey", ApiKeyUtils.generateApiKey(10, TENANT, USERNAME))
        .content("{\"role\": \"test\"}")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isBadRequest());
  }

  @Test
  @WireMockStub("/stubs/authn/201-post(system_login).json")
  void shouldThrowErrorForInvalidUUID() throws Exception {
    var dcbTransaction = createDcbTransaction();
    dcbTransaction.setItem(dcbTransaction.getItem().id("123"));
    mockMvc.perform(post("/dcbService/transactions/{transactionId}", TRANSACTION_ID)
        .queryParam("apiKey", ApiKeyUtils.generateApiKey(10, TENANT, USERNAME))
        .content(asJsonString(dcbTransaction))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isBadRequest());
  }

  @Test
  @WireMockStub({
    "/stubs/authn/201-post(system_login).json",
    "/stubs/mod-dcb/404-get(transaction-by-id).json",
  })
  void shouldThrowErrorForFeignException() throws Exception {
    mockMvc.perform(get("/dcbService/transactions/{transactionId}/status", TRANSACTION_ID)
        .queryParam("apiKey", ApiKeyUtils.generateApiKey(10, TENANT, USERNAME))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
      // As dcb will return Error message of type Errors, trying to assert the same here
      .andExpect(status().isInternalServerError())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.errors[0].message").value(containsString("404 Not Found:")))
      .andExpect(jsonPath("$.errors[0].message").value(containsString("Unable to find Dcb transaction")));
  }

  @Test
  @WireMockStub({
    "/stubs/authn/201-post(system_login).json",
    "/stubs/mod-dcb/200-get(tx-query).json"
  })
  void shouldConvertApiKeyToHeadersForGetTransactionStatusList() throws Exception {
    var fromDate = OffsetDateTime.of(LocalDate.ofYearDay(2026, 1), LocalTime.MIDNIGHT, ZoneOffset.UTC);
    var toDate = OffsetDateTime.of(LocalDate.ofYearDay(2026, 10), LocalTime.MIDNIGHT, ZoneOffset.UTC);
    mockMvc.perform(get("/dcbService/transactions/status")
        .queryParam("apiKey", ApiKeyUtils.generateApiKey(10, TENANT, USERNAME))
        .param("fromDate", String.valueOf(fromDate))
        .param("toDate", String.valueOf(toDate))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(content().string(not(emptyString())));

    verifyExchangeRequestHeaders(GET, "/transactions/status");
  }

  @Test
  @WireMockStub({
    "/stubs/authn/201-post(system_login).json",
    "/stubs/mod-dcb/200-get(tx-status).json"
  })
  void shouldReturnTransactionStatusById() throws Exception {
    var statusResponse = new TransactionStatusResponse()
      .role(TransactionStatusResponse.RoleEnum.LENDER)
      .status(TransactionStatusResponse.StatusEnum.ITEM_CHECKED_OUT)
      .item(new DcbItem().holdCount(10));

    mockMvc.perform(get("/dcbService/transactions/{transactionId}/status", TRANSACTION_ID)
        .queryParam("apiKey", ApiKeyUtils.generateApiKey(10, TENANT, USERNAME))
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().string(asJsonString(statusResponse)));

    verifyExchangeRequestHeaders(GET, "/transactions/%s/status".formatted(TRANSACTION_ID));
  }

  @Test
  @WireMockStub({
    "/stubs/authn/201-post(system_login).json",
    "/stubs/mod-dcb/418-get(tx-by-id).json"
  })
  void shouldReturnClientErrors() throws Exception {
    mockMvc.perform(get("/dcbService/transactions/{transactionId}/status", TRANSACTION_ID)
        .queryParam("apiKey", ApiKeyUtils.generateApiKey(10, TENANT, USERNAME))
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isInternalServerError())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.errors[0].code").value("INTERNAL_SERVER_ERROR"))
      .andExpect(jsonPath("$.errors[0].message").value(
        containsString("I'm a teapot, not an dcb transaction!")));

    verifyExchangeRequestHeaders(GET, "/transactions/%s/status".formatted(TRANSACTION_ID));
  }

  @Test
  @WireMockStub({
    "/stubs/authn/201-post(system_login).json",
    "/stubs/mod-dcb/418-put(update-tx).json",
  })
  void shouldReturnClientErrorsWhenPutTransactionDetails() throws Exception {
    mockMvc.perform(put("/dcbService/transactions/{transactionId}", TRANSACTION_ID)
        .queryParam("apiKey", ApiKeyUtils.generateApiKey(10, TENANT, USERNAME))
        .content(asJsonString(createDcbUpdateTransaction()))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
      // If mod-dcb doesn't return the error of type errors, then we edge-dcb will throw INTERNAL_SERVER_ERROR
      .andExpect(status().isInternalServerError())
      .andExpect(jsonPath("$.errors[0].message").value(containsString("I'm a teapot!")));

    verifyExchangeRequestHeaders(PUT, "/transactions/%s".formatted(TRANSACTION_ID));
  }

  @Test
  @WireMockStub({
    "/stubs/authn/201-post(system_login).json",
    "/stubs/mod-dcb/201-post(refresh-shadow-locations).json",
  })
  void shouldRefreshShadowLocations() throws Exception {
    mockMvc.perform(post("/dcbService/dcb/shadow-locations/refresh")
        .queryParam("apiKey", generateApiKey(10, TENANT, USERNAME))
        .content(asJsonString(shadowLocationRefreshBody()))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isCreated())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(content().json(asJsonString(shadowLocationRefreshResponse())));
  }

  @Test
  @WireMockStub("/stubs/authn/201-post(system_login).json")
  void shouldRefreshShadowLocationsEmptyRequestBody() throws Exception {
    mockMvc.perform(post("/dcbService/dcb/shadow-locations/refresh")
        .queryParam("apiKey", generateApiKey(10, TENANT, USERNAME))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.errors[0].code").value("VALIDATION_ERROR"));
  }

  @Test
  @WireMockStub({
    "/stubs/authn/201-post(system_login).json",
    "/stubs/mod-dcb/418-put(renew-transaction).json",
  })
  void shouldReturnClientErrorsWhenRenewLoanByTransactionId() throws Exception {
    mockMvc.perform(put("/dcbService/transactions/{transactionId}/renew", TRANSACTION_ID)
        .queryParam("apiKey", generateApiKey(10, TENANT, USERNAME))
        .content(asJsonString(createDcbUpdateTransaction()))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
      // If mod-dcb doesn't return the error of type errors, then we edge-dcb will throw INTERNAL_SERVER_ERROR
      .andExpect(status().isInternalServerError())
      .andExpect(jsonPath("$.errors[0].message").value("418 I'm a Teapot: \"I'm a teapot!\""));
  }

  @Test
  @WireMockStub({
    "/stubs/authn/201-post(system_login).json",
    "/stubs/mod-dcb/204-put(block-renewal).json",
  })
  void shouldBlockItemRenewalByTransactionId() throws Exception {
    mockMvc.perform(put("/dcbService/transactions/{transactionId}/block-renewal", TRANSACTION_ID)
        .queryParam("apiKey", generateApiKey(10, TENANT, USERNAME))
        .content(asJsonString(createDcbUpdateTransaction()))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNoContent());
  }

  @Test
  @WireMockStub({
    "/stubs/authn/201-post(system_login).json",
    "/stubs/mod-dcb/204-put(unblock-renewal).json",
  })
  void shouldUnblockItemRenewalByTransactionId() throws Exception {
    mockMvc.perform(put("/dcbService/transactions/{transactionId}/unblock-renewal", TRANSACTION_ID)
        .queryParam("apiKey", generateApiKey(10, TENANT, USERNAME))
        .content(asJsonString(createDcbUpdateTransaction()))
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNoContent());
  }

  private static String asJsonString(Object value) {
    return JSON_MAPPER.writeValueAsString(value);
  }

  private static void verifyExchangeRequestHeaders(HttpMethod method, String expectedUrl) {
    wiremock.verifyThat(1, requestedFor(method.name(), urlPathEqualTo(expectedUrl))
      .withHeader(OkapiHeaders.TENANT, equalTo(TENANT))
      .withHeader(OkapiHeaders.TOKEN, equalTo(TOKEN))
    );
  }
}
