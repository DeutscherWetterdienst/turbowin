package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ImmtLogWorkflowTest {

  @Test
  public void composesAnImmtRecord() {
    String originalYear = mydatetime.year;
    String originalCloudHeight = mycloudcover.h_code;
    String originalVisibility = myvisibility.VV_code;
    try {
      mydatetime.year = "2024";
      // IMMT quality indicators are 9 for missing cloud height and visibility.
      mycloudcover.h_code = "";
      myvisibility.VV_code = "";
      String record = ImmtLogWorkflow.composeRecord();

      assertNotNull(record);
      assertEquals('3', record.charAt(0));
      assertEquals("2024", record.substring(1, 5));
      assertTrue(record.contains("A599"));
    } finally {
      mydatetime.year = originalYear;
      mycloudcover.h_code = originalCloudHeight;
      myvisibility.VV_code = originalVisibility;
    }
  }
}
