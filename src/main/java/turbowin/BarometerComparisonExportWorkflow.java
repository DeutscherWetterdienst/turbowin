package turbowin;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

/** Writes a completed barometer comparison in the existing text format. */
final class BarometerComparisonExportWorkflow {
  private BarometerComparisonExportWorkflow() {}

  static void write(Path file, ExportData data) throws IOException {
    try (BufferedWriter out = Files.newBufferedWriter(file, Charset.defaultCharset())) {
      line(out, "BAROMETER COMPARISON");
      line(out, "");
      line(out, "ship name: " + data.shipName());
      line(
          out,
          "height ship barometer above Summer Load Line: " + data.barometerAboveSll() + " metres");
      line(
          out, "distance of bottom of the keel to Summer Load Line: " + data.keelSll() + " metres");
      line(out, "");
      line(out, "date and time: " + data.dateTime() + " UTC");
      line(out, "position or port: " + data.position());
      line(
          out,
          "ship barometer reading, indicating air pressure at bridge level: "
              + data.shipBarometerReading()
              + " hPa");
      line(
          out,
          "reference barometer reading, indicating air pressure at sea level: "
              + data.referenceBarometerReading()
              + " hPa");
      line(out, "actual ship draft: " + data.draft() + " metres");
      line(out, "outdoor air temperature: " + data.airTemp() + " °C");
      line(out, "");
      line(
          out,
          "actual height of the ship barometer above the waterline: "
              + data.shipBarometerAboveWl()
              + " metres");
      line(
          out,
          "ship barometer air pressure converted to sea level: "
              + data.shipBarometerSeaLevel()
              + " hPa");
      line(out, "instrument error ship barometer: " + data.instrumentErrorShipBarometer() + " hPa");
      line(
          out,
          "instrument correction ship barometer: "
              + data.instrumentCorrectionShipBarometer()
              + " hPa");
    }
  }

  private static void line(BufferedWriter out, String text) throws IOException {
    out.write(text);
    out.newLine();
  }

  record ExportData(
      String shipName,
      String barometerAboveSll,
      String keelSll,
      String dateTime,
      String position,
      String shipBarometerReading,
      String referenceBarometerReading,
      String draft,
      String airTemp,
      String shipBarometerAboveWl,
      String shipBarometerSeaLevel,
      String instrumentErrorShipBarometer,
      String instrumentCorrectionShipBarometer) {}
}
