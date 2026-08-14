package turbowin;

import java.io.BufferedWriter;
import java.io.IOException;

/** Writes the shared Leaflet shell used by the OSM map pages. */
final class OsmMapHtmlWriter {

  private OsmMapHtmlWriter() {}

  static void writeOnlineShell(BufferedWriter out, String title) throws IOException {
    out.write("<html>");
    out.newLine();
    out.write("<head>");
    out.newLine();
    out.write("  <meta charset=utf-8 />");
    out.newLine();
    out.newLine();
    out.write("  <title>" + title + "</title>");
    out.newLine();
    out.write(
        "  <meta name='viewport' content='initial-scale=1,maximum-scale=1,user-scalable=no' />");
    out.newLine();
    out.newLine();
    out.write("  <!-- Load Leaflet from CDN -->");
    out.newLine();
    out.write(main.LEAFLET_CSS_URL);
    out.newLine();
    out.write(main.LEAFLET_CSS_INTEGRITY);
    out.newLine();
    out.write("  crossorigin=\"\"/>");
    out.newLine();
    out.write(main.LEAFLET_JS_URL);
    out.newLine();
    out.write(main.LEAFLET_JS_INTEGRITY);
    out.newLine();
    out.write("  crossorigin=\"\"></script>");
    out.newLine();
    out.newLine();
    out.write("  <!-- Load Esri Leaflet from CDN -->");
    out.newLine();
    out.write(main.LEAFLET_ESRI_URL);
    out.newLine();
    out.write(main.LEAFLET_ESRI_INTEGRITY);
    out.newLine();
    out.write("  crossorigin=\"\"></script>");
    out.newLine();
    out.newLine();
    out.write("  <style>");
    out.newLine();
    out.write("    body { margin:0; padding:0; }");
    out.newLine();
    out.write("    #map { position: absolute; top:0; bottom:0; right:0; left:0; }");
    out.newLine();
    out.write("  </style>");
    out.newLine();
    out.newLine();
    out.write("</head>");
    out.newLine();
    out.newLine();
    out.write("<body>");
    out.newLine();
    out.write("<div id=\"map\"></div>");
    out.newLine();
    out.newLine();
    out.write("<script>");
    out.newLine();
    // origin of the map at 0.0 N and 0.0 E
    out.write("  var map = L.map(\"map\").setView([" + "0.0" + "," + "0.0" + "], 3);");
    out.newLine();
    out.write("  L.esri.basemapLayer(\"Topographic\").addTo(map);");
    out.newLine();
    out.write("  var markerOptions = { };");
    out.newLine();
    out.newLine();
  }

  static void writeOfflineShell(BufferedWriter out, String title, String iconOptions)
      throws IOException {
    out.write("<html>");
    out.newLine();
    out.write("<head>");
    out.newLine();
    out.newLine();
    out.write("  <title>" + title + "</title>");
    out.newLine();
    out.write(
        "  <meta name='viewport' content='initial-scale=1,maximum-scale=1,user-scalable=no' />");
    out.newLine();
    out.newLine();
    out.write("  <link rel=\"stylesheet\" charset=\"utf-8\" href=\"leaflet.css\" />");
    out.newLine();
    out.write("  <script type=\"text/javascript\" charset=\"utf-8\" src=\"leaflet.js\"></script>");
    out.newLine();
    out.newLine();
    out.write("  <style>");
    out.newLine();
    out.write("    body { margin:0; padding:0; }");
    out.newLine();
    out.write("    #map { position: absolute; top:0; bottom:0; right:0; left:0; }");
    out.newLine();
    out.write("  </style>");
    out.newLine();
    out.newLine();
    out.write("<body>");
    out.newLine();
    out.write("<div id=\"map\"></div>");
    out.newLine();
    out.newLine();
    out.write("<script>");
    out.newLine();
    out.write("var map = L.map('map').setView([0.0,0.0], 3);");
    out.newLine();
    out.write(
        "L.tileLayer('OSMPublicTransport/{z}/{x}/{y}.png',{ maxZoom: 4, minZoom:2 }).addTo(map);");
    out.newLine();
    out.write(iconOptions);
    out.newLine();
    out.write("var markerOptions = { icon: L.icon(iconOptions) };");
    out.newLine();
    out.newLine();
  }

  static void writeDocumentEnd(BufferedWriter out) throws IOException {
    out.newLine();
    out.write("</script>");
    out.newLine();
    out.newLine();
    out.write("</body>");
    out.newLine();
    out.write("</html>");
    out.newLine();
  }

  static void writeMarker(BufferedWriter out, String latitude, String longitude, String popup)
      throws IOException {
    out.write(
        "  L.marker(["
            + latitude
            + ","
            + longitude
            + "], markerOptions).addTo(map).bindPopup("
            + popup
            + ").openPopup();");
    out.newLine();
  }
}
