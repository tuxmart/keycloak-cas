package io.github.johnjcool.keycloak.broker.cas.mappers;

import io.github.johnjcool.keycloak.broker.cas.CasIdentityProviderFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.jboss.logging.Logger;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.common.util.CollectionUtil;
import org.keycloak.models.*;
import org.keycloak.provider.ProviderConfigProperty;

public class UserAttributeMapper extends AbstractAttributeMapper {

  private static final String[] cp = new String[] {CasIdentityProviderFactory.PROVIDER_ID};

  private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();

  private static final String ATTRIBUTE = "attribute";
  private static final String USER_ATTRIBUTE = "user.attribute";
  private static final String EMAIL = "email";
  private static final String FIRST_NAME = "firstName";
  private static final String LAST_NAME = "lastName";

  protected static final Logger logger = Logger.getLogger(UserAttributeMapper.class);

  static {
    ProviderConfigProperty property;
    property = new ProviderConfigProperty();
    property.setName(ATTRIBUTE);
    property.setLabel("Attribute");
    property.setHelpText("Name of attribute to search for in assertion.");
    property.setType(ProviderConfigProperty.STRING_TYPE);
    configProperties.add(property);
    property = new ProviderConfigProperty();
    property.setName(USER_ATTRIBUTE);
    property.setLabel("User Attribute Name");
    property.setHelpText(
        "User attribute name to store CAS attribute.  Use email, lastName, and firstName to map to those predefined user properties.");
    property.setType(ProviderConfigProperty.STRING_TYPE);
    configProperties.add(property);
  }

  public static final String PROVIDER_ID = "cas-user-attribute-idp-mapper";

  @Override
  public List<ProviderConfigProperty> getConfigProperties() {
    return configProperties;
  }

  @Override
  public String getId() {
    return PROVIDER_ID;
  }

  @Override
  public String[] getCompatibleProviders() {
    return cp;
  }

  @Override
  public String getDisplayCategory() {
    return "Attribute Importer";
  }

  @Override
  public String getDisplayType() {
    return "Attribute Importer";
  }

  @Override
  public void preprocessFederatedIdentity(
      final KeycloakSession session,
      final RealmModel realm,
      final IdentityProviderMapperModel mapperModel,
      final BrokeredIdentityContext context) {
    String attribute = mapperModel.getConfig().get(USER_ATTRIBUTE);
    if (attribute == null || attribute.isEmpty()) {
      logger.debug("preprocessFederatedIdentity called with empty attribute");
      return;
    }

    logger.debug("preprocessFederatedIdentity called with attribute " + attribute);

    List<String> value = getAttributeValue(mapperModel, context);

    logger.debug("Values: " + value.toString());

    if (EMAIL.equalsIgnoreCase(attribute)) {
      setIfNotEmpty(context::setEmail, value);
    } else if (FIRST_NAME.equalsIgnoreCase(attribute)) {
      setIfNotEmpty(context::setFirstName, value);
    } else if (LAST_NAME.equalsIgnoreCase(attribute)) {
      setIfNotEmpty(context::setLastName, value);
    } else {
      context.setUserAttribute(attribute, value);
    }
  }

  private void setIfNotEmpty(final Consumer<String> consumer, final List<String> values) {
    if (values != null && !values.isEmpty()) {
      consumer.accept(values.get(0));
    }
  }

  @Override
  public void updateBrokeredUser(
      final KeycloakSession session,
      final RealmModel realm,
      final UserModel user,
      final IdentityProviderMapperModel mapperModel,
      final BrokeredIdentityContext context) {
    String attribute = mapperModel.getConfig().get(USER_ATTRIBUTE);
    if (attribute == null || attribute.isEmpty()) {
      logger.debug("updateBrokeredUser called with empty attribute");
      return;
    }
    logger.debug("preprocessFederatedIdentity called with attribute " + attribute);

    List<String> value = getAttributeValue(mapperModel, context);

    logger.debug("Values: " + value.toString());

    if (EMAIL.equalsIgnoreCase(attribute)) {
      setIfNotEmpty(user::setEmail, value);
    } else if (FIRST_NAME.equalsIgnoreCase(attribute)) {
      setIfNotEmpty(user::setFirstName, value);
    } else if (LAST_NAME.equalsIgnoreCase(attribute)) {
      setIfNotEmpty(user::setLastName, value);
    } else {
      List<String> current = user.getAttributeStream(attribute).collect(Collectors.toList());
      if (!CollectionUtil.collectionEquals(value, current)) {
        user.setAttribute(attribute, value);
      } else if (value.isEmpty()) {
        user.removeAttribute(attribute);
      }
    }
  }

  @Override
  public boolean supportsSyncMode(IdentityProviderSyncMode syncMode) {
    return true;
  }

  @Override
  public String getHelpText() {
    return "Import declared CAS attribute if it exists in assertion into the specified user property or attribute.";
  }
}
