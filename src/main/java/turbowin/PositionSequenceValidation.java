package turbowin;

/** Applies the legacy maximum-distance rules for position sequence checks. */
final class PositionSequenceValidation {

  private PositionSequenceValidation() {}

  static boolean exceedsAllowedDistance(long elapsedHours, int distance) {
    return (elapsedHours >= 0 && elapsedHours <= 6 && distance > 180)
        || (elapsedHours > 6 && elapsedHours <= 12 && distance > 360)
        || (elapsedHours > 12 && elapsedHours <= 18 && distance > 540)
        || (elapsedHours > 18 && elapsedHours <= 24 && distance > 720);
  }
}
