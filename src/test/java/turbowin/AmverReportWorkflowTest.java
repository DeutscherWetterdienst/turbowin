package turbowin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;

public class AmverReportWorkflowTest {

  private final String originalReport = main.amver_report;

  @After
  public void restoreState() {
    main.amver_report = originalReport;
  }

  @Test
  public void detectsWhenAnAmverReportIsAlreadyOpen() {
    main.amver_report = main.AMVER_DR;
    assertTrue(AmverReportWorkflow.reportAlreadyOpen());
  }

  @Test
  public void detectsWhenNoAmverReportIsOpen() {
    main.amver_report = "";
    assertFalse(AmverReportWorkflow.reportAlreadyOpen());
  }
}
