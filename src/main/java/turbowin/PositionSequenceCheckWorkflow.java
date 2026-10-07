package turbowin;

import java.util.Calendar;
import java.util.GregorianCalendar;

/** Coordinates date/time and position sequence validation for an observation. */
final class PositionSequenceCheckWorkflow {

  private PositionSequenceCheckWorkflow() {}

  static boolean run(main_support support) {
    // initialisation
    boolean time_sequence_checks_ok = true;
    boolean doorgaan = true;
    PreviousObservationRecordParser.PreviousObservationRecord previousObservation =
        PreviousObservationRecordParser.parse(main.last_record);

    //
    //////////////// conversion from substring to ints or floats successful
    //
    if (previousObservation.valid()) {
      float num_vorige_obs_breedte = previousObservation.latitude();
      float num_vorige_obs_lengte = previousObservation.longitude();

      /*
      // compare date/time with the date/time of the previous obs
      */

      System.out.println("--- comparing date/time of entered obs with date/time of last saved obs");

      /* date-time previous saved obs */
      Calendar calendar_vorige_obs =
          new GregorianCalendar(
              previousObservation.year(),
              previousObservation.month() - 1,
              previousObservation.day(),
              previousObservation.hour(),
              0); // Month value is 0-based. e.g., 0 for January.

      /* date-time present obs */
      int num_huidige_obs_jaar = Integer.valueOf(mydatetime.year.trim());
      int num_huidige_obs_maand =
          Integer.valueOf(
              mydatetime.MM_code.trim()); // NB do not use mydatetime.month because = February etc.
      int num_huidige_obs_dag = Integer.valueOf(mydatetime.day.trim());
      int num_huidige_obs_uur = Integer.valueOf(mydatetime.hour.trim());

      Calendar calendar_huidige_obs =
          new GregorianCalendar(
              num_huidige_obs_jaar,
              num_huidige_obs_maand - 1,
              num_huidige_obs_dag,
              num_huidige_obs_uur,
              0); // Month value is 0-based. e.g., 0 for January.

      if (ObservationTimeSequenceValidation.isNotLater(calendar_huidige_obs, calendar_vorige_obs)) {
        if (!PositionSequenceWorkflow.confirmDateTime(calendar_huidige_obs)) {
          doorgaan = false;
          time_sequence_checks_ok = false;
        }
      } // if (calendar_huidige_obs.compareTo(calendar_vorige_obs) < 0)

      /* present obs position compared to the position of the previous obs */
      if (doorgaan) {
        System.out.println("--- comparing position of entered obs with position of last saved obs");

        /* first determine the time diff between present and previous obs */
        long obs_verschil_uur =
            ObservationTimeSequenceValidation.elapsedWholeHours(
                calendar_huidige_obs, calendar_vorige_obs);

        if (obs_verschil_uur
            >= 0) // alleen verdere checks als huidige obs datum/tijd is later dan vorige obs
        // datum/tijd
        {
          /* NB er wordt als grens genomen dat er max 30 mijl per uur afgelegd kan zijn */
          /* NB for testing see e.g.: http://williams.best.vwh.net/gccalc.htm */
          int afstand_vorige_huidige_obs =
              support.bepaal_afstand_huidige_obs_pos_tot_vorige_obs_pos(
                  num_vorige_obs_breedte, num_vorige_obs_lengte);

          // JOptionPane.showMessageDialog(null, afstand_vorige_huidige_obs,  main.APPLICATION_NAME
          // + " afstand tot vorige obs", JOptionPane.WARNING_MESSAGE);

          if (PositionSequenceValidation.exceedsAllowedDistance(
              obs_verschil_uur, afstand_vorige_huidige_obs)) {
            String info = "";
            info = "-position sequence check-\n";
            info += "obs position:\n";

            info += myposition.latitude_degrees;
            info += " ";
            info += myposition.latitude_minutes;
            info += "' ";
            info += myposition.latitude_hemisphere;
            info += "  ";
            info += myposition.longitude_degrees;
            info += "\u00B0 ";
            info += myposition.longitude_minutes;
            info += "' ";
            info += myposition.longitude_hemisphere;
            info += "\n";

            if (!PositionSequenceWorkflow.confirmPosition(info)) {
              doorgaan = false;
              time_sequence_checks_ok = false;
            }
          } // if ( ((obs_verschil_uur > 0 && obs_verschil <= 6) etc.
        } // if (obs_verschil_uur > 0)
      } // if (doorgaan)
    } // if (string_num_converions_ok == true)

    return time_sequence_checks_ok;
  }
}
