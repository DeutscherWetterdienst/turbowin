package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ProtectedWindowWorkflowTest {

  @Test
  public void selectsProtectedWindowAfterPasswordVerification() {
    assertEquals(
        "protected", ProtectedWindowWorkflow.select(true, () -> "protected", () -> "password"));
  }

  @Test
  public void selectsPasswordWindowWhenPasswordIsRequired() {
    assertEquals(
        "password", ProtectedWindowWorkflow.select(false, () -> "protected", () -> "password"));
  }
}
