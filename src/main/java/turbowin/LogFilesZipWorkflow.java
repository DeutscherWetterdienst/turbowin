package turbowin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;
import javax.swing.JOptionPane;

/** Creates the legacy ZIP archive used for emailed log files. */
final class LogFilesZipWorkflow {

  private LogFilesZipWorkflow() {}

  static void zip(String tempLogsDirectory, String shipName, String logsZip) {
    File fileLogsDirectory = new File(tempLogsDirectory);
    String[] filenames = fileLogsDirectory.list();

    for (int i = 0; i < filenames.length; i++) {
      filenames[i] = tempLogsDirectory + File.separator + filenames[i];
    }

    byte[] buffer = new byte[1024];

    try {
      String outputFilename = tempLogsDirectory + File.separator + shipName + " " + logsZip;
      ZipOutputStream output = new ZipOutputStream(new FileOutputStream(outputFilename));

      for (int i = 0; i < filenames.length; i++) {
        File file = new File(filenames[i]);
        // Do not add directories: they make this archive invalid.
        if (file.isDirectory() == false) {
          FileInputStream input = new FileInputStream(filenames[i]);

          try {
            // Use only the file name so full filesystem paths are not included in the archive.
            output.putNextEntry(new ZipEntry(file.getName()));
          } catch (ZipException ex) {
            JOptionPane.showMessageDialog(
                null,
                "zip error (Maintenance_Move_log_files_by_email_actionPerformed)",
                main.APPLICATION_NAME + " error",
                JOptionPane.ERROR_MESSAGE);
          }

          int length;
          while ((length = input.read(buffer)) > 0) {
            output.write(buffer, 0, length);
          }

          output.closeEntry();
          input.close();
        }
      }

      output.close();
    } catch (IOException ex) {
      JOptionPane.showMessageDialog(
          null,
          "i/o zip error (Maintenance_Move_log_files_by_email_actionPerformed)",
          main.APPLICATION_NAME + " error",
          JOptionPane.ERROR_MESSAGE);
    }
  }
}
