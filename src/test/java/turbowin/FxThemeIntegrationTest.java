package turbowin;

import static org.junit.Assert.*;

import java.awt.Dimension;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.SwingUtilities;
import org.assertj.swing.edt.GuiActionRunner;
import org.junit.Test;

public class FxThemeIntegrationTest extends AbstractUiTest {
  @Test
  public void switchingThemesUpdatesOpenAndNewDialogsWithoutLosingInput() throws Exception {
    AtomicReference<String> original = new AtomicReference<>();
    SwingUtilities.invokeAndWait(() -> {
      original.set(main.theme_mode);
      main.theme_mode = main.THEME_NIMBUS_DAY;
      FxTheme.setTheme(main.theme_mode);
      IcingState.Is_code = "";
      IcingState.Rs_code = "";
      IcingState.EsEs_code = "";
      IceState.ci_code = "";
    });
    JDialog icing = null;
    JDialog ice = null;
    JDialog about = null;
    try {
      icing = GuiActionRunner.execute(IcingDialog::open);
      ice = GuiActionRunner.execute(IceDialog::open);
      Scene icingScene = CloudCoverFxTestSupport.awaitScene(icing);
      Scene iceScene = CloudCoverFxTestSupport.awaitScene(ice);
      AtomicReference<JDialog> aboutWindow = new AtomicReference<>();
      SwingUtilities.invokeAndWait(() ->
          aboutWindow.set(
              FxSwingDialog.show(
                  frame.target(),
                  "About theme test",
                  new Dimension(700, 760),
                  close ->
                      AboutView.create(
                          new AboutInfo("Test", List.of("detail")), close, link -> {}))));
      about = aboutWindow.get();
      Scene aboutScene = CloudCoverFxTestSupport.awaitScene(about);
      CloudCoverFxTestSupport.onFx(() -> {
        assertFalse(icingScene.getRoot().getStyleClass().contains("night-theme"));
        ((TextField) icingScene.lookup("#icing_thickness")).setText("12");
        return null;
      });
      SwingUtilities.invokeAndWait(() -> ((main) frame.target()).Themes_2_actionPerformed(null));
      CloudCoverFxTestSupport.onFx(() -> {
        for (Scene scene : List.of(icingScene, iceScene, aboutScene)) {
          scene.getRoot().applyCss();
          assertTrue(scene.getRoot().getStyleClass().contains("night-theme"));
          assertTrue(background(scene).getBrightness() < 0.3);
        }
        assertEquals("12", ((TextField) icingScene.lookup("#icing_thickness")).getText());
        assertTrue(
            ((Color) ((Label) icingScene.lookup(".heading")).getTextFill()).getBrightness() > 0.8);
        assertTrue(
            color(
                        ((TextField) icingScene.lookup("#icing_thickness"))
                            .getBackground()
                            .getFills()
                            .get(0)
                            .getFill())
                    .getBrightness()
                < 0.4);
        assertTrue(
            ((Color) ((Label) aboutScene.lookup(".title")).getTextFill()).getBrightness() > 0.8);
        return null;
      });
      JDialog newDialog = GuiActionRunner.execute(VisibilityDialog::open);
      Scene newScene = CloudCoverFxTestSupport.awaitScene(newDialog);
      CloudCoverFxTestSupport.onFx(() -> {
        assertTrue(newScene.getRoot().getStyleClass().contains("night-theme"));
        return null;
      });
      SwingUtilities.invokeAndWait(newDialog::dispose);
      CloudCoverFxTestSupport.onFx(() -> {
        @SuppressWarnings("unchecked")
        ComboBox<IceCatalog.Choice> box =
            (ComboBox<IceCatalog.Choice>) iceScene.lookup("#ice_concentration");
        box.show();
        return null;
      });
      CloudCoverFxTestSupport.onFx(() -> {
        @SuppressWarnings("unchecked")
        ComboBox<IceCatalog.Choice> box =
            (ComboBox<IceCatalog.Choice>) iceScene.lookup("#ice_concentration");
        Region popup = (Region) ((ComboBoxListViewSkin<?>) box.getSkin()).getPopupContent();
        popup.applyCss();
        popup.layout();
        assertTrue(
            popup.getBackground().getFills().stream()
                .anyMatch(
                    fill -> fill.getFill() instanceof Color color && color.getBrightness() < 0.4));
        Label option =
            popup.lookupAll(".ice-choice-label").stream()
                .map(Label.class::cast)
                .filter(label -> label.getText().equals("No ice"))
                .findFirst()
                .orElseThrow();
        assertTrue(((Color) option.getTextFill()).getBrightness() > 0.8);
        return null;
      });
      SwingUtilities.invokeAndWait(() -> ((main) frame.target()).Themes_1_actionPerformed(null));
      CloudCoverFxTestSupport.onFx(() -> {
        for (Scene scene : List.of(icingScene, iceScene, aboutScene)) {
          scene.getRoot().applyCss();
          assertFalse(scene.getRoot().getStyleClass().contains("night-theme"));
          assertTrue(background(scene).getBrightness() > 0.8);
        }
        assertEquals("12", ((TextField) icingScene.lookup("#icing_thickness")).getText());
        @SuppressWarnings("unchecked")
        ComboBox<IceCatalog.Choice> box =
            (ComboBox<IceCatalog.Choice>) iceScene.lookup("#ice_concentration");
        if (!box.isShowing()) box.show();
        Region popup = (Region) ((ComboBoxListViewSkin<?>) box.getSkin()).getPopupContent();
        popup.applyCss();
        popup.layout();
        Label option =
            popup.lookupAll(".ice-choice-label").stream()
                .map(Label.class::cast)
                .filter(label -> label.getText().equals("No ice"))
                .findFirst()
                .orElseThrow();
        assertTrue(
            "Day popup text: "
                + option.getTextFill()
                + ", selected: "
                + ((javafx.scene.control.ListCell<?>) option.getParent()).isSelected(),
            ((Color) option.getTextFill()).getBrightness() < 0.3);
        return null;
      });
    } finally {
      JDialog finalIcing = icing, finalIce = ice, finalAbout = about;
      SwingUtilities.invokeAndWait(() -> {
        if (finalIcing != null) finalIcing.dispose();
        if (finalIce != null) finalIce.dispose();
        if (finalAbout != null) finalAbout.dispose();
        if (main.THEME_NIMBUS_NIGHT.equals(original.get()))
          ((main) frame.target()).Themes_2_actionPerformed(null);
        else ((main) frame.target()).Themes_1_actionPerformed(null);
        main.theme_mode = original.get();
        FxTheme.setTheme(main.theme_mode);
      });
    }
  }

  @Test
  public void themeMenuContainsOnlyDayAndNight() throws Exception {
    SwingUtilities.invokeAndWait(() -> {
      JMenu menu = null;
      var bar = ((main) frame.target()).getJMenuBar();
      for (int i = 0; i < bar.getMenuCount(); i++) {
        if (bar.getMenu(i) != null && "Themes".equals(bar.getMenu(i).getText()))
          menu = bar.getMenu(i);
      }
      assertNotNull(menu);
      assertEquals(2, menu.getItemCount());
      assertEquals("Day", menu.getItem(0).getText());
      assertEquals("Night", menu.getItem(1).getText());
    });
  }

  private static Color background(Scene scene) {
    return color(((Region) scene.getRoot()).getBackground().getFills().get(0).getFill());
  }

  private static Color color(javafx.scene.paint.Paint paint) {
    if (paint instanceof javafx.scene.paint.LinearGradient gradient)
      return gradient.getStops().get(0).getColor();
    return (Color) paint;
  }
}
