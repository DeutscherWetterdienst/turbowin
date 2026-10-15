package turbowin;

/** Latitude and longitude extracted from a validated Mintaka record. */
final class MintakaGpsFields {

  final String latitude;
  final String longitude;

  MintakaGpsFields(String latitude, String longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }
}
