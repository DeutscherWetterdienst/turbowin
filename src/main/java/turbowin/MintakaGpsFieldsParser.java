package turbowin;

/** Extracts the latitude and longitude fields from a validated Mintaka record. */
final class MintakaGpsFieldsParser {

  private MintakaGpsFieldsParser() {}

  static MintakaGpsFields parse(String record) {
    int pos1 = record.indexOf(",", 0);
    int pos2 = record.indexOf(",", pos1 + 1);
    int pos3 = record.indexOf(",", pos2 + 1);
    int pos4 = record.indexOf(",", pos3 + 1);
    int pos5 = record.indexOf(",", pos4 + 1);
    int pos6 = record.indexOf(",", pos5 + 1);

    return new MintakaGpsFields(record.substring(pos4 + 1, pos5), record.substring(pos5 + 1, pos6));
  }
}
