package turbowin;

import static org.junit.Assert.*;

import java.util.List;
import org.junit.Test;

public class PresentWeatherCatalogTest {
  @Test
  public void categoriesKeepMarineCodesInTheirOriginalPriorityOrder() {
    List<List<String>> dry =
        List.of(
            List.of("//"),
            List.of("17"),
            List.of("49", "48", "47", "46", "45", "44", "43", "42", "41", "40"),
            List.of("39", "38", "37", "36", "35", "34", "33", "32", "31", "30"),
            List.of("29", "28", "27", "26", "25", "24", "23", "22", "21", "20"),
            List.of("19", "18"),
            List.of("16", "15", "14", "13"),
            List.of("12", "11", "10"),
            List.of("09", "07", "06", "05", "04"),
            List.of("03", "02", "01", "00"));
    List<List<String>> wet =
        List.of(
            List.of("//"),
            List.of("99", "98", "97", "96", "95"),
            List.of("94", "93", "92", "91"),
            List.of("90", "89", "88", "87", "86", "85", "84", "83", "82", "81", "80"),
            List.of("79", "78", "77", "76", "75", "74", "73", "72", "71", "70"),
            List.of("69", "68", "67", "66", "65", "64", "63", "62", "61", "60"),
            List.of("59", "58", "57", "56", "55", "54", "53", "52", "51", "50"));
    assertEquals(dry, codes(PresentWeatherCatalog.DRY));
    assertEquals(wet, codes(PresentWeatherCatalog.WET));
    assertEquals(
        "fogbank (app. vis. in bank < 0.5 nm) at a distance;not at stat. during last hr",
        PresentWeatherCatalog.DRY.get(2).options().get(9).description());
  }

  private static List<List<String>> codes(List<PresentWeatherCatalog.Group> groups) {
    return groups.stream()
        .map(group -> group.options().stream().map(PresentWeatherCatalog.Weather::code).toList())
        .toList();
  }
}
