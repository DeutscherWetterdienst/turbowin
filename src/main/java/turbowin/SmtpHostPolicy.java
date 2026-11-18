package turbowin;

import java.net.InetAddress;
import java.net.UnknownHostException;

/** Network policy for the intentionally unencrypted local-relay SMTP mode. */
final class SmtpHostPolicy {

  private SmtpHostPolicy() {}

  static boolean isLocalRelayHost(String host) {
    if (host == null || host.trim().isEmpty()) {
      return false;
    }
    try {
      InetAddress[] addresses = InetAddress.getAllByName(host.trim());
      if (addresses.length == 0) {
        return false;
      }
      for (InetAddress address : addresses) {
        if (!isPrivateOrLocal(address)) {
          return false;
        }
      }
      return true;
    } catch (UnknownHostException exception) {
      return false;
    }
  }

  private static boolean isPrivateOrLocal(InetAddress address) {
    if (address.isAnyLocalAddress()
        || address.isLoopbackAddress()
        || address.isLinkLocalAddress()
        || address.isSiteLocalAddress()) {
      return true;
    }
    byte[] bytes = address.getAddress();
    if (bytes.length == 4) {
      int first = bytes[0] & 0xff;
      int second = bytes[1] & 0xff;
      return first == 10
          || (first == 172 && second >= 16 && second <= 31)
          || (first == 192 && second == 168);
    }
    // IPv6 Unique Local Address (fc00::/7).
    return bytes.length == 16 && (bytes[0] & 0xfe) == 0xfc;
  }
}
