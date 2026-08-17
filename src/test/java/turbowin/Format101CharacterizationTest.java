package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
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
  public void preservesProcessExitStatus() throws Exception {
    Process process = new ProcessBuilder("sh", "-c", "exit 7").start();

    assertEquals(7, invokePrivate("waitForProcess", new Class<?>[] {Process.class}, process));
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
  public void writesFormat101InputWithFixedHeaderAndRepresentativeFields() throws Exception {
    String originalLogsDirectory = main.logs_dir;
    String originalYear = mydatetime.year;
    String originalMonth = mydatetime.MM_code;
    String originalDay = mydatetime.day;
    String originalHour = mydatetime.hour;

    try {
      File logsDirectory = temporaryFolder.newFolder("logs");
      File temporaryDirectory =
          new File(logsDirectory, main.FORMAT_101_ROOT_DIR + File.separator + "temp");
      assertTrue(temporaryDirectory.mkdirs());
      main.logs_dir = logsDirectory.getPath();
      mydatetime.year = "2026";
      mydatetime.MM_code = "10";
      mydatetime.day = "04";
      mydatetime.hour = "12";

      invokePrivate("write_input_for_101_compression");

      List<String> lines =
          Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("0", lines.get(0));
      assertEquals("1 101.0           format identifier", lines.get(1));
      assertEquals("1 1.0             call sign encryption indicator", lines.get(2));
      assertEquals("1 2026.0          year", lines.get(7));
      assertEquals("1 10.0            month", lines.get(8));
      assertEquals("1 4.0             day", lines.get(9));
      assertEquals("1 12.0            hour", lines.get(10));
    } finally {
      main.logs_dir = originalLogsDirectory;
      mydatetime.year = originalYear;
      mydatetime.MM_code = originalMonth;
      mydatetime.day = originalDay;
      mydatetime.hour = originalHour;
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

  @Test
  public void preservesFormat101SensorConversionsAndRounding() throws Exception {
    String originalLogsDirectory = main.logs_dir;
    String originalWindUnits = main.wind_units;
    String originalPressureMode = main.pressure_reading_msl_yes_no;
    String originalYear = mydatetime.year;
    String originalMonth = mydatetime.MM_code;
    String originalDay = mydatetime.day;
    String originalHour = mydatetime.hour;
    int originalWindSpeed = mywind.int_true_wind_speed;
    String originalRelativeWindSpeed = mywind.RWS_code;
    String originalAirTemperature = mytemp.air_temp;
    String originalWetBulbTemperature = mytemp.wet_bulb_temp;
    String originalSeaWaterTemperature = mytemp.sea_water_temp;
    double originalDewPoint = mytemp.double_dew_point;
    String originalBarometerPressure = mybarometer.pressure_reading_corrected;
    String originalPressure = mybarometer.pressure_msl_corrected;

    try {
      File logsDirectory = temporaryFolder.newFolder("logs");
      File temporaryDirectory =
          new File(logsDirectory, main.FORMAT_101_ROOT_DIR + File.separator + "temp");
      assertTrue(temporaryDirectory.mkdirs());
      main.logs_dir = logsDirectory.getPath();
      main.wind_units = main.KNOTS;
      main.pressure_reading_msl_yes_no = main.PRESSURE_READING_MSL_NO;
      mydatetime.year = "2026";
      mydatetime.MM_code = "10";
      mydatetime.day = "04";
      mydatetime.hour = "12";
      mywind.int_true_wind_speed = 10;
      mywind.RWS_code = "10";
      mytemp.air_temp = "20";
      mytemp.wet_bulb_temp = "15";
      mytemp.sea_water_temp = "18";
      mytemp.double_dew_point = 10.0;
      mybarometer.pressure_reading_corrected = "1000.00";
      mybarometer.pressure_msl_corrected = "1013.25";

      invokePrivate("write_input_for_101_compression");

      List<String> lines =
          Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("1 5.144           true wind speed [m/s]", lines.get(19));
      assertEquals("1 5.144           relative wind speed [m/s]", lines.get(21));
      assertEquals("1 293.15          air temperature [K]", lines.get(24));
      assertEquals("1 288.15          wet bulb temperature [K]", lines.get(25));
      assertEquals("1 283.15          dew point temperature [K]", lines.get(26));
      assertEquals("1 291.15          sea water temperature [K]", lines.get(28));
      assertEquals("1 100000.0        pressure at barometer height [Pa]", lines.get(14));
      assertEquals("1 101325.0        pressure at MSL [Pa]", lines.get(15));
    } finally {
      main.logs_dir = originalLogsDirectory;
      main.wind_units = originalWindUnits;
      main.pressure_reading_msl_yes_no = originalPressureMode;
      mydatetime.year = originalYear;
      mydatetime.MM_code = originalMonth;
      mydatetime.day = originalDay;
      mydatetime.hour = originalHour;
      mywind.int_true_wind_speed = originalWindSpeed;
      mywind.RWS_code = originalRelativeWindSpeed;
      mytemp.air_temp = originalAirTemperature;
      mytemp.wet_bulb_temp = originalWetBulbTemperature;
      mytemp.sea_water_temp = originalSeaWaterTemperature;
      mytemp.double_dew_point = originalDewPoint;
      mybarometer.pressure_reading_corrected = originalBarometerPressure;
      mybarometer.pressure_msl_corrected = originalPressure;
    }
  }

  @Test
  public void writesInvalidWindSpeedAsAnAbsentRecord() throws Exception {
    String originalLogsDirectory = main.logs_dir;
    String originalWindUnits = main.wind_units;
    String originalYear = mydatetime.year;
    String originalMonth = mydatetime.MM_code;
    String originalDay = mydatetime.day;
    String originalHour = mydatetime.hour;
    int originalWindSpeed = mywind.int_true_wind_speed;

    try {
      File logsDirectory = temporaryFolder.newFolder("logs");
      File temporaryDirectory =
          new File(logsDirectory, main.FORMAT_101_ROOT_DIR + File.separator + "temp");
      assertTrue(temporaryDirectory.mkdirs());
      main.logs_dir = logsDirectory.getPath();
      main.wind_units = main.KNOTS;
      mydatetime.year = "2026";
      mydatetime.MM_code = "10";
      mydatetime.day = "04";
      mydatetime.hour = "12";
      mywind.int_true_wind_speed = main.INVALID;

      invokePrivate("write_input_for_101_compression");

      List<String> lines =
          Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("0                 true wind speed [m/s]", lines.get(19));
    } finally {
      main.logs_dir = originalLogsDirectory;
      main.wind_units = originalWindUnits;
      mydatetime.year = originalYear;
      mydatetime.MM_code = originalMonth;
      mydatetime.day = originalDay;
      mydatetime.hour = originalHour;
      mywind.int_true_wind_speed = originalWindSpeed;
    }
  }

  @Test
  public void roundsKnotsToMetersPerSecondWithFormat101Precision() throws Exception {
    assertEquals(
        5.144,
        (Double)
            invokePrivate("convertKnotsToMetersPerSecond", new Class<?>[] {double.class}, 10.0),
        0.0);
  }

  @Test
  public void convertsCelsiusToKelvinWithFormat101Precision() throws Exception {
    assertEquals(
        293.15,
        (Double) invokePrivate("convertCelsiusToKelvin", new Class<?>[] {double.class}, 20.0),
        0.0);
  }

  @Test
  public void convertsHectopascalsToPascals() throws Exception {
    assertEquals(
        101325.0,
        (Double)
            invokePrivate("convertHectopascalsToPascals", new Class<?>[] {double.class}, 1013.25),
        0.0);
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
