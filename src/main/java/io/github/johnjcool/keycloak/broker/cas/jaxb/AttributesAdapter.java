package io.github.johnjcool.keycloak.broker.cas.jaxb;

import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class AttributesAdapter extends XmlAdapter<AttributesWrapper, Map<String, List<String>>> {

  @Override
  public AttributesWrapper marshal(final Map<String, List<String>> attributes) {
    throw new UnsupportedOperationException("This adapter only supports from xml to map.");
  }

  @Override
  public Map<String, List<String>> unmarshal(final AttributesWrapper attributesWrapper) {
    return attributesWrapper.toMap();
  }
}
