package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class JakartaEmailLocalSmtpTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void sendsFm13MessageThroughLocalSmtpServer() throws Exception {
    try (FakeSmtpServer server = new FakeSmtpServer()) {
      int status =
          new Jakarta_Email()
              .send_jakarta_email_obs(
                  main.CUSTOM_TLS,
                  "127.0.0.1",
                  "",
                  "recipient@example.org",
                  "sender@example.org",
                  "FM13 subject",
                  "FM13 body",
                  "null",
                  Integer.toString(server.port()),
                  "null");

      assertEquals(0, status);
      server.awaitMessage();
      assertTrue(server.message().contains("Subject: FM13 subject"));
      assertTrue(server.message().contains("FM13 body"));
      assertTrue(server.message().contains("From: sender@example.org"));
      assertTrue(server.message().contains("To: recipient@example.org"));
    }
  }

  @Test
  public void sendsFormat101HpkAsAttachmentThroughLocalSmtpServer() throws Exception {
    String originalLogsDirectory = main.logs_dir;
    File logsDirectory = temporaryFolder.newFolder("logs");
    File tempDirectory =
        new File(logsDirectory, main.FORMAT_101_ROOT_DIR + File.separator + main.FORMAT_101_TEMP_DIR);
    assertTrue(tempDirectory.mkdirs());
    File hpkFile = new File(tempDirectory, "HPK_" + main.FORMAT_101_INPUT_FILE);
    Files.writeString(hpkFile.toPath(), "HPK payload\n", StandardCharsets.ISO_8859_1);
    main.logs_dir = logsDirectory.getPath();

    try (FakeSmtpServer server = new FakeSmtpServer()) {
      int status =
          new Jakarta_Email()
              .send_jakarta_email_obs(
                  main.CUSTOM_TLS,
                  "127.0.0.1",
                  "",
                  "recipient@example.org",
                  "sender@example.org",
                  "Format 101 subject",
                  "see attachment",
                  "null",
                  Integer.toString(server.port()),
                  "yes");

      assertEquals(0, status);
      server.awaitMessage();
      assertTrue(server.message().contains("Format 101 subject"));
      assertTrue(server.message().contains("see attachment"));
      assertTrue(server.message().contains("HPK_format_101.txt"));
      assertTrue(server.message().contains("HPK payload"));
    } finally {
      main.logs_dir = originalLogsDirectory;
    }
  }

  @Test
  public void reportsMissingFormat101AttachmentWithoutSending() throws Exception {
    String originalLogsDirectory = main.logs_dir;
    File logsDirectory = temporaryFolder.newFolder("missing-attachment-logs");
    main.logs_dir = logsDirectory.getPath();

    try (FakeSmtpServer server = new FakeSmtpServer()) {
      int status =
          new Jakarta_Email()
              .send_jakarta_email_obs(
                  main.CUSTOM_TLS,
                  "127.0.0.1",
                  "",
                  "recipient@example.org",
                  "sender@example.org",
                  "Format 101 subject",
                  "see attachment",
                  "null",
                  Integer.toString(server.port()),
                  "yes");

      assertEquals(1, status);
      server.awaitCompletion();
      assertTrue(server.message() == null || server.message().isEmpty());
    } finally {
      main.logs_dir = originalLogsDirectory;
    }
  }

  @Test
  public void reportsConnectionFailureWithoutSending() throws Exception {
    ServerSocket unusedPort = new ServerSocket(0);
    int port = unusedPort.getLocalPort();
    unusedPort.close();

    int status =
        new Jakarta_Email()
            .send_jakarta_email_obs(
                main.CUSTOM_TLS,
                "127.0.0.1",
                "",
                "recipient@example.org",
                "sender@example.org",
                "Subject",
                "Body",
                "null",
                Integer.toString(port),
                "null");

    assertEquals(1, status);
  }

  @Test
  public void reportsRejectedRecipient() throws Exception {
    try (FakeSmtpServer server = new FakeSmtpServer(true)) {
      int status =
          new Jakarta_Email()
              .send_jakarta_email_obs(
                  main.CUSTOM_TLS,
                  "127.0.0.1",
                  "",
                  "rejected@example.org",
                  "sender@example.org",
                  "Subject",
                  "Body",
                  "null",
                  Integer.toString(server.port()),
                  "null");

      assertEquals(1, status);
    }
  }

  @Test
  public void reportsAuthenticationFailure() throws Exception {
    try (FakeSmtpServer server = new FakeSmtpServer(false, true)) {
      int status =
          new Jakarta_Email()
              .send_jakarta_email_obs(
                  main.CUSTOM_TLS,
                  "127.0.0.1",
                  "wrong-password",
                  "recipient@example.org",
                  "sender@example.org",
                  "Subject",
                  "Body",
                  "null",
                  Integer.toString(server.port()),
                  "null");

      assertEquals(1, status);
    }
  }

  private static final class FakeSmtpServer implements AutoCloseable {
    private final ServerSocket serverSocket;
    private final AtomicReference<String> receivedMessage = new AtomicReference<>();
    private final Thread serverThread;
    private final boolean rejectRecipients;
    private final boolean rejectAuthentication;

    FakeSmtpServer() throws IOException {
      this(false, false);
    }

    FakeSmtpServer(boolean rejectRecipients) throws IOException {
      this(rejectRecipients, false);
    }

    FakeSmtpServer(boolean rejectRecipients, boolean rejectAuthentication) throws IOException {
      serverSocket = new ServerSocket(0);
      this.rejectRecipients = rejectRecipients;
      this.rejectAuthentication = rejectAuthentication;
      serverThread = new Thread(this::serve, "fake-smtp-server");
      serverThread.start();
    }

    int port() {
      return serverSocket.getLocalPort();
    }

    String message() {
      return receivedMessage.get();
    }

    void awaitMessage() throws InterruptedException {
      serverThread.join(5_000);
      assertTrue("Fake SMTP server did not receive a message", receivedMessage.get() != null);
    }

    void awaitCompletion() throws InterruptedException {
      serverThread.join(5_000);
      assertTrue("Fake SMTP server did not finish", !serverThread.isAlive());
    }

    private void serve() {
      try (Socket socket = serverSocket.accept();
          BufferedReader reader =
              new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));
          BufferedWriter writer =
              new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.US_ASCII))) {
        respond(writer, "220 localhost");
        String line;
        while ((line = reader.readLine()) != null) {
          String command = line.toUpperCase();
          if (command.startsWith("EHLO") || command.startsWith("HELO")) {
            respond(writer, "250-localhost");
            if (rejectAuthentication) {
              respond(writer, "250-AUTH LOGIN PLAIN");
            }
            respond(writer, "250 OK");
          } else if (command.startsWith("AUTH")) {
            respond(writer, "535 Authentication failed");
          } else if (command.startsWith("MAIL FROM")) {
            respond(writer, "250 OK");
          } else if (command.startsWith("RCPT TO")) {
            respond(writer, rejectRecipients ? "550 No such recipient" : "250 OK");
          } else if (command.equals("DATA")) {
            respond(writer, "354 End data with <CR><LF>.<CR><LF>");
            StringBuilder message = new StringBuilder();
            while ((line = reader.readLine()) != null && !line.equals(".")) {
              message.append(line).append('\n');
            }
            receivedMessage.set(message.toString());
            respond(writer, "250 OK");
          } else if (command.equals("QUIT")) {
            respond(writer, "221 Bye");
            return;
          } else {
            respond(writer, "250 OK");
          }
        }
      } catch (IOException ignored) {
        // The test assertions report a missing message or a transport failure.
      }
    }

    private static void respond(BufferedWriter writer, String response) throws IOException {
      writer.write(response);
      writer.write("\r\n");
      writer.flush();
    }

    @Override
    public void close() throws Exception {
      serverSocket.close();
      serverThread.join(5_000);
    }
  }
}
