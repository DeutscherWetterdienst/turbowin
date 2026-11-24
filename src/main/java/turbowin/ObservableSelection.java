package turbowin;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/** A UI-independent selection whose changes can be observed by an open dialog. */
final class ObservableSelection<T> {
  private final PropertyChangeSupport changes = new PropertyChangeSupport(this);
  private T value;

  ObservableSelection(T initial) {
    value = initial;
  }

  T get() {
    return value;
  }

  void set(T next) {
    T previous = value;
    value = next;
    changes.firePropertyChange("selection", previous, next);
  }

  void addListener(PropertyChangeListener listener) {
    changes.addPropertyChangeListener(listener);
  }
}
