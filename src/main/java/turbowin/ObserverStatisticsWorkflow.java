package turbowin;

import static turbowin.main.IMMT_LOG;
import static turbowin.main.OBSERVER_LOG;
import static turbowin.main.cal_systeem_datum_tijd;
import static turbowin.main.logs_dir;
import static turbowin.main.station_ID;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;

/** Coordinates the observer statistics export and backup workflow. */
final class ObserverStatisticsWorkflow {

  private ObserverStatisticsWorkflow() {}

  static void run() {
    // called from: Move_log_files()-- doInbackground---  [main_support.java]

    /*
    // checking which years are present in immt.txt staan bv 2003, 2004 en 2005
    */

    int[][] aantal_waarnemer = new int[main.MAX_AANTAL_JAREN_IN_IMMT][main.MAX_AANTAL_WAARNEMERS];
    String volledig_path_immt = logs_dir + java.io.File.separator + IMMT_LOG;
    // String moved_observername_file                = output_dir + java.io.File.separator +
    // OBSERVER_LOG;
    // String moved_observername_file                = output_dir + java.io.File.separator +
    // call_sign + "_" + OBSERVER_LOG;
    String moved_observername_file =
        main.output_dir + java.io.File.separator + station_ID + "_" + OBSERVER_LOG;
    String[] backup_moved_observername_file_array = new String[main.MAX_AANTAL_JAREN_IN_IMMT];
    String[] moved_observername_file_array = new String[main.MAX_AANTAL_JAREN_IN_IMMT];
    String observername_office;

    /* initialisation */
    for (int p = 0; p < main.MAX_AANTAL_JAREN_IN_IMMT; p++) {
      main.jaar_substring_array[p] = "";
    } // lege array plaats maken

    /* read all lines/records from immt log */
    try (BufferedReader in = new BufferedReader(new FileReader(volledig_path_immt))) {
      ObserverYearExtractor.extract(in, main.jaar_substring_array);
    } // try
    catch (IOException ex) {
      // do nothing, possible file was never created
    } // catch

    /*
    // download (en backup) file namen bepalen m.b.v argument "moved_observername_file" deze is bv A:\PGDE_observer.log
    //
    // dit uitbreiden met jaartallen eerder gelezen uit immt log
    // bv A:\PGDE_observer.log -> A:\PGDE_observer_2004.log
    //                         -> A:\PGDE_observer_2005.log
    //                         -> A:\PGDE_observer_2006.log
    //
    // dus nu meerdere download file namen
    */

    /* initialisation */
    for (int j = 0; j < main.MAX_AANTAL_JAREN_IN_IMMT; j++) {
      moved_observername_file_array[j] = "";
      backup_moved_observername_file_array[j] = "";
    }

    /* positie bepalen waar jaartal tussengevoegd moet worden (bv A:\observer.log -> A:\observer_2006.log) */
    /* (absolute) filenamen van de download (en download backup) observer files bepalen */

    cal_systeem_datum_tijd =
        new GregorianCalendar(
            new SimpleTimeZone(0, "UTC")); // geeft systeem datum tijd in UTC van dit moment
    String systeem_date_time = LogFileBackupDate.format(cal_systeem_datum_tijd);

    // volledig_path_backup_srcFilename_immt = logs_dir + java.io.File.separator + "IMMT_BACKUP " +
    // systeem_date_time + ".TXT";

    for (int j = 0; j < main.MAX_AANTAL_JAREN_IN_IMMT; j++) {
      if (main.jaar_substring_array[j].compareTo("") != 0) {
        /* eg A:\PGDE_observer_2006.log + A:\PGDE_observer_2007.log) */
        moved_observername_file_array[j] =
            ObserverStatisticsPathBuilder.outputPath(
                moved_observername_file, main.jaar_substring_array[j]);
        /* bepalen naam van de observername backup file (altijd op een vaste plaats) */

        /* eg PGDE_OBSERVER_2006_BACKUP November 22, 2009.txt + PGDE_OBSERVER_2007_BACKUP November 22, 2009.txt */
        // backup_moved_observername_file_array[j] = logs_dir + java.io.File.separator + "OBSERVER_"
        // + jaar_substring_array[j] + "_BACKUP " + systeem_date_time + ".TXT";
        // backup_moved_observername_file_array[j] = logs_dir + java.io.File.separator + call_sign +
        // "_" + "OBSERVER_" + jaar_substring_array[j] + "_BACKUP " + systeem_date_time + ".TXT";
        backup_moved_observername_file_array[j] =
            ObserverStatisticsPathBuilder.backupPath(
                logs_dir, station_ID, main.jaar_substring_array[j], systeem_date_time);
      } // if (jaar_substring_array[k] != "")
    } // for (int j = 0; j < main.MAX_AANTAL_JAREN_IN_IMMT; j++)

    /*
    // NB you know here that observer name file in this stage is present
    */

    /* initialisation */
    for (int b = 0; b < main.MAX_AANTAL_WAARNEMERS; b++) {
      main.observername_array[b] = "";
    }

    String volledig_path_observer = main.logs_dir + java.io.File.separator + main.OBSERVER_LOG;
    String record;

    /* read all lines/records from observer log */
    try (BufferedReader in = new BufferedReader(new FileReader(volledig_path_observer))) {
      ObserverNameReader.read(in, main.observername_array);

    } // try
    catch (IOException ex) {
      // do nothing, possible file was never created
    } // catch

    /*
    // uitlezen van immt log file om het aantal waarnemingen per waarnemer te tellen
    // NB je weet zeker dat de immt.log in deze fase aanwezig is
    */

    /* read all lines/records from immt log */
    try (BufferedReader in = new BufferedReader(new FileReader(volledig_path_immt))) {
      aantal_waarnemer =
          ObserverStatisticsCounter.count(in, main.observername_array, main.jaar_substring_array);
    } // try
    catch (IOException ex) {
      // do nothing, possible file was never created
    } // catch

    /*
    // observers and number of made observations to download and backup file(s)
    */

    /* open the moved(download) and backup observer files */
    ObserverStatisticsFileSet observerFiles =
        ObserverStatisticsFileSet.open(
            moved_observername_file_array, backup_moved_observername_file_array);

    /* write to the moved and backup files */
    BufferedWriter[] outputFiles = observerFiles.outputFiles();
    BufferedWriter[] backupFiles = observerFiles.backupFiles();
    for (int m = 0; m < main.MAX_AANTAL_JAREN_IN_IMMT; m++) {
      for (int i = 0; i < main.MAX_AANTAL_WAARNEMERS; i++) {
        observername_office = main.observername_array[i];

        if (observername_office.compareTo("") != 0) {
          ObserverStatisticsWriter.write(
              outputFiles, backupFiles, m, observername_office, aantal_waarnemer[m][i]);
        } // if (observername_office != "")
      } // for (i = 0; i < MAX_AANTAL_WAARNEMERS; i++)
    } // for (int m = 0; m < MAX_AANTAL_JAREN_IN_IMMT; m++)

    /* close all the (moved and backup) observer files */
    observerFiles.close();
  }
}
