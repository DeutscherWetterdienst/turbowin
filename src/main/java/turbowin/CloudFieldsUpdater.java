package turbowin;

import static turbowin.main.*;

/** Updates low, middle, and high cloud fields on the main screen. */
final class CloudFieldsUpdater {

  private CloudFieldsUpdater() {}

  static void updateLow() {
    if ((mycl.cl_code.compareTo("") != 0) && (mycl.cl_code != null))
      jTextField33.setText(mycl.cl_code + " (code)");
    else jTextField33.setText("");

    coded_obs_update();
  }

  static void updateMiddle() {
    if ((mycm.cm_code.compareTo("") != 0) && (mycm.cm_code != null)) {
      // Middle-cloud codes 7a, 7b, and 7c are displayed as the first digit, 7.
      jTextField34.setText(mycm.cm_code.substring(0, 1) + " (code)");
    } else jTextField34.setText("");

    coded_obs_update();
  }

  static void updateHigh() {
    if ((mych.ch_code.compareTo("") != 0) && (mych.ch_code != null))
      jTextField35.setText(mych.ch_code + " (code)");
    else jTextField35.setText("");

    coded_obs_update();
  }

  static void updateCover() {
    if ((mycloudcover.N.compareTo("") != 0) && (mycloudcover.N != null))
      jTextField30.setText(mycloudcover.N);
    else jTextField30.setText("");

    if ((mycloudcover.Nh.compareTo("") != 0) && (mycloudcover.Nh != null))
      jTextField31.setText(mycloudcover.Nh);
    else jTextField31.setText("");

    if ((mycloudcover.h.compareTo("") != 0) && (mycloudcover.h != null))
      jTextField32.setText(mycloudcover.h);
    else jTextField32.setText("");

    coded_obs_update();
  }
}
