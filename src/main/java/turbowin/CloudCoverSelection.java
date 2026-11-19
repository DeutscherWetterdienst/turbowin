package turbowin;

/** Applies the cloud-cover panel's legacy validation and WMO code conversion. */
final class CloudCoverSelection {

  private CloudCoverSelection() {}

  static Result validate(String n, String nh, String h) {
    if (CloudCoverState.N_CLOUDLESS.equals(n) && !CloudCoverState.NH_0_8.equals(nh)) {
      return invalid("If 'total cloud cover' = 'cloudless' then 'amount of Cl' must be '0/8'");
    }
    if (CloudCoverState.N_CLOUDLESS.equals(n) && !CloudCoverState.H_CLOUDLESS.equals(h)) {
      return invalid(
          "If 'total cloud cover' = 'cloudless' then 'height of base of lowest cloud' must be 'cloudless'");
    }
    if (!CloudCoverState.N_CLOUDLESS.equals(n) && CloudCoverState.H_CLOUDLESS.equals(h)) {
      return invalid(
          "If 'height of base of lowest cloud' = 'cloudless' then 'total cloud cover' must be 'cloudless'");
    }
    if (CloudCoverState.N_OBSCURED.equals(n)
        && !CloudCoverState.H_NOT_DETERMINED.equals(h)
        && !h.isEmpty()) {
      return invalid(
          "If 'total cloud cover' = 'obscured' then 'height of base of lowest cloud' must be 'not determined'");
    }
    if (CloudCoverState.N_OBSCURED.equals(n)
        && !CloudCoverState.NH_OBSCURED.equals(nh)
        && !CloudCoverState.NH_NOT_DETERMINED.equals(nh)
        && !nh.isEmpty()) {
      return invalid(
          "If 'total cloud cover' = 'obscured' then 'amount of Cl (or Cm)' must be 'obscured' or 'not determined'");
    }
    if ((CloudCoverState.N_NOT_DETERMINED.equals(n) || n.isEmpty())
        && !CloudCoverState.H_NOT_DETERMINED.equals(h)
        && !h.isEmpty()) {
      return invalid(
          "If 'total cloud cover' = 'not determined' then 'height of base of lowest cloud in the sky' must be 'not determined'");
    }
    if (CloudCoverState.N_NOT_DETERMINED.equals(n)
        && !CloudCoverState.NH_NOT_DETERMINED.equals(nh)
        && !nh.isEmpty()) {
      return invalid(
          "If 'total cloud cover' = 'not determined' then 'amount of Cl (or Cm)' must be 'not determined'");
    }
    if (numericCover(n) && numericAmount(nh) && number(nh) > number(n)) {
      return invalid("'total cloud cover' must be >= 'amount of Cl (or Cm)'");
    }
    if (CloudCoverState.NH_0_8.equals(nh)
        && !CloudCoverState.H_CLOUDLESS.equals(h)
        && !CloudCoverState.H_GROTER_2500.equals(h)
        && !CloudCoverState.H_NOT_DETERMINED.equals(h)
        && !h.isEmpty()) {
      return invalid(
          "If 'amount of Cl (or Cm if Cl not present)' = 0/8 then 'height of base of lowest cloud' cannot be "
              + h);
    }
    return new Result(n, nh, h, codeN(n), codeNh(nh), codeHeight(h), null);
  }

  private static boolean numericCover(String value) {
    return CloudCoverState.N_COMPLETELY_OVERCAST.equals(value)
        || (value.length() == 3 && value.charAt(1) == '/' && value.charAt(2) == '8');
  }

  private static boolean numericAmount(String value) {
    return value.length() == 3 && value.charAt(1) == '/' && value.charAt(2) == '8';
  }

  private static int number(String value) {
    if (CloudCoverState.N_COMPLETELY_OVERCAST.equals(value)) return 8;
    return Integer.parseInt(value.substring(0, 1));
  }

  private static String codeN(String value) {
    if (CloudCoverState.N_CLOUDLESS.equals(value)) return "0";
    if (CloudCoverState.N_COMPLETELY_OVERCAST.equals(value)) return "8";
    if (numericCover(value)) return value.substring(0, 1);
    if (CloudCoverState.N_OBSCURED.equals(value)) return "9";
    return "/";
  }

  private static String codeNh(String value) {
    if (numericAmount(value)) return value.substring(0, 1);
    if (CloudCoverState.NH_OBSCURED.equals(value)) return "9";
    return "/";
  }

  private static String codeHeight(String value) {
    if (CloudCoverState.H_CLOUDLESS.equals(value)) return "9";
    if (CloudCoverState.H_0_50.equals(value)) return "0";
    if (CloudCoverState.H_50_100.equals(value)) return "1";
    if (CloudCoverState.H_100_200.equals(value)) return "2";
    if (CloudCoverState.H_200_300.equals(value)) return "3";
    if (CloudCoverState.H_300_600.equals(value)) return "4";
    if (CloudCoverState.H_600_1000.equals(value)) return "5";
    if (CloudCoverState.H_1000_1500.equals(value)) return "6";
    if (CloudCoverState.H_1500_2000.equals(value)) return "7";
    if (CloudCoverState.H_2000_2500.equals(value)) return "8";
    if (CloudCoverState.H_GROTER_2500.equals(value)) return "9";
    return "/";
  }

  private static Result invalid(String message) {
    return new Result("", "", "", "/", "/", "/", message);
  }

  static final class Result {
    final String n;
    final String nh;
    final String h;
    final String nCode;
    final String nhCode;
    final String hCode;
    final String error;

    private Result(
        String n, String nh, String h, String nCode, String nhCode, String hCode, String error) {
      this.n = n;
      this.nh = nh;
      this.h = h;
      this.nCode = nCode;
      this.nhCode = nhCode;
      this.hCode = hCode;
      this.error = error;
    }

    boolean valid() {
      return error == null;
    }
  }
}
