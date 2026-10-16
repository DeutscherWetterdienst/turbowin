package turbowin;

/** Fields extracted from a validated Mintaka StarX observation record. */
final class MintakaStarXObservationFields {

  final String latitude;
  final String longitude;
  final String airTemperature;
  final String relativeHumidity;
  final String wetBulbTemperature;
  final String dewPoint;
  final String observationAge;

  MintakaStarXObservationFields(
      String latitude,
      String longitude,
      String airTemperature,
      String relativeHumidity,
      String wetBulbTemperature,
      String dewPoint,
      String observationAge) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.airTemperature = airTemperature;
    this.relativeHumidity = relativeHumidity;
    this.wetBulbTemperature = wetBulbTemperature;
    this.dewPoint = dewPoint;
    this.observationAge = observationAge;
  }
}
