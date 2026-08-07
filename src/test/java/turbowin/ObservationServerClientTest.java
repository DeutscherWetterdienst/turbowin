package turbowin;

import static org.junit.Assert.assertEquals;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.security.Principal;
import java.security.cert.Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ObservationServerClientTest {

  private HttpServer server;
  private static String lastHttpsRequestMethod;

  @Before
  public void startServer() throws Exception {
    server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext(
        "/ok",
        exchange -> {
          exchange.sendResponseHeaders(200, -1);
          exchange.close();
        });
    server.createContext(
        "/missing",
        exchange -> {
          exchange.sendResponseHeaders(404, -1);
          exchange.close();
        });
    server.start();
  }

  @After
  public void stopServer() {
    server.stop(0);
  }

  @Test
  public void returnsTheHttpResponseCode() throws Exception {
    String baseUrl = "http://localhost:" + server.getAddress().getPort();

    assertEquals(200, ObservationServerClient.getResponseCode(baseUrl + "/ok"));
    assertEquals(404, ObservationServerClient.getResponseCode(baseUrl + "/missing"));
  }

  @Test
  public void usesGetForHttpsConnections() throws Exception {
    assertEquals(
        204,
        ObservationServerClient.getResponseCode(
            "https://example.test/ok",
            uri -> new TestHttpsURLConnection(uri.toURL())));
    assertEquals("GET", lastHttpsRequestMethod);
  }

  private static final class TestHttpsURLConnection extends HttpsURLConnection {

    private TestHttpsURLConnection(URL url) {
      super(url);
    }

    @Override
    public void disconnect() {}

    @Override
    public boolean usingProxy() {
      return false;
    }

    @Override
    public void connect() throws IOException {}

    @Override
    public int getResponseCode() {
      lastHttpsRequestMethod = getRequestMethod();
      return 204;
    }

    @Override
    public String getCipherSuite() {
      return null;
    }

    @Override
    public Certificate[] getLocalCertificates() {
      return null;
    }

    @Override
    public Certificate[] getServerCertificates() throws SSLPeerUnverifiedException {
      return null;
    }

    @Override
    public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
      return null;
    }

    @Override
    public Principal getLocalPrincipal() {
      return null;
    }
  }
}
