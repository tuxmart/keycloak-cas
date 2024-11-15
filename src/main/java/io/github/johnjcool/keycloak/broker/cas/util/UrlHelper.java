package io.github.johnjcool.keycloak.broker.cas.util;

import io.github.johnjcool.keycloak.broker.cas.CasIdentityProvider;
import io.github.johnjcool.keycloak.broker.cas.CasIdentityProviderConfig;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.keycloak.broker.provider.AuthenticationRequest;
import org.keycloak.models.RealmModel;
import org.keycloak.services.resources.IdentityBrokerService;
import org.keycloak.services.resources.RealmsResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class UrlHelper {
  private static final String PROVIDER_PARAMETER_SERVICE = "service";
  private static final String PROVIDER_PARAMETER_RENEW = "renew";
  private static final String PROVIDER_PARAMETER_GATEWAY = "gateway";
  public static final String PROVIDER_PARAMETER_TICKET = "ticket";
  private static final Logger log = LoggerFactory.getLogger(UrlHelper.class);

  private UrlHelper() {
    // util
  }

  public static UriBuilder createAuthenticationUrl(
      final CasIdentityProviderConfig config, final AuthenticationRequest request) {
    UriBuilder builder =
        UriBuilder.fromUri(config.getCasServerLoginUrl())
            .queryParam(PROVIDER_PARAMETER_SERVICE, request.getRedirectUri());
    if (config.isRenew()) {
      builder.queryParam(PROVIDER_PARAMETER_RENEW, config.isRenew());
    }
    if (config.isGateway()) {
      builder.queryParam(PROVIDER_PARAMETER_GATEWAY, config.isGateway());
    }
    return builder;
  }

  public static UriBuilder createValidateServiceUrl(
      final CasIdentityProviderConfig config, final String ticket, final UriInfo uriInfo) {
    UriBuilder builder =
        UriBuilder.fromUri(config.getCasServiceValidateUrl())
            .queryParam(PROVIDER_PARAMETER_TICKET, ticket)
            .queryParam(PROVIDER_PARAMETER_SERVICE, uriInfo.getAbsolutePath().toString());
    if (config.isRenew()) {
      builder.queryParam(PROVIDER_PARAMETER_RENEW, config.isRenew());
    }
    return builder;
  }

  public static UriBuilder createLogoutUrl(
      final CasIdentityProviderConfig config, final RealmModel realm, final UriInfo uriInfo) {
    UriBuilder builder =
        UriBuilder.fromUri(config.getCasServerLogoutUrl())
            .queryParam(
                PROVIDER_PARAMETER_SERVICE,
                RealmsResource.brokerUrl(uriInfo)
                    .path(IdentityBrokerService.class, "getEndpoint")
                    .path(CasIdentityProvider.Endpoint.class, "logoutResponse")
                    .build(realm.getName(), config.getAlias())
                    .toString());
    log.debug("=====url-debug=====");
    log.debug("builder.toString()");
    return builder;
  }
}
