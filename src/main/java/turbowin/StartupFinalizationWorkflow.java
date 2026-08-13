package turbowin;

import static turbowin.main.*;

/** Completes application startup after configuration has been loaded. */
final class StartupFinalizationWorkflow {

  private StartupFinalizationWorkflow() {}

  static String startupMessage(boolean themeChanged) {
    if (!themeChanged) {
      return "[GENERAL] started "
          + APPLICATION_NAME
          + " "
          + application_mode
          + " "
          + TurboWinAppInfo.APPLICATION_VERSION;
    }
    return "[GENERAL] restarted main module (Theme changed)"
        + APPLICATION_NAME
        + " "
        + application_mode
        + " "
        + TurboWinAppInfo.APPLICATION_VERSION;
  }

  static void complete(main owner) {
    log_turbowin_system_message(startupMessage(theme_changed));
    support_class.log_java_version();
    support_class.log_integrated_libraries();
    support_class.log_memory_statistics();
    log_turbowin_system_message(
        "[GENERAL] deleting old (> 3 months) " + APPLICATION_NAME + " system logs");
    delete_logs_turbowin_system();
    ID_fields_update();
    owner.check_meta_data();
    owner.create_popup_menu();
    owner.check_immt_size();
    owner.specific_connection_initComponents();
    owner.disable_graph_menu_items();
    disable_dashboard_and_maps_menu_items();
    disable_and_enable_output_menu_items();
    set_APR_toolbar();
    set_AWSR_toolbar();
  }
}
