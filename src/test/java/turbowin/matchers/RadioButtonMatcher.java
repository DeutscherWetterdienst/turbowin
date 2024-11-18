package turbowin.matchers;

import org.assertj.swing.core.GenericTypeMatcher;

import javax.swing.*;

import static org.assertj.core.util.Preconditions.checkNotNull;

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
