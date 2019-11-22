package org.interledger.spsp.server;

import org.interledger.spsp.server.config.SpspServerConfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;

@SpringBootApplication
@Import({SpspServerConfig.class})
public class SpspServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpspServerApplication.class, args);
  }

  @PostConstruct
  public void init() {

    // Use IL-DCP to get an ILP address from the registered connector.

  }
}
