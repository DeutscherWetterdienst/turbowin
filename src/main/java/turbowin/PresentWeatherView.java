package turbowin;

import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/** General and specific marine present-weather selections. */
final class PresentWeatherView {
  record State(String code, String weather, boolean sequence, boolean offline) {}

  record Input(PresentWeatherCatalog.Weather weather, boolean generalSelected) {}

  private PresentWeatherView() {}

  static Scene create(
      State state,
      Consumer<Input> accept,
      Runnable cancel,
      Runnable back,
      Runnable stop,
      Runnable help) {
    ListView<PresentWeatherCatalog.Group> dry = groupList("present_weather_list_1");
    ListView<PresentWeatherCatalog.Group> wet = groupList("present_weather_list_2");
    dry.getItems().setAll(PresentWeatherCatalog.DRY);
    wet.getItems().setAll(PresentWeatherCatalog.WET);
    ListView<PresentWeatherCatalog.Weather> specific = new ListView<>();
    specific.setId("present_weather_list_3");
    specific.setPrefHeight(175);
    specific.setPlaceholder(new Label("Select a general weather condition"));
    specific.setCellFactory(list -> new WrappedCell<>(PresentWeatherCatalog.Weather::description));
    boolean[] updating = {false};
    boolean[] generalSelected = {false};
    dry.getSelectionModel()
        .selectedItemProperty()
        .addListener((obs, old, group) -> {
          if (group == null || updating[0]) return;
          updating[0] = true;
          wet.getSelectionModel().clearSelection();
          specific.getItems().setAll(group.options());
          specific.getSelectionModel().clearSelection();
          generalSelected[0] = true;
          updating[0] = false;
        });
    wet.getSelectionModel()
        .selectedItemProperty()
        .addListener((obs, old, group) -> {
          if (group == null || updating[0]) return;
          updating[0] = true;
          dry.getSelectionModel().clearSelection();
          specific.getItems().setAll(group.options());
          specific.getSelectionModel().clearSelection();
          generalSelected[0] = true;
          updating[0] = false;
        });
    if ("//".equals(state.code())) {
      specific.getItems().setAll(PresentWeatherCatalog.UNDETERMINED);
      if (PresentWeatherCatalog.UNDETERMINED.description().equals(state.weather())) {
        specific.getSelectionModel().select(0);
      }
    } else if (!state.code().isEmpty()) {
      String restoredCode = state.code();
      try {
        int number = Integer.parseInt(state.code().trim());
        restoredCode = number >= 0 && number < 10 ? "0" + number : Integer.toString(number);
      } catch (NumberFormatException ignored) {
        // Keep an unknown code unselected, as in the previous dialog.
      }
      final String lookupCode = restoredCode;
      for (ListView<PresentWeatherCatalog.Group> groups : java.util.List.of(dry, wet)) {
        PresentWeatherCatalog.Group group =
            groups.getItems().stream()
                .filter(item ->
                    item.options().stream().anyMatch(value -> value.code().equals(lookupCode)))
                .findFirst()
                .orElse(null);
        if (group == null && groups == dry && "08".equals(lookupCode)) {
          group = PresentWeatherCatalog.DRY.get(8);
        }
        if (group != null) {
          groups.getSelectionModel().select(group);
          groups.scrollTo(group);
          specific.getItems().stream()
              .filter(item -> item.description().equals(state.weather()))
              .findFirst()
              .ifPresent(item -> {
                specific.getSelectionModel().select(item);
                specific.scrollTo(item);
              });
          break;
        }
      }
    }

    Label heading = new Label("Present weather");
    heading.getStyleClass().add("heading");
    Label hint =
        new Label(
            "Select the topmost applicable general and specific weather condition. Present weather describes conditions at observation time or, where stated, during the preceding hour.");
    hint.setWrapText(true);
    hint.getStyleClass().add("hint");
    VBox dryBox = card("No precipitation (dry) at station", dry);
    VBox wetBox = card("Precipitation (wet) at station", wet);
    HBox.setHgrow(dryBox, Priority.ALWAYS);
    HBox.setHgrow(wetBox, Priority.ALWAYS);
    dryBox.setMaxWidth(Double.MAX_VALUE);
    wetBox.setMaxWidth(Double.MAX_VALUE);
    HBox general = new HBox(12, dryBox, wetBox);
    Label thunder = new Label("* Thunder heard; lightning may or may not be seen.");
    thunder.getStyleClass().add("hint");
    VBox specificCard = card("Specific weather condition", specific);
    VBox content = new VBox(10, heading, hint, general, thunder, specificCard);
    content.setPadding(new Insets(16, 24, 16, 24));
    content.setAlignment(Pos.TOP_CENTER);
    ScrollPane scroll = new ScrollPane(content);
    scroll.setFitToWidth(true);
    scroll.getStyleClass().add("cloud-cover-scroll");
    VBox.setVgrow(scroll, Priority.ALWAYS);

    Button backButton = new Button("Back");
    Button ok = new Button("OK");
    Button cancelButton = new Button("Cancel");
    Button helpButton = new Button(state.offline() ? "Help" : "Internet");
    Button stopButton = new Button("Stop");
    backButton.setDisable(!state.sequence());
    stopButton.setDisable(!state.sequence());
    backButton.setOnAction(e -> back.run());
    ok.setDefaultButton(true);
    ok.setOnAction(e ->
        accept.accept(
            new Input(specific.getSelectionModel().getSelectedItem(), generalSelected[0])));
    cancelButton.setOnAction(e -> cancel.run());
    helpButton.setOnAction(e -> help.run());
    stopButton.setOnAction(e -> stop.run());
    HBox buttons = new HBox(10, backButton, ok, cancelButton, helpButton, stopButton);
    buttons.setAlignment(Pos.CENTER);
    buttons.getStyleClass().add("cloud-cover-actions");
    VBox root = new VBox(scroll, buttons);
    root.getStyleClass().add("cloud-cover-root");
    Scene scene = new Scene(root);
    scene
        .getStylesheets()
        .add(PresentWeatherView.class.getResource("cloud-cover.css").toExternalForm());
    return scene;
  }

  private static ListView<PresentWeatherCatalog.Group> groupList(String id) {
    ListView<PresentWeatherCatalog.Group> list = new ListView<>();
    list.setId(id);
    list.setPrefHeight(215);
    list.setCellFactory(view -> new WrappedCell<>(PresentWeatherCatalog.Group::description));
    return list;
  }

  private static VBox card(String title, javafx.scene.Node child) {
    Label label = new Label(title);
    label.getStyleClass().add("group-title");
    VBox box = new VBox(6, label, child);
    box.getStyleClass().add("choice-group");
    box.setPadding(new Insets(12));
    box.setMaxWidth(Double.MAX_VALUE);
    return box;
  }

  private static final class WrappedCell<T> extends ListCell<T> {
    private final Label label = new Label();
    private final java.util.function.Function<T, String> text;

    WrappedCell(java.util.function.Function<T, String> text) {
      this.text = text;
      label.setWrapText(true);
      label.maxWidthProperty().bind(widthProperty().subtract(20));
    }

    @Override
    protected void updateItem(T item, boolean empty) {
      super.updateItem(item, empty);
      if (empty || item == null) {
        setText(null);
        setGraphic(null);
      } else {
        label.setText(text.apply(item));
        setText(null);
        setGraphic(label);
      }
    }
  }
}
