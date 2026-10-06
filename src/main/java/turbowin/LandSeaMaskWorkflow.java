package turbowin;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/** Reads the legacy one-degree land-sea mask and interprets its SAM values. */
final class LandSeaMaskWorkflow {

  private LandSeaMaskWorkflow() {}

  static boolean check(LandSeaMaskPositionCalculator.Position maskPosition) {
    try (InputStream input =
        LandSeaMaskWorkflow.class.getResourceAsStream(main.ICONS_DIRECTORY + "zgf_sam")) {
      return check(input, maskPosition);
    } catch (Exception ex) {
      System.out.println("--- Function Check_Land_Sea_Mask(): " + ex);
      return true;
    }
  }

  static boolean check(InputStream input, LandSeaMaskPositionCalculator.Position maskPosition) {
    boolean seaPosition = true;

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
      String record;
      while ((record = reader.readLine()) != null && maskPosition.octant() != main.INVALID) {
        if (record.length() == 127) {
          try {
            int tenDegreeCell = Integer.valueOf(record.substring(0, 3));

            if (tenDegreeCell == maskPosition.tenDegreeCell()) {
              int samValue =
                  Integer.valueOf(
                      record.substring(maskPosition.samIndex(), maskPosition.samIndex() + 1));
              // SAM values: 0 sea, 1 coast, 2 wrong position, 3 land, 4 not yet classified.
              if (samValue == 2 || samValue == 3) {
                seaPosition = false;
              } else if (samValue == 0 || samValue == 1 || samValue == 4) {
                seaPosition = true;
              }
              break;
            }
          } catch (NumberFormatException ignored) {
          }
        } else {
          break;
        }
      }
    } catch (Exception ex) {
      // JOptionPane.showMessageDialog(null, "Reading error 'sea-land mask' file", APPLICATION_NAME
      // + " error", JOptionPane.WARNING_MESSAGE);
      System.out.println("--- Function Check_Land_Sea_Mask(): " + ex);
    }

    return seaPosition;
  }
}
