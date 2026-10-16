package turbowin;

/**
 * Extracts observation fields from a validated Mintaka StarX record.
 *
 * <p>StarX records use the following format (the first part is the same as Star):
 *
 * <pre>
 * station pressure (mb), sea-level pressure (mb), 3-hour pressure tendency,
 * WMO tendency characteristic code, latitude, longitude, course, speed,
 * elevation, temperature, relative humidity, wet-bulb temperature, dew point,
 * observation age*checksum
 * 1009.73,1007.73,0.00,0, 52 41.9491N,  6 14.1802E,0,1,7,19.5,65,15.4,12.8,58*16
 * </pre>
 *
 * <p>Using zero-based field positions, latitude is 4 and longitude is 5; course is 6, speed is 7,
 * elevation is 8, air temperature is 9, relative humidity is 10, wet-bulb temperature is 11, dew
 * point is 12, and observation age is 13. Latitude is {@code ddd mm.mmmm[N|S]}, longitude is
 * {@code ddd mm.mmmm[E|W]}, course is true course, speed is in knots, elevation is in meters,
 * relative humidity is 0–100, temperatures are in degrees Celsius, and observation age is in
 * seconds.
 */
final class MintakaStarXObservationFieldsParser {

  private MintakaStarXObservationFieldsParser() {}

  static MintakaStarXObservationFields parse(String record) {
    int[] commaPositions = new int[13];
    int previousPosition = -1;
    for (int i = 0; i < commaPositions.length; i++) {
      commaPositions[i] = record.indexOf(',', previousPosition + 1);
      if (commaPositions[i] < 0) {
        return null;
      }
      previousPosition = commaPositions[i];
    }
    int starPosition = record.indexOf('*', commaPositions[12] + 1);
    if (starPosition < 0) {
      return null;
    }

    return new MintakaStarXObservationFields(
        field(record, commaPositions, 4),
        field(record, commaPositions, 5),
        field(record, commaPositions, 9),
        field(record, commaPositions, 10),
        field(record, commaPositions, 11),
        field(record, commaPositions, 12),
        record.substring(commaPositions[12] + 1, starPosition));
  }

  private static String field(String record, int[] commaPositions, int fieldNumber) {
    return record.substring(commaPositions[fieldNumber - 1] + 1, commaPositions[fieldNumber]);
  }
}
