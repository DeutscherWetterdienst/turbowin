package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class ObsStatsImmtLogProcessorCharacterizationTest {

  @Test
  public void allPeriodIgnoresShortRecordsAndTracksRawFirstAndLastRecords() throws IOException {
    String first = record("20240101", "first");
    String last = record("20240103", "last");
    File file = writeLines("short", first, last);

    try {
      ObsStatsImmtLogProcessor.Result result =
          ObsStatsImmtLogProcessor.process(file.getPath(), myimmtlogperiod.ALL, null, null, false);

      assertEquals(0, result.status());
      assertEquals(List.of(first, last), result.records());
      assertEquals(first, result.firstRecord());
      assertEquals(last, result.lastRecord());
    } finally {
      file.delete();
    }
  }

  @Test
  public void customPeriodIncludesBothBoundaryDatesAndKeepsRawFirstAndLast() throws IOException {
    String before = record("20231231", "before");
    String start = record("20240101", "start");
    String end = record("20240103", "end");
    String after = record("20240104", "after");
    File file = writeLines(before, start, end, after);

    try {
      ObsStatsImmtLogProcessor.Result result =
          ObsStatsImmtLogProcessor.process(
              file.getPath(), "custom", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 3), false);

      assertEquals(0, result.status());
      assertEquals(List.of(start, end), result.records());
      assertEquals(before, result.firstRecord());
      assertEquals(after, result.lastRecord());
    } finally {
      file.delete();
    }
  }

  @Test
  public void malformedDateIsExcludedFromCustomPeriod() throws IOException {
    String malformed = record("20241340", "malformed");
    File file = writeLines(malformed);

    try {
      ObsStatsImmtLogProcessor.Result result =
          ObsStatsImmtLogProcessor.process(
              file.getPath(),
              "custom",
              LocalDate.of(2024, 1, 1),
              LocalDate.of(2024, 12, 31),
              false);

      assertEquals(0, result.status());
      assertTrue(result.records().isEmpty());
      assertEquals(malformed, result.firstRecord());
      assertEquals(malformed, result.lastRecord());
    } finally {
      file.delete();
    }
  }

  @Test
  public void missingFileReturnsTheExistingOpenErrorCode() {
    ObsStatsImmtLogProcessor.Result result =
        ObsStatsImmtLogProcessor.process(
            "missing-immt-file.log", myimmtlogperiod.ALL, null, null, false);

    assertEquals(-2, result.status());
    assertTrue(result.records().isEmpty());
    assertEquals("", result.firstRecord());
    assertEquals("", result.lastRecord());
  }

  @Test
  public void moreThanTheAllowedNumberOfObserversReturnsWarningCode() throws IOException {
    List<String> records = new ArrayList<>();
    for (int i = 0; i <= Obs_Stats_view.MAX_NUMBER_OBSERVERS_NAMES; i++) {
      records.add(record("20240101", "observer-" + i));
    }
    File file = writeLines(records.toArray(new String[0]));

    try {
      ObsStatsImmtLogProcessor.Result result =
          ObsStatsImmtLogProcessor.process(file.getPath(), myimmtlogperiod.ALL, null, null, true);

      assertEquals(-3, result.status());
      assertFalse(result.records().isEmpty());
    } finally {
      file.delete();
    }
  }

  private static String record(String date, String observer) {
    StringBuilder record = new StringBuilder("3").append(date);
    while (record.length() < main.IMMT_5_POSITION_OBSERVER) {
      record.append(' ');
    }
    return record.append(observer).toString();
  }

  private static File writeLines(String... lines) throws IOException {
    File file = Files.createTempFile("turbowin-obs-stats", ".log").toFile();
    Files.write(file.toPath(), List.of(lines));
    return file;
  }
}
