package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ConfigurationManagerTest {

  @Test
  public void migratesPersistedPythonEmailSelectionToJakarta() {
    String originalEmailModule = main.custom_email_module;
    try {
      main.custom_email_module = main.PYTHON_EMAIL;

      ConfigurationManager.normalizeEmailModule();

      assertEquals(main.JAKARTA_EMAIL, main.custom_email_module);
    } finally {
      main.custom_email_module = originalEmailModule;
    }
  }

  @Test
  public void preservesNonPythonEmailSelections() {
    String originalEmailModule = main.custom_email_module;
    try {
      main.custom_email_module = main.JAKARTA_EMAIL;
      ConfigurationManager.normalizeEmailModule();
      assertEquals(main.JAKARTA_EMAIL, main.custom_email_module);

      main.custom_email_module = "";
      ConfigurationManager.normalizeEmailModule();
      assertEquals("", main.custom_email_module);
    } finally {
      main.custom_email_module = originalEmailModule;
    }
  }
}
