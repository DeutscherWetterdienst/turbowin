package turbowin;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

public class PopupMenuWorkflowTest {

  @Test
  public void preservesInputMenuLabelsAndOrder() {
    assertArrayEquals(
        new String[] {
          "Date & Time...",
          "Position, Course & Speed...",
          "Barometer reading...",
          "Barograph reading...",
          "Temperatures...",
          "Wind...",
          "Waves...",
          "Visibility...",
          "Present weather...",
          "Past weather...",
          "Clouds low...",
          "Clouds middle...",
          "Clouds high...",
          "Cloud cover & height...",
          "Icing...",
          "Ice...",
          "Observer...",
          "Day colours",
          "Night colours",
          "Sunrise colours",
          "Sunset colours",
          "Transparent"
        },
        PopupMenuWorkflow.menuLabels());
  }
}
