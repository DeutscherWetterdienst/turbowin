package turbowin;

import java.io.File;

/** Builds the legacy source, destination, and backup paths for log files. */
final class LogFilePathBuilder {

  private LogFilePathBuilder() {}

  static String captainSource(String logsDirectory) {
    return logsDirectory + File.separator + main.CAPTAIN_LOG;
  }

  static String captainDestination(String outputDirectory, String stationId) {
    return outputDirectory + File.separator + stationId + "_" + main.CAPTAIN_LOG;
  }

  static String captainBackup(String logsDirectory, String stationId, String date) {
    return logsDirectory + File.separator + stationId + "_CAPTAIN_BACKUP " + date + ".TXT";
  }

  static String immtSource(String logsDirectory) {
    return logsDirectory + File.separator + main.IMMT_LOG;
  }

  static String immtDestination(String outputDirectory, String stationId) {
    return outputDirectory + File.separator + stationId + "_" + main.IMMT_LOG;
  }

  static String immtBackup(String logsDirectory, String stationId, String date) {
    return logsDirectory + File.separator + stationId + "_IMMT_BACKUP " + date + ".TXT";
  }
}
