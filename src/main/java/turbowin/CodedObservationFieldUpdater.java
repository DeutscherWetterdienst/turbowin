package turbowin;

import static turbowin.main.*;

/** Updates the coded observation status field on the main screen. */
final class CodedObservationFieldUpdater {

  private CodedObservationFieldUpdater() {}

  static void update() {
    if ((RS232_connection_mode != 3)
        && (RS232_connection_mode != 9)
        && (RS232_connection_mode != 10)
        && (RS232_connection_mode != 11)) {
      // In compressed FORMAT 101 this is only a decompressed FM13 observation; icing (Is) has
      // special coding and meaning, so the icing group can contain six characters.
      String SPATIE = SPATIE_OBS_VIEW;
      jTextField4.setText(compose_coded_obs(SPATIE));
    }
  }
}
