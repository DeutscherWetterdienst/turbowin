package turbowin;

import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.fixture.JLabelFixture;
import org.junit.Test;

/**
 * Actual test class
 */
public class CalculatorFormTest extends AbstractUiTest {

    private JLabelFixture positionLabel;
    private JLabelFixture stationIdLabel;

    @Override
    protected void onSetUp() {
        this.positionLabel = this.frame.label(JLabelMatcher.withText("Position"));
        this.stationIdLabel = this.frame.label(JLabelMatcher.withText("Station ID"));
    }

    @Test
    public void testWithDifferingComponentMatchers() {
        this.positionLabel.requireVisible().requireEnabled().click();
        this.stationIdLabel.requireVisible().requireEnabled().click();
    }

    @Override
    protected void onTearDown() {
        this.positionLabel = null;
        this.stationIdLabel = null;
    }
}
