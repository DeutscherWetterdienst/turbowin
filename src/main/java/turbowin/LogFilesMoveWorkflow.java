package turbowin;

import static turbowin.main.*;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/** Coordinates moving, backing up, and preparing meteorological log files. */
final class LogFilesMoveWorkflow {

  private LogFilesMoveWorkflow() {}

  static boolean move(final String move_mode_logs) {
    // called from: - Maintenance_Move_log_files_to_disk_actionPerformed() [main.java]
    //              - Maintenance_Move_log_files_by_email_actionPerformed()[main.java]

    /*
    // Note: temp_dir only for move_mode_logs = MOVE_TO_EMAIL; in move_mode_logs = MOVE_TO_DISK temp_dir is a dummy
    */

    /*
    // Note:  If you intend to distribute your program as an unsigned Java Web Start application,
    //      then instead of using the JFileChooser API you should use the file services provided
    //      by the JNLP API. These services FileOpenService and FileSaveService not only
    //      provide support for choosing files in a restricted environment, but also take care of
    //      actually opening and saving them. An example of using these services is in JWSFileChooserDemo.
    //      Documentation for using the JNLP API can be found in the Java Web Start lesson.
    //     (http://java.sun.com/docs/books/tutorial/uiswing/components/filechooser.html)
    */

    boolean doorgaan = false;
    boolean doorgaan_captain = true;

    /* first check if there is an immt log source file present (and not empty) */
    main.volledig_path_srcFilename_immt = LogFilePathBuilder.immtSource(logs_dir);
    if (LogFilesMoveValidation.hasUsableImmtLog(main.volledig_path_srcFilename_immt)) {
      doorgaan = true;
    } else {
      JOptionPane.showMessageDialog(
          null,
          "Move log files cancelled, reason: nothing to move; IMMT log (file with all stored observations for climatological use) empty ",
          main.APPLICATION_NAME + " error",
          JOptionPane.WARNING_MESSAGE);
      doorgaan = false;
    }

    /* OK immt log present, so continue */

    if (doorgaan == true) {
      /* in MOVE_TO_DISK mode filechooser dialog popup */
      if (move_mode_logs.equals(main.MOVE_TO_DISK) == true) {
        // pop-up the file/directory chooser dialog box
        // JFileChooser chooser = new JFileChooser(".");
        //             // present dir
        // JFileChooser chooser = new
        // JFileChooser(javax.swing.filechooser.FileSystemView.getFileSystemView() ); // Constructs
        // a JFileChooser using the given FileSystemView
        JFileChooser chooser =
            new JFileChooser(); // Constructs a JFileChooser pointing to the user's default
        // directory. This default depends on the operating system. It is
        // typically the "My Documents" folder on Windows, and the user's
        // home directory on Unix.
        chooser.setFileSelectionMode(
            JFileChooser.DIRECTORIES_ONLY); // now the user can only select directories
        // int result = chooser.showSaveDialog(main.this);
        int result = chooser.showSaveDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
          output_dir =
              chooser
                  .getSelectedFile()
                  .getPath(); // getSelectedFile() -> in this case returns not a file but a
          // directory !

          final File dirs = new File(output_dir);

          if (dirs.exists() == false) // output_dir not exists
          {
            final boolean success = LogFilesDestinationValidation.ensureDirectory(output_dir);
            if (success == false) {
              JOptionPane.showMessageDialog(
                  null,
                  "Could not create " + output_dir,
                  main.APPLICATION_NAME + " error",
                  JOptionPane.WARNING_MESSAGE);
              doorgaan = false;
            }
          } // else destination path do not exist

          // output_dir and log_dir must be different !
          if (doorgaan == true) {
            if (LogFilesDestinationValidation.isSameDirectory(output_dir, logs_dir) == true) {
              JOptionPane.showMessageDialog(
                  null,
                  "Download folder the same as log files folder, LOG FILES NOT MOVED ",
                  main.APPLICATION_NAME + " error",
                  JOptionPane.WARNING_MESSAGE);

              output_dir = null;
              doorgaan = false;
            } // if (output_dir.equals(logs_dir) == true)
          } // if (doorgaan == true)

        } // if (result == JFileChooser.APPROVE_OPTION
        else // Cancel buttom / cross
        {
          doorgaan = false;
        } // else

      } // if (move_mode_logs.equals(MOVE_TO_DISK) == true)
      else if (move_mode_logs.equals(main.MOVE_TO_EMAIL) == true) {
        output_dir = main.temp_logs_dir;
      } // else if (move_mode_logs.equals(MOVE_TO_EMAIL) == true)
    } // if doorgaan == true

    /*
    // move captain source file to captain destination file (if captain source file is present)
    */
    if (doorgaan == true) {
      /* captain distination file (eg PGDE_captain.log) */
      // volledig_path_dstFilename_captain = output_dir + java.io.File.separator + CAPTAIN_LOG;
      // volledig_path_dstFilename_captain = output_dir + java.io.File.separator + call_sign + "_" +
      // CAPTAIN_LOG;
      main.volledig_path_dstFilename_captain =
          LogFilePathBuilder.captainDestination(output_dir, station_ID);

      /* captain source file (captain.log) */
      main.volledig_path_srcFilename_captain = LogFilePathBuilder.captainSource(logs_dir);

      /* captain backup file (eg PGDE_CAPTAIN_BACKUP May 08, 2015.TXT) */
      cal_systeem_datum_tijd =
          new GregorianCalendar(
              new SimpleTimeZone(0, "UTC")); // gives system date and time (UTC) of this moment
      String systeem_date_time = LogFileBackupDate.format(cal_systeem_datum_tijd);
      // volledig_path_backup_srcFilename_captain = logs_dir + java.io.File.separator +
      // "CAPTAIN_BACKUP " + systeem_date_time + ".TXT";
      // volledig_path_backup_srcFilename_captain = logs_dir + java.io.File.separator + call_sign +
      // "_" + "CAPTAIN_BACKUP " + systeem_date_time + ".TXT";
      main.volledig_path_backup_srcFilename_captain =
          LogFilePathBuilder.captainBackup(logs_dir, station_ID, systeem_date_time);

      File captain_source_file = new File(main.volledig_path_srcFilename_captain);
      if (captain_source_file.exists()) {
        doorgaan_captain = captain_source_file.length() > 5;
      } else // no captain source file present
      {
        // NB no message necessary, because captain data (file) not mandatory (contrary to immt log)
        doorgaan_captain = false;
      }
    } // if (doorgaan == true)

    if (doorgaan == true
        && doorgaan_captain == true
        && move_mode_logs.equals(main.MOVE_TO_DISK) == true) {
      CaptainLogMoveWorkflow.start(
          main.volledig_path_srcFilename_captain,
          main.volledig_path_dstFilename_captain,
          () -> handleCaptainMoveSuccess(move_mode_logs));
    } // if (doorgaan == true && doorgaan_captain == true &&
    // move_mode_logs.equals(main.MOVE_TO_DISK) == true)

    if (doorgaan == true
        && doorgaan_captain == true
        && move_mode_logs.equals(main.MOVE_TO_EMAIL) == true) {
      // NB do not use swingworker in case of email send, due to possible synchronisation issue (zip
      // attachement not created on time)

      /* copy captain source file to destination captain file */
      String result =
          LogFileCopyWorkflow.copy(
              main.volledig_path_srcFilename_captain, main.volledig_path_dstFilename_captain);

      if (result.equals("NOT_OK") == true) {
        JOptionPane.showMessageDialog(
            null,
            "Unable to move "
                + main.volledig_path_srcFilename_captain
                + " to "
                + main.volledig_path_dstFilename_captain,
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
      }
      if (result.equals("OK") == true) {
        handleCaptainMoveSuccess(move_mode_logs);
      } // if (result_opgehaald.equals("OK") == true)
    } // if (doorgaan == true && doorgaan_captain == true &&
    // move_mode_logs.equals(main.MOVE_TO_EMAIL) == true)

    /*
    // copy immt source file to destination and make a backup of the immt file
    //
    // + copy observer data plus number of observations (via function Kopieeren_Waarnemers_En_Aantallen())
    */
    if (doorgaan == true && move_mode_logs.equals(main.MOVE_TO_DISK) == true) {
      /* immt distination file (eg PGDE_immt.log) */
      // volledig_path_dstFilename_immt = output_dir + java.io.File.separator + IMMT_LOG;
      // volledig_path_dstFilename_immt = output_dir + java.io.File.separator + call_sign + "_" +
      // IMMT_LOG;
      main.volledig_path_dstFilename_immt =
          LogFilePathBuilder.immtDestination(output_dir, station_ID);

      /* immt source file (immt.log) */
      main.volledig_path_srcFilename_immt = LogFilePathBuilder.immtSource(logs_dir);

      /* immt backup file (eg PGDE_IMMT_BACKUP May 08, 2015.TXT) */
      cal_systeem_datum_tijd =
          new GregorianCalendar(
              new SimpleTimeZone(0, "UTC")); // gives system date and time (UTC) of this moment
      String systeem_date_time = LogFileBackupDate.format(cal_systeem_datum_tijd);
      // volledig_path_backup_srcFilename_immt = logs_dir + java.io.File.separator + "IMMT_BACKUP "
      // + systeem_date_time + ".TXT";
      // volledig_path_backup_srcFilename_immt = logs_dir + java.io.File.separator + call_sign + "_"
      // + "IMMT_BACKUP " + systeem_date_time + ".TXT";
      main.volledig_path_backup_srcFilename_immt =
          LogFilePathBuilder.immtBackup(logs_dir, station_ID, systeem_date_time);

      ImmtLogMoveWorkflow.start(
          main.volledig_path_srcFilename_immt,
          main.volledig_path_dstFilename_immt,
          () -> handleImmtMoveSuccess(move_mode_logs));
    } // if (doorgaan == true && move_mode_logs.equals(main.MOVE_TO_DISK) == true)

    if (doorgaan == true && move_mode_logs.equals(main.MOVE_TO_EMAIL) == true) {
      // NB do not use swingworker in case of email send, due to possible synchronisation issue (zip
      // attachement not created on time)

      /* immt distination file (eg PGDE_immt.log) */
      // volledig_path_dstFilename_immt = output_dir + java.io.File.separator + IMMT_LOG;
      // volledig_path_dstFilename_immt = output_dir + java.io.File.separator + call_sign + "_" +
      // IMMT_LOG;
      main.volledig_path_dstFilename_immt =
          output_dir + java.io.File.separator + station_ID + "_" + IMMT_LOG;

      /* immt source file (immt.log) */
      main.volledig_path_srcFilename_immt = LogFilePathBuilder.immtSource(logs_dir);

      /* immt backup file (eg PGDE_IMMT_BACKUP May 08, 2015.TXT) */
      cal_systeem_datum_tijd =
          new GregorianCalendar(
              new SimpleTimeZone(0, "UTC")); // gives system date and time (UTC) of this moment
      String systeem_date_time = LogFileBackupDate.format(cal_systeem_datum_tijd);
      // volledig_path_backup_srcFilename_immt = logs_dir + java.io.File.separator + "IMMT_BACKUP "
      // + systeem_date_time + ".TXT";
      // volledig_path_backup_srcFilename_immt = logs_dir + java.io.File.separator + call_sign + "_"
      // + "IMMT_BACKUP " + systeem_date_time + ".TXT";
      main.volledig_path_backup_srcFilename_immt =
          logs_dir
              + java.io.File.separator
              + station_ID
              + "_"
              + "IMMT_BACKUP "
              + systeem_date_time
              + ".TXT";

      /*
      // count number of obs per observer per year
      */
      support_class.Kopieeren_Waarnemers_En_Aantallen();

      /*
      // copy immt
      */
      String result =
          LogFileCopyWorkflow.copy(
              main.volledig_path_srcFilename_immt, main.volledig_path_dstFilename_immt);

      if ((result.equals("NOT_OK") == true)) {
        JOptionPane.showMessageDialog(
            null,
            "Unable to move "
                + main.volledig_path_srcFilename_immt
                + " to "
                + main.volledig_path_dstFilename_immt,
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
      }
      if ((result.equals("OK") == true)) {
        handleImmtMoveSuccess(move_mode_logs);
      } // if (result_opgehaald.equals("OK") == true)
    } // if (doorgaan == true && move_mode_logs.equals(main.MOVE_TO_EMAIL) == true)

    return doorgaan;
  }

  private static void handleCaptainMoveSuccess(final String move_mode_logs) {
    File source_file = new File(main.volledig_path_srcFilename_captain);
    File renamed_file = new File(main.volledig_path_backup_srcFilename_captain);

    // Rename the source only after the copy has completed successfully.
    if (source_file.renameTo(renamed_file) == false) {
      // A same-day backup may already exist, causing the rename to fail.
      // Backup failure is only reported for disk moves, preserving the legacy behavior.
      if (move_mode_logs.equals(main.MOVE_TO_DISK) == true) {
        JOptionPane.showMessageDialog(
            null,
            "Backing up log file "
                + main.volledig_path_srcFilename_captain
                + " failed (2nd move/backup same day?)",
            main.APPLICATION_NAME + " info",
            JOptionPane.INFORMATION_MESSAGE);
      }
      // Delete the source so it cannot be mistaken for the active captain log after backup fails.
      source_file.delete();
    }
  }

  private static void handleImmtMoveSuccess(final String move_mode_logs) {
    if (move_mode_logs.equals(main.MOVE_TO_DISK) == true) {
      // Show this message only for disk moves. Do not show it when the files are zipped for email:
      // displaying it can delay zip creation even though the email program has already been opened
      // with a reference to that zip file.
      String info = "meteo log files moved to folder: " + output_dir;
      JOptionPane.showMessageDialog(
          null, info, main.APPLICATION_NAME + " info", JOptionPane.INFORMATION_MESSAGE);
      main.log_turbowin_system_message("[GENERAL] " + info);
    }

    // Rename the source only after the copy has completed successfully.
    File source_file = new File(main.volledig_path_srcFilename_immt);
    File renamed_file = new File(main.volledig_path_backup_srcFilename_immt);

    if (source_file.renameTo(renamed_file) == false) {
      // A same-day backup may already exist, causing the rename to fail.
      // Backup failures are user-visible in both disk and email modes, preserving legacy behavior.
      if (move_mode_logs.equals(main.MOVE_TO_DISK) == true
          || move_mode_logs.equals(main.MOVE_TO_EMAIL) == true) {
        JOptionPane.showMessageDialog(
            null,
            "Backing up log file "
                + main.volledig_path_srcFilename_immt
                + " failed (2nd move/backup same day?)",
            main.APPLICATION_NAME + " info",
            JOptionPane.INFORMATION_MESSAGE);
      }
      // Delete the source so it cannot be mistaken for the active IMMT log after backup fails.
      source_file.delete();
    }

    if (move_mode_logs.equals(main.MOVE_TO_EMAIL) == true) {
      // Zip only after the asynchronous move completes to avoid an I/O race or incomplete archive.
      zip_log_files();
    }

    String info =
        "Clearing all the data of the observers (surname, full initials/full christian name, rank, discharge book number)";
    if (JOptionPane.showConfirmDialog(
            null,
            info,
            main.APPLICATION_NAME + " message",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE)
        == JOptionPane.YES_OPTION) {
      String volledig_path_observer = main.logs_dir + java.io.File.separator + main.OBSERVER_LOG;
      try {
        FileChannel.open(Paths.get(volledig_path_observer), StandardOpenOption.WRITE)
            .truncate(0)
            .close();
        JOptionPane.showMessageDialog(
            null,
            "Successfully cleared all the data of the observers",
            APPLICATION_NAME + " message",
            JOptionPane.INFORMATION_MESSAGE);
      } catch (IOException ex) {
        JOptionPane.showMessageDialog(
            null,
            "Clearing all the data of the observers failed",
            APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
      }
    }
  }

  private static void zip_log_files() {
    LogFilesZipWorkflow.zip(temp_logs_dir, ship_name, main.LOGS_ZIP);
  }
}
