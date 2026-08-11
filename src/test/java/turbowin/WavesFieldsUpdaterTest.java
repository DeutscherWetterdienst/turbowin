package turbowin;

import static org.junit.Assert.assertEquals;

import javax.swing.JTextField;
import org.junit.Test;

public class WavesFieldsUpdaterTest {

  @Test
  public void formatsWaveAndSwellValuesWithUnits() {
    prepareFields();
    mywaves.wind_waves_period = "8";
    mywaves.wind_waves_height = "2";
    mywaves.swell_1_period = "10";
    mywaves.swell_1_height = "3";
    mywaves.swell_1_dir = "180";
    mywaves.swell_2_period = "12";
    mywaves.swell_2_height = "4";
    mywaves.swell_2_dir = "270";

    WavesFieldsUpdater.update();

    assertEquals("8 sec", main.jTextField23.getText());
    assertEquals("2 metres", main.jTextField22.getText());
    assertEquals("10 sec", main.jTextField26.getText());
    assertEquals("3 metres", main.jTextField25.getText());
    assertEquals("180 degr", main.jTextField24.getText());
    assertEquals("12 sec", main.jTextField29.getText());
    assertEquals("4 metres", main.jTextField28.getText());
    assertEquals("270 degr", main.jTextField27.getText());
  }

  @Test
  public void preservesSpecialSwellValues() {
    prepareFields();
    mywaves.swell_1_period = "confused";
    mywaves.swell_1_height = "no swell";
    mywaves.swell_1_dir = "confused";

    WavesFieldsUpdater.update();

    assertEquals("confused", main.jTextField26.getText());
    assertEquals("no swell", main.jTextField25.getText());
    assertEquals("confused", main.jTextField24.getText());
  }

  private static void prepareFields() {
    main.jTextField22 = new JTextField();
    main.jTextField23 = new JTextField();
    main.jTextField24 = new JTextField();
    main.jTextField25 = new JTextField();
    main.jTextField26 = new JTextField();
    main.jTextField27 = new JTextField();
    main.jTextField28 = new JTextField();
    main.jTextField29 = new JTextField();
    main.jTextField4 = new JTextField();
    main.RS232_connection_mode = 3;
  }
}
