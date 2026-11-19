package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import org.junit.Test;

public class AboutDialogFxTest {
  @Test
  public void opensClosesAndReopensWithResourcesAndWorkingControls() throws Exception {
    assumeFalse(
        "JavaFX integration requires a graphical display", GraphicsEnvironment.isHeadless());
    for (int attempt = 0; attempt < 2; attempt++) {
      CountDownLatch ready = new CountDownLatch(1);
      CountDownLatch closed = new CountDownLatch(1);
      AtomicReference<Scene> scene = new AtomicReference<>();
      AtomicReference<JDialog> dialog = new AtomicReference<>();
      AtomicReference<String> link = new AtomicReference<>();
      SwingUtilities.invokeAndWait(() ->
          dialog.set(
              FxSwingDialog.show(
                  null,
                  "About test",
                  new Dimension(700, 760),
                  close -> {
                    Scene result =
                        AboutView.create(
                            new AboutInfo("Test version", List.of("operating system: test"), ""),
                            () -> {
                              close.run();
                              closed.countDown();
                            },
                            link::set);
                    scene.set(result);
                    Platform.runLater(ready::countDown);
                    return result;
                  })));
      assertTrue("JavaFX scene was not created", ready.await(15, TimeUnit.SECONDS));
      CountDownLatch checked = new CountDownLatch(1);
      AtomicReference<Throwable> failure = new AtomicReference<>();
      Platform.runLater(() -> {
        try {
          Scene view = scene.get();
          assertNotNull(view);
          assertFalse(view.getStylesheets().isEmpty());
          view.getRoot().applyCss();
          assertTrue(view.getRoot().lookupAll(".image-view").size() >= 2);
          Hyperlink vos =
              view.getRoot().lookupAll(".hyperlink").stream()
                  .map(Hyperlink.class::cast)
                  .filter(item -> item.getText().contains("Voluntary"))
                  .findFirst()
                  .orElseThrow();
          vos.fire();
          assertEquals(AboutDialog.sotLink(), link.get());
          ((Button) view.getRoot().lookup(".button")).fire();
        } catch (Throwable error) {
          failure.set(error);
        } finally {
          checked.countDown();
        }
      });
      assertTrue(checked.await(15, TimeUnit.SECONDS));
      if (failure.get() != null) {
        throw new AssertionError(failure.get());
      }
      assertTrue("Close action did not run", closed.await(15, TimeUnit.SECONDS));
      SwingUtilities.invokeAndWait(() -> assertFalse(dialog.get().isDisplayable()));
    }
  }

  @Test
  public void closingDuringSceneCreationDoesNotAttachTheScene() throws Exception {
    assumeFalse(
        "JavaFX integration requires a graphical display", GraphicsEnvironment.isHeadless());
    CountDownLatch creating = new CountDownLatch(1);
    CountDownLatch release = new CountDownLatch(1);
    AtomicReference<JDialog> dialog = new AtomicReference<>();
    AtomicReference<JFXPanel> panel = new AtomicReference<>();
    SwingUtilities.invokeAndWait(() -> {
      JDialog window =
          FxSwingDialog.show(
              null,
              "About test",
              new Dimension(700, 760),
              close -> {
                creating.countDown();
                try {
                  if (!release.await(15, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("Timed out waiting to close the dialog");
                  }
                } catch (InterruptedException e) {
                  Thread.currentThread().interrupt();
                  throw new IllegalStateException(e);
                }
                return AboutView.create(new AboutInfo("test", List.of(), ""), close, link -> {});
              });
      dialog.set(window);
      panel.set((JFXPanel) window.getContentPane().getComponent(0));
    });
    try {
      assertTrue(creating.await(15, TimeUnit.SECONDS));
      SwingUtilities.invokeAndWait(dialog.get()::dispose);
    } finally {
      release.countDown();
    }
    CountDownLatch checked = new CountDownLatch(1);
    AtomicReference<Scene> attached = new AtomicReference<>();
    Platform.runLater(() -> {
      attached.set(panel.get().getScene());
      checked.countDown();
    });
    assertTrue(checked.await(15, TimeUnit.SECONDS));
    assertEquals(null, attached.get());
  }
}
