package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.Arrays;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class Format101CharacterizationTest {

  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Test
  public void writesExpectedHostIdentificationRecord() throws Exception {
    String originalLogsDirectory = main.logs_dir;
    String originalImoNumber = main.imo_number;
    String originalShipName = main.ship_name;

    try {
      File logsDirectory = temporaryFolder.newFolder("logs");
      File configDirectory =
          new File(logsDirectory, main.FORMAT_101_ROOT_DIR + File.separator + "config");
      assertTrue(configDirectory.mkdirs());

      main.logs_dir = logsDirectory.getPath();
      main.imo_number = "1234567";
      main.ship_name = "Test Ship";

      invokePrivate("write_HC_identification_file", new Class<?>[] {String.class}, "TEST-ID");

      File identificationFile = new File(configDirectory, "HC_ident.txt");
      assertEquals(
          ";1234567;TEST-ID;1;Test Ship;INMC_SC;S-AWS-101" + System.lineSeparator(),
          Files.readString(identificationFile.toPath()));
    } finally {
      main.logs_dir = originalLogsDirectory;
      main.imo_number = originalImoNumber;
      main.ship_name = originalShipName;
    }
  }

  @Test
  public void buildsFormat101PathsBelowTheConfiguredLogsDirectory() throws Exception {
    String originalLogsDirectory = main.logs_dir;

    try {
      File logsDirectory = temporaryFolder.newFolder("logs");
      main.logs_dir = logsDirectory.getPath();

      assertEquals(
          new File(logsDirectory, "format_101/config/HC_ident.txt").getPath(),
          invokePrivate(
              "format101Path",
              new Class<?>[] {String[].class},
              (Object) new String[] {main.FORMAT_101_ROOT_DIR, "config", "HC_ident.txt"}));
    } finally {
      main.logs_dir = originalLogsDirectory;
    }
  }

  @Test
  public void configuresFormat101ProcessOutputAndWorkingDirectory() throws Exception {
    String originalLogsDirectory = main.logs_dir;

    try {
      File logsDirectory = temporaryFolder.newFolder("logs");
      File logFile = new File(logsDirectory, "process.log");
      main.logs_dir = logsDirectory.getPath();
      ProcessBuilder processBuilder = new ProcessBuilder("format101");

      invokePrivate(
          "configureProcessBuilder",
          new Class<?>[] {ProcessBuilder.class, File.class},
          processBuilder,
          logFile);

      assertTrue(processBuilder.redirectErrorStream());
      assertEquals(logFile, processBuilder.redirectOutput().file());
      assertEquals(new File(logsDirectory, main.FORMAT_101_ROOT_DIR), processBuilder.directory());
    } finally {
      main.logs_dir = originalLogsDirectory;
    }
  }

  @Test
  public void preservesCompressionCommandArguments() throws Exception {
    String originalImoNumber = main.imo_number;

    try {
      main.imo_number = "1234567";

      assertEquals(
          Arrays.asList(
              "/tmp/format101-compress",
              "/tmp/bufr-table.csv",
              "temp/format_101.txt",
              "TEST-ID",
              "S-AWS-101"),
          invokePrivate(
              "buildCompressionArguments",
              new Class<?>[] {File.class, String.class, String.class},
              new File("/tmp/format101-compress"),
              "/tmp/bufr-table.csv",
              "TEST-ID"));
    } finally {
      main.imo_number = originalImoNumber;
    }
  }

  @Test
  public void preservesDecompressionCommandArguments() throws Exception {
    String originalImoNumber = main.imo_number;

    try {
      main.imo_number = "1234567";

      assertEquals(
          Arrays.asList(
              "/tmp/format101-decompress",
              "-n",
              "1234567",
              "-f",
              "/tmp/compressed.txt",
              "-i",
              "HC_ident.txt",
              "-r",
              "config",
              "-h",
              "-l",
              "-o",
              "/tmp/decompressed.txt"),
          invokePrivate(
              "buildDecompressionArguments",
              new Class<?>[] {File.class, String.class, String.class},
              new File("/tmp/format101-decompress"),
              "/tmp/compressed.txt",
              "/tmp/decompressed.txt"));
    } finally {
      main.imo_number = originalImoNumber;
    }
  }

  @Test
  public void clearsFilesFromExistingFormat101TemporaryDirectory() throws Exception {
    String originalLogsDirectory = main.logs_dir;

    try {
      File logsDirectory = temporaryFolder.newFolder("logs");
      File temporaryDirectory =
          new File(
              logsDirectory, main.FORMAT_101_ROOT_DIR + File.separator + main.FORMAT_101_TEMP_DIR);
      assertTrue(temporaryDirectory.mkdirs());
      File temporaryFile = new File(temporaryDirectory, "stale.txt");
      assertTrue(temporaryFile.createNewFile());
      File temporaryDirectoryEntry = new File(temporaryDirectory, "stale-directory");
      assertTrue(temporaryDirectoryEntry.mkdir());

      main.logs_dir = logsDirectory.getPath();

      assertEquals(0, invokePrivate("check_and_clear_format_101_temp_folder"));
      assertFalse(temporaryFile.exists());
      assertFalse(temporaryDirectoryEntry.exists());
    } finally {
      main.logs_dir = originalLogsDirectory;
    }
  }

  @Test
  public void reportsMissingFormat101TemporaryDirectory() throws Exception {
    String originalLogsDirectory = main.logs_dir;

    try {
      main.logs_dir = temporaryFolder.newFolder("logs").getPath();

      assertEquals(1, invokePrivate("check_and_clear_format_101_temp_folder"));
    } finally {
      main.logs_dir = originalLogsDirectory;
    }
  }

  private static Object invokePrivate(String methodName, Class<?>[] parameterTypes, Object... args)
      throws Exception {
    Method method = FORMAT_101.class.getDeclaredMethod(methodName, parameterTypes);
    method.setAccessible(true);
    return method.invoke(new FORMAT_101(), args);
  }

  private static Object invokePrivate(String methodName) throws Exception {
    return invokePrivate(methodName, new Class<?>[0]);
  }
}
