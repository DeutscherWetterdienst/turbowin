package turbowin;

/** Sends one prepared observation email through a concrete transport. */
interface ObservationEmailSender {

  int send(ObservationEmailRequest request) throws Exception;
}
