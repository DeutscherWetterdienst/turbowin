package turbowin;

/** A validated air-temperature value for a Mintaka StarX graph. */
final class MintakaStarXAirTemperatureRecord {

  final String timestamp;
  final int minute;
  final String temperature;

  MintakaStarXAirTemperatureRecord(String timestamp, int minute, String temperature) {
    this.timestamp = timestamp;
    this.minute = minute;
    this.temperature = temperature;
  }
}
