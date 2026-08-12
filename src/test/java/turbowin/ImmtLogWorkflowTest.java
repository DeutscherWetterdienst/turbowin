package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ImmtLogWorkflowTest {

  @Test
  public void composesAnImmtRecord() {
    mydatetime.year = "2024";
    String record = ImmtLogWorkflow.composeRecord();

    assertNotNull(record);
    assertEquals('3', record.charAt(0));
    assertEquals("2024", record.substring(1, 5));
    assertTrue(record.contains("A599"));
  }
}
