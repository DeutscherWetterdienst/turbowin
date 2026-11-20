package turbowin;

import java.util.ArrayList;
import java.util.List;

/** Marine present-weather descriptions and their original ordering and codes. */
final class PresentWeatherCatalog {
  record Weather(String code, String description) {}

  record Group(String description, List<Weather> options) {}

  private PresentWeatherCatalog() {}

  public static final String ww_0 = "cloud development not observed or not observable";
  public static final String ww_1 = "clouds generally dissolving or becoming less developed";
  public static final String ww_2 = "state of the sky on the whole unchanged";
  public static final String ww_3 = "clouds generally forming or developing";
  public static final String ww_4 =
      "visibility reduced by smoke, e.g. industrial smoke, volcanic ash, forest fire";
  public static final String ww_5 = "haze";
  public static final String ww_6 =
      "widespread dust in suspension in the air, not raised by wind, at time of observation";
  public static final String ww_7 = "blowing spray at the station";
  public static final String ww_9 =
      "duststorm or sandstorm within sight at time of observation or during the preceding hour";
  public static final String ww_10 = "mist (visibility > 0.5 nm)";
  public static final String ww_11 = "shallow fog in patches (not deeper than 10 metres)";
  public static final String ww_12 =
      "shallow fog, more or less continuous (not deeper than 10 metres)";
  public static final String ww_13 = "lightning visible, no thunder heard";
  public static final String ww_14 = "precipitation, not reaching the surface of sea";
  public static final String ww_15 = "precipitation, beyond 2.7 nm, reaching the surface of sea";
  public static final String ww_16 = "precipitation, within 2.7 nm, reaching the surface of sea";
  public static final String ww_17 = "thunder audible during the last 10 minutes";
  public static final String ww_18 = "squalls, at or within sight of station";
  public static final String ww_19 =
      "funnel cloud(s) (tornado cloud or waterspout), at or within sight of station";
  public static final String ww_20 = "drizzle (not freezing) or snow grains";
  public static final String ww_21 = "rain (not freezing), not in showers";
  public static final String ww_22 = "snow, not in showers";
  public static final String ww_23 = "rain and snow or ice pellets, not in showers";
  public static final String ww_24 = "freezing drizzle or freezing rain, not in showers";
  public static final String ww_25 = "shower(s) of rain";
  public static final String ww_26 = "shower(s) of snow, or of rain and snow";
  public static final String ww_27 = "shower(s) of hail, or of hail and rain";
  public static final String ww_28 = "fog or ice fog";
  public static final String ww_29 = "thunderstorm, with or without precipitation";
  public static final String ww_30 =
      "duststorm or sandstorm, decreasing last hour, slight or moderate";
  public static final String ww_31 =
      "duststorm or sandstorm, unchanging last hour, slight or moderate";
  public static final String ww_32 =
      "duststorm or sandstorm, increasing last hour, slight or moderate";
  public static final String ww_33 = "duststorm or sandstorm, decreasing last hour, severe";
  public static final String ww_34 = "duststorm or sandstorm, unchanging last hour, severe";
  public static final String ww_35 = "duststorm or sandstorm, increasing last hour, severe";
  public static final String ww_36 = "drifting snow, below eye level, slight or moderate";
  public static final String ww_37 = "drifting snow, below eye level, heavy";
  public static final String ww_38 = "blowing snow, above eye level, slight or moderate";
  public static final String ww_39 = "blowing snow, above eye level, heavy";
  public static final String ww_40 =
      "fogbank (app. vis. in bank < 0.5 nm) at a distance;not at stat. during last hr";
  public static final String ww_41 = "fog in patches (apparent vis. in patches < 0.5 nm)";
  public static final String ww_42 = "fog (vis. < 0.5 nm), thinning in last hour, sky discernible";
  public static final String ww_43 =
      "fog (vis. < 0.5 nm), thinning in last hour, sky not discernible";
  public static final String ww_44 =
      "fog (vis. < 0.5 nm), unchanging in last hour, sky discernible";
  public static final String ww_45 =
      "fog (vis. < 0.5 nm), unchanging in last hour, sky not discernible";
  public static final String ww_46 =
      "fog (vis. < 0.5 nm), beginning or thickening in last hour, sky discernible";
  public static final String ww_47 =
      "fog (vis. < 0.5 nm), beginning or thickening in last hour, sky not discernible";
  public static final String ww_48 = "fog (vis. < 0.5 nm), depositing rime, sky discernible";
  public static final String ww_49 = "fog (vis. < 0.5 nm), depositing rime, sky not discernible";
  public static final String ww_50 = "slight drizzle, not freezing, intermittent";
  public static final String ww_51 = "slight drizzle, not freezing, continous";
  public static final String ww_52 = "moderate drizzle, not freezing, intermittent";
  public static final String ww_53 = "moderate drizzle, not freezing, continous";
  public static final String ww_54 = "dense drizzle, not freezing, intermittent";
  public static final String ww_55 = "dense drizzle, not freezing, continous";
  public static final String ww_56 = "drizzle, freezing, slight";
  public static final String ww_57 = "drizzle, freezing, moderate or dense";
  public static final String ww_58 = "drizzle and rain, slight";
  public static final String ww_59 = "drizzle and rain, moderate or dense";
  public static final String ww_60 = "slight rain, not freezing, intermittent";
  public static final String ww_61 = "slight rain, not freezing, continous";
  public static final String ww_62 = "moderate rain, not freezing, intermittent";
  public static final String ww_63 = "moderate rain, not freezing, continous";
  public static final String ww_64 = "heavy rain, not freezing, intermittent";
  public static final String ww_65 = "heavy rain, not freezing, continous";
  public static final String ww_66 = "rain, freezing, slight";
  public static final String ww_67 = "rain, freezing, moderate or heavy";
  public static final String ww_68 = "rain and snow or drizzle and snow, slight";
  public static final String ww_69 = "rain and snow or drizzle and snow, moderate or heavy";
  public static final String ww_70 = "slight fall of snow flakes, intermittent";
  public static final String ww_71 = "slight fall of snow flakes, continous";
  public static final String ww_72 = "moderate fall of snow flakes, intermittent";
  public static final String ww_73 = "moderate fall of snow flakes, continous";
  public static final String ww_74 = "heavy fall of snow flakes, intermittent";
  public static final String ww_75 = "heavy fall of snow flakes, continous";
  public static final String ww_76 = "ice prisms (with or without fog)";
  public static final String ww_77 = "snow grains (with or without fog)";
  public static final String ww_78 = "isolated star-like snow crystals (with or without fog)";
  public static final String ww_79 = "ice pellets";
  public static final String ww_80 = "rain shower(s), slight";
  public static final String ww_81 = "rain shower(s), moderate or heavy";
  public static final String ww_82 = "rain shower(s), violent";
  public static final String ww_83 = "shower(s) of rain and snow mixed, slight";
  public static final String ww_84 = "shower(s) of rain and snow mixed, moderate or heavy";
  public static final String ww_85 = "snow shower(s), slight";
  public static final String ww_86 = "snow shower(s), moderate or heavy";
  public static final String ww_87 =
      "slight shower(s) of soft or small hail (with or without rain/snow)";
  public static final String ww_88 =
      "moderate or heavy showers of soft or small hail (with or without rain/snow)";
  public static final String ww_89 = "slight showers of hail (with or without rain/snow)";
  public static final String ww_90 =
      "moderate or heavy showers of hail (with or without rain/snow)";
  public static final String ww_91 = "slight rain; thunder heard";
  public static final String ww_92 = "moderate or heavy rain; thunder heard";
  public static final String ww_93 = "slight snow, or rain and snow mixed, or hail; thunder heard";
  public static final String ww_94 =
      "moderate or heavy snow, or rain and snow mixed, or hail; thunder heard";
  public static final String ww_95 =
      "thunderstorm (thunder heard), slight or moderate, without hail";
  public static final String ww_96 = "thunderstorm (thunder heard), slight or moderate, with hail";
  public static final String ww_97 = "thunderstorm (thunder heard), heavy, without hail";
  public static final String ww_98 =
      "thunderstorm (thunder heard), combined with duststorm or sandstorm";
  public static final String ww_99 = "thunderstorm (thunder heard), heavy, with hail";
  public static final String ww_not_determined = "not determined";

  private static final String[] DESCRIPTIONS = {
    ww_0, ww_1, ww_2, ww_3, ww_4, ww_5, ww_6, ww_7, null, ww_9, ww_10, ww_11, ww_12, ww_13, ww_14,
    ww_15, ww_16, ww_17, ww_18, ww_19, ww_20, ww_21, ww_22, ww_23, ww_24, ww_25, ww_26, ww_27,
    ww_28, ww_29, ww_30, ww_31, ww_32, ww_33, ww_34, ww_35, ww_36, ww_37, ww_38, ww_39, ww_40,
    ww_41, ww_42, ww_43, ww_44, ww_45, ww_46, ww_47, ww_48, ww_49, ww_50, ww_51, ww_52, ww_53,
    ww_54, ww_55, ww_56, ww_57, ww_58, ww_59, ww_60, ww_61, ww_62, ww_63, ww_64, ww_65, ww_66,
    ww_67, ww_68, ww_69, ww_70, ww_71, ww_72, ww_73, ww_74, ww_75, ww_76, ww_77, ww_78, ww_79,
    ww_80, ww_81, ww_82, ww_83, ww_84, ww_85, ww_86, ww_87, ww_88, ww_89, ww_90, ww_91, ww_92,
    ww_93, ww_94, ww_95, ww_96, ww_97, ww_98, ww_99
  };
  static final Weather UNDETERMINED = new Weather("//", ww_not_determined);
  static final List<Group> DRY =
      List.of(
          group("not determined"),
          group("thunder audible during the last 10 minutes", 17),
          range("fog (or ice-fog) at time of obs", 49, 40),
          range("duststorm, sandstorm, drifting or blowing snow", 39, 30),
          range("precipitation, fog or thunderstorm within last hour; not at time of obs", 29, 20),
          range("squalls or funnel cloud(s) within last hour or at time of obs", 19, 18),
          range("lightning or precipitation within sight but not at station", 16, 13),
          range("shallow fog or mist", 12, 10),
          group("haze, dust, sand, smoke or blowing spray", 9, 7, 6, 5, 4),
          range("phenomena without significance", 3, 0));
  static final List<Group> WET =
      List.of(
          group("not determined"),
          range("thunderstorm* at time of obs", 99, 95),
          range("thunderstorm* during preceding hour but not at time of obs", 94, 91),
          range("showery precipitation, no thunder at time of obs or during preceding hr", 90, 80),
          range("solid precipitation, not in showers", 79, 70),
          range("rain", 69, 60),
          range("drizzle", 59, 50));

  private static Group group(String label, int... codes) {
    if (codes.length == 0) return new Group(label, List.of(UNDETERMINED));
    List<Weather> options = new ArrayList<>();
    for (int code : codes)
      options.add(new Weather(code < 10 ? "0" + code : Integer.toString(code), DESCRIPTIONS[code]));
    return new Group(label, List.copyOf(options));
  }

  private static Group range(String label, int high, int low) {
    int[] codes = new int[high - low + 1];
    for (int i = 0; i < codes.length; i++) codes[i] = high - i;
    return group(label, codes);
  }
}
