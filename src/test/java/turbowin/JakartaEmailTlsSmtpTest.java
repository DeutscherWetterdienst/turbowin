package turbowin;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
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
  private static SSLContext originalDefaultContext;

  @BeforeClass
  public static void configureTestCertificate() throws Exception {
    originalDefaultContext = SSLContext.getDefault();
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

    originalKeyStore = System.getProperty("javax.net.ssl.keyStore");
    originalTrustStore = System.getProperty("javax.net.ssl.trustStore");
    originalKeyStorePassword = System.getProperty("javax.net.ssl.keyStorePassword");
    originalTrustStorePassword = System.getProperty("javax.net.ssl.trustStorePassword");
    System.setProperty("javax.net.ssl.keyStore", keyStore.toString());
    System.setProperty("javax.net.ssl.trustStore", keyStore.toString());
    System.setProperty("javax.net.ssl.keyStorePassword", STORE_PASSWORD);
    System.setProperty("javax.net.ssl.trustStorePassword", STORE_PASSWORD);
    KeyStore trustStore = KeyStore.getInstance("PKCS12");
    try (java.io.InputStream input = Files.newInputStream(keyStore)) {
      trustStore.load(input, STORE_PASSWORD.toCharArray());
    }
    TrustManagerFactory trustManagers =
        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    trustManagers.init(trustStore);
    SSLContext clientContext = SSLContext.getInstance("TLS");
    clientContext.init(null, trustManagers.getTrustManagers(), null);
    SSLContext.setDefault(clientContext);
  }

  @AfterClass
  public static void restoreSslProperties() throws Exception {
    restore("javax.net.ssl.keyStore", originalKeyStore);
    restore("javax.net.ssl.trustStore", originalTrustStore);
    restore("javax.net.ssl.keyStorePassword", originalKeyStorePassword);
    restore("javax.net.ssl.trustStorePassword", originalTrustStorePassword);
    SSLContext.setDefault(originalDefaultContext);
    Files.deleteIfExists(keyStore);
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
      assertEquals(0, status);
      server.awaitCompletion();
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
      assertEquals(0, status);
      server.awaitCompletion();
    }
  }

  @Test
  public void rejectsServerWithUntrustedCertificate() throws Exception {
    Path untrustedKeyStore = createTestCertificate();
    try (TlsSmtpServer server = new TlsSmtpServer(false, untrustedKeyStore)) {
      try (SSLSocket socket =
          (SSLSocket)
              SSLContext.getDefault().getSocketFactory().createSocket("127.0.0.1", server.port())) {
        try {
          socket.startHandshake();
          throw new AssertionError("Untrusted certificate was accepted");
        } catch (SSLHandshakeException exception) {
          assertEquals(SmtpDiagnostic.Category.TLS_CERTIFICATE, SmtpDiagnostic.classify(exception));
        }
      }
    } finally {
      Files.deleteIfExists(untrustedKeyStore);
    }
  }

  @Test
  public void reportsActualDnsFailure() {
    try {
      InetAddress.getAllByName("smtp-turbowin-test.invalid");
      throw new AssertionError("Reserved invalid domain unexpectedly resolved");
    } catch (UnknownHostException exception) {
      assertEquals(SmtpDiagnostic.Category.DNS, SmtpDiagnostic.classify(exception));
    }
  }

  private static void restore(String name, String value) {
    if (value == null) {
      System.clearProperty(name);
    } else {
      System.setProperty(name, value);
    }
  }

  private static Path createTestCertificate() throws Exception {
    Path certificate = Files.createTempFile("turbowin-smtp-test-", ".p12");
    Files.deleteIfExists(certificate);
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
                certificate.toString(),
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
      throw new IOException("Could not create TLS test certificate");
    }
    return certificate;
  }

  private static final class TlsSmtpServer implements AutoCloseable {
    private final boolean startTls;
    private final ServerSocket serverSocket;
    private final Thread thread;
    private final SSLContext serverContext;
    private volatile Throwable failure;

    TlsSmtpServer(boolean startTls) throws Exception {
      this(startTls, keyStore);
    }

    TlsSmtpServer(boolean startTls, Path serverKeyStore) throws Exception {
      this.startTls = startTls;
      this.serverContext = serverContext(serverKeyStore);
      if (startTls) {
        serverSocket = new ServerSocket(0);
      } else {
        serverSocket = serverContext.getServerSocketFactory().createServerSocket(0);
      }
      thread = new Thread(this::serve, "turbowin-test-smtp");
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
      try (Socket accepted = serverSocket.accept()) {
        Socket socket = accepted;
        if (!startTls) {
          ((SSLSocket) socket).startHandshake();
        }
        boolean tls = !startTls;
        BufferedReader reader = reader(socket);
        BufferedWriter writer = writer(socket);
        reply(writer, "220 localhost");
        for (String line; (line = reader.readLine()) != null; ) {
          String command = line.toUpperCase();
          if (command.startsWith("EHLO") || command.startsWith("HELO")) {
            if (startTls && !tls) {
              reply(writer, "250-localhost");
              reply(writer, "250-STARTTLS");
              reply(writer, "250 OK");
            } else {
              reply(writer, "250 localhost");
            }
          } else if (command.startsWith("STARTTLS")) {
            reply(writer, "220 Ready to start TLS");
            SSLSocket secure =
                (SSLSocket)
                    serverContext
                        .getSocketFactory()
                        .createSocket(socket, "127.0.0.1", socket.getPort(), false);
            secure.setUseClientMode(false);
            secure.startHandshake();
            socket = secure;
            reader = reader(socket);
            writer = writer(socket);
            tls = true;
          } else if (command.startsWith("MAIL FROM") || command.startsWith("RCPT TO")) {
            reply(writer, "250 OK");
          } else if (command.equals("DATA")) {
            reply(writer, "354 End data with <CR><LF>.<CR><LF>");
            while (!".".equals(reader.readLine())) {}
            reply(writer, "250 OK");
          } else if (command.startsWith("QUIT")) {
            reply(writer, "221 Bye");
            return;
          } else {
            reply(writer, "250 OK");
          }
        }
      } catch (Throwable throwable) {
        failure = throwable;
      }
    }

    private static BufferedReader reader(Socket socket) throws IOException {
      return new BufferedReader(
          new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));
    }

    private static SSLContext serverContext(Path serverKeyStore) throws Exception {
      KeyStore keyStore = KeyStore.getInstance("PKCS12");
      try (java.io.InputStream input = Files.newInputStream(serverKeyStore)) {
        keyStore.load(input, STORE_PASSWORD.toCharArray());
      }
      KeyManagerFactory keyManagers =
          KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      keyManagers.init(keyStore, STORE_PASSWORD.toCharArray());
      SSLContext context = SSLContext.getInstance("TLS");
      context.init(keyManagers.getKeyManagers(), null, null);
      return context;
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
    }
  }
}
