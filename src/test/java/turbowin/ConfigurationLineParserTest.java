package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ConfigurationLineParserTest {

  @Test
  public void extractsValueAtLegacyConfigurationOffset() {
    assertEquals("value", ConfigurationLineParser.value("123456789012345678901value"));
  }

  @Test(expected = StringIndexOutOfBoundsException.class)
  public void rejectsLinesShorterThanLegacyConfigurationOffset() {
    ConfigurationLineParser.value("short");
  }
}
