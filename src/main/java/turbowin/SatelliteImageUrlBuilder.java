package turbowin;

/** Builds the satellite image URLs used by the SSEC and NOAA integrations. */
final class SatelliteImageUrlBuilder {

  private SatelliteImageUrlBuilder() {}

  static String buildSsec(String satelliteImageMode, int latitude, int longitude) {
    String products = "";

    if (satelliteImageMode.equals(main.SATELLITE_IR_IMAGE)) {
      products = "globalir";
    } else if (satelliteImageMode.equals(main.SATELLITE_VIS_IMAGE)) {
      products = "global1kmvis";
    } else if (satelliteImageMode.equals(main.SATELLITE_SST_IMAGE)) {
      products = "NESDIS-SST";
    }

    double centerLat = Math.max(-90, Math.min(90, latitude));
    double centerLon = Math.max(-180, Math.min(180, longitude));

    // Keep zoom <= 4 to reduce the chance of a watermark.
    return String.format(
        "https://realearth.ssec.wisc.edu/?products=%s&time=latest&center=%.6f,%.6f&zoom=%d",
        products, centerLat, centerLon, 2);
  }

  static String buildNoaa(String satelliteImageMode, int latitude, int longitude) {
    // Worldview real-time frames can contain black areas; cropping, viewport changes, and layer
    // switching do not reliably fix this tile-service behavior.
    double centerLat = Math.max(-90.0, Math.min(90.0, latitude));
    double centerLon = Math.max(-180.0, Math.min(180.0, longitude));
    // These half-width and half-height values provide the approximate zoom level and keep the
    // bounding box within the valid world limits after clamping.
    double halfWidth = 90.0;
    double halfHeight = 90.0;
    String layer = "GOES16:ABI-L2-CMIPF";

    if (satelliteImageMode.equals(main.SATELLITE_SST_IMAGE)) {
      // The alternate GHRSST/MUR SST layer was not used (November 2025).
      layer = "";
    }

    double minLon = Math.max(-180.0, centerLon - halfWidth);
    double maxLon = Math.min(180.0, centerLon + halfWidth);
    double minLat = Math.max(-90.0, centerLat - halfHeight);
    double maxLat = Math.min(90.0, centerLat + halfHeight);
    String viewport = String.format("%.6f,%.6f,%.6f,%.6f", minLon, minLat, maxLon, maxLat);

    StringBuilder url = new StringBuilder("https://worldview.earthdata.nasa.gov/?");
    url.append("v=").append(viewport);
    url.append("&l=").append(layer);
    if (satelliteImageMode.equals(main.SATELLITE_IR_IMAGE)) {
      // The alternate MCMIPF layer was not used because switching layers has no effect on the
      // tile gaps.
      // Worldview needs a visible/color layer for imagery to appear reliably; TrueColor also
      // provides a land/sea reference when the main layer is semi-transparent.
      url.append(",MODIS_Terra_CorrectedReflectance_TrueColor");
    } else if (satelliteImageMode.equals(main.SATELLITE_SST_IMAGE)) {
      // The alternate daily SST layers were not working (November 2025).
      url.append(",MODIS_Aqua_L2_Sea_Surface_Temp_Night,MODIS_Aqua_L2_Sea_Surface_Temp_Day");
    }
    // Without reference layers, the Worldview map may appear empty, dark, or not loaded yet.
    url.append(",Reference_Labels,Reference_Features");
    // Clear all previously stored layers before loading this URL.
    url.append("&al=false");
    return url.toString();
  }
}
