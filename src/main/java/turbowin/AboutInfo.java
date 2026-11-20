package turbowin;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;

/** Snapshot of the application and system information shown in the About dialog. */
record AboutInfo(String title, List<String> details) {
  static AboutInfo capture() {
    Runtime runtime = Runtime.getRuntime();
    long used = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
    long designated = runtime.maxMemory() / 1024 / 1024;
    String distribution = SystemInfo.linuxDistribution();
    String desktop = SystemInfo.desktopEnvironment();
    String linuxDetails =
        distribution.isEmpty() && desktop.isEmpty()
            ? ""
            : "; Linux: "
                + (distribution.isEmpty() ? "distribution unknown" : distribution)
                + (desktop.isEmpty() ? "" : "; desktop " + desktop);
    return new AboutInfo(
        main.APPLICATION_NAME + " " + TurboWinAppInfo.APPLICATION_VERSION,
        List.of(
            "operating system: " + SystemInfo.operatingSystem(),
            "app dir: " + System.getProperty("app.dir"),
            "logs dir: " + main.logs_dir,
            "java: " + SystemInfo.javaRuntime(),
            "memory: designated " + designated + " MB; used " + used + " MB",
            "default charset: " + Charset.defaultCharset(),
            "locale default: " + Locale.getDefault() + linuxDetails,
            "libraries-1: " + main.APPLICATION_MET_MODULES,
            "libraries-2: " + main_support.print_libraries_name_and_version()));
  }
}
