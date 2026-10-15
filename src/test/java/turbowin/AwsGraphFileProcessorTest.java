package turbowin;

import static org.junit.Assert.assertArrayEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import org.junit.Test;

public class AwsGraphFileProcessorTest {

  @Test
  public void processesValidRecordsAndIgnoresInvalidRecords() throws IOException {
    File file = Files.createTempFile("turbowin-aws-graph", ".txt").toFile();
    try {
      String validRecord =
          "$PEUMB,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,"
              + "202601010005";
      Files.write(file.toPath(), List.of("invalid", validRecord));
      String[] primary = {"", "", "", ""};
      String[] secondary = {"", "", "", ""};
      String[] timestamps = {"", "", "", ""};
      AwsGraphCollector collector = new AwsGraphCollector(primary, secondary, timestamps);

      AwsGraphFileProcessor.process(file, "2026010100", 5, -1, collector, 0);

      assertArrayEquals(new String[] {"", "5", "", ""}, primary);
      assertArrayEquals(new String[] {"", "", "", ""}, secondary);
      assertArrayEquals(new String[] {"", "202601010005", "", ""}, timestamps);
    } finally {
      file.delete();
    }
  }

  @Test
  public void processesMultipleGraphSeriesFromOneFile() throws IOException {
    File file = Files.createTempFile("turbowin-aws-graph", ".txt").toFile();
    try {
      String validRecord =
          "$PEUMB,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,"
              + "202601010005";
      Files.write(file.toPath(), List.of(validRecord));
      String[] pressure = {"", "", "", ""};
      String[] direction = {"", "", "", ""};
      String[] temperature = {"", "", "", ""};
      String[] windSpeed = {"", "", "", ""};
      AwsGraphCollector[] collectors = {
        new AwsGraphCollector(pressure, new String[4], new String[4]),
        new AwsGraphCollector(direction, new String[4], new String[4]),
        new AwsGraphCollector(temperature, new String[4], new String[4]),
        new AwsGraphCollector(windSpeed, new String[4], new String[4])
      };

      AwsGraphFileProcessor.processMultiple(
          file, "2026010100", new int[] {5, 6, 7, 8}, collectors, 0);

      assertArrayEquals(new String[] {"", "5", "", ""}, pressure);
      assertArrayEquals(new String[] {"", "6", "", ""}, direction);
      assertArrayEquals(new String[] {"", "7", "", ""}, temperature);
      assertArrayEquals(new String[] {"", "8", "", ""}, windSpeed);
    } finally {
      file.delete();
    }
  }
}
