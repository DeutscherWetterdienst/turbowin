package turbowin;

import java.util.Calendar;

/** Provides the calendar calculations used by the observation sequence check. */
final class ObservationTimeSequenceValidation {

  private ObservationTimeSequenceValidation() {}

  static boolean isNotLater(Calendar currentObservation, Calendar previousObservation) {
    return currentObservation.compareTo(previousObservation) <= 0;
  }

  static long elapsedWholeHours(Calendar currentObservation, Calendar previousObservation) {
    return (currentObservation.getTimeInMillis() - previousObservation.getTimeInMillis()) / 3600000;
  }
}
