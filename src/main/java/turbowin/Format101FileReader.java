package turbowin;

import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/** Reads the first line of a compressed Format 101 observation file. */
final class Format101FileReader {

  private Format101FileReader() {}

  static String readFirstLine(File file) {
    try (BufferedReader in = new BufferedReader(new FileReader(file))) {
      String line = in.readLine();
      if (line == null) {
        System.out.println(
            "--- error when retrieveing format 101 data empty file: " + file.getPath());
        return "";
      }
      return line;
    } catch (IOException | HeadlessException e) {
      System.out.println(
          "--- error when retrieving format 101 data error opening file: " + file.getPath());
      return "";
    }
  }
}
