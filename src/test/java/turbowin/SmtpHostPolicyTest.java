package turbowin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SmtpHostPolicyTest {

  @Test
  public void allowsLoopbackAndPrivateAddresses() {
    assertTrue(SmtpHostPolicy.isLocalRelayHost("localhost"));
    assertTrue(SmtpHostPolicy.isLocalRelayHost("127.0.0.1"));
    assertTrue(SmtpHostPolicy.isLocalRelayHost("192.168.10.20"));
    assertTrue(SmtpHostPolicy.isLocalRelayHost("10.20.30.40"));
    assertTrue(SmtpHostPolicy.isLocalRelayHost("fc00::1"));
  }

  @Test
  public void rejectsPublicAndUnknownHosts() {
    assertFalse(SmtpHostPolicy.isLocalRelayHost("8.8.8.8"));
    assertFalse(SmtpHostPolicy.isLocalRelayHost("2001:4860:4860::8888"));
    assertFalse(SmtpHostPolicy.isLocalRelayHost("does-not-exist.invalid"));
  }
}
