package turbowin.matchers;

import static org.assertj.core.util.Preconditions.checkNotNull;

import javax.swing.*;
import org.assertj.swing.core.GenericTypeMatcher;

public class DialogTitleMatcher extends GenericTypeMatcher<JDialog> {
  private String dialogTitleText = "";

  public DialogTitleMatcher(String dialogTitleText) {
    super(checkNotNull(JDialog.class));
    this.dialogTitleText = dialogTitleText;
  }

  @Override
  protected boolean isMatching(JDialog dialog) {
    return dialogTitleText.equals(dialog.getTitle());
  }
}
