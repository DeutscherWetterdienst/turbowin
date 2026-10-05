package turbowin;

import javax.swing.JOptionPane;

/** Validates the level-two relationships between cloud amounts and cloud types. */
final class LevelTwoCloudValidation {

  private LevelTwoCloudValidation() {}

  static boolean validate(
      boolean clCodeValid,
      boolean cmCodeValid,
      boolean chCodeValid,
      int lowCloud,
      int middleCloud,
      int highCloud) {
    if (mycloudcover.N.equals(mycloudcover.N_CLOUDLESS) && !mycl.cl_code.equals("0")) {
      return warning("if total cloud cover is 'cloudless', Cl must be 'no clouds Cl'");
    }

    if (mycloudcover.N.equals(mycloudcover.N_CLOUDLESS) && !mycm.cm_code.equals("0")) {
      return warning("if total cloud cover is 'cloudless', Cm must be 'no clouds Cm'");
    }

    if (mycloudcover.N.equals(mycloudcover.N_CLOUDLESS) && !mych.ch_code.equals("0")) {
      return warning("if total cloud cover is 'cloudless', Ch must be 'no clouds Ch'");
    }

    if (mycl.cl_code.equals("0")
        && mycm.cm_code.equals("0")
        && mych.ch_code.equals("0")
        && !mycloudcover.N.equals(mycloudcover.N_CLOUDLESS)) {
      return warning("if Cl and Cm and Ch is 'no clouds', total cloud cover must be 'cloudless'");
    }

    if (mycloudcover.Nh.equals(mycloudcover.NH_0_8) && !mycl.cl_code.equals("0")) {
      return warning(
          "if 'amount of Cl (or Cm if Cl not present)' is '0/8', Cl must be 'no clouds Cl'");
    }

    if (mycloudcover.Nh.equals(mycloudcover.NH_0_8)
        && mycl.cl_code.equals("0")
        && !mycm.cm_code.equals("0")) {
      return warning(
          "if 'amount of Cl (or Cm if Cl not present)' is '0/8' and Cl is 'no clouds Cl', Cm must be 'no clouds Cm'");
    }

    if (mycl.cl_code.equals("0")
        && mycm.cm_code.equals("0")
        && !mycloudcover.Nh.equals(mycloudcover.NH_0_8)) {
      return warning(
          "if Cl and Cm is 'no clouds', amount of Cl (or Cm if Cl not present)' must be '0/8'  ");
    }

    if (mycloudcover.Nh.equals(mycloudcover.NH_8_8)
        && chCodeValid
        && highCloud >= 0
        && highCloud <= 9) {
      return warning(
          "if 'amount of Cl (or Cm if Cl not present)' is '8/8', Ch must be 'not determined'");
    }

    if (mycloudcover.Nh.equals(mycloudcover.NH_8_8)
        && clCodeValid
        && cmCodeValid
        && lowCloud >= 1
        && lowCloud <= 9
        && middleCloud >= 0
        && middleCloud <= 9) {
      return warning(
          "if 'amount of Cl (or Cm if Cl not present)' is '8/8' and Cl was determined (in range 1 - 9 or 'no clouds Cl'), Cm must be 'not determined'");
    }

    return true;
  }

  private static boolean warning(String message) {
    JOptionPane.showMessageDialog(
        null, message, main.APPLICATION_NAME, JOptionPane.WARNING_MESSAGE);
    return false;
  }
}
