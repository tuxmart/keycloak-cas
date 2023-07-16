package io.github.johnjcool.keycloak.broker.cas.model;

import io.github.johnjcool.keycloak.broker.cas.jaxb.ServiceResponseJaxbContextResolver;
import io.github.johnjcool.keycloak.broker.cas.jaxb.ServiceResponseJaxbProvider;
import io.undertow.Undertow;
import io.undertow.util.Headers;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import org.apache.commons.io.FileUtils;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ServiceResponseTest {

  private static Undertow server;

  @Test
  public void testReadWithAttributes() {
    Client client = ResteasyClientBuilder.newClient(ResteasyProviderFactory.getInstance());
    WebTarget target =
        client.target(String.format("http://%s:%d%s", "localhost", 9999, "/with-attributes"));
    Response response = target.request().get();
    Assert.assertEquals(200, response.getStatus());
    response.bufferEntity();

    ServiceResponse serviceResponse = response.readEntity(ServiceResponse.class);
    Success success = serviceResponse.getSuccess();

    Assert.assertEquals("test", success.getUser());
    Assert.assertFalse(success.getAttributes().isEmpty());
    Assert.assertEquals(
        Collections.singletonList("test.test@test.test"), success.getAttributes().get("mail"));
    Assert.assertEquals(Collections.singletonList("tets"), success.getAttributes().get("sn"));
    Assert.assertEquals(Collections.singletonList("test"), success.getAttributes().get("cn"));
  }

  @Test
  public void testReadWithMultiValAttributes() throws JAXBException {
    Client client = ResteasyClientBuilder.newClient(ResteasyProviderFactory.getInstance());
    WebTarget target =
        client.target(
            String.format("http://%s:%d%s", "localhost", 9999, "/with-multival-attributes"));
    Response response = target.request().get();
    Assert.assertEquals(200, response.getStatus());
    response.bufferEntity();

    String stringResponse = response.readEntity(String.class);

    Unmarshaller um = JAXBContext.newInstance(ServiceResponse.class).createUnmarshaller();
    um.setEventHandler(new jakarta.xml.bind.helpers.DefaultValidationEventHandler());

    ServiceResponse serviceResponse =
        (ServiceResponse) um.unmarshal(new StringReader(stringResponse));

    System.out.println(serviceResponse);
    System.out.println(serviceResponse.getSuccess());
    System.out.println(serviceResponse.getSuccess().getAttributes());
    Success success = serviceResponse.getSuccess();

    Assert.assertEquals("test", success.getUser());
    Assert.assertFalse(success.getAttributes().isEmpty());
    Assert.assertEquals(
        Collections.singletonList("test.test@test.test"), success.getAttributes().get("mail"));
    Assert.assertEquals(Collections.singletonList("tets"), success.getAttributes().get("sn"));
    Assert.assertEquals(Collections.singletonList("test"), success.getAttributes().get("cn"));
    Assert.assertEquals(
        Arrays.asList("LdapAuthenticationHandler", "mfa-duo"),
        success.getAttributes().get("successfulAuthenticationHandlers"));
  }

  @Test
  public void testReadWithoutAttributes() {
    Client client = ResteasyClientBuilder.newClient(ResteasyProviderFactory.getInstance());
    WebTarget target =
        client.target(String.format("http://%s:%d%s", "localhost", 9999, "/without-attributes"));
    Response response = target.request().get();
    Assert.assertEquals(200, response.getStatus());
    response.bufferEntity();

    System.out.println(response.readEntity(String.class));

    ServiceResponse serviceResponse = response.readEntity(ServiceResponse.class);
    Success success = serviceResponse.getSuccess();

    Assert.assertEquals("test", success.getUser());
    Assert.assertTrue(success.getAttributes().isEmpty());
  }

  @BeforeClass
  public static void init() {
    ResteasyProviderFactory.getInstance().registerProvider(ServiceResponseJaxbProvider.class, true);
    ResteasyProviderFactory.getInstance()
        .registerProvider(ServiceResponseJaxbContextResolver.class, true);

    server =
        Undertow.builder()
            .addHttpListener(9999, "localhost")
            .setHandler(
                httpServerExchange -> {
                  switch (httpServerExchange.getRequestURI()) {
                    case "/without-attributes":
                      httpServerExchange
                          .getResponseHeaders()
                          .put(Headers.CONTENT_TYPE, "application/xml");
                      httpServerExchange
                          .getResponseSender()
                          .send(
                              FileUtils.readFileToString(
                                  new File("src/test/resources/test-without-attributes.xml"),
                                  "UTF-8"));
                      break;
                    case "/with-attributes":
                      httpServerExchange
                          .getResponseHeaders()
                          .put(Headers.CONTENT_TYPE, "application/xml");
                      httpServerExchange
                          .getResponseSender()
                          .send(
                              FileUtils.readFileToString(
                                  new File("src/test/resources/test-with-attributes.xml"),
                                  "UTF-8"));
                      break;
                    case "/with-multival-attributes":
                      httpServerExchange
                          .getResponseHeaders()
                          .put(Headers.CONTENT_TYPE, "application/xml");
                      httpServerExchange
                          .getResponseSender()
                          .send(
                              FileUtils.readFileToString(
                                  new File(
                                      "src/test/resources/test-with-multivalue-attributes.xml"),
                                  "UTF-8"));
                      break;
                    default:
                      throw new IllegalStateException(
                          "Unexpected value: " + httpServerExchange.getRequestURI());
                  }
                })
            .build();

    server.start();
  }

  @AfterClass
  public static void stop() {
    server.stop();
  }
}
