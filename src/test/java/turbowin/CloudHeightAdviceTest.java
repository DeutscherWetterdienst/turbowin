package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CloudHeightAdviceTest {
  private String cl;
  private String cm;
  private String ch;
  private String temperature;
  private String weather;
  private double dewPoint;
  private int latitude;

  @Before
  public void saveAndInitializeObservation() {
    cl = LowCloudState.cl_code;
    cm = MiddleCloudState.cm_code;
    ch = HighCloudState.ch_code;
    temperature = mytemp.air_temp;
    dewPoint = mytemp.double_dew_point;
    weather = PresentWeatherState.ww_code;
    latitude = myposition.int_latitude_degrees;
    LowCloudState.cl_code = "";
    MiddleCloudState.cm_code = "";
    HighCloudState.ch_code = "";
    mytemp.air_temp = "";
    mytemp.double_dew_point = main.INVALID;
    PresentWeatherState.ww_code = "";
    myposition.int_latitude_degrees = 45;
  }

  @After
  public void restoreObservation() {
    LowCloudState.cl_code = cl;
    MiddleCloudState.cm_code = cm;
    HighCloudState.ch_code = ch;
    mytemp.air_temp = temperature;
    mytemp.double_dew_point = dewPoint;
    PresentWeatherState.ww_code = weather;
    myposition.int_latitude_degrees = latitude;
  }

  @Test
  public void lowCloudAdviceUsesTemperatureAndDewPoint() {
    LowCloudState.cl_code = "1";
    mytemp.air_temp = "16";
    mytemp.double_dew_point = 12;
    assertEquals(4, CloudHeightAdvice.compute()); // 500 m: 300–600 m
    mytemp.double_dew_point = main.INVALID;
    assertEquals(main.INVALID, CloudHeightAdvice.compute());
  }

  @Test
  public void noLowOrMiddleCloudDistinguishesCloudlessAndHighCloud() {
    LowCloudState.cl_code = "0";
    MiddleCloudState.cm_code = "0";
    HighCloudState.ch_code = "0";
    assertEquals(-9, CloudHeightAdvice.compute());
    HighCloudState.ch_code = "1";
    assertEquals(9, CloudHeightAdvice.compute());
  }

  @Test
  public void middleCloudAdviceDependsOnPresentWeather() {
    LowCloudState.cl_code = "0";
    MiddleCloudState.cm_code = "2";
    PresentWeatherState.ww_code = "61";
    assertEquals(3, CloudHeightAdvice.compute());
    PresentWeatherState.ww_code = "0";
    assertEquals(7, CloudHeightAdvice.compute());
  }
}
