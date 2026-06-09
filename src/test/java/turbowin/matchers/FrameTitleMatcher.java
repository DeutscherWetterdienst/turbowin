package turbowin.matchers;

import static org.assertj.core.util.Preconditions.checkNotNull;

import javax.swing.*;
import org.assertj.swing.core.GenericTypeMatcher;

public class FrameTitleMatcher extends GenericTypeMatcher<JFrame> {
  private String frameTitleText = "";

  public FrameTitleMatcher(String frameTitleText) {
    super(checkNotNull(JFrame.class));
    this.frameTitleText = frameTitleText;
  }

  @Override
  protected boolean isMatching(JFrame frame) {
    return frameTitleText.equals(frame.getTitle());
  }
}
