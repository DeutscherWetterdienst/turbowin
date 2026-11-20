package turbowin;

import java.util.ArrayList;
import java.util.List;

/** Ice-reporting options in their established code order. */
final class IceCatalog {
  record Choice(String code, String description) {}

  private IceCatalog() {}

  static final List<Choice> CONCENTRATION =
      choices(
          new String[] {
            "No ice",
            "Ship in ice or within 0.5 n mile of ice. Ship in open lead > 1 n mile wide or ship in fast ice with boundary beyond limit of visibility",
            "Ship in ice or within 0.5 n mile of ice. Ice concentration uniform. Open water or very open pack ice, <3/8 concentration",
            "Ship in ice or within 0.5 n mile of ice. Ice concentration uniform. Open pack ice 3/8 to <6/8 concentration",
            "Ship in ice or within 0.5 n mile of ice. Ice concentration uniform. Close pack ice 6/8 to <7/8 concentration",
            "Ship in ice or within 0.5 n mile of ice. Ice concentration uniform. Very close pack ice 7/8 to <8/8 concentration",
            "Ship in ice or within 0.5 n mile of ice. Ice concentration not uniform. Strips & patches of pack ice with open water between",
            "Ship in ice or within 0.5 n mile of ice. Ice conc. not unif. Strips & patches of close/very close pack ice with areas of lesser conc. between",
            "Ship in ice or within 0.5 n mile of ice. Ice conc. not unif. Fast ice with open water, very open/open pack ice to seaward of the ice boundary",
            "Ship in ice or within 0.5 n mile of ice. Ice conc. not unif. Fast ice with close/very close pack ice to seaward of the ice boundary",
            "Unable to report, because of darkness, poor visibility or because ship is more than 0.5 n mile away from ice edge",
            "",
          });

  static final List<Choice> DEVELOPMENT =
      choices(
          new String[] {
            "New ice only (frazil ice, grease ice, slush, shuga)",
            "Nilas or ice rind, <10 cm thick",
            "Young ice (grey ice, grey-white ice) 10-30 cm thick",
            "Predominantly new and/or young ice with some first-year ice",
            "Predominantly thin first-year ice with some new and/or young ice",
            "All thin first-year ice (30-70 cm thick)",
            "Predominantly medium first-year ice (70-120 cm thick) & thick first-year ice (>120 cm thick) & some thinner (younger) first-year ice",
            "All medium and thick first-year ice",
            "Predominantly medium and thick first-year ice with some old ice (usually more than 2 metres thick)",
            "Predominantly old ice",
            "Unable to report, because of darkness, poor visibility or only ice of land origin visible or ship is > 0.5 n mile away from ice edge",
            "",
          });

  static final List<Choice> LAND =
      choices(
          new String[] {
            "No ice of land origin",
            "1-5 icebergs, no growlers or bergy bits",
            "6-10 icebergs, no growlers or bergy bits",
            "11-20 icebergs, no growlers or bergy bits",
            "Up to and including 10 growlers and bergy bits - no icebergs",
            "More than 10 growlers and bergy bits - no icebergs",
            "1-5 icebergs with growlers and bergy bits",
            "6-10 icebergs with growlers and bergy bits",
            "11-20 icebergs with growlers and bergy bits",
            "More than 20 icebergs with growlers and bergy bits - a major hazard to navigation",
            "Unable to report, because of darkness, poor visibility or only sea ice is visible",
            "",
          });

  static final List<Choice> BEARING =
      choices(
          new String[] {
            "Ship in shore or flaw lead",
            "Ice edge towards NE",
            "Ice edge towards east",
            "Ice edge towards SE",
            "Ice edge towards south",
            "Ice edge towards SW",
            "Ice edge towards west",
            "Ice edge towards NW",
            "Ice edge towards north",
            "Not determined (ship in ice)",
            "Unable to report, because of darkness, poor visibility or only ice of land origin visible",
            "",
          });

  static final List<Choice> SITUATION =
      choices(
          new String[] {
            "Ship in open water with floating ice in sight",
            "Ship in ice. Ship in easily penetrable ice: conditions improving",
            "Ship in ice. Ship in easily penetrable ice: conditions not changing",
            "Ship in ice. Ship in easily penetrable ice: conditions worsening.",
            "Ship in ice. Ship in ice difficult to penetrate: conditions improving.",
            "Ship in ice. Ship in ice difficult to penetrate: conditions not changing",
            "Ship in ice. Ice difficult to penetrate, conditions worsening. Ice forming and floes freezing together",
            "Ship in ice. Ice difficult to penetrate, conditions worsening. Ice under slight pressure",
            "Ship in ice. Ice difficult to penetrate, conditions worsening. Ice under moderate or severe pressure",
            "Ship in ice. Ice difficult to penetrate, conditions worsening. Ship beset",
            "Unable to report, because of darkness or poor visibility",
            "",
          });

  private static List<Choice> choices(String[] descriptions) {
    List<Choice> options = new ArrayList<>();
    for (int i = 0; i < descriptions.length; i++) {
      String code = i == 10 ? "u" : i == 11 ? "" : Integer.toString(i);
      options.add(new Choice(code, descriptions[i]));
    }
    return List.copyOf(options);
  }
}
