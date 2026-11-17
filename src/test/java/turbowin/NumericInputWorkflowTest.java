package turbowin;

import static org.junit.Assert.assertEquals;

import javax.swing.JTextField;
import org.junit.Test;

public class NumericInputWorkflowTest {

  @Test
  public void decimalFieldAcceptsSignedDecimalValuesAndRejectsLetters() {
    JTextField field = new JTextField();
    NumericInputWorkflow.decimal(field);

    field.setText("-12,5");
    assertEquals("-12.5", field.getText());
    field.setText("");
    field.setText("12 knots");
    assertEquals("", field.getText());
  }

  @Test
  public void integerFieldAcceptsVariableAsAnIncrementallyTypedException() {
    JTextField field = new JTextField();
    NumericInputWorkflow.integerOr(field, "variable", "calm");

    field.setText("variable");
    assertEquals("variable", field.getText());
    field.setText("calm");
    assertEquals("calm", field.getText());
    field.setText("");
    field.setText("12.5");
    assertEquals("", field.getText());
  }
}
