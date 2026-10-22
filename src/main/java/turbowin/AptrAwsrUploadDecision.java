package turbowin;

/**
 * Selects the next APR/AWSR upload action without performing I/O.
 *
 * <p>The speed supplied to this decision is port-mode SOG over the last 10 minutes, in knots. In
 * port mode, a positive speed below 0.5 knots cancels the upload as low speed; a speed above 100
 * knots means that SOG is not available and also cancels the upload.
 */
final class AptrAwsrUploadDecision {

  enum Action {
    CANCEL_LOW_SPEED,
    CANCEL_UNAVAILABLE_SPEED,
    SEND_FORMAT101_SERVER,
    SEND_FM13_SERVER,
    SEND_EMAIL,
    UNKNOWN_FORMAT,
    UNKNOWN_SEND_METHOD
  }

  private AptrAwsrUploadDecision() {}

  static Action decide(
      boolean portMode,
      double speed,
      String sendMethod,
      String observationFormat,
      String uploadsMethod) {
    if (portMode && speed > 0.0 && speed < 0.5) {
      return Action.CANCEL_LOW_SPEED;
    }
    if (portMode && speed > 100.0) {
      return Action.CANCEL_UNAVAILABLE_SPEED;
    }

    if (sendMethod.equals(main.APTR_AWSR_SERVER)) {
      if (observationFormat.equals(main.FORMAT_101)
          || (observationFormat.equals(main.FORMAT_AWS)
              && uploadsMethod.equals(main.UPLOADS_VIA_TURBOWIN))) {
        return Action.SEND_FORMAT101_SERVER;
      }
      if (observationFormat.equals(main.FORMAT_FM13)) {
        return Action.SEND_FM13_SERVER;
      }
      return Action.UNKNOWN_FORMAT;
    }

    if (sendMethod.equals(main.APTR_AWSR_SMTP_HOST)
        || sendMethod.equals(main.APTR_AWSR_GMAIL)
        || sendMethod.equals(main.APTR_AWSR_YAHOO_MAIL)
        || sendMethod.equals(main.APTR_AWSR_CUSTOM_MAIL)) {
      return Action.SEND_EMAIL;
    }
    return Action.UNKNOWN_SEND_METHOD;
  }
}
