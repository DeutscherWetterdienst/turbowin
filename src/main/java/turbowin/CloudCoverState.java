package turbowin;

/** Shared cloud-cover observation values and code descriptions. */
final class CloudCoverState {
  private CloudCoverState() {}

  // public constants
  public static final String N_CLOUDLESS = "cloudless";
  public static final String N_1_8 = "1/8";
  public static final String N_2_8 = "2/8";
  public static final String N_3_8 = "3/8";
  public static final String N_4_8 = "4/8";
  public static final String N_5_8 = "5/8";
  public static final String N_6_8 = "6/8";
  public static final String N_7_8 = "7/8";
  public static final String N_COMPLETELY_OVERCAST = "8/8 (compl. overcast)";
  public static final String N_OBSCURED = "obscured";
  public static final String N_NOT_DETERMINED = "not determined";

  public static final String NH_0_8 = "0/8";
  public static final String NH_1_8 = "1/8";
  public static final String NH_2_8 = "2/8";
  public static final String NH_3_8 = "3/8";
  public static final String NH_4_8 = "4/8";
  public static final String NH_5_8 = "5/8";
  public static final String NH_6_8 = "6/8";
  public static final String NH_7_8 = "7/8";
  public static final String NH_8_8 = "8/8";
  public static final String NH_OBSCURED = "obscured";
  public static final String NH_NOT_DETERMINED = "not determined";

  public static final String H_CLOUDLESS = "cloudless";
  public static final String H_0_50 = "0 - 50 m (0 - 150 ft)";
  public static final String H_50_100 = "50 - 100 m (150 - 300 ft)";
  public static final String H_100_200 = "100 - 200 m (300 - 600 ft)";
  public static final String H_200_300 = "200 - 300 m (600 - 1000 ft)";
  public static final String H_300_600 = "300 - 600 m (1000 - 2000 ft)";
  public static final String H_600_1000 = "600 - 1000 m (2000 - 3000 ft)";
  public static final String H_1000_1500 = "1000 - 1500 m (3000 - 5000 ft)";
  public static final String H_1500_2000 = "1500 - 2000 m (5000 - 6500 ft)";
  public static final String H_2000_2500 = "2000 - 2500 m (6500 - 8000 ft)";
  public static final String H_GROTER_2500 = ">= 2500 m (>= 8000 ft)";
  public static final String H_NOT_DETERMINED = "not determined";

  // scope this module + main module
  public static String N = "";
  public static String Nh = "";
  public static String h = "";
  public static String N_code = ""; // always 1 char
  public static String Nh_code = ""; // always 1 char
  public static String h_code = ""; // always 1 char
}
