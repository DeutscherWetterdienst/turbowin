package turbowin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ReportingSettingsValidationTest {

  @Test
  public void recognizesBlankAndConfiguredValues() {
    assertTrue(ReportingSettingsValidation.isBlank(""));
    assertFalse(ReportingSettingsValidation.isBlank("hourly"));
  }
}
