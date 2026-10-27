package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import format101.Encoder;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Test;

public class Format101LibraryIntegrationTest {

  @Test
  public void bundlesPilotTableAndEncodesWithoutCliOrNativeProcess() throws Exception {
    String resourceName = "/format101/config/S-AWS-101_modl_pilote.csv";
    try (InputStream resource = getClass().getResourceAsStream(resourceName)) {
      assertNotNull(resource);

      Path directory = Files.createTempDirectory("format101-library");
      Path pilote = directory.resolve("S-AWS-101_modl_pilote.csv");
      Files.copy(resource, pilote);

      String input = "0\n" + "0\n".repeat(57);
      Path format101 = directory.resolve("format_101.txt");
      Files.writeString(format101, input, StandardCharsets.UTF_8);

      Encoder.EncodedMessage message =
          Encoder.encodeFromFormat101Txt(pilote, format101, "T01");

      assertEquals("T01", message.stationId());
      assertEquals("    T01", message.stationIdRaw());
      assertTrue(message.toHpkLine().startsWith("    T01"));
      assertTrue(message.payloadOctets().length > 0);
    }
  }
}
