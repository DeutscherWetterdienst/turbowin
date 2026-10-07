package turbowin;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SatelliteImageUrlBuilderTest {

  @Test
  public void buildsSsecInfraredUrlWithClampedCoordinates() {
    assertEquals(
        "https://realearth.ssec.wisc.edu/?products=globalir&time=latest&center=90.000000,-180.000000&zoom=2",
        SatelliteImageUrlBuilder.buildSsec(main.SATELLITE_IR_IMAGE, 120, -200));
  }

  @Test
  public void buildsSsecVisibleAndSstProducts() {
    assertEquals(
        "https://realearth.ssec.wisc.edu/?products=global1kmvis&time=latest&center=12.000000,34.000000&zoom=2",
        SatelliteImageUrlBuilder.buildSsec(main.SATELLITE_VIS_IMAGE, 12, 34));
    assertEquals(
        "https://realearth.ssec.wisc.edu/?products=NESDIS-SST&time=latest&center=12.000000,34.000000&zoom=2",
        SatelliteImageUrlBuilder.buildSsec(main.SATELLITE_SST_IMAGE, 12, 34));
  }

  @Test
  public void buildsNoaaInfraredLayersAndViewport() {
    assertEquals(
        "https://worldview.earthdata.nasa.gov/?v=0.000000,-90.000000,180.000000,90.000000&l=GOES16:ABI-L2-CMIPF,MODIS_Terra_CorrectedReflectance_TrueColor,Reference_Labels,Reference_Features&al=false",
        SatelliteImageUrlBuilder.buildNoaa(main.SATELLITE_IR_IMAGE, 0, 90));
  }

  @Test
  public void buildsNoaaSstLayers() {
    assertEquals(
        "https://worldview.earthdata.nasa.gov/?v=0.000000,-90.000000,180.000000,90.000000&l=,MODIS_Aqua_L2_Sea_Surface_Temp_Night,MODIS_Aqua_L2_Sea_Surface_Temp_Day,Reference_Labels,Reference_Features&al=false",
        SatelliteImageUrlBuilder.buildNoaa(main.SATELLITE_SST_IMAGE, 0, 90));
  }
}
