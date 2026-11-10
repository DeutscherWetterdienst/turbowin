package turbowin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Stages typed configuration values without changing the application's live settings. */
final class ConfigurationSettings {

  private final List<Value<?>> values = new ArrayList<>();

  <T> void add(T parsed, Consumer<T> target) {
    values.add(new Value<>(parsed, target));
  }

  boolean isEmpty() {
    return values.isEmpty();
  }

  void apply() {
    // Only settings present in the file are assigned. Missing entries retain their live values,
    // and duplicate labels are applied in the same order as the legacy parser.
    for (Value<?> value : values) {
      value.apply();
    }
  }

  private record Value<T>(T parsed, Consumer<T> target) {
    void apply() {
      target.accept(parsed);
    }
  }
}
