package turbowin;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ObserverStatisticsFileSetTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void opensAndClosesConfiguredOutputPairs() {
    File output = new File(temporaryFolder.getRoot(), "observer.log");
    File backup = new File(temporaryFolder.getRoot(), "observer-backup.log");

    ObserverStatisticsFileSet files =
        ObserverStatisticsFileSet.open(
            new String[] {output.getPath()}, new String[] {backup.getPath()});

    assertNotNull(files.outputFiles()[0]);
    assertNotNull(files.backupFiles()[0]);
    files.close();
  }

  @Test
  public void leavesEmptyAndUnopenablePairsUnavailable() {
    File parentFile = new File(temporaryFolder.getRoot(), "parent");
    ObserverStatisticsFileSet files =
        ObserverStatisticsFileSet.open(
            new String[] {"", new File(parentFile, "observer.log").getPath()},
            new String[] {"", new File(parentFile, "backup.log").getPath()});

    assertNull(files.outputFiles()[0]);
    assertNull(files.backupFiles()[0]);
    assertNull(files.outputFiles()[1]);
    assertNull(files.backupFiles()[1]);
    files.close();
  }
}
