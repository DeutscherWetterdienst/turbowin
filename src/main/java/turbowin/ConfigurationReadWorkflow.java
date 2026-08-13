package turbowin;

import static turbowin.main.*;

import java.io.File;
import java.io.IOException;
import javax.swing.SwingWorker;

/** Loads configuration asynchronously and finalizes application startup. */
final class ConfigurationReadWorkflow {

  private ConfigurationReadWorkflow() {}

  static String configurationDirectory(String logsDirectory, String dataDirectory) {
    if (logsDirectory != null && !logsDirectory.isEmpty()) {
      return logsDirectory;
    }
    if (dataDirectory != null && !dataDirectory.isEmpty()) {
      return dataDirectory;
    }
    return "";
  }

  static void read(main owner) {
    main.hulp_dir = configurationDirectory(logs_dir, data_dir);

    if (!main.hulp_dir.isEmpty()) {
      new SwingWorker<Void, Void>() {
        @Override
        protected Void doInBackground() throws Exception {
          String path = main.hulp_dir + File.separator + CONFIGURATION_FILE;
          try {
            ConfigurationFileStore.read(
                new File(path), configuratie_regels, MAX_AANTAL_CONFIGURATIEREGELS);
          } catch (IOException e) {
            // The configuration file may not exist on first use.
          }
          meta_data_from_configuration_regels_into_global_vars();
          return null;
        }

        @Override
        protected void done() {
          if (!theme_changed) {
            log_turbowin_system_message(
                "[GENERAL] started "
                    + APPLICATION_NAME
                    + " "
                    + application_mode
                    + " "
                    + TurboWinAppInfo.APPLICATION_VERSION);
          } else {
            log_turbowin_system_message(
                "[GENERAL] restarted main module (Theme changed)"
                    + APPLICATION_NAME
                    + " "
                    + application_mode
                    + " "
                    + TurboWinAppInfo.APPLICATION_VERSION);
          }

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
      }.execute();
    }
  }
}
