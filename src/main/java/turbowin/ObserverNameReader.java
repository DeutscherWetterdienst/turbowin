package turbowin;

import java.io.BufferedReader;
import java.io.IOException;

/** Reads observer records into the legacy bounded observer array. */
final class ObserverNameReader {

  private ObserverNameReader() {}

  static void read(BufferedReader input, String[] observerNames) throws IOException {
    int position = 0;
    String record;

    while ((record = input.readLine()) != null) {
      if (position < main.MAX_AANTAL_WAARNEMERS) {
        observerNames[position++] = record;
      }
    }
  }
}
