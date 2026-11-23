package turbowin;

import static org.junit.Assert.*;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.DialogFixture;
import org.junit.Test;
import turbowin.matchers.DialogTitleMatcher;

public class IcingJavaFxIntegrationTest extends AbstractUiTest {
  @Test
  public void causesAndRatesSaveCodesAndRestoreOnReopening() throws Exception {
    reset();
    SwingUtilities.invokeAndWait(() -> main.obs_format = main.FORMAT_101);
    List<String> causes = List.of("1", "2", "3", "4", "5", "6", "14", "");
    List<String> rates = List.of("0", "1", "2", "3", "4", "");
    for (int i = 0; i < causes.size(); i++) {
      String cause = causes.get(i), rate = rates.get(i % rates.size());
      JDialog dialog = GuiActionRunner.execute(IcingDialog::open);
      Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
      CloudCoverFxTestSupport.onFx(() -> {
        option(scene, "icing_cause", cause).fire();
        option(scene, "icing_rate", rate).fire();
        field(scene).setText("4");
        CloudCoverFxTestSupport.click(scene, "OK");
        return null;
      });
      SwingUtilities.invokeAndWait(() -> {
        assertFalse(dialog.isDisplayable());
        assertEquals(cause, IcingState.Is_code);
        assertEquals(rate, IcingState.Rs_code);
        assertEquals("04", IcingState.EsEs_code);
        assertEquals("present", main.jTextField21.getText());
      });
      JDialog reopened = GuiActionRunner.execute(IcingDialog::open);
      Scene reopenedScene = CloudCoverFxTestSupport.awaitScene(reopened);
      CloudCoverFxTestSupport.onFx(() -> {
        assertTrue(option(reopenedScene, "icing_cause", cause).isSelected());
        assertTrue(option(reopenedScene, "icing_rate", rate).isSelected());
        assertEquals("04", field(reopenedScene).getText());
        CloudCoverFxTestSupport.click(reopenedScene, "Cancel");
        return null;
      });
      SwingUtilities.invokeAndWait(() -> assertFalse(reopened.isDisplayable()));
    }
  }

  @Test
  public void extendedCausesFollowFormatAndAwsMode() throws Exception {
    reset();
    SwingUtilities.invokeAndWait(() -> {
      main.obs_format = main.FORMAT_FM13;
      main.RS232_connection_mode = 0;
      IcingState.Is_code = "6";
      IcingState.EsEs_code = "02";
    });
    try {
      JDialog restricted = GuiActionRunner.execute(IcingDialog::open);
      Scene restrictedScene = CloudCoverFxTestSupport.awaitScene(restricted);
      CloudCoverFxTestSupport.onFx(() -> {
        assertFalse(option(restrictedScene, "icing_cause", "6").isVisible());
        assertFalse(option(restrictedScene, "icing_cause", "14").isVisible());
        assertTrue(option(restrictedScene, "icing_cause", "6").isSelected());
        CloudCoverFxTestSupport.click(restrictedScene, "OK");
        return null;
      });
      SwingUtilities.invokeAndWait(() -> {
        assertFalse(restricted.isDisplayable());
        assertEquals("6", IcingState.Is_code);
        main.RS232_connection_mode = 3;
      });
      JDialog aws = GuiActionRunner.execute(IcingDialog::open);
      Scene awsScene = CloudCoverFxTestSupport.awaitScene(aws);
      CloudCoverFxTestSupport.onFx(() -> {
        assertTrue(option(awsScene, "icing_cause", "6").isVisible());
        assertTrue(option(awsScene, "icing_cause", "14").isVisible());
        CloudCoverFxTestSupport.click(awsScene, "Cancel");
        return null;
      });
      SwingUtilities.invokeAndWait(() -> {
        assertFalse(aws.isDisplayable());
        main.RS232_connection_mode = 0;
        main.obs_format = main.FORMAT_101;
      });
      JDialog format101 = GuiActionRunner.execute(IcingDialog::open);
      Scene formatScene = CloudCoverFxTestSupport.awaitScene(format101);
      CloudCoverFxTestSupport.onFx(() -> {
        assertTrue(option(formatScene, "icing_cause", "6").isVisible());
        assertTrue(option(formatScene, "icing_cause", "14").isVisible());
        CloudCoverFxTestSupport.click(formatScene, "Cancel");
        return null;
      });
      SwingUtilities.invokeAndWait(() -> assertFalse(format101.isDisplayable()));
    } finally {
      SwingUtilities.invokeAndWait(() -> main.RS232_connection_mode = 0);
    }
  }

  @Test
  public void invalidThicknessKeepsTheDialogOpenAndCanBeCorrected() throws Exception {
    reset();
    JDialog dialog = GuiActionRunner.execute(IcingDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    for (String invalid : List.of("001", "1.5", "", "100")) {
      CloudCoverFxTestSupport.onFx(() -> {
        option(scene, "icing_cause", "1").fire();
        option(scene, "icing_rate", "2").fire();
        field(scene).setText(invalid);
        CloudCoverFxTestSupport.click(scene, "OK");
        return null;
      });
      DialogFixture warning =
          WindowFinder.findDialog(new DialogTitleMatcher(main.APPLICATION_NAME + " error"))
              .using(robot());
      SwingUtilities.invokeAndWait(() -> {
        assertEquals("", IcingState.Is_code);
        assertEquals("", IcingState.EsEs_code);
        assertEquals("", IcingState.Rs_code);
      });
      warning.button(JButtonMatcher.withText("OK")).click();
      SwingUtilities.invokeAndWait(() -> {
        assertTrue(dialog.isDisplayable());
        assertEquals("", IcingState.Is_code);
        assertEquals("", IcingState.EsEs_code);
      });
    }
    CloudCoverFxTestSupport.onFx(() -> {
      field(scene).setText("99");
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {
      assertFalse(dialog.isDisplayable());
      assertEquals("99", IcingState.EsEs_code);
      assertEquals("1", IcingState.Is_code);
    });
  }

  @Test
  public void blankInputClearsStateAndCancelPreservesIt() throws Exception {
    reset();
    SwingUtilities.invokeAndWait(() -> {
      IcingState.Is_code = "1";
      IcingState.EsEs_code = "05";
      IcingState.Rs_code = "2";
    });
    JDialog cancel = GuiActionRunner.execute(IcingDialog::open);
    Scene cancelScene = CloudCoverFxTestSupport.awaitScene(cancel);
    CloudCoverFxTestSupport.onFx(() -> {
      field(cancelScene).setText("10");
      CloudCoverFxTestSupport.click(cancelScene, "Cancel");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {
      assertFalse(cancel.isDisplayable());
      assertEquals("05", IcingState.EsEs_code);
    });
    JDialog clear = GuiActionRunner.execute(IcingDialog::open);
    Scene clearScene = CloudCoverFxTestSupport.awaitScene(clear);
    CloudCoverFxTestSupport.onFx(() -> {
      option(clearScene, "icing_cause", "").fire();
      option(clearScene, "icing_rate", "").fire();
      field(clearScene).clear();
      CloudCoverFxTestSupport.click(clearScene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {
      assertFalse(clear.isDisplayable());
      assertEquals("", IcingState.Is_code);
      assertEquals("", IcingState.EsEs_code);
      assertEquals("", IcingState.Rs_code);
      assertEquals("", main.jTextField21.getText());
    });
  }

  @Test
  public void footerHelpAndMenuEntryWork() throws Exception {
    reset();
    AtomicReference<JDialog> window = new AtomicReference<>();
    List<String> opened = new ArrayList<>();
    SwingUtilities.invokeAndWait(() ->
        window.set(
            FxSwingDialog.show(
                frame.target(),
                "Icing test",
                new Dimension(800, 600),
                close ->
                    IcingView.create(
                        new IcingView.State("", "", "", false, true),
                        input -> {},
                        close,
                        () -> opened.add("icing.pdf")))));
    Scene scene = CloudCoverFxTestSupport.awaitScene(window.get());
    CloudCoverFxTestSupport.onFx(() -> {
      HBox footer = (HBox) ((VBox) scene.getRoot()).getChildren().get(1);
      assertEquals(
          List.of("OK", "Cancel", "Help"),
          footer.getChildren().stream().map(Button.class::cast).map(Button::getText).toList());
      CloudCoverFxTestSupport.click(scene, "Help");
      assertEquals(List.of("icing.pdf"), opened);
      return null;
    });
    SwingUtilities.invokeAndWait(window.get()::dispose);
    SwingUtilities.invokeAndWait(
        () -> ((main) frame.target()).Input_Icing_menu_actionPerformed(null));
    DialogFixture dialog =
        WindowFinder.findDialog(new DialogTitleMatcher("Ice accretion (icing)")).using(robot());
    CloudCoverFxTestSupport.awaitScene((JDialog) dialog.target());
    SwingUtilities.invokeAndWait(dialog.target()::dispose);
  }

  private static void reset() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      main.RS232_connection_mode = 0;
      main.obs_format = main.FORMAT_FM13;
      IcingState.Is_code = "";
      IcingState.EsEs_code = "";
      IcingState.Rs_code = "";
    });
  }

  private static TextField field(Scene scene) {
    return (TextField) scene.lookup("#icing_thickness");
  }

  private static RadioButton option(Scene scene, String group, String code) {
    return scene.lookup("#" + group).lookupAll(".radio-button").stream()
        .map(RadioButton.class::cast)
        .filter(button -> code.equals(button.getUserData()))
        .findFirst()
        .orElseThrow();
  }
}
