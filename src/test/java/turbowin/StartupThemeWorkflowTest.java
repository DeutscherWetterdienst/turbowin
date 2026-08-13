package turbowin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StartupThemeWorkflowTest {

  @Test
  public void identifiesTransparentTheme() {
    assertTrue(StartupThemeWorkflow.usesTransparentTheme(main.THEME_TRANSPARENT));
    assertFalse(StartupThemeWorkflow.usesTransparentTheme(""));
  }

  @Test
  public void identifiesLinuxTransparentFontSetup() {
    assertTrue(StartupThemeWorkflow.usesLinuxFont(main.THEME_TRANSPARENT, "LINUX"));
    assertFalse(StartupThemeWorkflow.usesLinuxFont(main.THEME_TRANSPARENT, "WINDOWS"));
    assertFalse(StartupThemeWorkflow.usesLinuxFont("", "LINUX"));
  }

  @Test
  public void identifiesMissingUbuntuFontOnLinux() {
    assertTrue(StartupThemeWorkflow.requiresUbuntuFontWarning("LINUX", "Dialog"));
    assertFalse(StartupThemeWorkflow.requiresUbuntuFontWarning("LINUX", "Ubuntu"));
    assertFalse(StartupThemeWorkflow.requiresUbuntuFontWarning("WINDOWS", "Dialog"));
  }
}
