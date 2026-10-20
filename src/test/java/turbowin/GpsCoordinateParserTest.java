package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class GpsCoordinateParserTest {

  @Test
  public void convertsNorthernEasternCoordinateAndImmtCodes() {
    GpsCoordinateParser.Result result =
        GpsCoordinateParser.parse("4804.7041778", "N", "01144.3966270", "E");

    assertTrue(result.valid);
    assertEquals("48", result.latitudeDegrees);
    assertEquals("5", result.latitudeMinutes);
    assertEquals("011", result.longitudeDegrees);
    assertEquals("44", result.longitudeMinutes);
    assertEquals("480", result.latitudeCode);
    assertEquals("0117", result.longitudeCode);
    assertEquals("1", result.quadrant);
  }

  @Test
  public void carriesRoundedSixtyMinutesIntoDegrees() {
    GpsCoordinateParser.Result result = GpsCoordinateParser.parse("1259.6", "S", "17959.6", "W");

    assertTrue(result.valid);
    assertEquals("13", result.latitudeDegrees);
    assertEquals("0", result.latitudeMinutes);
    assertEquals("180", result.longitudeDegrees);
    assertEquals("0", result.longitudeMinutes);
    assertEquals("5", result.quadrant == null ? "" : result.quadrant);
    assertEquals(-13.993333333333334, result.signedLatitude, 0.0000000001);
    assertEquals(-180.99333333333334, result.signedLongitude, 0.0000000001);
  }

  @Test
  public void rejectsUnknownHemispheres() {
    GpsCoordinateParser.Result result = GpsCoordinateParser.parse("4804.7", "X", "01144.3", "Y");

    assertTrue(!result.valid);
  }
}
