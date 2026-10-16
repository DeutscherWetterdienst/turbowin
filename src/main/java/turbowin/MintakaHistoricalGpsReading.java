package turbowin;

/** Coordinates extracted from a validated historical Mintaka record. */
final class MintakaHistoricalGpsReading {

  final String latitude;
  final String longitude;

  MintakaHistoricalGpsReading(String latitude, String longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }
}
