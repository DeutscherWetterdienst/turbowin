package turbowin;

/** A parsed fixed-width HMP155 air-temperature record. */
final class Hmp155AirTemperatureRecord {

  final String timestamp;
  final int minute;
  final String temperature;
  final boolean temperatureOutsideExpectedRange;

  Hmp155AirTemperatureRecord(
      String timestamp, int minute, String temperature, boolean temperatureOutsideExpectedRange) {
    this.timestamp = timestamp;
    this.minute = minute;
    this.temperature = temperature;
    this.temperatureOutsideExpectedRange = temperatureOutsideExpectedRange;
  }
}
