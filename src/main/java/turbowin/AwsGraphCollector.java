package turbowin;

/** Stores parsed AWS graph values in the selected graph arrays. */
final class AwsGraphCollector {

  private final String[] primaryValues;
  private final String[] secondaryValues;
  private final String[] timestamps;

  AwsGraphCollector(String[] primaryValues, String[] secondaryValues, String[] timestamps) {
    this.primaryValues = primaryValues;
    this.secondaryValues = secondaryValues;
    this.timestamps = timestamps;
  }

  void add(AwsGraphRecord record, int index) {
    if (record.primaryValue != null) {
      primaryValues[index] = record.primaryValue;
      timestamps[index] = record.timestamp;
    }
    if (record.secondaryValue != null) {
      secondaryValues[index] = record.secondaryValue;
    }
  }
}
