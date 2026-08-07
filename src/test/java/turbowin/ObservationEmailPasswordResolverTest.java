package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class ObservationEmailPasswordResolverTest {

  @Test
  public void preservesNullMarkerWithoutCallingDecryptor() {
    assertEquals(
        "null",
        ObservationEmailPasswordResolver.resolve(
            "null",
            value -> {
              fail("The null marker must not be decrypted");
              return "";
            }));
  }

  @Test
  public void decryptsConfiguredPassword() {
    assertEquals(
        "plain-text", ObservationEmailPasswordResolver.resolve("encrypted", value -> "plain-text"));
  }
}
