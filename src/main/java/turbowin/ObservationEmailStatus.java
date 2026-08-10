package turbowin;

/** Stable status codes exchanged by the observation email workflows. */
final class ObservationEmailStatus {

  static final int PYTHON_MODULE_UNAVAILABLE = 1000;
  static final int EMPTY_OBSERVATION = 1001;
  static final int INVALID_MODE = 1002;

  private ObservationEmailStatus() {}
}
