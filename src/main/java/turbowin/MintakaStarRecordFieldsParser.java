package turbowin;

/**
 * Parses common pressure and position fields from Mintaka Star and StarX records.
 *
 * <p>Star records use this layout:
 *
 * <pre>1029.97,1029.97,-0.90,7, 52 41.9535N,  6 14.1943E,0,0,19*1A201703152006</pre>
 *
 * <p>StarX adds the observation fields:
 *
 * <pre>1018.12,1018.13,0.00,0, 16 30.4429N, 88 21.9040W,0,2,7,29.4,85,27.4,26.7,60*18</pre>
 *
 * <p>Using zero-based field positions, field 0 is station pressure in mb, field 1 is sea-level
 * pressure in mb, field 2 is the three-hour pressure tendency, field 3 is the WMO tendency
 * characteristic code, field 4 is latitude, field 5 is longitude, field 6 is true course, field 7
 * is speed in knots, and field 8 is elevation in meters. StarX fields 9 through 12 are air
 * temperature, relative humidity (0–100), wet-bulb temperature, and dew point; field 13 is
 * observation age in seconds. Latitude and longitude use {@code ddd mm.mmmm[N|S]} and
 * {@code ddd mm.mmmm[E|W]} respectively. Non-StarX records use observation age {@code 0}.
 */
final class MintakaStarRecordFieldsParser {

  private MintakaStarRecordFieldsParser() {}

  static MintakaStarRecordFields parse(String record, boolean starX) {
    if (record == null) {
      return null;
    }
    int firstComma = record.indexOf(',');
    int secondComma = record.indexOf(',', firstComma + 1);
    int thirdComma = record.indexOf(',', secondComma + 1);
    int fourthComma = record.indexOf(',', thirdComma + 1);
    int fifthComma = record.indexOf(',', fourthComma + 1);
    int sixthComma = record.indexOf(',', fifthComma + 1);
    if (firstComma < 0
        || secondComma < 0
        || thirdComma < 0
        || fourthComma < 0
        || fifthComma < 0
        || sixthComma < 0) {
      return null;
    }

    MintakaStarXObservationFields starXFields =
        starX ? MintakaStarXObservationFieldsParser.parse(record) : null;
    if (starX && starXFields == null) {
      return null;
    }

    return new MintakaStarRecordFields(
        record.substring(0, firstComma),
        record.substring(secondComma + 1, thirdComma),
        record.substring(thirdComma + 1, fourthComma),
        record.substring(fourthComma + 1, fifthComma),
        record.substring(fifthComma + 1, sixthComma),
        starX ? starXFields.observationAge : "0");
  }
}
