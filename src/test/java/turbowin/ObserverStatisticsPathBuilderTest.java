package turbowin;

import static org.junit.Assert.assertEquals;

import java.io.File;
import org.junit.Test;

public class ObserverStatisticsPathBuilderTest {

  @Test
  public void buildsYearlyObserverOutputPaths() {
    assertEquals(
        "output" + File.separator + "station_observer_2024.log",
        ObserverStatisticsPathBuilder.outputPath(
            "output" + File.separator + "station_observer.log", "2024"));
  }

  @Test
  public void buildsObserverBackupPaths() {
    assertEquals(
        "logs" + File.separator + "ABCD_OBSERVER_2024_BACKUP January 02, 2024.TXT",
        ObserverStatisticsPathBuilder.backupPath("logs", "ABCD", "2024", "January 02, 2024"));
  }

  @Test(expected = StringIndexOutOfBoundsException.class)
  public void preservesTheLegacyFailureWhenTheObserverPathHasNoLogExtension() {
    ObserverStatisticsPathBuilder.outputPath("observer", "2024");
  }
}
