package turbowin;

import javax.swing.JOptionPane;

/** Shared wave observation state and existing wave validation and encoding. */
public final class mywaves {
  WavesView.Input initialValues() {
    WavesView.Swell swell;
    if (swell_1_dir.equals("no swell")) swell = WavesView.Swell.NONE;
    else if (swell_1_dir.equals("confused")) swell = WavesView.Swell.CONFUSED;
    else if (!swell_1_dir.isEmpty() && swell_2_dir.isEmpty()) swell = WavesView.Swell.ONE;
    else if (!swell_1_dir.isEmpty() && !swell_2_dir.isEmpty()) swell = WavesView.Swell.TWO;
    else swell = WavesView.Swell.UNDETERMINED;
    return new WavesView.Input(wind_waves_period, wind_waves_height, swell);
  }

  private void Reset_All_Waves_Vars() {
    // scope this module + myturbowin.java main module (all of type: static)
    wind_waves_period = "";
    wind_waves_height = "";
    swell_1_dir = "";
    swell_1_period = "";
    swell_1_height = "";
    swell_2_dir = "";
    swell_2_period = "";
    swell_2_height = "";
    Hw_code = "";
    Pw_code = "";
    Dw1_code = "";
    Pw1_code = "";
    Hw1_code = "";
    Dw2_code = "";
    Hw2_code = "";

    // local var's with initialisation
    float_wind_waves_height = main.INVALID; // could become e.g. 2.5 (-> use float)
    int_wind_waves_period = main.INVALID;
    checks_ok = false;

    /* update wave fields on main (progress)screen */
    main.waves_fields_update();
  }

  boolean accept(WavesView.Input input) {
    // TODO add your handling code here:

    // initialisation

    // Only if Swell not determined or Calm sea -> reset all waves vars
    // if onther wave options was chosen -> will be followed by a second appropriate waves input
    // form
    if ((input.swell() == WavesView.Swell.UNDETERMINED)
        || (input.swell() == WavesView.Swell.NONE)) {
      Reset_All_Waves_Vars();
    }

    // get text from inserted fields
    wind_waves_period = input.period().trim();
    wind_waves_height = input.height().trim();

    /*
    //////////////// checks
    */

    // convert collected (string) values to ints and floats

    // wind waves
    try {
      int_wind_waves_period = Integer.parseInt(wind_waves_period.trim());
    } catch (NumberFormatException e) {
      /* ... */
    }

    try {
      float_wind_waves_height = Float.parseFloat(wind_waves_height.trim());
    } catch (NumberFormatException e) {
      /* ... */
    }

    //
    ///////////////// wind waves checks
    //
    if ((wind_waves_period.trim().compareTo("confused") != 0)
        && (wind_waves_period.trim().length() > 0)
        && (int_wind_waves_period < 0
            || int_wind_waves_period > 50
            || int_wind_waves_period == main.INVALID)) {
      JOptionPane.showMessageDialog(
          null,
          "Wind waves period must be in range 0 - 50 (no decimals), confused or blank",
          main.APPLICATION_NAME + " error",
          JOptionPane.WARNING_MESSAGE);
      Reset_All_Waves_Vars();
    } else if ((wind_waves_height.trim().compareTo("confused") != 0)
        && (wind_waves_height.trim().length() > 0)
        && (float_wind_waves_height < 0.0
            || float_wind_waves_height > 49.0
            || float_wind_waves_height == main.INVALID)) {
      JOptionPane.showMessageDialog(
          null,
          "Wind waves height must be in range 0 - 49.0, confused or blank",
          main.APPLICATION_NAME + " error",
          JOptionPane.WARNING_MESSAGE);
      Reset_All_Waves_Vars();
    } else {
      checks_ok = true;
    }

    /*
    ///////////// consistency checks
    */
    if (checks_ok == true) {

      ////// wind waves
      if ((int_wind_waves_period == 0)
          && (float_wind_waves_height > 2.2)
          && (float_wind_waves_height != main.INVALID)) {
        JOptionPane.showMessageDialog(
            null,
            "If 'wind waves period' = 0 sec. then 'wind waves height' can not be > 2.2 metres",
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
        Reset_All_Waves_Vars();
      } // if ((int_wind_waves_period == 0) etc.
      else if ((float_wind_waves_height == 0.0) && (wind_waves_period.trim().equals("confused"))) {
        JOptionPane.showMessageDialog(
            null,
            "If 'wind waves height' = 0 metre then 'wind waves period' can not be > 5 sec. or confused",
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
        Reset_All_Waves_Vars();
      } // if ((int_wind_waves_period == 0) etc.
      else if ((float_wind_waves_height == 0.0)
          && (int_wind_waves_period > 5)
          && (int_wind_waves_period != main.INVALID)
          && (wind_waves_height.trim().compareTo("confused") != 0)) {
        JOptionPane.showMessageDialog(
            null,
            "If 'wind waves height' = 0 metre then 'wind waves period' can not be > 5 sec. or confused",
            main.APPLICATION_NAME + " error",
            JOptionPane.WARNING_MESSAGE);
        Reset_All_Waves_Vars();
      } // if ((int_wind_waves_period == 0) etc.
    } // if (checks_ok == true)

    /*
    ///////////// determine meteorological code figures wind waves
    */
    if (checks_ok == true) {
      ///////////// (wind) wave period
      if (wind_waves_period.trim().length() == 0) // nothing inserted
      {
        // JOptionPane.showMessageDialog(null, "nothing inserted" , "wind waves period",
        // JOptionPane.INFORMATION_MESSAGE);
        Pw_code = "//";
      } else if (wind_waves_period.trim().equals("confused")) {
        Pw_code = "99";
      } else {
        // nb in this stage always in whole seconds (otherwise earlier error message)
        Pw_code =
            Integer.toString(
                int_wind_waves_period); // convert to string      // or (alternative):Pw_code =
        // String.valueOf(num_Pw_code);

        // Pw code always 2 characters width e.g. 03
        int len = 2;
        if (Pw_code.length() < len) // pad on left with zeros
        Pw_code = "0000000000".substring(0, len - Pw_code.length()) + Pw_code;
      } // else

      //////////// (wind) wave height
      if (wind_waves_height.trim().length() == 0) // nothing inserted
      {
        Hw_code = "//";
      } else if (wind_waves_height.trim().equals("confused")) {
        Hw_code = "//";
      } else {
        // round to units of 0.5 metres

        // e.g. 2.2 metres -> Hw = 4 units of 0.5 metres
        // e.g. 2.3 metres -> Hw = 5 units of 0.5 metres

        int num_Hw_code = (int) Math.floor(float_wind_waves_height * 2 + 0.5);
        Hw_code =
            Integer.toString(num_Hw_code); // convert to string      // or (alternative): Hw_code =
        // String.valueOf(num_Hw_code);

        // Hw code always 2 characters width e.g. 03
        int len = 2;
        if (Hw_code.length() < len) // pad on left with zeros
        Hw_code = "0000000000".substring(0, len - Hw_code.length()) + Hw_code;
      } // else
    } // if (checks_ok == true)

    if (checks_ok) {
      main.waves_fields_update();
      if (input.swell() == WavesView.Swell.NONE) {
        Dw1_code = "00";
        Dw2_code = "00";
        swell_1_dir = "no swell";
        swell_1_period = "no swell";
        swell_1_height = "no swell";
      }
    }
    return checks_ok;
  }

  // var's scope this module + andere waves modules + myturbowin.java main module (all of type:
  // static)
  public static String wind_waves_period =
      ""; // if not ="" -> nullpointerexception if update button clicked on main page
  public static String wind_waves_height = "";
  public static String swell_1_dir = "";
  public static String swell_1_period = "";
  public static String swell_1_height = "";
  public static String swell_2_dir = "";
  public static String swell_2_period = "";
  public static String swell_2_height = "";
  public static String Hw_code = "";
  public static String Pw_code = "";
  public static String Dw1_code = "";
  public static String Pw1_code = "";
  public static String Hw1_code = "";
  public static String Dw2_code = "";
  public static String Pw2_code = "";
  public static String Hw2_code = "";

  // var's scope only this module (no static)
  private float float_wind_waves_height = main.INVALID;
  private int int_wind_waves_period = main.INVALID;
  private boolean checks_ok = false;
}
