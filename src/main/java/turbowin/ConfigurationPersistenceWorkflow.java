package turbowin;

import static turbowin.main.*;

import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/** Owns asynchronous persistence and loading of the application configuration. */
final class ConfigurationPersistenceWorkflow {

  private ConfigurationPersistenceWorkflow() {}

  static String configurationDirectory(String logsDirectory, String dataDirectory) {
    if (logsDirectory != null && !logsDirectory.isEmpty()) {
      return logsDirectory;
    }
    if (dataDirectory != null && !dataDirectory.isEmpty()) {
      return dataDirectory;
    }
    return "";
  }

  /**
   * Writes configuration.txt as a backup for the muffin/persistent configuration in both the
   * system-defined data_dir and the user-defined logs_dir.
   */
  static void write() {
    fill_configuratie_array();

    if ((data_dir != null) && (data_dir.compareTo("") != 0)) {
      new SwingWorker<Void, Void>() {
        @Override
        protected Void doInBackground() throws Exception {
          String path = data_dir + java.io.File.separator + CONFIGURATION_FILE;
          try {
            ConfigurationFileStore.write(
                new File(path), configuratie_regels, MAX_AANTAL_CONFIGURATIEREGELS);
          } catch (IOException ex) {
            System.out.println("--- Function schrijf_configuratie_regels(): " + ex);
          }
          return null;
        }
      }.execute();
    }

    if ((logs_dir != null) && (logs_dir.compareTo("") != 0)) {
      new SwingWorker<Void, Void>() {
        @Override
        protected Void doInBackground() throws Exception {
          String path = logs_dir + java.io.File.separator + CONFIGURATION_FILE;
          try {
            ConfigurationFileStore.write(
                new File(path), configuratie_regels, MAX_AANTAL_CONFIGURATIEREGELS);
          } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                null,
                "unable to write to: " + path,
                APPLICATION_NAME + " error",
                JOptionPane.WARNING_MESSAGE);
          }
          return null;
        }
      }.execute();
    }
  }

  /**
   * Reads configuration.txt when muffin/persistent configuration reading is unavailable. The
   * user-defined logs_dir is preferred in online mode (and is a fixed offline subdirectory), with
   * the system-defined data_dir as the fallback.
   */
  static void read(main owner) {
    main.hulp_dir = configurationDirectory(logs_dir, data_dir);

    if ((main.hulp_dir != null) && (main.hulp_dir.compareTo("") != 0)) {
      new SwingWorker<Void, Void>() {
        @Override
        protected Void doInBackground() throws Exception {
          String path = main.hulp_dir + java.io.File.separator + CONFIGURATION_FILE;
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
