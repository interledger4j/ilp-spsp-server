package org.interledger.spsp.server;

import org.interledger.codecs.ildcp.IldcpUtils;
import org.interledger.core.InterledgerPreparePacket;
import org.interledger.ildcp.IldcpFetcher;
import org.interledger.ildcp.IldcpRequest;
import org.interledger.ildcp.IldcpRequestPacket;
import org.interledger.ildcp.IldcpResponse;
import org.interledger.link.Link;
import org.interledger.link.LinkFactoryProvider;
import org.interledger.link.LinkId;
import org.interledger.link.http.IlpOverHttpLink;
import org.interledger.link.http.IlpOverHttpLinkSettings;
import org.interledger.spsp.server.config.SpspServerConfig;
import org.interledger.spsp.server.config.model.SpspServerSettingsFromPropertyFile;
import org.interledger.spsp.server.model.ParentAccountSettings;
import org.interledger.spsp.server.model.SpspServerSettings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import java.util.Objects;

import javax.annotation.PostConstruct;

@SpringBootApplication
@Import({SpspServerConfig.class})
public class SpspServerApplication {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private SpspServerSettings spspServerSettings;

  @Autowired
  private LinkFactoryProvider linkFactoryProvider;

  public static void main(String[] args) {
    SpringApplication.run(SpspServerApplication.class, args);
  }

  @PostConstruct
  public void init() {

    // Use IL-DCP to get an ILP address from the registered connector.

    final ParentAccountSettings parentAccountSettings = spspServerSettings.parentAccountSettings();

    final IlpOverHttpLinkSettings linkSettings = IlpOverHttpLinkSettings
      .fromCustomSettings(parentAccountSettings.customSettings())
      .build();

    final Link<?> connectorParentLink = linkFactoryProvider.getLinkFactory(IlpOverHttpLink.LINK_TYPE)
      .constructLink(() -> spspServerSettings.operatorAddress(), linkSettings);
    connectorParentLink.setLinkId(LinkId.of("parentConnector"));

    ///////////////
    // Construct a lambda that implements the Fetch logic for IL-DCP.
    final IldcpFetcher ildcpFetcher = ildcpRequest -> {
      Objects.requireNonNull(ildcpRequest);

      final IldcpRequestPacket ildcpRequestPacket = IldcpRequestPacket.builder().build();
      final InterledgerPreparePacket preparePacket =
        InterledgerPreparePacket.builder().from(ildcpRequestPacket).build();

      // Fetch the IL-DCP response using the Link.
      return connectorParentLink.sendPacket(preparePacket)
        .map(
          // If FulfillPacket...
          IldcpUtils::toIldcpResponse,
          // If Reject Packet...
          (interledgerRejectPacket) -> {
            throw new RuntimeException(
              String.format("IL-DCP negotiation failed! Reject: %s", interledgerRejectPacket)
            );
          }
        );
    };

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
      modifiableSpspServerSettings.operatorAddress(),
      modifiableSpspServerSettings.parentAccountSettings().assetCode(),
      modifiableSpspServerSettings.parentAccountSettings().assetScale()
    );
  }
}
