package org.folio.ed.client.config;

import lombok.RequiredArgsConstructor;
import org.folio.ed.client.DcbClient;
import org.folio.ed.client.DcbRefreshLocationsClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@RequiredArgsConstructor
public class HttpExchangeConfiguration {

  @Qualifier("edgeHttpServiceProxyFactory")
  private final HttpServiceProxyFactory factory;

  /**
   * Creates a {@link DcbClient} bean.
   *
   * @return the {@link DcbClient} instance
   */
  @Bean
  public DcbClient dcbClient() {
    return factory.createClient(DcbClient.class);
  }

  /**
   * Creates a {@link DcbRefreshLocationsClient} bean.
   *
   * @return the {@link DcbRefreshLocationsClient} instance
   */
  @Bean
  public DcbRefreshLocationsClient dcbRefreshLocationsClient() {
    return factory.createClient(DcbRefreshLocationsClient.class);
  }
}
