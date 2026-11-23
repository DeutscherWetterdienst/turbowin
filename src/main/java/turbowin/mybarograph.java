package turbowin;

import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/** Barograph observation state and compatibility controls used by legacy sensor readers. */
public final class mybarograph {
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

  // These controls are not displayed. The existing sensor readers update them on the Swing thread.
  public static JTextField jTextField1;
  public static JRadioButton jRadioButton1, jRadioButton2, jRadioButton3, jRadioButton4;
  public static JRadioButton jRadioButton5, jRadioButton6, jRadioButton7, jRadioButton8;
  public static JRadioButton jRadioButton9, jRadioButton10, jRadioButton11, jRadioButton12;

  private final boolean aws;

  mybarograph() {
    int mode = main.RS232_connection_mode;
    aws = mode == 3 || mode == 9 || mode == 10 || mode == 11;
    boolean barometer = mode == 1 || mode == 2 || (mode >= 4 && mode <= 8);
    jTextField1 = new JTextField();
    NumericInputWorkflow.decimal(jTextField1);
    jTextField1.setEditable(!aws);
    jRadioButton1 = new JRadioButton();
    jRadioButton2 = new JRadioButton();
    jRadioButton3 = new JRadioButton();
    jRadioButton4 = new JRadioButton();
    jRadioButton5 = new JRadioButton();
    jRadioButton6 = new JRadioButton();
    jRadioButton7 = new JRadioButton();
    jRadioButton8 = new JRadioButton();
    jRadioButton9 = new JRadioButton();
    jRadioButton10 = new JRadioButton();
    jRadioButton11 = new JRadioButton();
    jRadioButton12 = new JRadioButton();
    ButtonGroup group = new ButtonGroup();
    for (JRadioButton button : options()) group.add(button);

    if (mode == 4) {
      RS232_mintaka.RS232_Mintaka_Duo_Read_Sensor_Data_a_ppp_Data_Files_For_Obs();
    } else if (mode >= 5 && mode <= 8) {
      RS232_mintaka.RS232_Mintaka_Star_And_StarX_Read_Sensor_Data_a_ppp_Data_Files_For_Obs(
          mode >= 7);
    } else if (mode == 1 || mode == 2) {
      RS232_vaisala.RS232_Read_Sensor_Data_a_ppp_For_Obs();
    }

    if (!barometer && !pressure_amount_tendency.isEmpty()) {
      jTextField1.setText(pressure_amount_tendency);
    }
    if (!barometer && !a_code.isEmpty()) {
      boolean same =
          !pressure_amount_tendency.isEmpty() && Double.valueOf(pressure_amount_tendency) < 0.01;
      int selected =
          switch (a_code) {
            case A_0 -> same ? 8 : 0;
            case A_1 -> 1;
            case A_2 -> 2;
            case A_3 -> 3;
            case A_4 -> 9;
            case A_5 -> same ? 10 : 4;
            case A_6 -> 5;
            case A_7 -> 6;
            case A_8 -> 7;
            default -> -1;
          };
      if (selected >= 0) options()[selected].setSelected(true);
    } else {
      jRadioButton12.setSelected(true);
    }
  }

  boolean accept() {
    if (aws) {
      main.barograph_fields_update();
      return true;
    }
    reset();
    pressure_amount_tendency = jTextField1.getText().trim();
    String[] codes = {A_0, A_1, A_2, A_3, A_5, A_6, A_7, A_8, A_0_SAME, A_4, A_5_SAME, ""};
    JRadioButton[] options = options();
    for (int i = 0; i < options.length; i++) {
      if (options[i].isSelected()) {
        a_code = codes[i];
        break;
      }
    }
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

  private static JRadioButton[] options() {
    return new JRadioButton[] {
      jRadioButton1,
      jRadioButton2,
      jRadioButton3,
      jRadioButton4,
      jRadioButton5,
      jRadioButton6,
      jRadioButton7,
      jRadioButton8,
      jRadioButton9,
      jRadioButton10,
      jRadioButton11,
      jRadioButton12
    };
  }
}
