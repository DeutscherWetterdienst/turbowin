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
  public void buildsFormat101PathsBelowTheConfiguredLogsDirectory() throws Exception {
    String originalLogsDirectory = main.logs_dir;

    try {
      File logsDirectory = temporaryFolder.newFolder("logs");
      main.logs_dir = logsDirectory.getPath();

      assertEquals(
          new File(logsDirectory, "format_101/config/S-AWS-101_modl_pilote.csv").getPath(),
          invokePrivate(
              "format101Path",
              new Class<?>[] {String[].class},
              (Object)
                  new String[] {main.FORMAT_101_ROOT_DIR, "config", "S-AWS-101_modl_pilote.csv"}));
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
  public void writesFormat101InputWithFixedHeaderAndRepresentativeFields() throws Exception {
    String originalLogsDirectory = main.logs_dir;
    String originalYear = DateTimeModel.year;
    String originalMonth = DateTimeModel.MM_code;
    String originalDay = DateTimeModel.day;
    String originalHour = DateTimeModel.hour;

    try {
      File logsDirectory = temporaryFolder.newFolder("logs");
      File temporaryDirectory =
          new File(logsDirectory, main.FORMAT_101_ROOT_DIR + File.separator + "temp");
      assertTrue(temporaryDirectory.mkdirs());
      main.logs_dir = logsDirectory.getPath();
      DateTimeModel.year = "2026";
      DateTimeModel.MM_code = "10";
      DateTimeModel.day = "04";
      DateTimeModel.hour = "12";

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
      DateTimeModel.year = originalYear;
      DateTimeModel.MM_code = originalMonth;
      DateTimeModel.day = originalDay;
      DateTimeModel.hour = originalHour;
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
    String originalYear = DateTimeModel.year;
    String originalMonth = DateTimeModel.MM_code;
    String originalDay = DateTimeModel.day;
    String originalHour = DateTimeModel.hour;
    int originalWindSpeed = WindModel.int_true_wind_speed;
    String originalShipHeading = WindModel.ship_heading;
    String originalRelativeWindDirection = WindModel.RWD_code;
    String originalRelativeWindSpeed = WindModel.RWS_code;
    String originalAirTemperature = TemperatureModel.air_temp;
    String originalWetBulbTemperature = TemperatureModel.wet_bulb_temp;
    String originalSeaWaterTemperature = TemperatureModel.sea_water_temp;
    double originalDewPoint = TemperatureModel.double_dew_point;
    String originalBarometerPressure = BarometerModel.pressure_reading_corrected;
    String originalPressure = BarometerModel.pressure_msl_corrected;

    try {
      File logsDirectory = temporaryFolder.newFolder("logs");
      File temporaryDirectory =
          new File(logsDirectory, main.FORMAT_101_ROOT_DIR + File.separator + "temp");
      assertTrue(temporaryDirectory.mkdirs());
      main.logs_dir = logsDirectory.getPath();
      main.wind_units = main.KNOTS;
      main.pressure_reading_msl_yes_no = main.PRESSURE_READING_MSL_NO;
      DateTimeModel.year = "2026";
      DateTimeModel.MM_code = "10";
      DateTimeModel.day = "04";
      DateTimeModel.hour = "12";
      WindModel.int_true_wind_speed = 10;
      WindModel.ship_heading = "180";
      WindModel.RWD_code = "90";
      WindModel.RWS_code = "10";
      TemperatureModel.air_temp = "20";
      TemperatureModel.wet_bulb_temp = "15";
      TemperatureModel.sea_water_temp = "18";
      TemperatureModel.double_dew_point = 10.0;
      BarometerModel.pressure_reading_corrected = "1000.00";
      BarometerModel.pressure_msl_corrected = "1013.25";

      invokePrivate("write_input_for_101_compression");

      List<String> lines =
          Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("1 5.144           true wind speed [m/s]", lines.get(19));
      assertEquals("1 180.0           heading [degrees]", lines.get(5));
      assertEquals("1 5.144           relative wind speed [m/s]", lines.get(21));
      assertEquals("1 293.15          air temperature [K]", lines.get(24));
      assertEquals("1 288.15          wet bulb temperature [K]", lines.get(25));
      assertEquals("1 283.15          dew point temperature [K]", lines.get(26));
      assertEquals("1 291.15          sea water temperature [K]", lines.get(28));
      assertEquals("1 100000.0        pressure at barometer height [Pa]", lines.get(14));
      assertEquals("1 101325.0        pressure at MSL [Pa]", lines.get(15));

      WindModel.ship_heading = "0";
      invokePrivate("write_input_for_101_compression");
      lines = Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("0                 heading [degrees]", lines.get(5));

      WindModel.RWD_code = "999";
      WindModel.RWS_code = "///";
      invokePrivate("write_input_for_101_compression");
      lines = Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("1 0.0             relative wind direction [degrees]", lines.get(20));
      assertEquals("0                 relative wind speed [m/s]", lines.get(21));

      WindModel.RWD_code = "///";
      WindModel.RWS_code = "invalid";
      invokePrivate("write_input_for_101_compression");
      lines = Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("0                 relative wind direction [degrees]", lines.get(20));
      assertEquals("0                 relative wind speed [m/s]", lines.get(21));

      TemperatureModel.air_temp = "invalid";
      TemperatureModel.wet_bulb_temp = "";
      TemperatureModel.sea_water_temp = "invalid";
      invokePrivate("write_input_for_101_compression");
      lines = Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("0                 air temperature [K]", lines.get(24));
      assertEquals("0                 wet bulb temperature [K]", lines.get(25));
      assertEquals("0                 sea water temperature [K]", lines.get(28));

      BarometerModel.pressure_reading_corrected = "799";
      BarometerModel.pressure_msl_corrected = "1100";
      invokePrivate("write_input_for_101_compression");
      lines = Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("0                 pressure at barometer height [Pa]", lines.get(14));
      assertEquals("0                 pressure at MSL [Pa]", lines.get(15));

      BarometerModel.pressure_reading_corrected = "invalid";
      BarometerModel.pressure_msl_corrected = "";
      invokePrivate("write_input_for_101_compression");
      lines = Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("0                 pressure at barometer height [Pa]", lines.get(14));
      assertEquals("0                 pressure at MSL [Pa]", lines.get(15));
    } finally {
      main.logs_dir = originalLogsDirectory;
      main.wind_units = originalWindUnits;
      main.pressure_reading_msl_yes_no = originalPressureMode;
      DateTimeModel.year = originalYear;
      DateTimeModel.MM_code = originalMonth;
      DateTimeModel.day = originalDay;
      DateTimeModel.hour = originalHour;
      WindModel.int_true_wind_speed = originalWindSpeed;
      WindModel.ship_heading = originalShipHeading;
      WindModel.RWD_code = originalRelativeWindDirection;
      WindModel.RWS_code = originalRelativeWindSpeed;
      TemperatureModel.air_temp = originalAirTemperature;
      TemperatureModel.wet_bulb_temp = originalWetBulbTemperature;
      TemperatureModel.sea_water_temp = originalSeaWaterTemperature;
      TemperatureModel.double_dew_point = originalDewPoint;
      BarometerModel.pressure_reading_corrected = originalBarometerPressure;
      BarometerModel.pressure_msl_corrected = originalPressure;
    }
  }

  @Test
  public void writesInvalidWindSpeedAsAnAbsentRecord() throws Exception {
    String originalLogsDirectory = main.logs_dir;
    String originalWindUnits = main.wind_units;
    String originalYear = DateTimeModel.year;
    String originalMonth = DateTimeModel.MM_code;
    String originalDay = DateTimeModel.day;
    String originalHour = DateTimeModel.hour;
    int originalWindSpeed = WindModel.int_true_wind_speed;

    try {
      File logsDirectory = temporaryFolder.newFolder("logs");
      File temporaryDirectory =
          new File(logsDirectory, main.FORMAT_101_ROOT_DIR + File.separator + "temp");
      assertTrue(temporaryDirectory.mkdirs());
      main.logs_dir = logsDirectory.getPath();
      main.wind_units = main.KNOTS;
      DateTimeModel.year = "2026";
      DateTimeModel.MM_code = "10";
      DateTimeModel.day = "04";
      DateTimeModel.hour = "12";
      WindModel.int_true_wind_speed = main.INVALID;

      invokePrivate("write_input_for_101_compression");

      List<String> lines =
          Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("0                 true wind speed [m/s]", lines.get(19));
    } finally {
      main.logs_dir = originalLogsDirectory;
      main.wind_units = originalWindUnits;
      DateTimeModel.year = originalYear;
      DateTimeModel.MM_code = originalMonth;
      DateTimeModel.day = originalDay;
      DateTimeModel.hour = originalHour;
      WindModel.int_true_wind_speed = originalWindSpeed;
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
  public void preservesLegacyShipSpeedConversionPrecision() throws Exception {
    assertEquals(
        4.116,
        (Double)
            invokePrivate(
                "convertLegacyShipSpeedToMetersPerSecond", new Class<?>[] {double.class}, 8.0),
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

  @Test
  public void scalesAndSignsPressureChanges() throws Exception {
    assertEquals(
        123.4,
        (Double)
            invokePrivate(
                "convertPressureChangeToPascals",
                new Class<?>[] {double.class, int.class},
                1.234,
                2),
        0.0);
    assertEquals(
        -123.4,
        (Double)
            invokePrivate(
                "convertPressureChangeToPascals",
                new Class<?>[] {double.class, int.class},
                1.234,
                5),
        0.0);
  }

  @Test
  public void convertsDegreesAndMinutesWithThreeDecimalPlaces() throws Exception {
    assertEquals(
        12.5,
        (Double)
            invokePrivate(
                "convertCoordinateToDegrees",
                new Class<?>[] {int.class, int.class, boolean.class},
                12,
                30,
                false),
        0.0);
    assertEquals(
        -45.5,
        (Double)
            invokePrivate(
                "convertCoordinateToDegrees",
                new Class<?>[] {int.class, int.class, boolean.class},
                45,
                30,
                true),
        0.0);
  }

  @Test
  public void mapsDirectionCodesToDegreesAndRejectsUnknownCodes() throws Exception {
    assertEquals(0, invokePrivate("directionCodeToDegrees", new Class<?>[] {String.class}, "0"));
    assertEquals(270, invokePrivate("directionCodeToDegrees", new Class<?>[] {String.class}, "6"));
    assertEquals(null, invokePrivate("directionCodeToDegrees", new Class<?>[] {String.class}, "9"));
  }

  @Test
  public void appliesCloudTypeOffsets() throws Exception {
    assertEquals(
        32.0,
        (Double)
            invokePrivate(
                "convertCloudTypeCode", new Class<?>[] {String.class, double.class}, "2", 30.0),
        0.0);
    assertEquals(
        27.0,
        (Double)
            invokePrivate(
                "convertCloudTypeCode", new Class<?>[] {String.class, double.class}, "7", 20.0),
        0.0);
  }

  @Test
  public void mapsIceCauseCodes() throws Exception {
    assertEquals(8, invokePrivate("iceCauseCodeToBufrCode", new Class<?>[] {String.class}, "1"));
    assertEquals(14, invokePrivate("iceCauseCodeToBufrCode", new Class<?>[] {String.class}, "14"));
    assertEquals(null, invokePrivate("iceCauseCodeToBufrCode", new Class<?>[] {String.class}, "9"));
  }

  @Test
  public void parsesNumericCodes() throws Exception {
    assertEquals(
        20.0, (Double) invokePrivate("parseNumericCode", new Class<?>[] {String.class}, "20"), 0.0);
    assertEquals(
        6.0, (Double) invokePrivate("parseNumericCode", new Class<?>[] {String.class}, "6"), 0.0);
  }

  @Test
  public void mapsLegacySpeedCodesToKnots() throws Exception {
    assertEquals(0, invokePrivate("legacySpeedCodeToKnots", new Class<?>[] {String.class}, "0"));
    assertEquals(8, invokePrivate("legacySpeedCodeToKnots", new Class<?>[] {String.class}, "2"));
    assertEquals(42, invokePrivate("legacySpeedCodeToKnots", new Class<?>[] {String.class}, "9"));
    assertEquals(
        Integer.MAX_VALUE,
        invokePrivate("legacySpeedCodeToKnots", new Class<?>[] {String.class}, "invalid"));
  }

  @Test
  public void preservesLegacyShipSpeedConversionsAndMissingValues() throws Exception {
    String originalLogsDirectory = main.logs_dir;
    String originalYear = DateTimeModel.year;
    String originalMonth = DateTimeModel.MM_code;
    String originalDay = DateTimeModel.day;
    String originalHour = DateTimeModel.hour;
    double originalAprSpeed = PositionModel.SOG_APR;
    String originalSpeedCode = PositionModel.vs_code;

    try {
      File logsDirectory = temporaryFolder.newFolder("logs");
      File temporaryDirectory =
          new File(logsDirectory, main.FORMAT_101_ROOT_DIR + File.separator + "temp");
      assertTrue(temporaryDirectory.mkdirs());
      main.logs_dir = logsDirectory.getPath();
      DateTimeModel.year = "2026";
      DateTimeModel.MM_code = "10";
      DateTimeModel.day = "04";
      DateTimeModel.hour = "12";
      PositionModel.SOG_APR = 10.0;
      PositionModel.vs_code = "";

      invokePrivate("write_input_for_101_compression");
      List<String> lines =
          Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("1 5.144           SOG past 10 minutes [m/s]", lines.get(4));

      PositionModel.SOG_APR = Double.MAX_VALUE;
      PositionModel.vs_code = "2";
      invokePrivate("write_input_for_101_compression");
      lines = Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("1 4.116           SOG past 10 minutes [m/s]", lines.get(4));

      PositionModel.vs_code = "";
      invokePrivate("write_input_for_101_compression");
      lines = Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("0                 SOG past 10 minutes [m/s]", lines.get(4));
    } finally {
      main.logs_dir = originalLogsDirectory;
      DateTimeModel.year = originalYear;
      DateTimeModel.MM_code = originalMonth;
      DateTimeModel.day = originalDay;
      DateTimeModel.hour = originalHour;
      PositionModel.SOG_APR = originalAprSpeed;
      PositionModel.vs_code = originalSpeedCode;
    }
  }

  @Test
  public void preservesCloudAmountConversionsAndMissingValues() throws Exception {
    String originalLogsDirectory = main.logs_dir;
    String originalYear = DateTimeModel.year;
    String originalMonth = DateTimeModel.MM_code;
    String originalDay = DateTimeModel.day;
    String originalHour = DateTimeModel.hour;
    String originalCloudCover = CloudCoverState.N_code;
    String originalLowCloudAmount = CloudCoverState.Nh_code;
    String originalCloudBase = CloudCoverState.h_code;

    try {
      File logsDirectory = temporaryFolder.newFolder("logs");
      File temporaryDirectory =
          new File(logsDirectory, main.FORMAT_101_ROOT_DIR + File.separator + "temp");
      assertTrue(temporaryDirectory.mkdirs());
      main.logs_dir = logsDirectory.getPath();
      DateTimeModel.year = "2026";
      DateTimeModel.MM_code = "10";
      DateTimeModel.day = "04";
      DateTimeModel.hour = "12";
      CloudCoverState.N_code = "5";
      CloudCoverState.Nh_code = "3";
      CloudCoverState.h_code = "600";

      invokePrivate("write_input_for_101_compression");
      List<String> lines =
          Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("1 5.0             total cloud cover [code]", lines.get(34));
      assertEquals("1 3.0             cloud amount (low) [code]", lines.get(35));
      assertEquals("1 600.0           height of base of lowest clouds [code]", lines.get(39));

      CloudCoverState.N_code = "/";
      CloudCoverState.Nh_code = "/";
      CloudCoverState.h_code = "/";
      invokePrivate("write_input_for_101_compression");
      lines = Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("0                 total cloud cover [code]", lines.get(34));
      assertEquals("0                 cloud amount (low) [code]", lines.get(35));
      assertEquals("0                 height of base of lowest clouds [code]", lines.get(39));
    } finally {
      main.logs_dir = originalLogsDirectory;
      DateTimeModel.year = originalYear;
      DateTimeModel.MM_code = originalMonth;
      DateTimeModel.day = originalDay;
      DateTimeModel.hour = originalHour;
      CloudCoverState.N_code = originalCloudCover;
      CloudCoverState.Nh_code = originalLowCloudAmount;
      CloudCoverState.h_code = originalCloudBase;
    }
  }

  @Test
  public void preservesCloudTypeOffsetsAndMiddleCloudSuffixHandling() throws Exception {
    String originalLogsDirectory = main.logs_dir;
    String originalYear = DateTimeModel.year;
    String originalMonth = DateTimeModel.MM_code;
    String originalDay = DateTimeModel.day;
    String originalHour = DateTimeModel.hour;
    String originalLowCloudType = LowCloudState.cl_code;
    String originalMiddleCloudType = MiddleCloudState.cm_code;
    String originalHighCloudType = HighCloudState.ch_code;

    try {
      File logsDirectory = temporaryFolder.newFolder("logs");
      File temporaryDirectory =
          new File(logsDirectory, main.FORMAT_101_ROOT_DIR + File.separator + "temp");
      assertTrue(temporaryDirectory.mkdirs());
      main.logs_dir = logsDirectory.getPath();
      DateTimeModel.year = "2026";
      DateTimeModel.MM_code = "10";
      DateTimeModel.day = "04";
      DateTimeModel.hour = "12";
      LowCloudState.cl_code = "2";
      MiddleCloudState.cm_code = "7a";
      HighCloudState.ch_code = "4";

      invokePrivate("write_input_for_101_compression");
      List<String> lines =
          Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("1 32.0            cloud type low (Cl) [code]", lines.get(36));
      assertEquals("1 27.0            cloud type middle (Cm) [code]", lines.get(37));
      assertEquals("1 14.0            cloud type high (Ch) [code]", lines.get(38));

      for (String middleCloudType : new String[] {"7b", "7c"}) {
        MiddleCloudState.cm_code = middleCloudType;
        invokePrivate("write_input_for_101_compression");
        lines =
            Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
        assertEquals("1 27.0            cloud type middle (Cm) [code]", lines.get(37));
      }

      LowCloudState.cl_code = "/";
      MiddleCloudState.cm_code = "/";
      HighCloudState.ch_code = "/";
      invokePrivate("write_input_for_101_compression");
      lines = Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("0                 cloud type low (Cl) [code]", lines.get(36));
      assertEquals("0                 cloud type middle (Cm) [code]", lines.get(37));
      assertEquals("0                 cloud type high (Ch) [code]", lines.get(38));

      LowCloudState.cl_code = "2a";
      MiddleCloudState.cm_code = "";
      HighCloudState.ch_code = "4b";
      invokePrivate("write_input_for_101_compression");
      lines = Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("0                 cloud type low (Cl) [code]", lines.get(36));
      assertEquals("0                 cloud type middle (Cm) [code]", lines.get(37));
      assertEquals("0                 cloud type high (Ch) [code]", lines.get(38));
    } finally {
      main.logs_dir = originalLogsDirectory;
      DateTimeModel.year = originalYear;
      DateTimeModel.MM_code = originalMonth;
      DateTimeModel.day = originalDay;
      LowCloudState.cl_code = originalLowCloudType;
      MiddleCloudState.cm_code = originalMiddleCloudType;
      HighCloudState.ch_code = originalHighCloudType;
    }
  }

  @Test
  public void preservesWaveValuesAndSpecialWaveCodes() throws Exception {
    String originalLogsDirectory = main.logs_dir;
    String originalYear = DateTimeModel.year;
    String originalMonth = DateTimeModel.MM_code;
    String originalDay = DateTimeModel.day;
    String originalHour = DateTimeModel.hour;
    String originalWindWavePeriod = WavesModel.wind_waves_period;
    String originalWindWaveHeight = WavesModel.wind_waves_height;
    String originalSwell1Direction = WavesModel.swell_1_dir;
    String originalSwell1Period = WavesModel.swell_1_period;
    String originalSwell1Height = WavesModel.swell_1_height;
    String originalSwell2Direction = WavesModel.swell_2_dir;
    String originalSwell2Period = WavesModel.swell_2_period;
    String originalSwell2Height = WavesModel.swell_2_height;

    try {
      File logsDirectory = temporaryFolder.newFolder("logs");
      File temporaryDirectory =
          new File(logsDirectory, main.FORMAT_101_ROOT_DIR + File.separator + "temp");
      assertTrue(temporaryDirectory.mkdirs());
      main.logs_dir = logsDirectory.getPath();
      DateTimeModel.year = "2026";
      DateTimeModel.MM_code = "10";
      DateTimeModel.day = "04";
      DateTimeModel.hour = "12";
      WavesModel.wind_waves_period = "8";
      WavesModel.wind_waves_height = "2.5";
      WavesModel.swell_1_dir = "90";
      WavesModel.swell_1_period = "12";
      WavesModel.swell_1_height = "3";
      WavesModel.swell_2_dir = "180";
      WavesModel.swell_2_period = "10";
      WavesModel.swell_2_height = "2";

      invokePrivate("write_input_for_101_compression");
      List<String> lines =
          Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("1 8.0             period of wind waves [seconds]", lines.get(41));
      assertEquals("1 2.5             height of wind waves [metres]", lines.get(42));
      assertEquals("1 90.0            direction of 1st swell [degree])", lines.get(43));
      assertEquals("1 12.0            period of 1st swell [seconds]", lines.get(44));
      assertEquals("1 3.0             height of 1st swell [metres]", lines.get(45));
      assertEquals("1 180.0           direction of 2nd swell [degrees]", lines.get(46));
      assertEquals("1 10.0            period of 2nd swell [seconds]]", lines.get(47));
      assertEquals("1 2.0             height of 2nd swell [metres]", lines.get(48));

      WavesModel.wind_waves_period = "confused";
      WavesModel.wind_waves_height = "confused";
      WavesModel.swell_1_dir = "no swell";
      WavesModel.swell_1_period = "confused";
      WavesModel.swell_1_height = "confused";
      WavesModel.swell_2_dir = "confused";
      WavesModel.swell_2_period = "confused";
      WavesModel.swell_2_height = "confused";
      invokePrivate("write_input_for_101_compression");
      lines = Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("0                 period of wind waves [seconds]", lines.get(41));
      assertEquals("0                 height of wind waves [metres]", lines.get(42));
      assertEquals("0                 direction of 1st swell [degree])", lines.get(43));
      assertEquals("0                 period of 1st swell [seconds]", lines.get(44));
      assertEquals("0                 height of 1st swell [metres]", lines.get(45));
      assertEquals("0                 direction of 2nd swell [degrees]", lines.get(46));
      assertEquals("0                 period of 2nd swell [seconds]]", lines.get(47));
      assertEquals("0                 height of 2nd swell [metres]", lines.get(48));
    } finally {
      main.logs_dir = originalLogsDirectory;
      DateTimeModel.year = originalYear;
      DateTimeModel.MM_code = originalMonth;
      DateTimeModel.day = originalDay;
      DateTimeModel.hour = originalHour;
      WavesModel.wind_waves_period = originalWindWavePeriod;
      WavesModel.wind_waves_height = originalWindWaveHeight;
      WavesModel.swell_1_dir = originalSwell1Direction;
      WavesModel.swell_1_period = originalSwell1Period;
      WavesModel.swell_1_height = originalSwell1Height;
      WavesModel.swell_2_dir = originalSwell2Direction;
      WavesModel.swell_2_period = originalSwell2Period;
      WavesModel.swell_2_height = originalSwell2Height;
    }
  }

  @Test
  public void preservesIcePresenceAndBasicIceConversions() throws Exception {
    String originalLogsDirectory = main.logs_dir;
    String originalYear = DateTimeModel.year;
    String originalMonth = DateTimeModel.MM_code;
    String originalDay = DateTimeModel.day;
    String originalHour = DateTimeModel.hour;
    String originalThickness = IcingState.EsEs_code;
    String originalRate = IcingState.Rs_code;
    String originalCause = IcingState.Is_code;
    String originalConcentration = IceState.ci_code;
    String originalType = IceState.bi_code;
    String originalSituation = IceState.zi_code;
    String originalDevelopment = IceState.Si_code;
    String originalBearing = IceState.Di_code;

    try {
      File logsDirectory = temporaryFolder.newFolder("logs");
      File temporaryDirectory =
          new File(logsDirectory, main.FORMAT_101_ROOT_DIR + File.separator + "temp");
      assertTrue(temporaryDirectory.mkdirs());
      main.logs_dir = logsDirectory.getPath();
      DateTimeModel.year = "2026";
      DateTimeModel.MM_code = "10";
      DateTimeModel.day = "04";
      DateTimeModel.hour = "12";
      IcingState.EsEs_code = "100";
      IcingState.Rs_code = "2";
      IcingState.Is_code = "";
      IceState.ci_code = "";
      IceState.bi_code = "";
      IceState.zi_code = "";
      IceState.Si_code = "";
      IceState.Di_code = "";

      invokePrivate("write_input_for_101_compression");
      List<String> lines =
          Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("1 1.0             ice obs. precence indicator", lines.get(49));
      assertEquals("1 1.0             ice deposit/thickness (EsEs) [metres]", lines.get(50));
      assertEquals("1 2.0             rate of ice accretion (Rs) [code]", lines.get(51));

      IcingState.EsEs_code = "";
      IcingState.Rs_code = "";
      invokePrivate("write_input_for_101_compression");
      lines = Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("1 0.0             ice obs. precence indicator", lines.get(49));
      assertEquals("0                 ice deposit/thickness (EsEs) [metres]", lines.get(50));
      assertEquals("0                 rate of ice accretion (Rs) [code]", lines.get(51));

      IcingState.EsEs_code = "//";
      IcingState.Rs_code = "/";
      invokePrivate("write_input_for_101_compression");
      lines = Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("0                 ice deposit/thickness (EsEs) [metres]", lines.get(50));
      assertEquals("0                 rate of ice accretion (Rs) [code]", lines.get(51));
    } finally {
      main.logs_dir = originalLogsDirectory;
      DateTimeModel.year = originalYear;
      DateTimeModel.MM_code = originalMonth;
      DateTimeModel.day = originalDay;
      DateTimeModel.hour = originalHour;
      IcingState.EsEs_code = originalThickness;
      IcingState.Rs_code = originalRate;
      IcingState.Is_code = originalCause;
      IceState.ci_code = originalConcentration;
      IceState.bi_code = originalType;
      IceState.zi_code = originalSituation;
      IceState.Si_code = originalDevelopment;
      IceState.Di_code = originalBearing;
    }
  }

  @Test
  public void preservesIceCauseMappingsAndSpecialBearingFallback() throws Exception {
    String originalLogsDirectory = main.logs_dir;
    String originalYear = DateTimeModel.year;
    String originalMonth = DateTimeModel.MM_code;
    String originalDay = DateTimeModel.day;
    String originalHour = DateTimeModel.hour;
    String originalCause = IcingState.Is_code;
    String originalBearing = IceState.Di_code;

    try {
      File logsDirectory = temporaryFolder.newFolder("logs");
      File temporaryDirectory =
          new File(logsDirectory, main.FORMAT_101_ROOT_DIR + File.separator + "temp");
      assertTrue(temporaryDirectory.mkdirs());
      main.logs_dir = logsDirectory.getPath();
      DateTimeModel.year = "2026";
      DateTimeModel.MM_code = "10";
      DateTimeModel.day = "04";
      DateTimeModel.hour = "12";
      IcingState.Is_code = "1";
      IceState.Di_code = "0";

      invokePrivate("write_input_for_101_compression");
      List<String> lines =
          Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("1 8.0             cause of ice accretion (Is) [code]", lines.get(52));
      assertEquals("0                 bearing of ice edge (Di) [degrees]", lines.get(57));

      IcingState.Is_code = "14";
      IceState.Di_code = "6";
      invokePrivate("write_input_for_101_compression");
      lines = Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("1 14.0            cause of ice accretion (Is) [code]", lines.get(52));
      assertEquals("1 270.0           bearing of ice edge (Di) [degrees]", lines.get(57));

      IcingState.Is_code = "9";
      IceState.Di_code = "/";
      invokePrivate("write_input_for_101_compression");
      lines = Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("0                 cause of ice accretion (Is) [code]", lines.get(52));
      assertEquals("0                 bearing of ice edge (Di) [degrees]", lines.get(57));
    } finally {
      main.logs_dir = originalLogsDirectory;
      DateTimeModel.year = originalYear;
      DateTimeModel.MM_code = originalMonth;
      DateTimeModel.day = originalDay;
      DateTimeModel.hour = originalHour;
      IcingState.Is_code = originalCause;
      IceState.Di_code = originalBearing;
    }
  }

  @Test
  public void preservesRemainingNumericIceCodesAndMissingValues() throws Exception {
    String originalLogsDirectory = main.logs_dir;
    String originalYear = DateTimeModel.year;
    String originalMonth = DateTimeModel.MM_code;
    String originalDay = DateTimeModel.day;
    String originalHour = DateTimeModel.hour;
    String originalConcentration = IceState.ci_code;
    String originalType = IceState.bi_code;
    String originalSituation = IceState.zi_code;
    String originalDevelopment = IceState.Si_code;

    try {
      File logsDirectory = temporaryFolder.newFolder("logs");
      File temporaryDirectory =
          new File(logsDirectory, main.FORMAT_101_ROOT_DIR + File.separator + "temp");
      assertTrue(temporaryDirectory.mkdirs());
      main.logs_dir = logsDirectory.getPath();
      DateTimeModel.year = "2026";
      DateTimeModel.MM_code = "10";
      DateTimeModel.day = "04";
      DateTimeModel.hour = "12";
      IceState.ci_code = "3";
      IceState.bi_code = "4";
      IceState.zi_code = "5";
      IceState.Si_code = "6";

      invokePrivate("write_input_for_101_compression");
      List<String> lines =
          Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("1 3.0             sea ice concentration (ci) [code]", lines.get(53));
      assertEquals("1 4.0             amount and type of ice (bi) [code]", lines.get(54));
      assertEquals("1 5.0             ice situation (zi) [code]", lines.get(55));
      assertEquals("1 6.0             ice development (Si) code]", lines.get(56));

      IceState.ci_code = "/";
      IceState.bi_code = "/";
      IceState.zi_code = "/";
      IceState.Si_code = "/";
      invokePrivate("write_input_for_101_compression");
      lines = Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("0                 sea ice concentration (ci) [code]", lines.get(53));
      assertEquals("0                 amount and type of ice (bi) [code]", lines.get(54));
      assertEquals("0                 ice situation (zi) [code]", lines.get(55));
      assertEquals("0                 ice development (Si) code]", lines.get(56));
    } finally {
      main.logs_dir = originalLogsDirectory;
      DateTimeModel.year = originalYear;
      DateTimeModel.MM_code = originalMonth;
      DateTimeModel.day = originalDay;
      IceState.ci_code = originalConcentration;
      IceState.bi_code = originalType;
      IceState.zi_code = originalSituation;
      IceState.Si_code = originalDevelopment;
    }
  }

  @Test
  public void preservesWeatherCodeConversionsAndSentinels() throws Exception {
    String originalLogsDirectory = main.logs_dir;
    String originalYear = DateTimeModel.year;
    String originalMonth = DateTimeModel.MM_code;
    String originalDay = DateTimeModel.day;
    String originalHour = DateTimeModel.hour;
    String originalVisibility = VisibilityState.VV_code;
    String originalPresentWeather = PresentWeatherState.ww_code;
    String originalPastWeather1 = PastWeatherState.W1_code;
    String originalPastWeather2 = PastWeatherState.W2_code;

    try {
      File logsDirectory = temporaryFolder.newFolder("logs");
      File temporaryDirectory =
          new File(logsDirectory, main.FORMAT_101_ROOT_DIR + File.separator + "temp");
      assertTrue(temporaryDirectory.mkdirs());
      main.logs_dir = logsDirectory.getPath();
      DateTimeModel.year = "2026";
      DateTimeModel.MM_code = "10";
      DateTimeModel.day = "04";
      DateTimeModel.hour = "12";
      VisibilityState.VV_code = "5";
      PresentWeatherState.ww_code = "20";
      PastWeatherState.W1_code = "3";
      PastWeatherState.W2_code = "4";

      invokePrivate("write_input_for_101_compression");
      List<String> lines =
          Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("1 5.0             visibility (VV) [code]", lines.get(30));
      assertEquals("1 20.0            present weather (ww) [code]", lines.get(31));
      assertEquals("1 3.0             past weather 1 (W1) [code]", lines.get(32));
      assertEquals("1 4.0             past weather 2 (W2) [code]", lines.get(33));

      VisibilityState.VV_code = "//";
      PresentWeatherState.ww_code = "//";
      PastWeatherState.W1_code = "/";
      PastWeatherState.W2_code = "/";
      invokePrivate("write_input_for_101_compression");
      lines = Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("0                 visibility (VV) [code]", lines.get(30));
      assertEquals("0                 present weather (ww) [code]", lines.get(31));
      assertEquals("0                 past weather 1 (W1) [code]", lines.get(32));
      assertEquals("0                 past weather 2 (W2) [code]", lines.get(33));
    } finally {
      main.logs_dir = originalLogsDirectory;
      DateTimeModel.year = originalYear;
      DateTimeModel.MM_code = originalMonth;
      DateTimeModel.day = originalDay;
      DateTimeModel.hour = originalHour;
      VisibilityState.VV_code = originalVisibility;
      PresentWeatherState.ww_code = originalPresentWeather;
      PastWeatherState.W1_code = originalPastWeather1;
      PastWeatherState.W2_code = originalPastWeather2;
    }
  }

  @Test
  public void preservesDirectionCodeMappingsAndFallbacks() throws Exception {
    String originalLogsDirectory = main.logs_dir;
    String originalYear = DateTimeModel.year;
    String originalMonth = DateTimeModel.MM_code;
    String originalDay = DateTimeModel.day;
    String originalHour = DateTimeModel.hour;
    String originalCourseCode = PositionModel.Ds_code;
    String originalIceBearingCode = IceState.Di_code;

    try {
      File logsDirectory = temporaryFolder.newFolder("logs");
      File temporaryDirectory =
          new File(logsDirectory, main.FORMAT_101_ROOT_DIR + File.separator + "temp");
      assertTrue(temporaryDirectory.mkdirs());
      main.logs_dir = logsDirectory.getPath();
      DateTimeModel.year = "2026";
      DateTimeModel.MM_code = "10";
      DateTimeModel.day = "04";
      DateTimeModel.hour = "12";
      PositionModel.Ds_code = "3";
      IceState.Di_code = "6";

      invokePrivate("write_input_for_101_compression");
      List<String> lines =
          Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("1 135.0           COG past 10 minutes [degrees]", lines.get(3));
      assertEquals("1 270.0           bearing of ice edge (Di) [degrees]", lines.get(57));

      PositionModel.Ds_code = "9";
      IceState.Di_code = "/";
      invokePrivate("write_input_for_101_compression");
      lines = Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("0                 COG past 10 minutes [degrees]", lines.get(3));
      assertEquals("0                 bearing of ice edge (Di) [degrees]", lines.get(57));
    } finally {
      main.logs_dir = originalLogsDirectory;
      DateTimeModel.year = originalYear;
      DateTimeModel.MM_code = originalMonth;
      DateTimeModel.day = originalDay;
      PositionModel.Ds_code = originalCourseCode;
      IceState.Di_code = originalIceBearingCode;
    }
  }

  @Test
  public void preservesPressureChangeRoundingAndSign() throws Exception {
    String originalLogsDirectory = main.logs_dir;
    String originalYear = DateTimeModel.year;
    String originalMonth = DateTimeModel.MM_code;
    String originalDay = DateTimeModel.day;
    String originalHour = DateTimeModel.hour;
    String originalPressureChange = BarographModel.pressure_amount_tendency;
    String originalPressureCharacteristic = BarographModel.a_code;

    try {
      File logsDirectory = temporaryFolder.newFolder("logs");
      File temporaryDirectory =
          new File(logsDirectory, main.FORMAT_101_ROOT_DIR + File.separator + "temp");
      assertTrue(temporaryDirectory.mkdirs());
      main.logs_dir = logsDirectory.getPath();
      DateTimeModel.year = "2026";
      DateTimeModel.MM_code = "10";
      DateTimeModel.day = "04";
      DateTimeModel.hour = "12";
      BarographModel.pressure_amount_tendency = "1.234";
      BarographModel.a_code = "2";

      invokePrivate("write_input_for_101_compression");
      List<String> lines =
          Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("1 123.4           3 hour pressure change [Pa]", lines.get(16));
      assertEquals("1 2.0             characteristic of pressure change [code]", lines.get(17));

      BarographModel.a_code = "5";
      invokePrivate("write_input_for_101_compression");
      lines = Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("1 -123.4          3 hour pressure change [Pa]", lines.get(16));
      assertEquals("1 5.0             characteristic of pressure change [code]", lines.get(17));

      BarographModel.pressure_amount_tendency = "";
      BarographModel.a_code = "invalid";
      invokePrivate("write_input_for_101_compression");
      lines = Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("0                 characteristic of pressure change [code]", lines.get(17));

      BarographModel.pressure_amount_tendency = "invalid";
      BarographModel.a_code = "2";
      invokePrivate("write_input_for_101_compression");
      lines = Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("0                 3 hour pressure change [Pa]", lines.get(16));
    } finally {
      main.logs_dir = originalLogsDirectory;
      DateTimeModel.year = originalYear;
      DateTimeModel.MM_code = originalMonth;
      DateTimeModel.day = originalDay;
      DateTimeModel.hour = originalHour;
      BarographModel.pressure_amount_tendency = originalPressureChange;
      BarographModel.a_code = originalPressureCharacteristic;
    }
  }

  @Test
  public void writesMissingPressureChangeAsAnAbsentRecord() throws Exception {
    String originalLogsDirectory = main.logs_dir;
    String originalYear = DateTimeModel.year;
    String originalMonth = DateTimeModel.MM_code;
    String originalDay = DateTimeModel.day;
    String originalHour = DateTimeModel.hour;
    String originalPressureChange = BarographModel.pressure_amount_tendency;
    String originalPressureCharacteristic = BarographModel.a_code;

    try {
      File logsDirectory = temporaryFolder.newFolder("logs");
      File temporaryDirectory =
          new File(logsDirectory, main.FORMAT_101_ROOT_DIR + File.separator + "temp");
      assertTrue(temporaryDirectory.mkdirs());
      main.logs_dir = logsDirectory.getPath();
      DateTimeModel.year = "2026";
      DateTimeModel.MM_code = "10";
      DateTimeModel.day = "04";
      DateTimeModel.hour = "12";
      BarographModel.pressure_amount_tendency = "";
      BarographModel.a_code = "";

      invokePrivate("write_input_for_101_compression");
      List<String> lines =
          Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("0                 3 hour pressure change [Pa]", lines.get(16));
    } finally {
      main.logs_dir = originalLogsDirectory;
      DateTimeModel.year = originalYear;
      DateTimeModel.MM_code = originalMonth;
      DateTimeModel.day = originalDay;
      DateTimeModel.hour = originalHour;
      BarographModel.pressure_amount_tendency = originalPressureChange;
      BarographModel.a_code = originalPressureCharacteristic;
    }
  }

  @Test
  public void preservesCoordinateConversionAndHemisphereSigns() throws Exception {
    String originalLogsDirectory = main.logs_dir;
    String originalYear = DateTimeModel.year;
    String originalMonth = DateTimeModel.MM_code;
    String originalDay = DateTimeModel.day;
    String originalHour = DateTimeModel.hour;
    int originalLatitudeDegrees = PositionModel.int_latitude_degrees;
    int originalLatitudeMinutes = PositionModel.int_latitude_minutes;
    String originalLatitudeHemisphere = PositionModel.latitude_hemisphere;
    int originalLongitudeDegrees = PositionModel.int_longitude_degrees;
    int originalLongitudeMinutes = PositionModel.int_longitude_minutes;
    String originalLongitudeHemisphere = PositionModel.longitude_hemisphere;

    try {
      File logsDirectory = temporaryFolder.newFolder("logs");
      File temporaryDirectory =
          new File(logsDirectory, main.FORMAT_101_ROOT_DIR + File.separator + "temp");
      assertTrue(temporaryDirectory.mkdirs());
      main.logs_dir = logsDirectory.getPath();
      DateTimeModel.year = "2026";
      DateTimeModel.MM_code = "10";
      DateTimeModel.day = "04";
      DateTimeModel.hour = "12";
      PositionModel.int_latitude_degrees = 12;
      PositionModel.int_latitude_minutes = 30;
      PositionModel.latitude_hemisphere = PositionModel.HEMISPHERE_SOUTH;
      PositionModel.int_longitude_degrees = 45;
      PositionModel.int_longitude_minutes = 30;
      PositionModel.longitude_hemisphere = PositionModel.HEMISPHERE_WEST;

      invokePrivate("write_input_for_101_compression");
      List<String> lines =
          Files.readAllLines(new File(temporaryDirectory, main.FORMAT_101_INPUT_FILE).toPath());
      assertEquals("1 -12.5           latitude [degrees]", lines.get(12));
      assertEquals("1 -45.5           longitude [degrees]", lines.get(13));
    } finally {
      main.logs_dir = originalLogsDirectory;
      DateTimeModel.year = originalYear;
      DateTimeModel.MM_code = originalMonth;
      DateTimeModel.day = originalDay;
      DateTimeModel.hour = originalHour;
      PositionModel.int_latitude_degrees = originalLatitudeDegrees;
      PositionModel.int_latitude_minutes = originalLatitudeMinutes;
      PositionModel.latitude_hemisphere = originalLatitudeHemisphere;
      PositionModel.int_longitude_degrees = originalLongitudeDegrees;
      PositionModel.int_longitude_minutes = originalLongitudeMinutes;
      PositionModel.longitude_hemisphere = originalLongitudeHemisphere;
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
