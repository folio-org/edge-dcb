package org.folio.ed;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration;

import static org.folio.common.utils.tls.FipsChecker.getFipsChecksResultString;

@Log4j2
@SpringBootApplication
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class })
public class EdgeDcbApplication {
  public static void main(String[] args) {
    log.info(getFipsChecksResultString());
    SpringApplication.run(EdgeDcbApplication.class, args);
  }
}
