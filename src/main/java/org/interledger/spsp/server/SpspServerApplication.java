package org.interledger.spsp.server;

import org.interledger.ildcp.IldcpFetcher;
import org.interledger.ildcp.IldcpRequest;
import org.interledger.ildcp.IldcpResponse;
import org.interledger.spsp.server.config.SpspServerConfig;
import org.interledger.spsp.server.config.model.SpspServerSettingsFromPropertyFile;
import org.interledger.spsp.server.model.SpspServerSettings;

import ch.qos.logback.classic.LoggerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

@SpringBootApplication
@Import({SpspServerConfig.class})
public class SpspServerApplication {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private Environment env;

  @Autowired
  private BuildProperties buildProperties;

  @Autowired
  private SpspServerSettings spspServerSettings;

  @Autowired
  private IldcpFetcher ildcpFetcher;

  public static void main(String[] args) {
    SpringApplication.run(SpspServerApplication.class, args);
  }

  @PostConstruct
  @SuppressWarnings("PMD.UnusedPrivateMethod")
  private void init() {
    // Configure Sentry
    LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
    context.putProperty("name", buildProperties.getName());
    context.putProperty("group", buildProperties.getGroup());
    context.putProperty("artifact", buildProperties.getArtifact());
    context.putProperty("release", buildProperties.getVersion());
    context.putProperty("spring-profiles", Arrays.stream(env.getActiveProfiles()).collect(Collectors.joining(" ")));

    final IldcpResponse ildcpResponse = ildcpFetcher.fetch(IldcpRequest.builder().build());

    final SpspServerSettingsFromPropertyFile modifiableSpspServerSettings =
      ((SpspServerSettingsFromPropertyFile) this.spspServerSettings);

    //////////////////////////////////
    // Update the Account Settings with data returned by IL-DCP!
    //////////////////////////////////
    modifiableSpspServerSettings.setOperatorAddress(ildcpResponse.getClientAddress());

    modifiableSpspServerSettings.parentAccountSettings().setAssetCode(ildcpResponse.getAssetCode());
    modifiableSpspServerSettings.parentAccountSettings().setAssetScale(ildcpResponse.getAssetScale());

    logger.info(
      "IL-DCP Succeeded! operatorAddress={} assetCode={} assetScale={}",
      modifiableSpspServerSettings.operatorAddress().getValue(),
      modifiableSpspServerSettings.parentAccountSettings().assetCode(),
      modifiableSpspServerSettings.parentAccountSettings().assetScale()
    );
  }
}
