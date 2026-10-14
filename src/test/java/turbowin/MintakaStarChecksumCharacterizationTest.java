package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MintakaStarChecksumCharacterizationTest {

  @Test
  public void preservesKnownMintakaStarChecksumValues() {
    assertEquals(
        "03",
        main_RS232_RS422.Mintaka_Star_Checksum(
            "1018.61,1018.61,1.90,2, 15 39.0161N, 89 00.1226W,0,0,0*03"));
    assertEquals(
        "31",
        main_RS232_RS422.Mintaka_Star_Checksum(
            "1011.20,1011.20,,, 47 37.5965N,122 31.1453W,0,0,0*31"));
  }

  @Test
  public void preservesKnownMintakaStarXChecksumValue() {
    assertEquals(
        "32",
        main_RS232_RS422.Mintaka_Star_Checksum(
            "1018.12,1018.13,0.00,0, 16 30.4429N, 88 21.9040W,0,2,7,29.4,85,27.4,26.7,1017.89,60*18"));
  }

  @Test
  public void returnsEmptyChecksumWhenRecordHasNoAsterisk() {
    assertEquals("", main_RS232_RS422.Mintaka_Star_Checksum("1018.61,1018.61"));
  }
}
