package turbowin;

import javax.swing.JOptionPane;

/** Barograph observation state and compatibility controls used by legacy sensor readers. */
public final class BarographModel {
  public static final String A_0 = "0";
  public static final String A_0_SAME = "0";
  public static final String A_1 = "1";
  public static final String A_2 = "2";
  public static final String A_3 = "3";
  public static final String A_4 = "4";
  public static final String A_5 = "5";
  public static final String A_5_SAME = "5";
  public static final String A_6 = "6";
  public static final String A_7 = "7";
  public static final String A_8 = "8";
  public static final String BAROGRAAF_HELP_DIR = "barograph.pdf";

  public static String a_code = "";
  public static String ppp_code = "";
  public static String pressure_amount_tendency = "";

  static ObservableText amountInput;

  enum Tendency {
    RISING_THEN_FALLING(A_0),
    RISING_SLOWER(A_1),
    RISING(A_2),
    RISING_FASTER(A_3),
    FALLING_THEN_RISING(A_5),
    FALLING_SLOWER(A_6),
    FALLING(A_7),
    FALLING_FASTER(A_8),
    SAME_RISING_THEN_FALLING(A_0_SAME),
    STEADY(A_4),
    SAME_FALLING_THEN_RISING(A_5_SAME),
    UNDETERMINED("");

    final String code;

    Tendency(String code) {
      this.code = code;
    }
  }

  static ObservableSelection<Tendency> tendency = new ObservableSelection<>(Tendency.UNDETERMINED);

  private final boolean aws;

  boolean sensorReadOnly() {
    return aws;
  }

  BarographModel() {
    int mode = main.RS232_connection_mode;
    aws = mode == 3 || mode == 9 || mode == 10 || mode == 11;
    boolean barometer = mode == 1 || mode == 2 || (mode >= 4 && mode <= 8);
    amountInput = ObservableText.decimal();
    tendency = new ObservableSelection<>(Tendency.UNDETERMINED);

    if (mode == 4) {
      RS232_mintaka.RS232_Mintaka_Duo_Read_Sensor_Data_a_ppp_Data_Files_For_Obs();
    } else if (mode >= 5 && mode <= 8) {
      RS232_mintaka.RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_a_ppp_Data_Files_For_Obs(
          mode >= 7);
    } else if (mode == 1 || mode == 2) {
      RS232_vaisala.RS232_Read_Sensor_Data_a_ppp_For_Obs();
    }

    if (!barometer && !pressure_amount_tendency.isEmpty()) {
      amountInput.set(pressure_amount_tendency);
    }
    if (!barometer && !a_code.isEmpty()) {
      boolean same =
          !pressure_amount_tendency.isEmpty() && Double.valueOf(pressure_amount_tendency) < 0.01;
      Tendency selected =
          switch (a_code) {
            case A_0 -> same ? Tendency.SAME_RISING_THEN_FALLING : Tendency.RISING_THEN_FALLING;
            case A_1 -> Tendency.RISING_SLOWER;
            case A_2 -> Tendency.RISING;
            case A_3 -> Tendency.RISING_FASTER;
            case A_4 -> Tendency.STEADY;
            case A_5 -> same ? Tendency.SAME_FALLING_THEN_RISING : Tendency.FALLING_THEN_RISING;
            case A_6 -> Tendency.FALLING_SLOWER;
            case A_7 -> Tendency.FALLING;
            case A_8 -> Tendency.FALLING_FASTER;
            default -> Tendency.UNDETERMINED;
          };
      tendency.set(selected);
    }
  }

  boolean accept() {
    if (aws) {
      main.barograph_fields_update();
      return true;
    }
    reset();
    pressure_amount_tendency = amountInput.get().trim();
    Tendency selected = tendency.get();
    if (selected != null) a_code = selected.code;
    double amount = main.INVALID;
    try {
      amount = Double.parseDouble(pressure_amount_tendency);
    } catch (NumberFormatException ignored) {
      // A blank or incomplete number retains the legacy invalid sentinel.
    }
    if (!pressure_amount_tendency.isEmpty() && (amount < 0.0 || amount > 50.0)) {
      warning("amount of pressure tendency must be in range 0.0 - 50.0 or blank");
      reset();
      return false;
    }
    if (a_code.equals(A_4) && amount != 0) {
      warning(
          "if characteristic of pressure tendency = 'steady' (a = 4) then amount of pressure tendency must be 0.0");
      reset();
      return false;
    }
    if (amount != main.INVALID) {
      int code = (int) Math.floor(amount * 10 + 0.5);
      ppp_code = String.format(java.util.Locale.ROOT, "%03d", code);
    }
    main.barograph_fields_update();
    return true;
  }

  private static void reset() {
    a_code = "";
    ppp_code = "";
    pressure_amount_tendency = "";
    main.barograph_fields_update();
  }

  private static void warning(String message) {
    JOptionPane.showMessageDialog(
        null, message, main.APPLICATION_NAME + " error", JOptionPane.WARNING_MESSAGE);
  }
}
