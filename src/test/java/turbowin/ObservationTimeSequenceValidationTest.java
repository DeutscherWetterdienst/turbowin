package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.GregorianCalendar;
import org.junit.Test;

public class ObservationTimeSequenceValidationTest {

  @Test
  public void identifiesEqualAndEarlierObservations() {
    Calendar previous = new GregorianCalendar(2026, Calendar.JANUARY, 1, 12, 0);
    Calendar equal = new GregorianCalendar(2026, Calendar.JANUARY, 1, 12, 0);
    Calendar earlier = new GregorianCalendar(2026, Calendar.JANUARY, 1, 11, 59);
    Calendar later = new GregorianCalendar(2026, Calendar.JANUARY, 1, 12, 1);

    assertTrue(ObservationTimeSequenceValidation.isNotLater(equal, previous));
    assertTrue(ObservationTimeSequenceValidation.isNotLater(earlier, previous));
    assertFalse(ObservationTimeSequenceValidation.isNotLater(later, previous));
  }

  @Test
  public void calculatesWholeElapsedHoursUsingLegacyTruncation() {
    Calendar previous = new GregorianCalendar(2026, Calendar.JANUARY, 1, 12, 0);
    Calendar current = new GregorianCalendar(2026, Calendar.JANUARY, 1, 18, 59);

    assertEquals(6, ObservationTimeSequenceValidation.elapsedWholeHours(current, previous));
  }

  @Test
  public void preservesNegativeElapsedHours() {
    Calendar current = new GregorianCalendar(2026, Calendar.JANUARY, 1, 11, 0);
    Calendar previous = new GregorianCalendar(2026, Calendar.JANUARY, 1, 12, 0);

    assertEquals(-1, ObservationTimeSequenceValidation.elapsedWholeHours(current, previous));
  }
}
