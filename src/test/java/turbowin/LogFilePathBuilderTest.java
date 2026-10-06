package turbowin;

import static org.junit.Assert.assertEquals;

import java.io.File;
import org.junit.Test;

public class LogFilePathBuilderTest {

  private static final String LOGS_DIRECTORY = "logs";
  private static final String OUTPUT_DIRECTORY = "output";
  private static final String STATION_ID = "ABCD";
  private static final String DATE = "January 02, 2024";

  @Test
  public void buildsCaptainPaths() {
    assertEquals(
        LOGS_DIRECTORY + File.separator + main.CAPTAIN_LOG,
        LogFilePathBuilder.captainSource(LOGS_DIRECTORY));
    assertEquals(
        OUTPUT_DIRECTORY + File.separator + STATION_ID + "_" + main.CAPTAIN_LOG,
        LogFilePathBuilder.captainDestination(OUTPUT_DIRECTORY, STATION_ID));
    assertEquals(
        LOGS_DIRECTORY + File.separator + STATION_ID + "_CAPTAIN_BACKUP " + DATE + ".TXT",
        LogFilePathBuilder.captainBackup(LOGS_DIRECTORY, STATION_ID, DATE));
  }

  @Test
  public void buildsImmtPaths() {
    assertEquals(
        LOGS_DIRECTORY + File.separator + main.IMMT_LOG,
        LogFilePathBuilder.immtSource(LOGS_DIRECTORY));
    assertEquals(
        OUTPUT_DIRECTORY + File.separator + STATION_ID + "_" + main.IMMT_LOG,
        LogFilePathBuilder.immtDestination(OUTPUT_DIRECTORY, STATION_ID));
    assertEquals(
        LOGS_DIRECTORY + File.separator + STATION_ID + "_IMMT_BACKUP " + DATE + ".TXT",
        LogFilePathBuilder.immtBackup(LOGS_DIRECTORY, STATION_ID, DATE));
  }
}
