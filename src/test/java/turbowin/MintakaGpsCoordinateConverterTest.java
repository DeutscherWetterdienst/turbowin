package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MintakaGpsCoordinateConverterTest {

  @Test
  public void convertsNorthAndEastCoordinates() {
    assertEquals(
        52.0784016667, MintakaGpsCoordinateConverter.toDecimal(" 52 04.7041N", true), 0.0000001);
    assertEquals(
        6.2365733333, MintakaGpsCoordinateConverter.toDecimal("  6 14.1944E", false), 0.0000001);
  }

  @Test
  public void convertsSouthAndWestCoordinates() {
    assertEquals(
        -16.5073816667, MintakaGpsCoordinateConverter.toDecimal("16 30.4429S", true), 0.0000001);
    assertEquals(
        -88.3650666667, MintakaGpsCoordinateConverter.toDecimal("88 21.9040W", false), 0.0000001);
  }

  @Test
  public void returnsSentinelForWrongHemisphereCoordinates() {
    assertEquals(
        MintakaGpsCoordinateConverter.INVALID_COORDINATE,
        MintakaGpsCoordinateConverter.toDecimal("52 04.7041X", true),
        0.0);
  }

  @Test(expected = NumberFormatException.class)
  public void propagatesMalformedNumericCoordinates() {
    MintakaGpsCoordinateConverter.toDecimal("52 invalidN", true);
  }

  @Test(expected = StringIndexOutOfBoundsException.class)
  public void propagatesMalformedCoordinateStructure() {
    MintakaGpsCoordinateConverter.toDecimal("not-a-coordinate", false);
  }
}
