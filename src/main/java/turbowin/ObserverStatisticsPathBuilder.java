package turbowin;

import java.io.File;

/** Builds yearly observer output and backup filenames. */
final class ObserverStatisticsPathBuilder {

  private ObserverStatisticsPathBuilder() {}

  static String outputPath(String observerFile, String year) {
    int extensionPosition = observerFile.indexOf(".log");
    return observerFile.substring(0, extensionPosition) + "_" + year + ".log";
  }

  static String backupPath(String logsDirectory, String stationId, String year, String date) {
    return logsDirectory
        + File.separator
        + stationId
        + "_OBSERVER_"
        + year
        + "_BACKUP "
        + date
        + ".TXT";
  }
}
