package turbowin.matchers;

import org.assertj.swing.core.GenericTypeMatcher;

import javax.swing.*;

import static org.assertj.core.util.Preconditions.checkNotNull;

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
