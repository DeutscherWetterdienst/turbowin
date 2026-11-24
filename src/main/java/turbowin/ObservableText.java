package turbowin;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Objects;

/** Text shared by observation models and sensor readers. */
final class ObservableText {
  private final PropertyChangeSupport changes = new PropertyChangeSupport(this);
  private final boolean decimal;
  private volatile String value = "";

  ObservableText() {
    this(false);
  }

  private ObservableText(boolean decimal) {
    this.decimal = decimal;
  }

  static ObservableText decimal() {
    return new ObservableText(true);
  }

  String get() {
    return value;
  }

  void set(String next) {
    next = Objects.requireNonNullElse(next, "");
    if (decimal) {
      next = next.replace(',', '.');
      if (!next.matches("-?\\d*(?:\\.\\d*)?")) return;
    }
    String previous = value;
    value = next;
    changes.firePropertyChange("value", previous, value);
  }

  void addListener(PropertyChangeListener listener) {
    changes.addPropertyChangeListener(listener);
  }
}
