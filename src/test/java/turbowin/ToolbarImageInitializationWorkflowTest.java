package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class ToolbarImageInitializationWorkflowTest {

  @Test
  public void usesSynchronousLoadingForLinux() {
    List<String> synchronous = new ArrayList<>();
    List<String> asynchronous = new ArrayList<>();

    ToolbarImageInitializationWorkflow.initialize(true, synchronous::add, asynchronous::add);

    assertEquals(19, synchronous.size());
    assertEquals(0, asynchronous.size());
    assertTrue(synchronous.get(0).endsWith("date_time.png"));
    assertTrue(synchronous.get(18).endsWith("next_screen.png"));
  }

  @Test
  public void usesAsynchronousLoadingOutsideLinux() {
    List<String> synchronous = new ArrayList<>();
    List<String> asynchronous = new ArrayList<>();

    ToolbarImageInitializationWorkflow.initialize(false, synchronous::add, asynchronous::add);

    assertEquals(0, synchronous.size());
    assertEquals(19, asynchronous.size());
  }
}
