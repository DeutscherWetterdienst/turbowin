package turbowin;

import static org.junit.Assert.assertEquals;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import org.junit.Test;

public class ObservationClipboardWriterTest {

  @Test
  public void writesTheObservationToTheProvidedClipboard() throws Exception {
    Clipboard clipboard = new Clipboard("test clipboard");

    ObservationClipboardWriter.write(clipboard, "observation-content");

    assertEquals(
        "observation-content",
        clipboard.getContents(null).getTransferData(DataFlavor.stringFlavor));
  }
}
