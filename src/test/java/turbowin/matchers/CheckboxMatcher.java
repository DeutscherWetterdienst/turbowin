package turbowin.matchers;

import static org.assertj.core.util.Preconditions.checkNotNull;

import javax.swing.*;
import org.assertj.swing.core.GenericTypeMatcher;

public class CheckboxMatcher extends GenericTypeMatcher<JCheckBox> {
  private String checkboxText = "";

  public CheckboxMatcher(String checkboxText) {
    super(checkNotNull(JCheckBox.class));
    this.checkboxText = checkboxText;
  }

  @Override
  protected boolean isMatching(JCheckBox checkbox) {
    return checkboxText.equals(checkbox.getText());
  }
}
