package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MintakaGpsFieldsParserTest {

  @Test
  public void extractsLatitudeAndLongitudeWithoutChangingWhitespace() {
    MintakaGpsFields fields =
        MintakaGpsFieldsParser.parse(
            "1029.97,1029.97,-0.90,7, 52 41.9535N,  6 14.1943E,0,0,19*1A201703152006");

    assertEquals(" 52 41.9535N", fields.latitude);
    assertEquals("  6 14.1943E", fields.longitude);
  }
}
