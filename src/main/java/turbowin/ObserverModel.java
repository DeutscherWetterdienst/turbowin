package turbowin;

import javax.swing.JOptionPane;

/** Observer log state and OBS_ID validation, independent of the old Swing form. */
public final class ObserverModel {
  private String[][] rows = new String[OBSERVER_ROWS][OBSERVER_COLUMNS];
  private int selectedRow = -1;

  public ObserverModel() {
    ObserverLogReadWorkflow.start(this);
  }

  boolean accept() {
    // TODO add your handling code here:

    if (main.eucaws_obs_id == true) {
      /* initialisation */
      clear_observer_data_array();
      boolean checks_ok = true;

      // collect data from all table cells
      for (int r = 0; r < OBSERVER_ROWS; r++) {
        for (int c = 0; c < OBSERVER_COLUMNS; c++) {
          observer_data[r][c] = rows[r][c];
        }
      }

      // check the inserted values
      checks_ok = check_data_observer_log();

      // compute OBS_Ids
      if (checks_ok) {
        checks_ok = compute_OBS_IDs();
      }

      if (checks_ok) {
        /* save observer data to file */
        schrijf_observer_log();

        // retrieve selected observer (surname + full initials + rank + discharge number)
        int selectedRow =
            this.selectedRow; // index of the firstselected row (start 0); -1 if no row was
        // selected
        // JOptionPane.showMessageDialog(null, selectedRow);
        if (selectedRow != -1) // -1 means no observer row selected
        {
          // nu kan het zijn dat na invullen van een nieuwe naam deze direct wordt geselecteerd
          // dan was deze bv Janssen;K;;; en wordt zo geschreven in immt.log
          // maar wordt opgeslagen in observer.log als Janssen;K;-;-;
          // dan bij downloaden log files geen match !!

          if (observer_data[selectedRow][1].equals("") == true) // full initials
          {
            observer_data[selectedRow][1] = "-";
          }
          if (observer_data[selectedRow][2].equals("") == true) // rank (or OBS_ID)
          {
            observer_data[selectedRow][2] = "-";
          }
          if (observer_data[selectedRow][3].equals("") == true) // discharge book number
          {
            observer_data[selectedRow][3] = "-";
          }

          selected_observer =
              observer_data[selectedRow][0]
                  + ";"
                  + observer_data[selectedRow][1]
                  + ";"
                  + observer_data[selectedRow][2]
                  + ";"
                  + observer_data[selectedRow][3]
                  + ";";
        } // if (selectedRow != -1)

        /* update observer field on main (progress) screen */
        main.observer_field_update();
      } // if (checks_ok)

      /* next screen if in_next_screen mode */
      if (checks_ok && main.in_next_sequence == true) {
        // last input screen of the next_screen_mode sequence
        main.in_next_sequence = false;
      }

      return checks_ok;
    } // if (main.eucaws_obs_id == true)
    else // NOT obs_id
    {
      /* initialisation */
      clear_observer_data_array();

      // collect data from all table cells
      for (int r = 0; r < OBSERVER_ROWS; r++) {
        for (int c = 0; c < OBSERVER_COLUMNS; c++) {
          observer_data[r][c] = rows[r][c];
        }
      }

      /* save observer data to file */
      schrijf_observer_log();

      // retrieve selected observer (surname + full initials + rank + discharge number)
      int selectedRow =
          this.selectedRow; // index of the firstselected row (start 0); -1 if no row was
      // selected
      // JOptionPane.showMessageDialog(null, selectedRow);
      if (selectedRow != -1) // -1 means no observer row selected
      {
        // nu kan het zijn dat na invullen van een nieuwe naam deze direct wordt geselecteerd
        // dan was deze bv Janssen;K;;; en wordt zo geschreven in immt.log
        // maar wordt opgeslagen in observer.log als Janssen;K;-;-;
        // dan bij downloaden log files geen match !!

        if (observer_data[selectedRow][1].equals("") == true) // full initials
        {
          observer_data[selectedRow][1] = "-";
        }
        if (observer_data[selectedRow][2].equals("") == true) // rank
        {
          observer_data[selectedRow][2] = "-";
        }
        if (observer_data[selectedRow][3].equals("") == true) // discharge book number
        {
          observer_data[selectedRow][3] = "-";
        }

        selected_observer =
            observer_data[selectedRow][0]
                + ";"
                + observer_data[selectedRow][1]
                + ";"
                + observer_data[selectedRow][2]
                + ";"
                + observer_data[selectedRow][3]
                + ";";
      } // if (selectedRow != -1)

      /* update observer field on main (progress) screen */
      main.observer_field_update();

      /* next screen if in_next_screen mode */
      if (main.in_next_sequence == true) {
        // last input screen of the next_screen_mode sequence
        main.in_next_sequence = false;
      }
      return true;
    } // else (NOT obs_id)
  } // GEN-LAST:event_OK_button_actionPerformed

  void loadValues(String[][] values, int selectedRow) {
    for (int row = 0; row < OBSERVER_ROWS; row++) {
      for (int column = 0; column < OBSERVER_COLUMNS; column++) {
        rows[row][column] = values[row][column] == null ? "" : values[row][column];
      }
    }
    this.selectedRow = selectedRow;
  }

  String[][] tableValues() {
    String[][] values = new String[OBSERVER_ROWS][OBSERVER_COLUMNS];
    for (int row = 0; row < OBSERVER_ROWS; row++) {
      System.arraycopy(rows[row], 0, values[row], 0, OBSERVER_COLUMNS);
    }
    return values;
  }

  int selectedRow() {
    return selectedRow;
  }

  void whenLoaded(Runnable callback) {
    observerLoadedCallback = callback;
    if (observerLoaded) callback.run();
  }

  private boolean compute_OBS_IDs() {
    boolean checks_ok = true;

    for (int r = 0; r < OBSERVER_ROWS; r++) {
      String surname = observer_data[r][0];
      String initials =
          observer_data[r][
              1]; // e.g. 'J' or "J.B. or 'JB' or 'JBC' or 'J.B.C.' or 'Hans Christansen'
      String obs_id = observer_data[r][2]; // colomn 2 (0 = first column) = OBS_ID

      // String row_number = String.valueOf(r + 1);
      // String info3 = "TEST (" + "row " + row_number + ")" + "\n" + "obs_id=" + obs_id;
      // JOptionPane.showMessageDialog(null, info3, main.APPLICATION_NAME + " error",
      // JOptionPane.WARNING_MESSAGE);

      if ("".equals(obs_id) && (!"".equals(surname))) // OBS_ID is empty and surname is inserted!
      {
        // check the initials
        StringBuilder obs_id_raw =
            new StringBuilder(); // obs_id_raw: after first computation but still not checked for
        // 'doubles'

        // Collect up to 2 valid letters
        // where a valid character is defined as a letter (e.g., A–Z, a–z) — so digits (0–9),
        // punctuation (like .), and other symbols are ignored.
        for (int i = 0; i < initials.length() && obs_id_raw.length() < 2; i++) {
          char c = initials.charAt(i);
          if (Character.isLetter(c)) {
            obs_id_raw.append(Character.toUpperCase(c)); // always append the letter as upper case
          }
        }
        // so now":
        // -empty            -> ""
        // J                 -> J
        // J.B.              -> JB
        // J.B.C.            -> JB
        // Hans Christiansen -> HA
        //

        // Pad with '1' characters if less than 3
        while (obs_id_raw.length() < 3) {
          obs_id_raw.append('1');
        }
        // so now":
        // ""                -> 111
        // J                 -> J11
        // JB                -> JB1
        // JB                -> JB1
        // HA                -> HA1
        //

        boolean duplicate = false;
        for (int x = 0; x < OBSERVER_ROWS; x++) {
          String existing_obs_id = observer_data[x][2]; // column 3 = index 2 = OBS_ID

          if ((!obs_id_raw.toString().equals("111"))
              && (existing_obs_id.equals(obs_id_raw.toString()))) {

            duplicate = true; // Duplicate found
            break;
          }
          // } // if (r != x)
        } // for (int x = 0; x < OBSERVER_ROWS; x++)

        if (!duplicate) {
          observer_data[r][2] = obs_id_raw.toString();
        }

        if (duplicate) {
          String obs_id_raw_alternative = compute_alternative_OBS_ID(obs_id_raw);

          if (!"".equals(obs_id_raw_alternative)) {
            observer_data[r][2] = obs_id_raw_alternative;
          } else {
            checks_ok = false;
            String info =
                "Error when computing OBS_ID (more than 9 records with the same first 2 letters of 'full initials'?), observer data will not be saved";
            JOptionPane.showMessageDialog(
                null, info, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
          }
        }
      } // for (int r = 0; r < OBSERVER_ROWS; r++)
    } // if ("".equals(obs_id))

    return checks_ok;
  }

  private String compute_alternative_OBS_ID(StringBuilder obs_id_raw) {
    String obs_id_raw_alternative = "";
    int MAX_DIGIT = 9;

    for (int y = 0; y < MAX_DIGIT; y++) {
      if (obs_id_raw.length() != 3) {
        // throw new IllegalArgumentException("obs_id_raw must be exactly 3 characters long.");
        obs_id_raw_alternative = "";
        break;
      }

      char lastChar = obs_id_raw.charAt(2);

      if (!Character.isDigit(lastChar)) {
        // throw new IllegalArgumentException("Last character must be a digit.");
        obs_id_raw_alternative = "";
        break;
      }

      int digit = lastChar - '0'; // Convert char to int
      digit++; // Increment

      if (digit > 9) {
        // throw new IllegalArgumentException("Digit overflow: only 1 to 9 supported.");
        obs_id_raw_alternative = "";
        break;
      }

      obs_id_raw.setCharAt(2, (char) ('0' + digit)); // Convert back to char and set

      boolean duplicate = false;
      for (int x = 0; x < OBSERVER_ROWS; x++) // do not use r as iterator!
      {
        String existing_obs_id = observer_data[x][2]; // column 3 = index 2 = OBS_ID
        if ((!obs_id_raw.toString().equals("111"))
            && (existing_obs_id.equals(obs_id_raw.toString()))) {
          duplicate = true; // Duplicate found
          break;
        }
      } // for (int x = 0; x < OBSERVER_ROWS; x++)

      if (!duplicate) {
        obs_id_raw_alternative = obs_id_raw.toString();
        break;
      }
    } // for (int y = 0; y < MAX_DIGIT; y++)

    return obs_id_raw_alternative; // NB obs_id_raw_alternative = "" if error or duplicate check
    // coulnot be performed becuse > 9 options
  }

  private boolean check_data_observer_log() {
    // check 'full initials' [1], must be present (if surname is not present the record will be
    // never saved (same result as the complete record is deleted)
    if (main.eucaws_obs_id == true) {
      if (observer_data == null) {
        return true;
      } else {
        for (int r = 0; r < OBSERVER_ROWS; r++) {
          if ((!"".equals(observer_data[r][0])) && ("".equals(observer_data[r][1]))) {
            // name not empty but initials empty
            String row_num = String.valueOf(r + 1);
            String info =
                "full initials empty ("
                    + "row "
                    + row_num
                    + ")"
                    + "\n"
                    + "observer data changes will not be saved";
            JOptionPane.showMessageDialog(
                null, info, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
            return false;
          }
        } // for (int r = 0; r < OBSERVER_ROWS; r++)
      } // else
    } // if (main.eucaws_obs_id == true)

    return true;
  }

  void clear_observer_data_array() {
    for (int r = 0; r < OBSERVER_ROWS; r++) {
      for (int c = 0; c < OBSERVER_COLUMNS; c++) {
        observer_data[r][c] = "";
      }
    }
  }

  void updateObserverTable() {
    loadValues(observer_data, -1);
    for (int row = 0; row < OBSERVER_ROWS; row++) {
      if (!selected_observer.isEmpty()
          && !rows[row][0].isEmpty()
          && !rows[row][1].isEmpty()
          && selected_observer.contains(rows[row][0])
          && selected_observer.contains(rows[row][1])) {
        selectedRow = row;
        break;
      }
    }
    observerLoaded = true;
    if (observerLoadedCallback != null) observerLoadedCallback.run();
  }

  private void schrijf_observer_log() {
    /* NB input/output in a GUI always via a SwingWorker (Core Java Volume 1 bld 795 e.v.; Volume 2 bld 37, 215) */
    boolean doorgaan = true;

    // first test if logs folder was defined
    if (main.logs_dir.equals("") == true) {
      JOptionPane.showMessageDialog(
          null,
          "Logs folder unknown, please select: Maintenance -> Log files settings",
          main.APPLICATION_NAME + " error",
          JOptionPane.WARNING_MESSAGE);
      doorgaan = false;
    }

    // ok logs dir is known
    if (doorgaan == true) {
      ObserverLogWriteWorkflow.start();
    } // if (doorgaan == true)
  }

  public static final int OBSERVER_ROWS =
      20; // static!!    // zie ook MAX_AANTAL_WAARNEMERS in main.java
  public static final int OBSERVER_COLUMNS = 4; // static!!
  private boolean observerLoaded;
  private Runnable observerLoadedCallback;

  // scope this module + myturbowin.java main module (all of type: static)
  public static String[][] observer_data =
      new String[OBSERVER_ROWS][OBSERVER_COLUMNS]; // default values: null
  public static String selected_observer = "";
}
