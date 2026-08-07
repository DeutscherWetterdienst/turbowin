package turbowin;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/** Copies generated observations to a clipboard. */
final class ObservationClipboardWriter {

  private ObservationClipboardWriter() {}

  static void write(Clipboard clipboard, String observation) {
    clipboard.setContents(new StringSelection(observation), null);
  }
}
