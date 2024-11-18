package turbowin;

import junit.framework.TestCase;


public class OsmTest extends TestCase {
    public void testCalculator() {
        OSM osm = new OSM();
        assert osm.OSM_control_center();
    }
}
