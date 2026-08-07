package turbowin;

import java.awt.HeadlessException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/** Writes a generated observation to a user-selected file. */
final class ObservationFileWriter {

  private ObservationFileWriter() {}

  static boolean write(File file, String observation) {
    try (BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
      out.write(observation);
      return true;
    } catch (IOException | HeadlessException e) {
      return false;
    }
  }
}
