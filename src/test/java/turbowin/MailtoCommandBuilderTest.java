package turbowin;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

public class MailtoCommandBuilderTest {

  private static final String MAILTO_PAYLOAD = "recipient@example.com?subject=Subject";

  @Test
  public void buildsTheWindowsCommand() {
    assertArrayEquals(
        new String[] {"cmd", "/c", "start", "mailto:", MAILTO_PAYLOAD},
        MailtoCommandBuilder.forWindows(MAILTO_PAYLOAD));
  }

  @Test
  public void buildsTheMacOsCommand() {
    assertArrayEquals(
        new String[] {"open", "mailto:", MAILTO_PAYLOAD},
        MailtoCommandBuilder.forMacOs(MAILTO_PAYLOAD));
  }

  @Test
  public void buildsTheLinuxCommands() {
    assertArrayEquals(
        new String[] {"xdg-open", "mailto:", MAILTO_PAYLOAD},
        MailtoCommandBuilder.forLinux(MAILTO_PAYLOAD));
    assertArrayEquals(
        new String[] {"kde-open", "mailto:", MAILTO_PAYLOAD},
        MailtoCommandBuilder.forKde(MAILTO_PAYLOAD));
  }
}
