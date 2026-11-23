package turbowin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import org.junit.Test;

public class DateTimeConfirmationWorkflowTest {

  @Test
  public void formatsSingleDigitDateTimeCodesWithLeadingZero() {
    assertEquals("01", DateTimeConfirmationWorkflow.formatTwoDigitCode(1));
    assertEquals("09", DateTimeConfirmationWorkflow.formatTwoDigitCode(9));
  }

  @Test
  public void preservesTwoDigitDateTimeCodes() {
    assertEquals("10", DateTimeConfirmationWorkflow.formatTwoDigitCode(10));
    assertEquals("23", DateTimeConfirmationWorkflow.formatTwoDigitCode(23));
  }

  @Test
  public void usesStrictThirtyMinuteCutoff() {
    assertObservationHour(30, "10");
    assertObservationHour(31, "11");
  }

  @Test
  public void rollsObservationDateAtHourBoundary() {
    prepareForWorkflow();

    DateTimeConfirmationWorkflow.checkAndSet(
        utcCalendar(2024, Calendar.JANUARY, 31, 23, 23),
        utcCalendar(2024, Calendar.JANUARY, 31, 23, 31),
        new RecordingDialogService(JOptionPane.YES_OPTION));

    assertEquals("2024", DateTimeModel.year);
    assertEquals("February", DateTimeModel.month);
    assertEquals("1", DateTimeModel.day);
    assertEquals("0", DateTimeModel.hour);
    assertEquals("02", DateTimeModel.MM_code);
    assertEquals("01", DateTimeModel.YY_code);
    assertEquals("00", DateTimeModel.GG_code);
  }

  @Test
  public void confirmationUpdatesDateTimeAndEnablesAutomaticUpdates() {
    prepareForWorkflow();

    DateTimeConfirmationWorkflow.checkAndSet(
        utcCalendar(2024, Calendar.JANUARY, 15, 10, 10),
        utcCalendar(2024, Calendar.JANUARY, 15, 10, 10),
        new RecordingDialogService(JOptionPane.YES_OPTION));

    assertEquals("2024", DateTimeModel.year);
    assertEquals("January", DateTimeModel.month);
    assertEquals("15", DateTimeModel.day);
    assertEquals("10", DateTimeModel.hour);
    assertEquals("01", DateTimeModel.MM_code);
    assertEquals("15", DateTimeModel.YY_code);
    assertEquals("10", DateTimeModel.GG_code);
    assertTrue(main.use_system_date_time_for_updating);
  }

  @Test
  public void rejectionDisablesAutomaticUpdatesAndWarnsWhenAprIsEnabled() {
    prepareForWorkflow();
    DateTimeModel.year = "previous-year";
    DateTimeModel.month = "previous-month";
    DateTimeModel.day = "previous-day";
    DateTimeModel.hour = "previous-hour";
    DateTimeModel.MM_code = "previous-month-code";
    DateTimeModel.YY_code = "previous-day-code";
    DateTimeModel.GG_code = "previous-hour-code";
    main.use_system_date_time_for_updating = true;
    main.APR = true;
    RecordingDialogService dialogs = new RecordingDialogService(JOptionPane.NO_OPTION);
    String expectedWarning =
        "If this computer is not running on the correct date/time, APR (Automated Pressure Reports)"
            + " will stop working!";

    DateTimeConfirmationWorkflow.checkAndSet(
        utcCalendar(2024, Calendar.JANUARY, 15, 10, 10),
        utcCalendar(2024, Calendar.JANUARY, 15, 10, 10),
        dialogs);

    assertEquals("previous-year", DateTimeModel.year);
    assertEquals("previous-month", DateTimeModel.month);
    assertEquals("previous-day", DateTimeModel.day);
    assertEquals("previous-hour", DateTimeModel.hour);
    assertEquals("previous-month-code", DateTimeModel.MM_code);
    assertEquals("previous-day-code", DateTimeModel.YY_code);
    assertEquals("previous-hour-code", DateTimeModel.GG_code);
    assertFalse(main.use_system_date_time_for_updating);
    assertEquals(expectedWarning, dialogs.warningMessage);
  }

  private void assertObservationHour(int minute, String expectedHour) {
    prepareForWorkflow();

    DateTimeConfirmationWorkflow.checkAndSet(
        utcCalendar(2024, Calendar.JANUARY, 15, 10, minute),
        utcCalendar(2024, Calendar.JANUARY, 15, 10, minute),
        new RecordingDialogService(JOptionPane.YES_OPTION));

    assertEquals(expectedHour, DateTimeModel.hour);
  }

  private void prepareForWorkflow() {
    main.RS232_connection_mode = 3;
    main.displayed_aws_data_obsolate = false;
    main.jTextField3 = new JTextField();
    main.APR = false;
    main.use_system_date_time_for_updating = false;
    DateTimeModel.minute = "00";
  }

  private GregorianCalendar utcCalendar(int year, int month, int day, int hour, int minute) {
    GregorianCalendar calendar = new GregorianCalendar(new SimpleTimeZone(0, "UTC"));
    calendar.clear();
    calendar.set(year, month, day, hour, minute, 0);
    return calendar;
  }

  private static final class RecordingDialogService
      implements DateTimeConfirmationWorkflow.DialogService {
    private final int response;
    private String warningMessage;

    private RecordingDialogService(int response) {
      this.response = response;
    }

    @Override
    public int showConfirmation(String message) {
      return response;
    }

    @Override
    public void showWarning(String message) {
      warningMessage = message;
    }
  }
}
