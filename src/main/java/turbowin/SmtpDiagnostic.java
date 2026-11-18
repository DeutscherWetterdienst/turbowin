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
    LOCAL_RELAY_POLICY,
    SMTP,
    UNKNOWN
  }

  private SmtpDiagnostic() {}

  static Category classify(Throwable failure) {
    for (Throwable current = failure; current != null; current = current.getCause()) {
      String status = extractStatus(current.getMessage());
      if (current instanceof UnknownHostException) {
        return Category.DNS;
      }
      if (current instanceof CertificateException
          || current instanceof SSLException
          || contains(current, "certificate", "PKIX", "handshake", "trust anchor")) {
        return Category.TLS_CERTIFICATE;
      }
      if ("535".equals(status)
          || contains(current, "authentication", "auth failed", "invalid credentials")) {
        return Category.AUTHENTICATION;
      }
      if (("550".equals(status) || "551".equals(status) || "553".equals(status))
          || contains(current, "recipient", "address rejected")) {
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

  static String extractStatus(String text) {
    if (text != null) {
      for (int index = 0; index <= text.length() - 3; index++) {
        char first = text.charAt(index);
        char second = text.charAt(index + 1);
        char third = text.charAt(index + 2);
        boolean validCode =
            first >= '2'
                && first <= '5'
                && second >= '0'
                && second <= '9'
                && third >= '0'
                && third <= '9';
        boolean separated =
            (index == 0 || !Character.isDigit(text.charAt(index - 1)))
                && (index + 3 == text.length() || !Character.isDigit(text.charAt(index + 3)))
                && (index + 3 == text.length()
                    || text.charAt(index + 3) == ' '
                    || text.charAt(index + 3) == '-');
        if (validCode && separated) {
          return text.substring(index, index + 3);
        }
      }
    }
    return "unknown";
  }

  static String label(Category category) {
    return switch (category) {
      case DNS -> "DNS resolution";
      case CONNECTION -> "connection or timeout";
      case TLS_CERTIFICATE -> "TLS certificate or handshake";
      case AUTHENTICATION -> "authentication";
      case RECIPIENT_REJECTED -> "recipient rejected";
      case LOCAL_RELAY_POLICY -> "unencrypted remote relay blocked";
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
