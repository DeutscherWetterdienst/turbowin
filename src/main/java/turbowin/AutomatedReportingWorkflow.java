package turbowin;

/** Shared state finalization for automated reporting toolbar changes. */
final class AutomatedReportingWorkflow {

  private AutomatedReportingWorkflow() {}

  static void finishToggle() {
    main.Reset_all_meteo_parameters();
    main.disable_and_enable_output_menu_items();
    main.disable_dashboard_and_maps_menu_items();
    main.schrijf_configuratie_regels();
  }
}
