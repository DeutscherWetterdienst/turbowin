package turbowin;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;
import javafx.scene.Scene;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.data.TableCell;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.edt.GuiTask;
import org.assertj.swing.exception.ComponentLookupException;
import org.assertj.swing.exception.WaitTimedOutError;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.*;
import org.junit.Test;
import turbowin.matchers.DialogTitleMatcher;
import turbowin.matchers.FrameTitleMatcher;
import turbowin.matchers.RadioButtonMatcher;

/** Actual test class */
public class ObservationFillingTest extends AbstractUiTest {
  public static final long TIMEOUT = 500L;
  private JMenuItemFixture maintenanceMenu;

  @Override
  protected void onSetUp() {
    this.maintenanceMenu = this.frame.menuItemWithPath("Maintenance");
  }

  @Test
  public void testFillingOutAllValuesForAFM13() throws Exception {
    addObserverInformation();

    fillAllStationData();

    fillDateTimeInformation();

    fillPositionInformation();

    fillPressureInformation();

    // With no temperature yet, 990 hPa at 13 m uses the 15 °C fallback: 991.5 hPa.
    new JTextComponentFixture(robot(), main.jTextField10).requireText("991.5 hPa");

    fillPressureTendencyInformation();

    fillAirTemperatureInformation();

    // Entering 5 °C after pressure must update the reduction to 991.6 hPa.
    new JTextComponentFixture(robot(), main.jTextField10).requireText("991.6 hPa");

    fillTrueWindInformation();

    fillWaveInformation();

    fillVisibilityInformation();

    fillPresentWeatherInformation();

    fillPastWeatherInformation();

    fillLowCloudInformation();
    fillMiddleCloudInformation();
    fillHighCloudInformation();

    fillTotalCloudCoverInformation();
    verifyFM13Creation();
  }

  @Test
  public void resetClearsRepresentativeVisibleField() throws InterruptedException {
    GuiActionRunner.execute(
        new GuiTask() {
          @Override
          protected void executeInEDT() {
            main.jTextField36.setText("12 °C");
            mytemp.air_temp = "12";
            main.Reset_all_meteo_parameters();
          }
        });

    new JTextComponentFixture(robot(), main.jTextField36).requireText("");
  }

  private void fillPositionInformation() throws Exception {
    this.frame.label(JLabelMatcher.withText("Position")).requireVisible().requireEnabled().click();
    DialogFixture dialog =
        WindowFinder.findDialog(new DialogTitleMatcher("Position, course and speed"))
            .using(this.robot());
    Scene scene = CloudCoverFxTestSupport.awaitScene((JDialog) dialog.target());
    CloudCoverFxTestSupport.onFx(() -> {
      ((javafx.scene.control.TextField) scene.lookup("#lat_degree")).setText("10");
      ((javafx.scene.control.TextField) scene.lookup("#lat_minute")).setText("11");
      ((javafx.scene.control.TextField) scene.lookup("#lon_degree")).setText("12");
      ((javafx.scene.control.TextField) scene.lookup("#lon_minute")).setText("13");
      ((javafx.scene.control.RadioButton) scene.lookup("#position_option_0_0")).fire();
      ((javafx.scene.control.RadioButton) scene.lookup("#position_option_1_0")).fire();
      ((javafx.scene.control.RadioButton) scene.lookup("#position_option_2_1")).fire();
      ((javafx.scene.control.RadioButton) scene.lookup("#position_option_3_2")).fire();
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {});
  }

  private void handleLocalizedButtonClick(DialogFixture dialog, String textEn, String textDe) {
    try {
      dialog.button(JButtonMatcher.withText(textEn)).requireVisible().requireEnabled().click();
    } catch (ComponentLookupException e) {
      try {
        dialog.button(JButtonMatcher.withText(textDe)).requireVisible().requireEnabled().click();
      } catch (Exception ex) {
        System.out.println(
            "Error finding button: " + e.getMessage() + "\nsecond Error: " + ex.getMessage());
        throw new RuntimeException("Could not find button in either language");
      }
    }
  }

  private void fillDateTimeInformation() throws Exception {
    this.frame
        .label(JLabelMatcher.withText("Date & Time obs"))
        .requireVisible()
        .requireEnabled()
        .click();
    TimeUnit.MILLISECONDS.sleep(TIMEOUT);

    DialogFixture dateTimeDialog =
        WindowFinder.findDialog(new DialogTitleMatcher("Date and Time")).using(this.robot());

    handleLocalizedButtonClick(dateTimeDialog, "No", "Nein");

    TimeUnit.MILLISECONDS.sleep(TIMEOUT);

    DialogFixture manual =
        WindowFinder.findDialog(new DialogTitleMatcher("Date and Time")).using(this.robot());
    Scene scene = CloudCoverFxTestSupport.awaitScene((JDialog) manual.target());
    CloudCoverFxTestSupport.onFx(() -> {
      selectDateTime(scene, "year", "2024");
      selectDateTime(scene, "month", "October");
      selectDateTime(scene, "day", "29");
      selectDateTime(scene, "hour", "10");
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {});
  }

  @SuppressWarnings("unchecked")
  private static void selectDateTime(Scene scene, String id, String value) {
    ((javafx.scene.control.ListView<String>) scene.lookup("#" + id))
        .getSelectionModel()
        .select(value);
  }

  private void fillPressureInformation() throws Exception {
    this.frame
        .label(JLabelMatcher.withText("Pressure (read+ic)"))
        .requireVisible()
        .requireEnabled()
        .click();
    DialogFixture dialog =
        WindowFinder.findDialog(new DialogTitleMatcher("Barometer reading")).using(this.robot());
    Scene scene = CloudCoverFxTestSupport.awaitScene((JDialog) dialog.target());
    CloudCoverFxTestSupport.onFx(() -> {
      ((javafx.scene.control.TextField) scene.lookup("#barometer_reading")).setText("990");
      ((javafx.scene.control.TextField) scene.lookup("#deepest_draft")).setText("10");
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {});
  }

  private void fillPressureTendencyInformation() throws Exception {
    this.frame
        .label(JLabelMatcher.withText("Pressure tendency"))
        .requireVisible()
        .requireEnabled()
        .click();
    DialogFixture dialog =
        WindowFinder.findDialog(new DialogTitleMatcher("Barograph reading")).using(this.robot());
    Scene scene = CloudCoverFxTestSupport.awaitScene((JDialog) dialog.target());
    CloudCoverFxTestSupport.onFx(() -> {
      ((javafx.scene.control.TextField) scene.lookup("#pressure_tendency")).setText("5");
      ((javafx.scene.control.RadioButton) scene.lookup("#barograph_option_1")).fire();
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {});
  }

  private void fillAirTemperatureInformation() throws Exception {
    this.frame.label(JLabelMatcher.withText("Air temp")).requireVisible().requireEnabled().click();
    DialogFixture temperatures =
        WindowFinder.findDialog(new DialogTitleMatcher("Temperatures")).using(this.robot());
    Scene scene = CloudCoverFxTestSupport.awaitScene((JDialog) temperatures.target());
    CloudCoverFxTestSupport.onFx(() -> {
      ((javafx.scene.control.TextField) scene.lookup("#sea_water_temp")).setText("5");
      ((javafx.scene.control.TextField) scene.lookup("#air_temp")).setText("5");
      ((javafx.scene.control.TextField) scene.lookup("#wet_bulb_temp")).setText("5");
      ((javafx.scene.control.TextField) scene.lookup("#rel_humidity")).setText("5");
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {});
  }

  private void fillTrueWindInformation() throws Exception {
    this.frame.label(JLabelMatcher.withText("True wind")).requireVisible().requireEnabled().click();
    DialogFixture wind =
        WindowFinder.findDialog(new DialogTitleMatcher("Wind")).using(this.robot());
    Scene scene = CloudCoverFxTestSupport.awaitScene((JDialog) wind.target());
    CloudCoverFxTestSupport.onFx(() -> {
      ((javafx.scene.control.TextField) scene.lookup("#wind_1")).setText("5");
      ((javafx.scene.control.TextField) scene.lookup("#wind_2")).setText("5");
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {});
  }

  private static void clickOkButtonInFrame(FrameFixture frame) throws InterruptedException {
    TimeUnit.MILLISECONDS.sleep(TIMEOUT);
    frame.button(JButtonMatcher.withText("OK")).requireVisible().requireEnabled().click();
    TimeUnit.MILLISECONDS.sleep(TIMEOUT);
  }

  private void fillWaveInformation() throws Exception {
    this.frame
        .label(JLabelMatcher.withText("(Wind) wave per"))
        .requireVisible()
        .requireEnabled()
        .click();
    DialogFixture waves =
        WindowFinder.findDialog(new DialogTitleMatcher("Waves")).using(this.robot());
    Scene scene = CloudCoverFxTestSupport.awaitScene((JDialog) waves.target());
    CloudCoverFxTestSupport.onFx(() -> {
      ((javafx.scene.control.TextField) scene.lookup("#wave_1")).setText("5");
      ((javafx.scene.control.TextField) scene.lookup("#wave_2")).setText("5");
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {});
  }

  private void fillVisibilityInformation() throws Exception {
    this.frame
        .label(JLabelMatcher.withText("Visibility"))
        .requireVisible()
        .requireEnabled()
        .click();
    DialogFixture visibility =
        WindowFinder.findDialog(new DialogTitleMatcher("Visibility")).using(this.robot());
    Scene scene = CloudCoverFxTestSupport.awaitScene((JDialog) visibility.target());
    CloudCoverFxTestSupport.onFx(() -> {
      scene.getRoot().lookupAll(".radio-button").stream()
          .map(javafx.scene.control.RadioButton.class::cast)
          .filter(item -> item.getText().equals("0.5 - 1.1 nm"))
          .findFirst()
          .orElseThrow()
          .fire();
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {});
  }

  private void fillPresentWeatherInformation() throws Exception {
    this.frame
        .label(JLabelMatcher.withText("Present weath."))
        .requireVisible()
        .requireEnabled()
        .click();
    DialogFixture weather =
        WindowFinder.findDialog(new DialogTitleMatcher("Present weather")).using(this.robot());
    Scene scene = CloudCoverFxTestSupport.awaitScene((JDialog) weather.target());
    CloudCoverFxTestSupport.onFx(() -> {
      ((javafx.scene.control.ListView<?>) scene.lookup("#present_weather_list_1"))
          .getSelectionModel()
          .select(2);
      ((javafx.scene.control.ListView<?>) scene.lookup("#present_weather_list_3"))
          .getSelectionModel()
          .select(2);
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {});
  }

  private void fillPastWeatherInformation() throws Exception {
    this.frame
        .label(JLabelMatcher.withText("Past weath. 1st"))
        .requireVisible()
        .requireEnabled()
        .click();
    DialogFixture weather =
        WindowFinder.findDialog(new DialogTitleMatcher("Past weather")).using(this.robot());
    Scene scene = CloudCoverFxTestSupport.awaitScene((JDialog) weather.target());
    CloudCoverFxTestSupport.onFx(() -> {
      scene.getRoot().lookupAll(".check-box").stream()
          .map(javafx.scene.control.CheckBox.class::cast)
          .filter(item -> item.getText().equals("drizzle") || item.getText().equals("rain"))
          .forEach(javafx.scene.control.CheckBox::fire);
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {});
  }

  private void fillLowCloudInformation() throws Exception {
    this.frame.label(JLabelMatcher.withText("Cl")).requireVisible().requireEnabled().click();
    DialogFixture lowClouds =
        WindowFinder.findDialog(new DialogTitleMatcher("Clouds low")).using(this.robot());
    Scene scene = CloudCoverFxTestSupport.awaitScene((JDialog) lowClouds.target());
    CloudCoverFxTestSupport.onFx(() -> {
      scene.getRoot().applyCss();
      CloudCoverFxTestSupport.selectRadio(scene, "2");
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {});
  }

  private void fillMiddleCloudInformation() throws Exception {
    this.frame.label(JLabelMatcher.withText("Cm")).requireVisible().requireEnabled().click();
    DialogFixture middleClouds =
        WindowFinder.findDialog(new DialogTitleMatcher("Clouds middle")).using(this.robot());
    Scene scene = CloudCoverFxTestSupport.awaitScene((JDialog) middleClouds.target());
    CloudCoverFxTestSupport.onFx(() -> {
      scene.getRoot().applyCss();
      CloudCoverFxTestSupport.selectRadio(scene, "4");
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {});
  }

  private void fillHighCloudInformation() throws Exception {
    this.frame.label(JLabelMatcher.withText("Ch")).requireVisible().requireEnabled().click();
    DialogFixture highClouds =
        WindowFinder.findDialog(new DialogTitleMatcher("Clouds high")).using(this.robot());
    Scene scene = CloudCoverFxTestSupport.awaitScene((JDialog) highClouds.target());
    CloudCoverFxTestSupport.onFx(() -> {
      scene.getRoot().applyCss();
      CloudCoverFxTestSupport.selectRadio(scene, "6");
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {});
  }

  private void fillTotalCloudCoverInformation() throws Exception {
    this.frame
        .label(JLabelMatcher.withText("Total cloud cov"))
        .requireVisible()
        .requireEnabled()
        .click();
    DialogFixture lowCloudsFrame =
        WindowFinder.findDialog(
                new DialogTitleMatcher(
                    "Total cloud cover, amount of Cl (Cm) and height of base of lowest cloud"))
            .using(this.robot());
    Scene scene = CloudCoverFxTestSupport.awaitScene((JDialog) lowCloudsFrame.target());
    CloudCoverFxTestSupport.onFx(() -> {
      scene.getRoot().applyCss();
      CloudCoverFxTestSupport.choose(scene, "total", CloudCoverState.N_4_8);
      CloudCoverFxTestSupport.choose(scene, "cl", CloudCoverState.NH_3_8);
      CloudCoverFxTestSupport.choose(scene, "low", CloudCoverState.H_300_600);
      CloudCoverFxTestSupport.click(scene, "OK");
      return null;
    });
    SwingUtilities.invokeAndWait(() -> {});
  }

  private void addObserverInformation() throws InterruptedException {
    GuiActionRunner.execute(
        () -> ((main) this.frame.target()).Input_Observer_menu_actionPerformed(null));
    FrameFixture observerFrame =
        WindowFinder.findFrame(new FrameTitleMatcher("Observer")).using(this.robot());
    JTableFixture table = observerFrame.table();
    table.enterValue(TableCell.row(0).column(0), "Doe");
    table.enterValue(TableCell.row(0).column(1), "J D");
    table.enterValue(TableCell.row(0).column(2), "Captain");
    table.enterValue(TableCell.row(0).column(3), "N1234");

    clickOkButtonInFrame(observerFrame);
  }

  private void fillAllStationData() throws InterruptedException {
    clickOnStationData();
    enterPassword();
    enterStationData();
    closeRestartDialog();
  }

  private void closeRestartDialog() throws InterruptedException {
    DialogFixture turboWinInfoFrame =
        WindowFinder.findDialog(new DialogTitleMatcher("TurboWin info")).using(this.robot());

    TimeUnit.MILLISECONDS.sleep(TIMEOUT);
    turboWinInfoFrame
        .button(JButtonMatcher.withText("OK"))
        .requireVisible()
        .requireEnabled()
        .click();
  }

  private void clickOnStationData() {
    this.maintenanceMenu.requireVisible().requireEnabled().click();
    JMenuItemFixture maintenanceStationDataMenu =
        this.frame.menuItemWithPath("Maintenance|Station data...");
    maintenanceStationDataMenu.requireVisible().requireEnabled().click();
  }

  private void enterStationData() throws InterruptedException {
    // Enter station details
    FrameFixture stationDataFrame =
        WindowFinder.findFrame(new FrameTitleMatcher("Station data")).using(this.robot());
    stationDataFrame.textBox("ship_name").setText("Ship Name 1");
    stationDataFrame.textBox("imo_number").setText("IMO Number 1");
    stationDataFrame.textBox("station_id").setText("00000");
    // Select country
    stationDataFrame.list().clickItem(0);
    stationDataFrame
        .radioButton(new RadioButtonMatcher("estimated; true speed and true direction"))
        .click();
    stationDataFrame.textBox("max_height_deck_cargo").setText("50");
    stationDataFrame.textBox("difference_sll_wl").setText("10");
    stationDataFrame.textBox("barometer_height").setText("11");
    stationDataFrame.textBox("keel_distance").setText("12");
    stationDataFrame.radioButton(new RadioButtonMatcher("no*")).click();
    stationDataFrame.radioButton(new RadioButtonMatcher("marine screen")).click();
    stationDataFrame.radioButton(new RadioButtonMatcher("intake")).click();

    clickOkButtonInFrame(stationDataFrame);
  }

  private void enterPassword() throws InterruptedException {
    try {
      FrameFixture passwordFrame =
          WindowFinder.findFrame(new FrameTitleMatcher("Password"))
              .withTimeout(TIMEOUT)
              .using(this.robot());
    } catch (WaitTimedOutError e) {
      return;
    }

    FrameFixture passwordFrame =
        WindowFinder.findFrame(new FrameTitleMatcher("Password")).using(this.robot());
    JLabelFixture password = passwordFrame.label(JLabelMatcher.withText("password:"));
    password.requireVisible();
    JTextComponentFixture passwordTextField = passwordFrame.textBox();
    String dummy = "01";
    String combined = "TurboWin JWS" + dummy;
    String finalInput = combined.replaceFirst("TurboWin ", "");
    passwordTextField.setText(finalInput);

    clickOkButtonInFrame(passwordFrame);
    passwordFrame.requireNotVisible();
  }

  private void verifyFM13Creation() throws InterruptedException {
    TimeUnit.MILLISECONDS.sleep(TIMEOUT);
    JTextComponentFixture fm13Field = this.frame.textBox("fm13_field");
    assertEquals(
        "BBXX 00000 29103 99101 10122 41494 40105 10050 20050 49916 51050 74765 83246 22212 00050 20510 80050=",
        fm13Field.text());
  }

  @Override
  protected void onTearDown() {
    this.maintenanceMenu = null;
  }
}
