package turbowin;

/** Refreshes the observation fields after the underlying observation state is reset. */
final class ObservationScreenResetWorkflow {

  private ObservationScreenResetWorkflow() {}

  static void refresh() {
    main.date_time_fields_update();
    main.visibility_fields_update();
    main.barometer_fields_update();
    main.barograph_fields_update();
    main.cloud_cover_fields_update();
    main.clouds_high_fields_update();
    main.clouds_low_fields_update();
    main.clouds_middle_fields_update();
    main.ice_fields_update();
    main.icing_fields_update();
    main.observer_field_update();
    main.past_weather_fields_update();
    main.position_fields_update();
    main.present_weather_fields_update();
    main.temperatures_fields_update();
    main.waves_fields_update();
    main.wind_fields_update();

    if (main.dashboard_form_APR_radar != null) {
      DASHBOARD_view_APR_radar.reset_APR_wind_variables();
    }
  }
}
