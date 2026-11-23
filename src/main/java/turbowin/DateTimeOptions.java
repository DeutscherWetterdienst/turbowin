package turbowin;

import java.util.stream.IntStream;

/** Values offered by the manual UTC date and time selector. */
final class DateTimeOptions {
  private DateTimeOptions() {}

  static String[][] all() {
    return new String[][] {
      numbers(2021, 2035),
      {
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December"
      },
      numbers(1, 31),
      numbers(0, 23)
    };
  }

  private static String[] numbers(int first, int last) {
    return IntStream.rangeClosed(first, last).mapToObj(Integer::toString).toArray(String[]::new);
  }
}
