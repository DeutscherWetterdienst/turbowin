package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Format101ServerClientTest {

  private HttpServer server;
  private final AtomicInteger retryAttempts = new AtomicInteger();

  @Before
  public void startServer() throws Exception {
    server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext(
        "/format101",
        exchange -> {
          exchange.sendResponseHeaders(200, -1);
          exchange.close();
        });
    server.createContext(
        "/retry",
        exchange -> {
          retryAttempts.incrementAndGet();
          exchange.sendResponseHeaders(500, -1);
          exchange.close();
        });
    server.createContext(
        "/error",
        exchange -> {
          exchange.sendResponseHeaders(500, -1);
          exchange.close();
        });
    server.start();
  }

  @After
  public void stopServer() {
    server.stop(0);
  }

  @Test
  public void returnsTheSuccessfulResponseCodeAsAString() throws Exception {
    String url = "http://localhost:" + server.getAddress().getPort() + "/format101";

    assertEquals("200", Format101ServerClient.execute(url, false));
  }

  @Test
  public void retriesAnUnsuccessfulResponseThreeTimes() throws Exception {
    String url = "http://127.0.0.1:" + server.getAddress().getPort() + "/retry";

    assertEquals("500", Format101ServerClient.execute(url, false));
    assertEquals(3, retryAttempts.get());
  }

  @Test
  public void propagatesMalformedUri() throws Exception {
    try {
      Format101ServerClient.execute("http://[invalid", false);
    } catch (URISyntaxException expected) {
      return;
    }
    throw new AssertionError("Expected malformed URI to escape the retry client");
  }

  @Test
  public void returnsNoInternetForUnknownHost() throws Exception {
    assertTrue(
        Format101ServerClient.execute("http://format101-test.invalid/observation", false)
            .startsWith("711 ("));
  }

  @Test
  public void returnsServerErrorResponseCodeAsAString() throws Exception {
    String url = "http://localhost:" + server.getAddress().getPort() + "/error";

    assertEquals("500", Format101ServerClient.execute(url, false));
  }

  @Test
  public void propagatesInterruptionDuringRetryBackoff() throws Exception {
    int unusedPort = server.getAddress().getPort();
    server.stop(0);
    Thread.currentThread().interrupt();

    try {
      Format101ServerClient.execute("http://localhost:" + unusedPort + "/format101", false);
      fail("Expected the interrupted retry delay to be propagated");
    } catch (InterruptedException expected) {
      // Expected: the retry delay preserves the legacy interruption behavior.
    } finally {
      Thread.interrupted();
    }
  }
}
