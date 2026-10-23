package turbowin;

/** Updates the shared dew point and FM13 code fields from the current AWS temperature data. */
final class DewPointStateUpdater {

  private DewPointStateUpdater() {}

  static void updateFromAwsValues() {
    mytemp.double_dew_point =
        DewPointCalculator.calculate(
            mytemp.air_temp, mytemp.double_rv, mybarometer.pressure_msl_corrected);
    main_RS232_RS422.RS232_make_dew_point_APR_FM13_IMMT_ready();
  }
}
