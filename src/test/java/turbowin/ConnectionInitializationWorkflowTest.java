package turbowin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.swing.JCheckBox;
import org.junit.Test;

public class ConnectionInitializationWorkflowTest {

  @Test
  public void disablesBothReportOptionsWithoutAConnection() {
    JCheckBox apr = new JCheckBox();
    JCheckBox awsr = new JCheckBox();

    ConnectionInitializationWorkflow.configureReportCheckboxes(0, apr, awsr);

    assertFalse(apr.isEnabled());
    assertFalse(awsr.isEnabled());
  }

  @Test
  public void disablesBothReportsForEucaws() {
    JCheckBox apr = new JCheckBox();
    JCheckBox awsr = new JCheckBox();

    ConnectionInitializationWorkflow.configureReportCheckboxes(3, apr, awsr);

    assertFalse(apr.isEnabled());
    assertFalse(awsr.isEnabled());
  }

  @Test
  public void enablesAprButNotAwsReportsForBarometerMode() {
    JCheckBox apr = new JCheckBox();
    JCheckBox awsr = new JCheckBox();

    ConnectionInitializationWorkflow.configureReportCheckboxes(1, apr, awsr);

    assertTrue(apr.isEnabled());
    assertFalse(awsr.isEnabled());
  }

  @Test
  public void enablesOnlyAwsReportsForOmcAndAmosAwsModes() {
    for (int connectionMode : new int[] {9, 10, 11}) {
      JCheckBox apr = new JCheckBox();
      JCheckBox awsr = new JCheckBox();

      ConnectionInitializationWorkflow.configureReportCheckboxes(connectionMode, apr, awsr);

      assertFalse(apr.isEnabled());
      assertTrue(awsr.isEnabled());
    }
  }
}
