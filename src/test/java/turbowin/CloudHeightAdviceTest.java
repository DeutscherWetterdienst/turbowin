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
    cl = mycl.cl_code;
    cm = mycm.cm_code;
    ch = mych.ch_code;
    temperature = mytemp.air_temp;
    dewPoint = mytemp.double_dew_point;
    weather = mypresentweather.ww_code;
    latitude = myposition.int_latitude_degrees;
    mycl.cl_code = "";
    mycm.cm_code = "";
    mych.ch_code = "";
    mytemp.air_temp = "";
    mytemp.double_dew_point = main.INVALID;
    mypresentweather.ww_code = "";
    myposition.int_latitude_degrees = 45;
  }

  @After
  public void restoreObservation() {
    mycl.cl_code = cl;
    mycm.cm_code = cm;
    mych.ch_code = ch;
    mytemp.air_temp = temperature;
    mytemp.double_dew_point = dewPoint;
    mypresentweather.ww_code = weather;
    myposition.int_latitude_degrees = latitude;
  }

  @Test
  public void lowCloudAdviceUsesTemperatureAndDewPoint() {
    mycl.cl_code = "1";
    mytemp.air_temp = "16";
    mytemp.double_dew_point = 12;
    assertEquals(4, CloudHeightAdvice.compute()); // 500 m: 300–600 m
    mytemp.double_dew_point = main.INVALID;
    assertEquals(main.INVALID, CloudHeightAdvice.compute());
  }

  @Test
  public void noLowOrMiddleCloudDistinguishesCloudlessAndHighCloud() {
    mycl.cl_code = "0";
    mycm.cm_code = "0";
    mych.ch_code = "0";
    assertEquals(-9, CloudHeightAdvice.compute());
    mych.ch_code = "1";
    assertEquals(9, CloudHeightAdvice.compute());
  }

  @Test
  public void middleCloudAdviceDependsOnPresentWeather() {
    mycl.cl_code = "0";
    mycm.cm_code = "2";
    mypresentweather.ww_code = "61";
    assertEquals(3, CloudHeightAdvice.compute());
    mypresentweather.ww_code = "0";
    assertEquals(7, CloudHeightAdvice.compute());
  }
}
