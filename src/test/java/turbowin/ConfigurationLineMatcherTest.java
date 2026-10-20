package turbowin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ConfigurationLineMatcherTest {

  @Test
  public void matchesKeysAsSubstrings() {
    assertTrue(ConfigurationLineMatcher.contains("prefix-value", "prefix"));
    assertTrue(ConfigurationLineMatcher.contains("prefix-value", "value"));
    assertFalse(ConfigurationLineMatcher.contains("prefix-value", "other"));
  }

  @Test
  public void preservesEmptyKeyAndNullLineBehaviorOfLegacyMatching() {
    assertTrue(ConfigurationLineMatcher.contains("line", ""));
  }

  @Test(expected = NullPointerException.class)
  public void rejectsNullLinesLikeLegacyMatching() {
    ConfigurationLineMatcher.contains(null, "key");
  }
}
