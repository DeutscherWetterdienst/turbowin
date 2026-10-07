package turbowin;

import static org.junit.Assert.assertArrayEquals;

import java.io.BufferedReader;
import java.io.StringReader;
import org.junit.Test;

public class SemicolonLogFileReaderTest {

  @Test
  public void preservesEmptyFieldsWhenARecordHasTooFewSeparators() throws Exception {
    String[][] data = new String[][] {{"", "", ""}};

    SemicolonLogFileReader.read(new BufferedReader(new StringReader("first;second")), data, 1, 3);

    assertArrayEquals(new String[] {"first", "", ""}, data[0]);
  }

  @Test
  public void stopsAfterConfiguredRowCount() throws Exception {
    String[][] data = new String[][] {{"", ""}};

    SemicolonLogFileReader.read(
        new BufferedReader(new StringReader("first;\nsecond;\nthird;\n")), data, 1, 2);

    assertArrayEquals(new String[] {"first", ""}, data[0]);
  }
}
