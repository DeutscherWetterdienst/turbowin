package turbowin;

import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.data.TableCell;
import org.assertj.swing.exception.ComponentLookupException;
import org.assertj.swing.exception.WaitTimedOutError;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.*;
import org.junit.Test;
import turbowin.matchers.CheckboxMatcher;
import turbowin.matchers.DialogTitleMatcher;
import turbowin.matchers.FrameTitleMatcher;
import turbowin.matchers.RadioButtonMatcher;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * Actual test class
 */
public class ObservationFillingTest extends AbstractUiTest {
    public static final long TIMEOUT = 500L;
    private JMenuItemFixture maintenanceMenu;


    @Override
    protected void onSetUp() {
        this.maintenanceMenu = this.frame.menuItemWithPath("Maintenance");
    }

    @Test
    public void testFillingOutAllValuesForAFM13() throws InterruptedException {
        fillAllStationData();

        fillDateTimeInformation();

        fillPositionInformation();

        fillPressureInformation();

        fillPressureTendencyInformation();

        fillAirTemperatureInformation();

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
    public void testAddingObservers() throws InterruptedException {
        fillLogFileSettings();
        addObserverInformation();
    }



    private void fillPositionInformation() throws InterruptedException {
        this.frame.label(JLabelMatcher.withText("Position")).requireVisible().requireEnabled().click();
        FrameFixture positionFrame = WindowFinder.findFrame(new FrameTitleMatcher("Position, course and speed")).using(this.robot());
        positionFrame.textBox("lat_degree").setText("10");
        positionFrame.textBox("lat_minute").setText("11");
        positionFrame.textBox("lon_degree").setText("12");
        positionFrame.textBox("lon_minute").setText("13");
        positionFrame.radioButton(new RadioButtonMatcher("North")).click();
        positionFrame.radioButton(new RadioButtonMatcher("East")).click();
        positionFrame.radioButton(new RadioButtonMatcher("023 - 067")).click();
        positionFrame.radioButton(new RadioButtonMatcher("6 - 10")).click();
        clickOkButtonInFrame(positionFrame);
    }

    private void handleLocalizedButtonClick(DialogFixture dialog, String textEn, String textDe) {
        try {
            dialog.button(JButtonMatcher.withText(textEn)).requireVisible().requireEnabled().click();
        } catch (ComponentLookupException e) {
            try {
                dialog.button(JButtonMatcher.withText(textDe)).requireVisible().requireEnabled().click();
            } catch (Exception ex) {
                System.out.println("Error finding button: " + e.getMessage() + "\nsecond Error: " + ex.getMessage());
                throw new RuntimeException("Could not find button in either language");
            }
        }
    }

    private void fillDateTimeInformation() throws InterruptedException {
        this.frame.label(JLabelMatcher.withText("Date & Time obs")).requireVisible().requireEnabled().click();
        TimeUnit.MILLISECONDS.sleep(TIMEOUT);

        DialogFixture dateTimeDialog = WindowFinder.findDialog(new DialogTitleMatcher("Date and Time")).using(this.robot());

        handleLocalizedButtonClick(dateTimeDialog, "No", "Nein");

        TimeUnit.MILLISECONDS.sleep(TIMEOUT);

        FrameFixture dateTimeFrame = WindowFinder.findFrame(new FrameTitleMatcher("Date and Time")).using(this.robot());
        dateTimeFrame.list("year").selectItem("2024");
        dateTimeFrame.list("month").selectItem("October");
        dateTimeFrame.list("day").selectItem("29");
        dateTimeFrame.list("hour").selectItem("10");

        clickOkButtonInFrame(dateTimeFrame);
    }

    private void fillPressureInformation() throws InterruptedException {
        this.frame.label(JLabelMatcher.withText("Pressure (read+ic)")).requireVisible().requireEnabled().click();
        FrameFixture pressureFrame = WindowFinder.findFrame(new FrameTitleMatcher("barometer reading")).using(this.robot());
        pressureFrame.textBox("barometer_reading").setText("990");
        pressureFrame.textBox("instrument_correction").setText("0");
        pressureFrame.textBox("deepest_draft").setText("10");

        clickOkButtonInFrame(pressureFrame);
    }

    private void fillPressureTendencyInformation() throws InterruptedException {
        this.frame.label(JLabelMatcher.withText("Pressure tendency")).requireVisible().requireEnabled().click();
        FrameFixture pressureTendencyFrame = WindowFinder.findFrame(new FrameTitleMatcher("barograph reading")).using(this.robot());
        pressureTendencyFrame.textBox().setText("5");
        pressureTendencyFrame.radioButton(new RadioButtonMatcher("(a = 1)")).click();

        clickOkButtonInFrame(pressureTendencyFrame);
    }

    private void fillAirTemperatureInformation() throws InterruptedException {
        this.frame.label(JLabelMatcher.withText("Air temp")).requireVisible().requireEnabled().click();
        FrameFixture temperaturesFrame = WindowFinder.findFrame(new FrameTitleMatcher("Temperatures")).using(this.robot());
        temperaturesFrame.textBox("sea_water_temp").setText("5");
        temperaturesFrame.textBox("air_temp").setText("5");
        temperaturesFrame.textBox("wet_bulb_temp").setText("5");
        temperaturesFrame.textBox("rel_humidity").setText("5");

        clickOkButtonInFrame(temperaturesFrame);
    }

    private void fillTrueWindInformation() throws InterruptedException {
        this.frame.label(JLabelMatcher.withText("True wind")).requireVisible().requireEnabled().click();
        FrameFixture windFrame = WindowFinder.findFrame(new FrameTitleMatcher("Wind")).using(this.robot());
        windFrame.textBox("wind_1").setText("5");
        windFrame.textBox("wind_2").setText("5");

        clickOkButtonInFrame(windFrame);
    }

    private static void clickOkButtonInFrame(FrameFixture frame) throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(TIMEOUT);
        frame.button(JButtonMatcher.withText("OK")).requireVisible().requireEnabled().click();
        TimeUnit.MILLISECONDS.sleep(TIMEOUT);
    }

    private void fillWaveInformation() throws InterruptedException {
        this.frame.label(JLabelMatcher.withText("(Wind) wave per")).requireVisible().requireEnabled().click();
        FrameFixture wavesFrame = WindowFinder.findFrame(new FrameTitleMatcher("Waves")).using(this.robot());
        wavesFrame.textBox("wave_1").setText("5");
        wavesFrame.textBox("wave_2").setText("5");

        clickOkButtonInFrame(wavesFrame);
    }

    private void fillVisibilityInformation() throws InterruptedException {
        this.frame.label(JLabelMatcher.withText("Visibility")).requireVisible().requireEnabled().click();
        FrameFixture visibilityFrame = WindowFinder.findFrame(new FrameTitleMatcher("Visibility")).using(this.robot());
        visibilityFrame.radioButton(new RadioButtonMatcher("0.5 - 1.1 nm")).click();

        clickOkButtonInFrame(visibilityFrame);
    }

    private void fillPresentWeatherInformation() throws InterruptedException {
        this.frame.label(JLabelMatcher.withText("Present weath.")).requireVisible().requireEnabled().click();
        FrameFixture visibilityFrame = WindowFinder.findFrame(new FrameTitleMatcher("Present weather")).using(this.robot());
        visibilityFrame.list("present_weather_list_1").selectItem(2);
        visibilityFrame.list("present_weather_list_2").selectItem(2);
        visibilityFrame.list("present_weather_list_3").selectItem(2);

        clickOkButtonInFrame(visibilityFrame);
    }

    private void fillPastWeatherInformation() throws InterruptedException {
        this.frame.label(JLabelMatcher.withText("Past weath. 1st")).requireVisible().requireEnabled().click();
        FrameFixture pastWeatherFrame = WindowFinder.findFrame(new FrameTitleMatcher("Past weather")).using(this.robot());
        pastWeatherFrame.checkBox(new CheckboxMatcher("drizzle")).click();
        pastWeatherFrame.checkBox(new CheckboxMatcher("rain")).click();

        clickOkButtonInFrame(pastWeatherFrame);
    }

    private void fillLowCloudInformation() throws InterruptedException {
        this.frame.label(JLabelMatcher.withText("Cl")).requireVisible().requireEnabled().click();
        FrameFixture lowCloudsFrame = WindowFinder.findFrame(new FrameTitleMatcher("Clouds low")).using(this.robot());
        lowCloudsFrame.radioButton(new RadioButtonMatcher("Cl2")).click();

        clickOkButtonInFrame(lowCloudsFrame);
    }

    private void fillMiddleCloudInformation() throws InterruptedException {
        this.frame.label(JLabelMatcher.withText("Cm")).requireVisible().requireEnabled().click();
        FrameFixture lowCloudsFrame = WindowFinder.findFrame(new FrameTitleMatcher("Clouds middle")).using(this.robot());
        lowCloudsFrame.radioButton(new RadioButtonMatcher("Cm4")).click();

        clickOkButtonInFrame(lowCloudsFrame);
    }

    private void fillHighCloudInformation() throws InterruptedException {
        this.frame.label(JLabelMatcher.withText("Ch")).requireVisible().requireEnabled().click();
        FrameFixture lowCloudsFrame = WindowFinder.findFrame(new FrameTitleMatcher("Clouds high")).using(this.robot());
        lowCloudsFrame.radioButton(new RadioButtonMatcher("Ch6")).click();

        clickOkButtonInFrame(lowCloudsFrame);
    }

    private void fillTotalCloudCoverInformation() throws InterruptedException {
        this.frame.label(JLabelMatcher.withText("Total cloud cov")).requireVisible().requireEnabled().click();
        FrameFixture lowCloudsFrame = WindowFinder.findFrame(new FrameTitleMatcher("Total cloud cover, amount of Cl (Cm) and height of base of lowest cloud")).using(this.robot());
        lowCloudsFrame.panel("total").radioButton(new RadioButtonMatcher("4/8")).click();
        lowCloudsFrame.panel("cl").radioButton(new RadioButtonMatcher("3/8")).click();
        lowCloudsFrame.panel("low").radioButton(new RadioButtonMatcher("1000 - 2000 ft")).click();

        clickOkButtonInFrame(lowCloudsFrame);
    }

    private void addObserverInformation() throws InterruptedException {
        this.frame.label(JLabelMatcher.withText("Observer")).requireVisible().requireEnabled().click();
        FrameFixture observerFrame = WindowFinder.findFrame(new FrameTitleMatcher("Observer")).using(this.robot());
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

    private void fillLogFileSettings() throws InterruptedException {
        clickOnLogFileSettings();
        enterPassword();
        selectLogFolder();
        closeRestartDialog();
    }

    private void closeRestartDialog() throws InterruptedException {
        DialogFixture turboWinInfoFrame = WindowFinder.findDialog(new DialogTitleMatcher("TurboWin+ info")).using(this.robot());

        TimeUnit.MILLISECONDS.sleep(TIMEOUT);
        turboWinInfoFrame.button(JButtonMatcher.withText("OK")).requireVisible().requireEnabled().click();
    }

    private void clickOnStationData() {
        this.maintenanceMenu.requireVisible().requireEnabled().click();
        JMenuItemFixture maintenanceStationDataMenu = this.frame.menuItemWithPath("Maintenance|Station data...");
        maintenanceStationDataMenu.requireVisible().requireEnabled().click();
    }

    private void clickOnLogFileSettings() {
        this.maintenanceMenu.requireVisible().requireEnabled().click();
        JMenuItemFixture maintenanceStationDataMenu = this.frame.menuItemWithPath("Maintenance|Log files settings...");
        maintenanceStationDataMenu.requireVisible().requireEnabled().click();
    }

    private void enterStationData() throws InterruptedException {
        // Enter station details
        FrameFixture stationDataFrame = WindowFinder.findFrame(new FrameTitleMatcher("Station data")).using(this.robot());
        stationDataFrame.textBox("ship_name").setText("Ship Name 1");
        stationDataFrame.textBox("imo_number").setText("IMO Number 1");
        stationDataFrame.textBox("station_id").setText("00000");
        // Select country
        stationDataFrame.list().clickItem(0);
        stationDataFrame.radioButton(new RadioButtonMatcher("estimated; true speed and true direction")).click();
        stationDataFrame.textBox("max_height_deck_cargo").setText("50");
        stationDataFrame.textBox("difference_sll_wl").setText("10");
        stationDataFrame.textBox("barometer_height").setText("11");
        stationDataFrame.textBox("keel_distance").setText("12");
        stationDataFrame.radioButton(new RadioButtonMatcher("no*")).click();
        stationDataFrame.radioButton(new RadioButtonMatcher("marine screen")).click();
        stationDataFrame.radioButton(new RadioButtonMatcher("intake")).click();

        clickOkButtonInFrame(stationDataFrame);
    }

    private void selectLogFolder() throws InterruptedException {
        FrameFixture logFilesFrame = WindowFinder.findFrame(new FrameTitleMatcher("Log files")).using(this.robot());
        logFilesFrame.button(JButtonMatcher.withText("folder")).click();
        try {
            DialogFixture folderSelectFrame = WindowFinder.findDialog(new DialogTitleMatcher("Speichern")).using(this.robot());
            folderSelectFrame.button(JButtonMatcher.withText("Speichern")).click();
        } catch (WaitTimedOutError e) {
            DialogFixture folderSelectFrame = WindowFinder.findDialog(new DialogTitleMatcher("Save")).using(this.robot());
            folderSelectFrame.button(JButtonMatcher.withText("Save")).click();
        }


        clickOkButtonInFrame(logFilesFrame);
    }

    private void enterPassword() throws InterruptedException {
        try {
            FrameFixture passwordFrame = WindowFinder.findFrame(new FrameTitleMatcher("Password")).withTimeout(TIMEOUT).using(this.robot());
        } catch (WaitTimedOutError e) {
            return;
        }

        FrameFixture passwordFrame = WindowFinder.findFrame(new FrameTitleMatcher("Password")).using(this.robot());
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
        assertEquals(fm13Field.text(), "BBXX 00000 29103 99101 10122 41494 40105 10050 20050 49915 51050 74765 83246 22212 00050 20510 80050=");
    }

    @Override
    protected void onTearDown() {
        this.maintenanceMenu = null;
    }
}
