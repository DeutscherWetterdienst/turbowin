package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ImmtLogWorkflowTest {

  @Test
  public void composesAnImmtRecord() {
    String originalYear = DateTimeModel.year;
    String originalCloudHeight = CloudCoverState.h_code;
    String originalVisibility = VisibilityState.VV_code;
    try {
      DateTimeModel.year = "2024";
      // IMMT quality indicators are 9 for missing cloud height and visibility.
      CloudCoverState.h_code = "";
      VisibilityState.VV_code = "";
      String record = ImmtLogWorkflow.composeRecord();

      assertNotNull(record);
      assertEquals('3', record.charAt(0));
      assertEquals("2024", record.substring(1, 5));
      assertTrue(record.contains("A599"));
    } finally {
      DateTimeModel.year = originalYear;
      CloudCoverState.h_code = originalCloudHeight;
      VisibilityState.VV_code = originalVisibility;
    }
  }
}
