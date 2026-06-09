package turbowin.matchers;

import static org.assertj.core.util.Preconditions.checkNotNull;

import javax.swing.*;
import org.assertj.swing.core.GenericTypeMatcher;

public class RadioButtonMatcher extends GenericTypeMatcher<JRadioButton> {
  private String radioButtonText = "";

  public RadioButtonMatcher(String radioButtonText) {
    super(checkNotNull(JRadioButton.class));
    this.radioButtonText = radioButtonText;
  }

  @Override
  protected boolean isMatching(JRadioButton radioButton) {
    return radioButtonText.equals(radioButton.getText());
  }
}
