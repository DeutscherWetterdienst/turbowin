package turbowin;

/** Independent position and movement selections, shared with the GPS reader. */
final class PositionSelections {
  enum Latitude {
    NORTH,
    SOUTH
  }

  enum Longitude {
    EAST,
    WEST
  }

  enum Course {
    STATIONARY,
    NE,
    E,
    SE,
    S,
    SW,
    W,
    NW,
    N
  }

  enum Speed {
    ZERO,
    ONE_TO_FIVE,
    SIX_TO_TEN,
    ELEVEN_TO_FIFTEEN,
    SIXTEEN_TO_TWENTY,
    TWENTY_ONE_TO_TWENTY_FIVE,
    TWENTY_SIX_TO_THIRTY,
    THIRTY_ONE_TO_THIRTY_FIVE,
    THIRTY_SIX_TO_FORTY,
    OVER_FORTY
  }

  final ObservableSelection<Latitude> latitude = new ObservableSelection<>(null);
  final ObservableSelection<Longitude> longitude = new ObservableSelection<>(null);
  final ObservableSelection<Course> course = new ObservableSelection<>(null);
  final ObservableSelection<Speed> speed = new ObservableSelection<>(null);

  ObservableSelection<?> group(int index) {
    return switch (index) {
      case 0 -> latitude;
      case 1 -> longitude;
      case 2 -> course;
      case 3 -> speed;
      default -> throw new IllegalArgumentException("Invalid position group: " + index);
    };
  }

  int selectedIndex(int group) {
    Enum<?> value = (Enum<?>) group(group).get();
    return value == null ? -1 : value.ordinal();
  }

  void selectIndex(int group, int index) {
    switch (group) {
      case 0 -> latitude.set(index < 0 ? null : Latitude.values()[index]);
      case 1 -> longitude.set(index < 0 ? null : Longitude.values()[index]);
      case 2 -> course.set(index < 0 ? null : Course.values()[index]);
      case 3 -> speed.set(index < 0 ? null : Speed.values()[index]);
      default -> throw new IllegalArgumentException("Invalid position group: " + group);
    }
  }
}
