package org.interledger.spsp.server.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableSpspServerSettingsResponse.class)
public interface SpspServerSettingsResponse {

  static ImmutableSpspServerSettingsResponse.Builder builder() {
    return ImmutableSpspServerSettingsResponse.builder();
  }

  String version();

}
