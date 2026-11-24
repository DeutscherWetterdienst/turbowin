package turbowin;

import static org.junit.Assert.*;

import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import org.assertj.swing.finder.WindowFinder;
import org.junit.Test;
import turbowin.matchers.DialogTitleMatcher;

public class LogFolderJavaFxIntegrationTest extends AbstractUiTest {
  @Test
  public void infoMenuShowsConfiguredPathWithoutChangingIt() throws Exception {
    String original = main.logs_dir;
    String custom = startupDirectory.newFolder("custom-logs").getAbsolutePath();
    SwingUtilities.invokeAndWait(() -> main.logs_dir = custom);
    try {
      frame.menuItemWithPath("Info").click();
      frame.menuItemWithPath("Info|Log folder...").click();
      JDialog dialog =
          (JDialog)
              WindowFinder.findDialog(new DialogTitleMatcher("Log folder")).using(robot()).target();
      Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
      CloudCoverFxTestSupport.onFx(() -> {
        TextField path = (TextField) scene.lookup("#log_folder_path");
        assertEquals(custom, path.getText());
        assertFalse(path.isEditable());
        CloudCoverFxTestSupport.click(scene, "Close");
        return null;
      });
      SwingUtilities.invokeAndWait(() -> {
        assertFalse(dialog.isDisplayable());
        assertEquals(custom, main.logs_dir);
      });
    } finally {
      SwingUtilities.invokeAndWait(() -> main.logs_dir = original);
    }
  }
}
