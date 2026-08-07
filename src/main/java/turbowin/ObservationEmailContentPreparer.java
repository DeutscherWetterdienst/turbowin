package turbowin;

/** Prepares the subject and body shared by the observation email transports. */
final class ObservationEmailContentPreparer {

  private ObservationEmailContentPreparer() {}

  static Result prepare(
      String observationFormat,
      boolean uploadsViaTurbowin,
      String format101Mode,
      String format101Body,
      String fm13Body,
      String subject,
      String yearCode,
      String groupCode) {
    String emailSubject = subject;
    String emailBody = "null";

    boolean hasFormat101Observation =
        main.FORMAT_101.equals(observationFormat)
            || (main.FORMAT_AWS.equals(observationFormat) && uploadsViaTurbowin);

    if (hasFormat101Observation) {
      if (main.FORMAT_101_ATTACHEMENT.equals(format101Mode)) {
        emailBody = "see attachment";
      } else {
        emailBody = format101Body;
      }
    }

    if (main.FORMAT_FM13.equals(observationFormat)) {
      if (subject.contains("ddhhmm")) {
        emailSubject = subject.replaceAll("ddhhmm", yearCode + groupCode + "00");
      }
      emailBody = fm13Body;
    }

    boolean contentIsRequired =
        hasFormat101Observation || main.FORMAT_FM13.equals(observationFormat);
    return new Result(emailSubject, emailBody, !contentIsRequired || !emailBody.isEmpty());
  }

  static final class Result {
    private final String subject;
    private final String body;
    private final boolean nonEmpty;

    private Result(String subject, String body, boolean nonEmpty) {
      this.subject = subject;
      this.body = body;
      this.nonEmpty = nonEmpty;
    }

    String subject() {
      return subject;
    }

    String body() {
      return body;
    }

    boolean nonEmpty() {
      return nonEmpty;
    }
  }
}
