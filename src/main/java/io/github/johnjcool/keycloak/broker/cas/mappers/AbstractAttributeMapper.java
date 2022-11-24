package io.github.johnjcool.keycloak.broker.cas.mappers;

import io.github.johnjcool.keycloak.broker.cas.CasIdentityProvider;

import java.util.*;

import org.jboss.logging.Logger;
import org.keycloak.broker.provider.AbstractIdentityProviderMapper;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.common.util.CollectionUtil;
import org.keycloak.models.IdentityProviderMapperModel;

public abstract class AbstractAttributeMapper extends AbstractIdentityProviderMapper {

	protected static final Logger logger = Logger.getLogger(AbstractAttributeMapper.class);

	public static final String ATTRIBUTE = "attribute";
	public static final String ATTRIBUTE_VALUE = "attribute.value";

	public static List<String> getAttributeValue(final IdentityProviderMapperModel mapperModel, final BrokeredIdentityContext user) {
		String attributeName = mapperModel.getConfig().get(ATTRIBUTE);
		@SuppressWarnings("unchecked")
		Map<String, List<String>> userAttributes = (Map<String, List<String>>) user.getContextData().get(CasIdentityProvider.USER_ATTRIBUTES);
		logger.debug("getAttributeValue attributes: " + userAttributes);
		return userAttributes.get(attributeName);
	}

	protected boolean hasAttributeValue(final IdentityProviderMapperModel mapperModel, final BrokeredIdentityContext context) {
		List<String> value = getAttributeValue(mapperModel, context);
		String desiredValue = mapperModel.getConfig().get(ATTRIBUTE_VALUE);
		return CollectionUtil.collectionEquals(Collections.singletonList(desiredValue), value);
	}
}
