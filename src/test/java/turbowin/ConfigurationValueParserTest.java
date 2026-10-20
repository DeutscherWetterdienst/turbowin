package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ConfigurationValueParserTest {

  @Test
  public void parsesIntegerValuesLikeLegacyConfigurationCode() {
    assertEquals(123, ConfigurationValueParser.integer("123"));
    assertEquals(-7, ConfigurationValueParser.integer("-7"));
  }

  @Test(expected = NumberFormatException.class)
  public void rejectsInvalidIntegerValuesLikeLegacyConfigurationCode() {
    ConfigurationValueParser.integer("not-a-number");
  }

  @Test
  public void parsesBooleanValuesLikeLegacyConfigurationCode() {
    assertTrue(ConfigurationValueParser.bool("true"));
    assertTrue(ConfigurationValueParser.bool("TRUE"));
    assertFalse(ConfigurationValueParser.bool("false"));
    assertFalse(ConfigurationValueParser.bool("yes"));
    assertFalse(ConfigurationValueParser.bool(null));
  }
}
