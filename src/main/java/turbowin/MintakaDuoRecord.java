package turbowin;

/** Parsed fields from a Mintaka Duo pressure record. */
final class MintakaDuoRecord {

  final String timestamp;
  final String pressure;
  final String pressureTendency;
  final String tendencyCharacteristic;

  MintakaDuoRecord(
      String timestamp, String pressure, String pressureTendency, String tendencyCharacteristic) {
    this.timestamp = timestamp;
    this.pressure = pressure;
    this.pressureTendency = pressureTendency;
    this.tendencyCharacteristic = tendencyCharacteristic;
  }
}
