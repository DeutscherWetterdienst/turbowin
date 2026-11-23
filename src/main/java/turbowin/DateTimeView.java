package turbowin;

import java.util.Objects;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/** UTC year, month, day and hour selection for a manual observation. */
final class DateTimeView {
  record State(String[] selected, boolean readOnly, boolean offline) {}

  private DateTimeView() {}

  static Scene create(State state, Consumer<String[]> accept, Runnable cancel, Runnable help) {
    String[][] values = DateTimeOptions.all();
    String[] ids = {"year", "month", "day", "hour"};
    String[] titles = {"Year [UTC]", "Month [UTC]", "Day [UTC]", "Hour [UTC]"};
    @SuppressWarnings("unchecked")
    ListView<String>[] lists = new ListView[4];
    HBox columns = new HBox(14);
    columns.setAlignment(Pos.CENTER);
    for (int i = 0; i < lists.length; i++) {
      ListView<String> list = new ListView<>(FXCollections.observableArrayList(values[i]));
      list.setId(ids[i]);
      list.setDisable(state.readOnly());
      list.setPrefHeight(300);
      list.setPrefWidth(i == 1 ? 160 : 110);
      if (state.selected()[i] != null) {
        list.getSelectionModel().select(state.selected()[i]);
        int selectedIndex = list.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) Platform.runLater(() -> list.scrollTo(selectedIndex));
      }
      lists[i] = list;
      Label label = new Label(titles[i]);
      label.setLabelFor(list);
      label.getStyleClass().add("group-title");
      VBox column = new VBox(8, label, list);
      column.setMaxWidth(Double.MAX_VALUE);
      HBox.setHgrow(column, Priority.ALWAYS);
      columns.getChildren().add(column);
    }
    Label heading = new Label("Date and Time");
    heading.getStyleClass().add("heading");
    VBox card = new VBox(10, columns);
    card.getStyleClass().add("choice-group");
    card.setPadding(new Insets(16));
    card.setMaxWidth(620);
    VBox content = new VBox(18, heading, card);
    content.setAlignment(Pos.TOP_CENTER);
    content.setPadding(new Insets(24));
    VBox.setVgrow(content, Priority.ALWAYS);
    Button ok = new Button("OK");
    Button cancelButton = new Button("Cancel");
    Button helpButton = new Button(state.offline() ? "Help" : "Internet");
    ok.setDefaultButton(true);
    ok.setOnAction(event -> {
      String[] selection = new String[lists.length];
      for (int i = 0; i < lists.length; i++)
        selection[i] = lists[i].getSelectionModel().getSelectedItem();
      accept.accept(selection);
    });
    cancelButton.setOnAction(event -> cancel.run());
    helpButton.setOnAction(event -> help.run());
    HBox buttons = new HBox(10, ok, cancelButton, helpButton);
    buttons.setAlignment(Pos.CENTER);
    buttons.getStyleClass().add("cloud-cover-actions");
    VBox root = new VBox(content, buttons);
    root.getStyleClass().add("cloud-cover-root");
    Scene scene = new Scene(root);
    scene
        .getStylesheets()
        .add(
            Objects.requireNonNull(DateTimeView.class.getResource("cloud-cover.css"))
                .toExternalForm());
    return scene;
  }
}
