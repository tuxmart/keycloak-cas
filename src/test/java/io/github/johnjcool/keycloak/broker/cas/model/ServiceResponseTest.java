package io.github.johnjcool.keycloak.broker.cas.model;

import io.github.johnjcool.keycloak.broker.cas.jaxb.ServiceResponseJaxbContextResolver;
import io.github.johnjcool.keycloak.broker.cas.jaxb.ServiceResponseJaxbProvider;
import io.undertow.Undertow;
import java.io.File;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.io.FileUtils;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ServiceResponseTest {

  private static UndertowJaxrsServer server;

  @Test
  public void testReadWithAttributes() {
    server.deploy(MyApp.class);
    ResteasyProviderFactory.getInstance().registerProvider(ServiceResponseJaxbProvider.class, true);
    ResteasyProviderFactory.getInstance()
        .registerProvider(ServiceResponseJaxbContextResolver.class, true);
    Client client = ResteasyClientBuilder.newClient(ResteasyProviderFactory.getInstance());
    WebTarget target =
        client.target(String.format("http://%s:%d%s", "localhost", 9999, "/with-attributes"));
    Response response = target.request().get();
    Assert.assertEquals(200, response.getStatus());
    response.bufferEntity();

    System.out.println(response.readEntity(String.class));

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
    server.deploy(MyApp.class);
    ResteasyProviderFactory.getInstance().registerProvider(ServiceResponseJaxbProvider.class, true);
    ResteasyProviderFactory.getInstance()
        .registerProvider(ServiceResponseJaxbContextResolver.class, true);
    Client client = ResteasyClientBuilder.newClient(ResteasyProviderFactory.getInstance());
    WebTarget target =
        client.target(
            String.format("http://%s:%d%s", "localhost", 9999, "/with-multival-attributes"));
    Response response = target.request().get();
    Assert.assertEquals(200, response.getStatus());
    response.bufferEntity();

    System.out.println(response.readEntity(String.class));

    String stringResponse = response.readEntity(String.class);

    Unmarshaller um = JAXBContext.newInstance(ServiceResponse.class).createUnmarshaller();
    um.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());

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
    server.deploy(MyApp.class);
    ResteasyProviderFactory.getInstance().registerProvider(ServiceResponseJaxbProvider.class, true);
    ResteasyProviderFactory.getInstance()
        .registerProvider(ServiceResponseJaxbContextResolver.class, true);
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

  @Path("")
  public static class Resource {

    @GET
    @Path("with-attributes")
    @Consumes("*/*")
    @Produces("text/html; charset=UTF-8")
    public String withAttributes() throws Exception {
      return FileUtils.readFileToString(
          new File("src/test/resources/test-with-attributes.xml"), "UTF-8");
    }

    @GET
    @Path("with-multival-attributes")
    @Consumes("*/*")
    @Produces("text/html; charset=UTF-8")
    public String withMultiValAttributes() throws Exception {
      return FileUtils.readFileToString(
          new File("src/test/resources/test-with-multivalue-attributes.xml"), "UTF-8");
    }

    @GET
    @Path("without-attributes")
    @Consumes("*/*")
    @Produces("text/html; charset=UTF-8")
    public String withoutAttributes() throws Exception {
      return FileUtils.readFileToString(
          new File("src/test/resources/test-without-attributes.xml"), "UTF-8");
    }
  }

  @ApplicationPath("")
  public static class MyApp extends Application {
    @Override
    public Set<Class<?>> getClasses() {
      HashSet<Class<?>> classes = new HashSet<Class<?>>();
      classes.add(Resource.class);
      return classes;
    }
  }

  @BeforeClass
  public static void init() throws Exception {
    server = new UndertowJaxrsServer().start(Undertow.builder().addHttpListener(9999, "localhost"));
  }

  @AfterClass
  public static void stop() throws Exception {
    server.stop();
  }
}
