package turbowin;

import static org.junit.Assert.*;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class BarometerComparisonModelTest {
  @Rule public TemporaryFolder folder = new TemporaryFolder();

  @Test
  public void computesAndExportsTheComparison() throws Exception {
    var input =
        new BarometerComparisonModel.Input(
            "Test ship", "20", "15", "12 UTC", "Harbour", "1000", "1014", "12.8", "20");
    var result = BarometerComparisonModel.calculate(input, false);
    assertEquals("22.2", result.height());
    assertEquals("1002.6", result.seaLevel());
    assertEquals("-11.4", result.error());
    assertEquals("11.4", result.correction());
    Path file = folder.getRoot().toPath().resolve("comparison.txt");
    BarometerComparisonExportWorkflow.write(file, result.exportData());
    String exported = Files.readString(file);
    assertTrue(exported.contains("ship name: Test ship"));
    assertTrue(exported.contains("ship barometer air pressure converted to sea level: 1002.6 hPa"));
    assertTrue(exported.contains("instrument correction ship barometer: 11.4 hPa"));
  }

  @Test
  public void rejectsSeaLevelReadingsAndInvalidMeasurementsInOrder() {
    var missing = new BarometerComparisonModel.Input("", "", "", "", "", "", "", "", "");
    assertTrue(
        assertThrows(
                IllegalArgumentException.class,
                () -> BarometerComparisonModel.calculate(missing, true))
            .getMessage()
            .contains("bridge level"));
    assertTrue(
        assertThrows(
                IllegalArgumentException.class,
                () -> BarometerComparisonModel.calculate(missing, false))
            .getMessage()
            .contains("height above SLL"));
    var badPressure =
        new BarometerComparisonModel.Input("", "20", "15", "", "", "799", "1000", "12", "20");
    assertEquals(
        "ship barometer reading out of physical range",
        assertThrows(
                IllegalArgumentException.class,
                () -> BarometerComparisonModel.calculate(badPressure, false))
            .getMessage());
    var belowWater =
        new BarometerComparisonModel.Input("", "2", "3", "", "", "1000", "1000", "12", "20");
    assertTrue(
        assertThrows(
                IllegalArgumentException.class,
                () -> BarometerComparisonModel.calculate(belowWater, false))
            .getMessage()
            .contains("waterline negative"));
  }
}
