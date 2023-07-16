package io.github.johnjcool.keycloak.broker.cas;

import org.keycloak.models.IdentityProviderModel;

public class CasIdentityProviderConfig extends IdentityProviderModel {

  private static final long serialVersionUID = 1L;

  private static final String DEFAULT_CAS_LOGIN_SUFFIX = "login";
  private static final String DEFAULT_CAS_LOGOUT_SUFFIX = "logout";
  private static final String DEFAULT_CAS_SERVICE_VALIDATE_SUFFIX = "serviceValidate";
  private static final String DEFAULT_CAS_3_PROTOCOL_PREFIX = "p3";

  public CasIdentityProviderConfig() {
    super();
  }

  public CasIdentityProviderConfig(final IdentityProviderModel model) {
    super(model);
  }

  public void setCasServerUrlPrefix(final String casServerUrlPrefix) {
    getConfig().put("casServerUrlPrefix", casServerUrlPrefix);
  }

  public String getCasServerUrlPrefix() {
    return getConfig().get("casServerUrlPrefix");
  }

  public void setCasServerProtocol3(final boolean casServerProtocol3) {
    getConfig().put("casServerProtocol3", String.valueOf(casServerProtocol3));
  }

  public boolean isCasServerProtocol3() {
    return Boolean.valueOf(getConfig().get("casServerProtocol3"));
  }

  public void setGateway(final boolean gateway) {
    getConfig().put("gateway", String.valueOf(gateway));
  }

  public boolean isGateway() {
    return Boolean.valueOf(getConfig().get("gateway"));
  }

  public void setRenew(final boolean renew) {
    getConfig().put("renew", String.valueOf(renew));
  }

  public boolean isRenew() {
    return Boolean.valueOf(getConfig().get("renew"));
  }

  public String getCasServerLoginUrl() {
    return String.format("%s/%s", getConfig().get("casServerUrlPrefix"), DEFAULT_CAS_LOGIN_SUFFIX);
  }

  public String getCasServerLogoutUrl() {
    return String.format("%s/%s", getConfig().get("casServerUrlPrefix"), DEFAULT_CAS_LOGOUT_SUFFIX);
  }

  public String getCasServiceValidateUrl() {
    return isCasServerProtocol3()
        ? String.format(
            "%s/%s/%s",
            getConfig().get("casServerUrlPrefix"),
            DEFAULT_CAS_3_PROTOCOL_PREFIX,
            DEFAULT_CAS_SERVICE_VALIDATE_SUFFIX)
        : String.format(
            "%s/%s", getConfig().get("casServerUrlPrefix"), DEFAULT_CAS_SERVICE_VALIDATE_SUFFIX);
  }
}
