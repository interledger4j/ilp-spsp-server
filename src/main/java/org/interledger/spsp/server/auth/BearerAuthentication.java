package org.interledger.spsp.server.auth;

import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;
import org.immutables.value.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Optional;

import javax.annotation.Nullable;

/**
 * Bearer authentication credentials for an ILP-over-HTTP connection.
 *
 * @see "https://github.com/sappenin/java-ilpv4-connector/issues/457"
 * @deprecated Remove this once the version of this in `java-ilp-connector` is extracted per #457.
 */
@Deprecated

@Value.Immutable
public interface BearerAuthentication extends Authentication {

  static ImmutableBearerAuthentication.Builder builder() {
    return ImmutableBearerAuthentication.builder();
  }

  @Override
  @Nullable
  default Object getPrincipal() {
    return null;
  }

  @Override
  @Value.Default()
  default boolean isAuthenticated() {
    return false;
  }

  @Override
  @Value.Derived
  default void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    // No-op.
  }

  @Value.Redacted
  byte[] getBearerToken();

  HashCode hmacSha256();

  @Override
  @Value.Derived
  default Collection<? extends GrantedAuthority> getAuthorities() {
    return Lists.newArrayList();
  }

  @Override
  @Value.Derived
  default Object getCredentials() {
    return this.getBearerToken();
  }

  /**
   * Stores additional details about the authentication request. These might be an IP address, certificate serial number
   * etc.
   *
   * @return additional details about the authentication request, or <code>null</code> if not used
   */
  @Override
  @Value.Derived
  @Nullable
  default Object getDetails() {
    return null;
  }

  /**
   * Returns the name of this principal.
   *
   * @return the name of this principal.
   */
  @Override
  @Nullable
  default String getName() {
    return Optional.ofNullable(getPrincipal())
      .map(Object::toString)
      .orElse(null);
  }
}
