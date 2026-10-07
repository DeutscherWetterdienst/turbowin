package turbowin;

import java.io.BufferedWriter;
import java.io.FileWriter;

/** Owns the yearly observer output and backup writers. */
final class ObserverStatisticsFileSet {

  private final String[] outputPaths;
  private final String[] backupPaths;
  private final BufferedWriter[] outputFiles;
  private final BufferedWriter[] backupFiles;

  private ObserverStatisticsFileSet(String[] outputPaths, String[] backupPaths) {
    this.outputPaths = outputPaths;
    this.backupPaths = backupPaths;
    this.outputFiles = new BufferedWriter[outputPaths.length];
    this.backupFiles = new BufferedWriter[backupPaths.length];
  }

  static ObserverStatisticsFileSet open(String[] outputPaths, String[] backupPaths) {
    ObserverStatisticsFileSet files = new ObserverStatisticsFileSet(outputPaths, backupPaths);
    for (int index = 0; index < outputPaths.length; index++) {
      if (!outputPaths[index].equals("")) {
        try {
          files.outputFiles[index] = new BufferedWriter(new FileWriter(outputPaths[index]));
          files.backupFiles[index] = new BufferedWriter(new FileWriter(backupPaths[index]));
        } catch (Exception ignored) {
        }
      }
    }
    return files;
  }

  BufferedWriter[] outputFiles() {
    return outputFiles;
  }

  BufferedWriter[] backupFiles() {
    return backupFiles;
  }

  void close() {
    for (int index = 0; index < outputPaths.length; index++) {
      if (!outputPaths[index].equals("")) {
        try {
          if (outputFiles[index] != null) {
            outputFiles[index].close();
          }
          if (backupFiles[index] != null) {
            backupFiles[index].close();
          }
        } catch (Exception ignored) {
        }
      }
    }
  }
}
