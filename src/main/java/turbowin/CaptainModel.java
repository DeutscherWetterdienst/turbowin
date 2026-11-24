package turbowin;

import javax.swing.JOptionPane;

/** Captain log state and date validation, independent of the old Swing form. */
public final class CaptainModel {
  private String[][] rows = new String[CAPTAIN_ROWS][CAPTAIN_COLUMNS];

  public CaptainModel() {
    CaptainLogReadWorkflow.start(this);
  }

  void updateCaptainTable() {
    loadValues(captain_data);
    captainLoaded = true;
    if (captainLoadedCallback != null) captainLoadedCallback.run();
  }

  void clear_captain_data_array() {
    for (int r = 0; r < CAPTAIN_ROWS; r++) {
      for (int c = 0; c < CAPTAIN_COLUMNS; c++) {
        captain_data[r][c] = "";
      }
    } // for (int r = 0; r < CAPTAIN_ROWS; r++)
  }

  boolean accept() {
    // TODO add your handling code here:

    boolean doorgaan = true;
    String hulp_date_of_joining = "";
    String hulp_date_of_leaving = "";

    /* initialisation */
    clear_captain_data_array();

    // check correct format date of joining
    for (int r = 0; r < CAPTAIN_ROWS; r++) {
      hulp_date_of_joining = rows[r][COLUMN_NUMBER_DATE_JOINING];

      if ((hulp_date_of_joining.length() > 0)
          && (hulp_date_of_joining.compareTo("-") != 0)
          && (hulp_date_of_joining.length() != 10)) {
        JOptionPane.showMessageDialog(
            null,
            "format date of joining must be DD/MM/YYYY, updated data not saved",
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
        doorgaan = false;
      } // if ((hulp_date_of_joining.length() > 0

      if (doorgaan && hulp_date_of_joining.length() == 10) {
        if ((hulp_date_of_joining.substring(2, 3).compareTo("/") != 0)
            || (hulp_date_of_joining.substring(5, 6).compareTo("/") != 0)) {
          JOptionPane.showMessageDialog(
              null,
              "format date of joining must be DD/MM/YYYY, updated data not saved",
              main.APPLICATION_NAME + " error",
              JOptionPane.WARNING_MESSAGE);
          doorgaan = false;
        }
      } // if ((hulp_date_of_joining.length() > 0
    } // for (int r = 0; r < CAPTAIN_ROWS; r++)

    if (doorgaan) {
      // check correct format date of leaving
      for (int r = 0; r < CAPTAIN_ROWS; r++) {
        hulp_date_of_leaving = rows[r][COLUMN_NUMBER_DATE_LEAVING];

        if ((hulp_date_of_leaving.length() > 0)
            && (hulp_date_of_leaving.compareTo("-") != 0)
            && (hulp_date_of_leaving.length() != 10)) {
          JOptionPane.showMessageDialog(
              null,
              "format date of leaving must be DD/MM/YYYY, updated data not saved",
              main.APPLICATION_NAME + " error",
              JOptionPane.WARNING_MESSAGE);
          doorgaan = false;
        }

        if (doorgaan && hulp_date_of_leaving.length() == 10) {
          if ((hulp_date_of_leaving.substring(2, 3).compareTo("/") != 0)
              || (hulp_date_of_leaving.substring(5, 6).compareTo("/") != 0)) {
            JOptionPane.showMessageDialog(
                null,
                "format date of leaving must be DD/MM/YYYY, updated data not saved",
                main.APPLICATION_NAME + " error",
                JOptionPane.WARNING_MESSAGE);
            doorgaan = false;
          }
        } // if ((hulp_date_of_joining.length() > 0
      } // for (int r = 0; r < CAPTAIN_ROWS; r++)
    } // if (doorgaan)

    if (doorgaan) {
      // collect data from all table cells
      for (int r = 0; r < CAPTAIN_ROWS; r++) {
        for (int c = 0; c < CAPTAIN_COLUMNS; c++) {
          captain_data[r][c] = rows[r][c];
        }
      }

      /* save observer data to file */
      schrijf_captain_log();

      /* update observer field on main (progress) screen */
      // main.captain_field_update();

    } // if (doorgaan)
    return doorgaan;
  } // GEN-LAST:event_OK_button_actionPerformed

  void loadValues(String[][] values) {
    for (int row = 0; row < CAPTAIN_ROWS; row++) {
      for (int column = 0; column < CAPTAIN_COLUMNS; column++) {
        rows[row][column] = values[row][column] == null ? "" : values[row][column];
      }
    }
  }

  String[][] tableValues() {
    String[][] values = new String[CAPTAIN_ROWS][CAPTAIN_COLUMNS];
    for (int row = 0; row < CAPTAIN_ROWS; row++) {
      System.arraycopy(rows[row], 0, values[row], 0, CAPTAIN_COLUMNS);
    }
    return values;
  }

  void whenLoaded(Runnable callback) {
    captainLoadedCallback = callback;
    if (captainLoaded) callback.run();
  }

  private void schrijf_captain_log() {
    /* NB input/output in a GUI always via a SwingWorker (Core Java Volume 1 bld 795 e.v.; Volume 2 bld 37, 215) */
    boolean doorgaan = true;

    // first test if logs dir was defined
    if (main.logs_dir.equals("") == true) {
      JOptionPane.showMessageDialog(
          null,
          "Log folder is not configured; check the TurboWin configuration",
          main.APPLICATION_NAME + " error",
          JOptionPane.WARNING_MESSAGE);
      doorgaan = false;
    }

    // ok, logs dir is known
    if (doorgaan == true) {
      CaptainLogWriteWorkflow.start();
    } // if (doorgaan == true)
  }

  public static final int CAPTAIN_ROWS = 10; // static !!
  public static final int CAPTAIN_COLUMNS = 5; // static !!
  private static final int COLUMN_NUMBER_DATE_JOINING = 2;
  private static final int COLUMN_NUMBER_DATE_LEAVING = 3;
  private boolean captainLoaded;
  private Runnable captainLoadedCallback;

  // scope this module + myturbowin.java main module (all of type: static)
  public static String[][] captain_data =
      new String[CAPTAIN_ROWS][CAPTAIN_COLUMNS]; // default values: null
}
