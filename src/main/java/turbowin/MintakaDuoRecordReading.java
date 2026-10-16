package turbowin;

/** A parsed Mintaka Duo record together with its age at read time. */
final class MintakaDuoRecordReading {

  final MintakaDuoRecord record;
  final long ageMinutes;

  MintakaDuoRecordReading(MintakaDuoRecord record, long ageMinutes) {
    this.record = record;
    this.ageMinutes = ageMinutes;
  }

  boolean isWithin(long maximumAgeMinutes) {
    return ageMinutes <= maximumAgeMinutes;
  }
}
