package turbowin;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Formats numeric values used in the EUCAWS message. */
final class AwsNumericFormatter {

  private AwsNumericFormatter() {}

  static double roundToOneDecimal(double value) {
    return new BigDecimal(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
  }
}
