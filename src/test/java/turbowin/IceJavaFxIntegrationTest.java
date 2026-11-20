package turbowin;

import static org.junit.Assert.*;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.DialogFixture;
import org.junit.Test;
import turbowin.matchers.DialogTitleMatcher;

public class IceJavaFxIntegrationTest extends AbstractUiTest {
  private static final List<String> IDS =
      List.of("ice_concentration", "ice_development", "ice_land", "ice_bearing", "ice_situation");

  @Test
  public void allOptionIndicesSaveTheCorrectCodesAndRestore() throws Exception {
    reset();
    List<String> codes = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "u", "");
    for (int index = 0; index < codes.size(); index++) {
      int selectedIndex = index;
      String code = codes.get(index);
      JDialog dialog = GuiActionRunner.execute(IceDialog::open);
      Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
      CloudCoverFxTestSupport.onFx(() -> {
        for (String id : IDS) {
          ComboBox<IceCatalog.Choice> box = combo(scene, id);
          assertEquals(12, box.getItems().size());
          box.getSelectionModel().select(selectedIndex);
        }
        CloudCoverFxTestSupport.click(scene, "OK");
        return null;
      });
      SwingUtilities.invokeAndWait(() -> {
        assertFalse(dialog.isDisplayable());
        assertEquals(List.of(code, code, code, code, code), state());
        assertEquals(
            code.isEmpty() || code.equals("u") ? "" : "present", main.jTextField19.getText());
      });
      JDialog reopened = GuiActionRunner.execute(IceDialog::open);
      Scene reopenedScene = CloudCoverFxTestSupport.awaitScene(reopened);
      CloudCoverFxTestSupport.onFx(() -> {
        for (String id : IDS) {
          IceCatalog.Choice selected = combo(reopenedScene, id).getValue();
          if (code.isEmpty()) assertNull(selected);
          else assertEquals(code, selected.code());
        }
        CloudCoverFxTestSupport.click(reopenedScene, "Cancel");
        return null;
      });
      SwingUtilities.invokeAndWait(() -> assertFalse(reopened.isDisplayable()));
    }
  }

  @Test
  public void fieldsAreIndependentAndCancelDoesNotSave() throws Exception {
    reset();
    JDialog dialog = GuiActionRunner.execute(IceDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    CloudCoverFxTestSupport.onFx(() -> {
      for (int i = 0; i < IDS.size(); i++)
        combo(scene, IDS.get(i)).getSelectionModel().select(i + 1);
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {
      assertFalse(dialog.isDisplayable());
      assertEquals(List.of("1", "2", "3", "4", "5"), state());
    });
    JDialog cancel = GuiActionRunner.execute(IceDialog::open);
    Scene cancelScene = CloudCoverFxTestSupport.awaitScene(cancel);
    CloudCoverFxTestSupport.onFx(() -> {
      combo(cancelScene, "ice_concentration").getSelectionModel().select(9);
      CloudCoverFxTestSupport.click(cancelScene, "Cancel");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {
      assertFalse(cancel.isDisplayable());
      assertEquals(List.of("1", "2", "3", "4", "5"), state());
    });
  }

  @Test
  public void unknownCodesStayUnselectedAndLongDescriptionsRemainReadable() throws Exception {
    reset();
    SwingUtilities.invokeAndWait(() -> {
      IceState.ci_code = "07";
      IceState.Si_code = "unknown";
      IceState.bi_code = "u";
      IceState.Di_code = "/";
    });
    JDialog dialog = GuiActionRunner.execute(IceDialog::open);
    Scene scene = CloudCoverFxTestSupport.awaitScene(dialog);
    CloudCoverFxTestSupport.onFx(() -> {
      assertEquals("7", combo(scene, "ice_concentration").getValue().code());
      assertNull(combo(scene, "ice_development").getValue());
      assertEquals("u", combo(scene, "ice_land").getValue().code());
      assertNull(combo(scene, "ice_bearing").getValue());
      Label description = (Label) scene.lookup("#ice_concentration_description");
      assertTrue(description.isVisible());
      assertEquals(IceCatalog.CONCENTRATION.get(7).description(), description.getText());
      assertTrue(description.isWrapText());
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {
      assertFalse(dialog.isDisplayable());
      assertEquals(List.of("7", "", "u", "", ""), state());
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
                "Ice test",
                new Dimension(800, 650),
                close ->
                    IceView.create(
                        new IceView.State(new IceView.Input("", "", "", "", ""), true),
                        input -> {},
                        close,
                        () -> opened.add("ice.pdf")))));
    Scene scene = CloudCoverFxTestSupport.awaitScene(window.get());
    CloudCoverFxTestSupport.onFx(() -> {
      HBox footer = (HBox) ((VBox) scene.getRoot()).getChildren().get(1);
      assertEquals(
          List.of("OK", "Cancel", "Help"),
          footer.getChildren().stream().map(Button.class::cast).map(Button::getText).toList());
      CloudCoverFxTestSupport.click(scene, "Help");
      assertEquals(List.of("ice.pdf"), opened);
      return null;
    });
    SwingUtilities.invokeAndWait(window.get()::dispose);
    SwingUtilities.invokeAndWait(
        () -> ((main) frame.target()).Input_Ice_menu_actionPerformed(null));
    DialogFixture dialog = WindowFinder.findDialog(new DialogTitleMatcher("Ice")).using(robot());
    CloudCoverFxTestSupport.awaitScene((JDialog) dialog.target());
    SwingUtilities.invokeAndWait(dialog.target()::dispose);
  }

  private static void reset() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      IceState.ci_code = "";
      IceState.Si_code = "";
      IceState.bi_code = "";
      IceState.Di_code = "";
      IceState.zi_code = "";
    });
  }

  private static List<String> state() {
    return List.of(
        IceState.ci_code, IceState.Si_code, IceState.bi_code, IceState.Di_code, IceState.zi_code);
  }

  @SuppressWarnings("unchecked")
  private static ComboBox<IceCatalog.Choice> combo(Scene scene, String id) {
    return (ComboBox<IceCatalog.Choice>) scene.lookup("#" + id);
  }
}
