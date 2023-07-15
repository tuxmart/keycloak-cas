package io.github.johnjcool.keycloak.broker.cas.jaxb;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.Provider;
import org.jboss.resteasy.plugins.providers.jaxb.XmlJAXBContextFinder;

@Provider
@Produces(MediaType.WILDCARD)
public class ServiceResponseJaxbContextResolver extends XmlJAXBContextFinder {}
