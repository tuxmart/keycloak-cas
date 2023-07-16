package io.github.johnjcool.keycloak.broker.cas;

import io.github.johnjcool.keycloak.broker.cas.jaxb.ServiceResponseJaxbContextResolver;
import io.github.johnjcool.keycloak.broker.cas.jaxb.ServiceResponseJaxbProvider;
import java.util.List;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.keycloak.Config;
import org.keycloak.broker.provider.AbstractIdentityProviderFactory;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ConfiguredProvider;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;

public class CasIdentityProviderFactory extends AbstractIdentityProviderFactory<CasIdentityProvider>
    implements ConfiguredProvider {

  public static final String PROVIDER_ID = "cas";

  @Override
  public void init(final Config.Scope config) {
    super.init(config);
    ResteasyProviderFactory.getInstance().registerProvider(ServiceResponseJaxbProvider.class, true);
    ResteasyProviderFactory.getInstance()
        .registerProvider(ServiceResponseJaxbContextResolver.class, true);
  }

  @Override
  public String getName() {
    return "CAS";
  }

  @Override
  public CasIdentityProvider create(
      final KeycloakSession session, final IdentityProviderModel model) {
    return new CasIdentityProvider(session, new CasIdentityProviderConfig(model));
  }

  @Override
  public String getId() {
    return PROVIDER_ID;
  }

  @Override
  public IdentityProviderModel createConfig() {
    return new CasIdentityProviderConfig();
  }

  @Override
  public List<ProviderConfigProperty> getConfigProperties() {
    return ProviderConfigurationBuilder.create()
        .property()
        .name("casServerUrlPrefix")
        .type(ProviderConfigProperty.STRING_TYPE)
        .label("CAS server URL prefix")
        .helpText("The start of the CAS server URL, i.e. https://localhost:8443/cas")
        .add()
        .property()
        .name("renew")
        .type(ProviderConfigProperty.BOOLEAN_TYPE)
        .label("CAS renew")
        .helpText(
            "If enabled, renew=true will be sent to the CAS server, and users will be forced to "
                + "reauthenticate.")
        .add()
        .property()
        .name("gateway")
        .type(ProviderConfigProperty.BOOLEAN_TYPE)
        .label("CAS gateway")
        .helpText(
            "Enables the CAS server gateway feature. Users who are logged out will not be automatically "
                + "redirected to the login page. There is no change in behavior for users who are already "
                + "authenticated.")
        .add()
        .build();
  }
}
