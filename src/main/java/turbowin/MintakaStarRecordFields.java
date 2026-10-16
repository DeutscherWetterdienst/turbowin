package turbowin;

/** Common fields consumed from a Mintaka Star or StarX record. */
final class MintakaStarRecordFields {

  final String pressure;
  final String pressureTendency;
  final String tendencyCharacteristic;
  final String latitude;
  final String longitude;
  final String observationAge;

  MintakaStarRecordFields(
      String pressure,
      String pressureTendency,
      String tendencyCharacteristic,
      String latitude,
      String longitude,
      String observationAge) {
    this.pressure = pressure;
    this.pressureTendency = pressureTendency;
    this.tendencyCharacteristic = tendencyCharacteristic;
    this.latitude = latitude;
    this.longitude = longitude;
    this.observationAge = observationAge;
  }
}
