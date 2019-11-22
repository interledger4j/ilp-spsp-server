package org.interledger.spsp.server.config.ilp;

import org.interledger.link.Link;
import org.interledger.link.LinkFactoryProvider;
import org.interledger.link.LoopbackLink;
import org.interledger.link.LoopbackLinkFactory;
import org.interledger.link.PacketRejector;
import org.interledger.link.PingLoopbackLink;
import org.interledger.link.PingLoopbackLinkFactory;
import org.interledger.link.http.IlpOverHttpLink;
import org.interledger.spsp.server.model.SpspServerSettings;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.function.Supplier;

@Configuration
@Import({IlpOverHttpLink.class})
public class LinkConfig {

//  private static Link newIlpOverHttpLink() {
//    String sharedSecret = "some random secret here";
//    final IlpOverHttpLinkSettings linkSettings = IlpOverHttpLinkSettings.builder()
//      .incomingHttpLinkSettings(IncomingLinkSettings.builder()
//        .authType(IlpOverHttpLinkSettings.AuthType.SIMPLE)
//        .encryptedTokenSharedSecret(sharedSecret)
//        .build())
//      .outgoingHttpLinkSettings(OutgoingLinkSettings.builder()
//        .authType(IlpOverHttpLinkSettings.AuthType.SIMPLE)
//        .tokenSubject(SENDER_ACCOUNT_USERNAME)
//        .url(HttpUrl.parse(TESTNET_URI + "/ilp"))
//        .encryptedTokenSharedSecret(sharedSecret)
//        .build())
//      .build();
//
//    return new IlpOverHttpLink(
//      () -> Link.SELF,
//      linkSettings,
//      newHttpClient(),
//      new ObjectMapper(),
//      InterledgerCodecContextFactory.oer(),
//      new SimpleBearerTokenSupplier(SENDER_ACCOUNT_USERNAME + ":" + SENDER_PASS_KEY)
//    );
//  }

//  private static Link ildcpLink() {
//    String sharedSecret = "some random secret here";
//    final IlpOverHttpLinkSettings linkSettings = IlpOverHttpLinkSettings.builder()
//      .incomingHttpLinkSettings(IncomingLinkSettings.builder()
//        .authType(IlpOverHttpLinkSettings.AuthType.SIMPLE)
//        .encryptedTokenSharedSecret(sharedSecret)
//        .build())
//      .outgoingHttpLinkSettings(OutgoingLinkSettings.builder()
//        .authType(IlpOverHttpLinkSettings.AuthType.SIMPLE)
//        .tokenSubject(SENDER_ACCOUNT_USERNAME)
//        .url(HttpUrl.parse(TESTNET_URI + "/ilp"))
//        .encryptedTokenSharedSecret(sharedSecret)
//        .build())
//      .build();
//
//    return new IlpOverHttpLink(
//      () -> Link.SELF,
//      linkSettings,
//      newHttpClient(),
//      new ObjectMapper(),
//      InterledgerCodecContextFactory.oer(),
//      new SimpleBearerTokenSupplier(SENDER_ACCOUNT_USERNAME + ":" + SENDER_PASS_KEY)
//    );
//  }

  /**
   * The link to the parent connector that this SpspServer is a chile of.
   *
   * @return A {@link Link}.
   */
  @Bean
  protected Link parentLink() {
    final Link parentLink = null;

    return parentLink;
  }

  @Bean
  protected LoopbackLinkFactory loopbackLinkFactory(PacketRejector packetRejector) {
    return new LoopbackLinkFactory(packetRejector);
  }

  @Bean
  protected PingLoopbackLinkFactory unidirectionalPingLinkFactory() {
    return new PingLoopbackLinkFactory();
  }

  @Bean
  protected LinkFactoryProvider linkFactoryProvider(
    final LoopbackLinkFactory loopbackLinkFactory, final PingLoopbackLinkFactory pingLoopbackLinkFactory
  ) {
    final LinkFactoryProvider provider = new LinkFactoryProvider();

    // Register known types...Spring will register proper known types based upon config...
    provider.registerLinkFactory(LoopbackLink.LINK_TYPE, loopbackLinkFactory);
    provider.registerLinkFactory(PingLoopbackLink.LINK_TYPE, pingLoopbackLinkFactory);

    return provider;
  }

  @Bean
  protected PacketRejector packetRejector(final Supplier<SpspServerSettings> spspServerSettingsSupplier) {
    return new PacketRejector(() -> spspServerSettingsSupplier.get().operatorAddress());
  }
}
