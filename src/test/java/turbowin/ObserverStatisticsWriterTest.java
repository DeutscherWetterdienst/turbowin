package turbowin;

import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.StringWriter;
import org.junit.Test;

public class ObserverStatisticsWriterTest {

  @Test
  public void writesCountedObserverToOutputAndBackup() {
    StringWriter outputText = new StringWriter();
    StringWriter backupText = new StringWriter();
    BufferedWriter output = new BufferedWriter(outputText);
    BufferedWriter backup = new BufferedWriter(backupText);

    ObserverStatisticsWriter.write(
        new BufferedWriter[] {output}, new BufferedWriter[] {backup}, 0, "Smith;J;Captain;123", 4);
    flush(output, backup);

    assertEquals("Smith;J;Captain;1234\n", outputText.toString());
    assertEquals("Smith;J;Captain;1234\n", backupText.toString());
  }

  @Test
  public void doesNotWriteAnObserverWithNoObservations() {
    StringWriter outputText = new StringWriter();
    StringWriter backupText = new StringWriter();
    BufferedWriter output = new BufferedWriter(outputText);
    BufferedWriter backup = new BufferedWriter(backupText);

    ObserverStatisticsWriter.write(
        new BufferedWriter[] {output}, new BufferedWriter[] {backup}, 0, "Smith;J;Captain;123", 0);

    assertEquals("", outputText.toString());
    assertEquals("", backupText.toString());
  }

  @Test
  public void toleratesUnavailableOutputWriters() {
    ObserverStatisticsWriter.write(
        new BufferedWriter[] {null}, new BufferedWriter[] {null}, 0, "Smith", 1);
  }

  private static void flush(BufferedWriter... writers) {
    try {
      for (BufferedWriter writer : writers) {
        writer.flush();
      }
    } catch (java.io.IOException ex) {
      throw new RuntimeException(ex);
    }
  }
}
