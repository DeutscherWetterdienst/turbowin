package turbowin;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLException;

/** Classifies common SMTP failures without exposing credentials. */
final class SmtpDiagnostic {

  enum Category {
    DNS,
    CONNECTION,
    TLS_CERTIFICATE,
    AUTHENTICATION,
    RECIPIENT_REJECTED,
    SMTP,
    UNKNOWN
  }

  private SmtpDiagnostic() {}

  static Category classify(Throwable failure) {
    for (Throwable current = failure; current != null; current = current.getCause()) {
      if (current instanceof UnknownHostException) {
        return Category.DNS;
      }
      if (current instanceof CertificateException
          || current instanceof SSLException
          || contains(current, "certificate", "PKIX", "handshake", "trust anchor")) {
        return Category.TLS_CERTIFICATE;
      }
      if (contains(current, "535", "authentication", "auth failed", "invalid credentials")) {
        return Category.AUTHENTICATION;
      }
      if (contains(current, "550", "551", "553", "recipient", "address rejected")) {
        return Category.RECIPIENT_REJECTED;
      }
      if (current instanceof ConnectException
          || current instanceof SocketTimeoutException
          || contains(current, "connection refused", "connect timed out", "read timed out")) {
        return Category.CONNECTION;
      }
    }
    return failure instanceof jakarta.mail.MessagingException ? Category.SMTP : Category.UNKNOWN;
  }

  static String label(Category category) {
    return switch (category) {
      case DNS -> "DNS resolution";
      case CONNECTION -> "connection or timeout";
      case TLS_CERTIFICATE -> "TLS certificate or handshake";
      case AUTHENTICATION -> "authentication";
      case RECIPIENT_REJECTED -> "recipient rejected";
      case SMTP -> "SMTP protocol";
      case UNKNOWN -> "unknown";
    };
  }

  private static boolean contains(Throwable failure, String... terms) {
    String text = String.valueOf(failure.getMessage()).toLowerCase();
    for (String term : terms) {
      if (text.contains(term.toLowerCase())) {
        return true;
      }
    }
    return false;
  }
}
