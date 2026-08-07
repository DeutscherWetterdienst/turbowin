package turbowin;

/** Runs email transport calls using the legacy virtual-thread execution policy. */
final class ObservationEmailTransportExecutor {

  private ObservationEmailTransportExecutor() {}

  static int execute(ObservationEmailSender sender, ObservationEmailRequest request)
      throws Exception {
    try (var executor = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()) {
      return executor.submit(() -> sender.send(request)).get();
    }
  }
}
