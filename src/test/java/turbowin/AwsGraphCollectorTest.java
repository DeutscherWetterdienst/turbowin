package turbowin;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

public class AwsGraphCollectorTest {

  @Test
  public void storesPrimarySecondaryAndTimestampValues() {
    String[] primary = {"", ""};
    String[] secondary = {"", ""};
    String[] timestamps = {"", ""};
    AwsGraphCollector collector = new AwsGraphCollector(primary, secondary, timestamps);

    collector.add(new AwsGraphRecord("202601010005", 5, "12.3", "15.6"), 1);

    assertArrayEquals(new String[] {"", "12.3"}, primary);
    assertArrayEquals(new String[] {"", "15.6"}, secondary);
    assertArrayEquals(new String[] {"", "202601010005"}, timestamps);
  }

  @Test
  public void doesNotOverwriteTimestampWhenPrimaryValueIsMissing() {
    String[] primary = {"old"};
    String[] secondary = {""};
    String[] timestamps = {"old-time"};
    AwsGraphCollector collector = new AwsGraphCollector(primary, secondary, timestamps);

    collector.add(new AwsGraphRecord("202601010005", 5, null, "15.6"), 0);

    assertArrayEquals(new String[] {"old"}, primary);
    assertArrayEquals(new String[] {"15.6"}, secondary);
    assertArrayEquals(new String[] {"old-time"}, timestamps);
  }
}
