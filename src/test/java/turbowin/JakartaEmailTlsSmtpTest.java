package turbowin;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class JakartaEmailTlsSmtpTest {

  private static final String STORE_PASSWORD = "changeit";
  private static Path keyStore;
  private static String originalKeyStore;
  private static String originalTrustStore;
  private static String originalKeyStorePassword;
  private static String originalTrustStorePassword;
  private static SSLContext testSslContext;
  private static SSLContext originalSslContext;

  @BeforeClass
  public static void configureTestCertificate() throws Exception {
    keyStore = Files.createTempFile("turbowin-smtp-test-", ".p12");
    Files.deleteIfExists(keyStore);
    String keytool = Path.of(System.getProperty("java.home"), "bin", "keytool").toString();
    Process process =
        new ProcessBuilder(
                keytool,
                "-genkeypair",
                "-alias",
                "smtp-test",
                "-keyalg",
                "RSA",
                "-keysize",
                "2048",
                "-validity",
                "1",
                "-storetype",
                "PKCS12",
                "-keystore",
                keyStore.toString(),
                "-storepass",
                STORE_PASSWORD,
                "-keypass",
                STORE_PASSWORD,
                "-dname",
                "CN=127.0.0.1",
                "-ext",
                "SAN=IP:127.0.0.1")
            .redirectErrorStream(true)
            .start();

    if (process.waitFor() != 0) {
      throw new IOException(
          "Could not create TLS test certificate: "
              + new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
    }

    // Save original JVM SSL properties.
    originalKeyStore = System.getProperty("javax.net.ssl.keyStore");
    originalTrustStore = System.getProperty("javax.net.ssl.trustStore");
    originalKeyStorePassword = System.getProperty("javax.net.ssl.keyStorePassword");
    originalTrustStorePassword = System.getProperty("javax.net.ssl.trustStorePassword");

    // Save the original default SSL context before changing it.
    originalSslContext = SSLContext.getDefault();

    // Configure JVM SSL properties.
    System.setProperty("javax.net.ssl.keyStore", keyStore.toString());
    System.setProperty("javax.net.ssl.trustStore", keyStore.toString());
    System.setProperty("javax.net.ssl.keyStorePassword", STORE_PASSWORD);
    System.setProperty("javax.net.ssl.trustStorePassword", STORE_PASSWORD);

    // Create explicitly initialized SSL context.
    testSslContext = createTestSslContext();

    // Make the test context available to Jakarta Mail.
    SSLContext.setDefault(testSslContext);
  }

  private static SSLContext createTestSslContext() throws Exception {
    // Load server keystore.
    KeyStore serverStore = KeyStore.getInstance("PKCS12");
    try (InputStream input = Files.newInputStream(keyStore)) {
      serverStore.load(input, STORE_PASSWORD.toCharArray());
    }

    // Configure server identity.
    KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    kmf.init(serverStore, STORE_PASSWORD.toCharArray());

    // Create a separate client truststore.
    KeyStore trustStore = KeyStore.getInstance("PKCS12");
    trustStore.load(null, null);
    trustStore.setCertificateEntry("smtp-test", serverStore.getCertificate("smtp-test"));
    TrustManagerFactory tmf =
        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    tmf.init(trustStore);

    // Initialize SSL context.
    SSLContext context = SSLContext.getInstance("TLS");
    context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
    return context;
  }

  @AfterClass
  public static void restoreSslProperties() throws Exception {
    // Restore the original default SSL context.
    if (originalSslContext != null) {
      SSLContext.setDefault(originalSslContext);
    }
    restore("javax.net.ssl.keyStore", originalKeyStore);
    restore("javax.net.ssl.trustStore", originalTrustStore);
    restore("javax.net.ssl.keyStorePassword", originalKeyStorePassword);
    restore("javax.net.ssl.trustStorePassword", originalTrustStorePassword);
    if (keyStore != null) {
      Files.deleteIfExists(keyStore);
    }
  }

  private static void restore(String name, String value) {
    if (value == null) {
      System.clearProperty(name);
    } else {
      System.setProperty(name, value);
    }
  }

  @Test
  public void sendsThroughLocalStartTlsServer() throws Exception {
    try (TlsSmtpServer server = new TlsSmtpServer(true)) {
      int status =
          new Jakarta_Email()
              .send_jakarta_email_obs(
                  SmtpSecurityMode.STARTTLS,
                  "127.0.0.1",
                  "",
                  "recipient@example.org",
                  "sender@example.org",
                  "STARTTLS test",
                  "body",
                  "null",
                  Integer.toString(server.port()),
                  "null");
      server.awaitCompletion();
      assertEquals(0, status);
    }
  }

  @Test
  public void sendsThroughLocalImplicitTlsServer() throws Exception {
    try (TlsSmtpServer server = new TlsSmtpServer(false)) {
      int status =
          new Jakarta_Email()
              .send_jakarta_email_obs(
                  SmtpSecurityMode.IMPLICIT_TLS,
                  "127.0.0.1",
                  "",
                  "recipient@example.org",
                  "sender@example.org",
                  "Implicit TLS test",
                  "body",
                  "null",
                  Integer.toString(server.port()),
                  "null");
      server.awaitCompletion();

      assertEquals(0, status);
    }
  }

  private static final class TlsSmtpServer implements AutoCloseable {
    private final boolean startTls;
    private final ServerSocket serverSocket;
    private final Thread thread;

    private volatile Throwable failure;

    TlsSmtpServer(boolean startTls) throws Exception {

      this.startTls = startTls;
      if (startTls) {
        // STARTTLS begins with a plain TCP connection.
        serverSocket = new ServerSocket(0);
      } else {
        // Implicit TLS begins with a TLS connection.
        serverSocket = testSslContext.getServerSocketFactory().createServerSocket(0);
      }
      thread = new Thread(this::serve, "turbowin-test-smtp");
      thread.setDaemon(true);
      thread.start();
    }

    int port() {
      return serverSocket.getLocalPort();
    }

    void awaitCompletion() throws Exception {
      thread.join(10_000);
      if (thread.isAlive()) {
        throw new AssertionError("TLS SMTP server did not finish");
      }
      if (failure != null) {
        throw new AssertionError("TLS SMTP server failed", failure);
      }
    }

    private void serve() {
      Socket socket = null;
      try {
        socket = serverSocket.accept();
        boolean tls = !startTls;
        if (!startTls) {
          // Implicit TLS handshake.
          SSLSocket secure = (SSLSocket) socket;
          secure.setUseClientMode(false);
          secure.startHandshake();
        }
        BufferedReader reader = reader(socket);
        BufferedWriter writer = writer(socket);
        reply(writer, "220 localhost ESMTP");
        for (String line; (line = reader.readLine()) != null; ) {
          String command = line.toUpperCase(java.util.Locale.ROOT);
          if (command.startsWith("EHLO") || command.startsWith("HELO")) {
            if (startTls && !tls) {
              reply(writer, "250-localhost");
              reply(writer, "250-STARTTLS");
              reply(writer, "250 OK");
            } else {
              reply(writer, "250-localhost");
              reply(writer, "250 SIZE 10000000");
            }
          } else if (command.startsWith("STARTTLS")) {
            if (!startTls || tls) {
              reply(writer, "503 Bad sequence of commands");
              continue;
            }
            reply(writer, "220 Ready to start TLS");
            SSLSocket secure =
                (SSLSocket)
                    testSslContext
                        .getSocketFactory()
                        .createSocket(socket, "127.0.0.1", socket.getPort(), false);
            secure.setUseClientMode(false);
            // Perform TLS handshake.
            secure.startHandshake();
            socket = secure;
            reader = reader(socket);
            writer = writer(socket);
            tls = true;
          } else if (command.startsWith("MAIL FROM")) {
            reply(writer, "250 2.1.0 Sender OK");
          } else if (command.startsWith("RCPT TO")) {
            reply(writer, "250 2.1.5 Recipient OK");
          } else if (command.equals("DATA")) {
            reply(writer, "354 End data with <CR><LF>.<CR><LF>");
            while (true) {
              String dataLine = reader.readLine();
              if (dataLine == null) {
                throw new IOException("Connection closed while receiving DATA");
              }
              if (".".equals(dataLine)) {
                break;
              }
            }
            reply(writer, "250 2.0.0 Message accepted");
          } else if (command.startsWith("RSET")) {
            reply(writer, "250 OK");
          } else if (command.startsWith("NOOP")) {
            reply(writer, "250 OK");
          } else if (command.startsWith("QUIT")) {
            reply(writer, "221 2.0.0 Bye");
            return;
          } else {
            reply(writer, "250 OK");
          }
        }
      } catch (IOException exception) {
        // A client disconnect during TLS negotiation is useful
        // diagnostic information, but preserve the actual failure.
        failure = exception;
      } catch (Throwable throwable) {
        failure = throwable;
        throwable.printStackTrace();
      } finally {
        if (socket != null) {
          try {
            socket.close();
          } catch (IOException ignored) {
            // Ignore cleanup errors.
          }
        }
      }
    }

    private static BufferedReader reader(Socket socket) throws IOException {
      return new BufferedReader(
          new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));
    }

    private static BufferedWriter writer(Socket socket) throws IOException {
      return new BufferedWriter(
          new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.US_ASCII));
    }

    private static void reply(BufferedWriter writer, String response) throws IOException {
      writer.write(response);
      writer.write("\r\n");
      writer.flush();
    }

    @Override
    public void close() throws Exception {
      serverSocket.close();
      thread.join(10_000);
      if (thread.isAlive()) {
        throw new AssertionError("TLS SMTP server thread did not terminate");
      }
    }
  }
}
