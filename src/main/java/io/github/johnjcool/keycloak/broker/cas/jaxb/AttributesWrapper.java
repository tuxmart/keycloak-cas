package io.github.johnjcool.keycloak.broker.cas.jaxb;

import java.util.*;
import java.util.stream.Collectors;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlType;
import org.w3c.dom.Node;

@XmlType
public class AttributesWrapper {

  private final List<Node> attributes = new ArrayList<>();

  @XmlAnyElement
  public List<Node> getAttributes() {
    return attributes;
  }

  public Map<String, List<String>> toMap() {
    return attributes.stream()
        .collect(
            Collectors.groupingBy(
                Node::getLocalName, Collectors.mapping(Node::getTextContent, Collectors.toList())));
  }
}
