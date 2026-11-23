package turbowin;

import java.util.Objects;
import java.util.function.Consumer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/** Editable observer table. Rows are made available after the observer log is loaded. */
final class ObserverView {
  record State(boolean german, boolean obsId, boolean sequence, boolean offline) {}

  record Data(String[][] rows, int selectedRow) {}

  static final class Row {
    private final StringProperty[] values = {
      new SimpleStringProperty(""), new SimpleStringProperty(""),
      new SimpleStringProperty(""), new SimpleStringProperty("")
    };

    StringProperty value(int column) {
      return values[column];
    }
  }

  private final TableView<Row> table = new TableView<>();
  private final Button ok = new Button("OK");
  private final Scene scene;

  ObserverView(
      State state,
      Consumer<Data> accept,
      Runnable cancel,
      Runnable back,
      Runnable stop,
      Runnable help) {
    table.setId("observer_table");
    table.setEditable(true);
    table.setDisable(true);
    table.setPlaceholder(new Label("Loading observer data…"));
    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    String[] headers = {
      "Surname",
      state.german() && !state.obsId() ? "Full christian name" : "Full initials",
      state.obsId() ? "OBS_ID" : "Rank",
      "Discharge book"
    };
    for (int i = 0; i < headers.length; i++) {
      int index = i;
      TableColumn<Row, String> column = new TableColumn<>(headers[i]);
      column.setId("observer_column_" + i);
      column.setSortable(false);
      column.setReorderable(false);
      column.setCellValueFactory(cell -> cell.getValue().value(index));
      column.setEditable(!state.obsId() || i != 2);
      if (column.isEditable()) column.setCellFactory(ignored -> new EditingCell(index));
      table.getColumns().add(column);
    }
    VBox.setVgrow(table, Priority.ALWAYS);
    Label title = new Label("Observer");
    title.getStyleClass().add("heading");
    Label instructions =
        new Label(
            state.obsId()
                ? "Select a row; double-click a cell to edit. OBS_ID is computed automatically."
                : "Select a row; double-click a cell to edit. Clear the surname to remove a record.");
    instructions.setWrapText(true);
    instructions.getStyleClass().add("hint");
    Label note =
        new Label(
            state.obsId()
                ? "Full initials are required. Clear the surname to remove a record. Upload via Maintenance → Move log files."
                : (state.german() ? "Enter full christian names" : "Enter full initials, e.g. A.B.")
                    + "; discharge book or seaman's card number if applicable.");
    note.setWrapText(true);
    note.getStyleClass().add("hint");
    VBox content = new VBox(12, title, instructions, table, note);
    content.setPadding(new Insets(20, 24, 20, 24));
    content.setAlignment(Pos.TOP_CENTER);
    VBox.setVgrow(content, Priority.ALWAYS);
    Button backButton = new Button("Back");
    Button cancelButton = new Button("Cancel");
    Button helpButton = new Button(state.offline() ? "Help" : "Internet");
    Button stopButton = new Button("Stop");
    backButton.setDisable(!state.sequence());
    stopButton.setDisable(!state.sequence());
    ok.setDisable(true);
    ok.setDefaultButton(true);
    backButton.setOnAction(e -> back.run());
    ok.setOnAction(e -> accept.accept(snapshot()));
    cancelButton.setOnAction(e -> cancel.run());
    helpButton.setOnAction(e -> help.run());
    stopButton.setOnAction(e -> stop.run());
    HBox buttons = new HBox(10, backButton, ok, cancelButton, helpButton, stopButton);
    buttons.setAlignment(Pos.CENTER);
    buttons.getStyleClass().add("cloud-cover-actions");
    VBox root = new VBox(content, buttons);
    root.getStyleClass().add("cloud-cover-root");
    scene = new Scene(root);
    scene
        .getStylesheets()
        .add(
            Objects.requireNonNull(ObserverView.class.getResource("cloud-cover.css"))
                .toExternalForm());
  }

  Scene scene() {
    return scene;
  }

  void load(Data data) {
    var rows = FXCollections.<Row>observableArrayList();
    for (String[] values : data.rows()) {
      Row row = new Row();
      for (int i = 0; i < values.length; i++) row.value(i).set(values[i]);
      rows.add(row);
    }
    table.setItems(rows);
    if (data.selectedRow() >= 0) table.getSelectionModel().select(data.selectedRow());
    table.setDisable(false);
    ok.setDisable(false);
  }

  private Data snapshot() {
    String[][] rows = new String[myobserver.OBSERVER_ROWS][myobserver.OBSERVER_COLUMNS];
    for (int row = 0; row < rows.length; row++) {
      for (int column = 0; column < rows[row].length; column++)
        rows[row][column] = table.getItems().get(row).value(column).get();
    }
    return new Data(rows, table.getSelectionModel().getSelectedIndex());
  }

  /** Copy keystrokes immediately so clicking OK also saves an actively edited cell. */
  private static final class EditingCell extends TableCell<Row, String> {
    private final int column;
    private TextField editor;

    EditingCell(int column) {
      this.column = column;
    }

    @Override
    public void startEdit() {
      super.startEdit();
      if (!isEditing()) return;
      Row row = getTableRow().getItem();
      if (row == null) {
        cancelEdit();
        return;
      }
      editor = new TextField(getItem() == null ? "" : getItem());
      editor
          .textProperty()
          .addListener((observable, oldValue, newValue) -> row.value(column).set(newValue));
      editor.setOnAction(event -> commitEdit(editor.getText()));
      editor
          .focusedProperty()
          .addListener((observable, wasFocused, focused) -> {
            if (!focused && isEditing()) commitEdit(editor.getText());
          });
      setText(null);
      setGraphic(editor);
      editor.selectAll();
      editor.requestFocus();
    }

    @Override
    public void cancelEdit() {
      super.cancelEdit();
      setGraphic(null);
      setText(getItem());
    }

    @Override
    protected void updateItem(String item, boolean empty) {
      super.updateItem(item, empty);
      if (empty) {
        setText(null);
        setGraphic(null);
      } else if (!isEditing()) {
        setText(item);
        setGraphic(null);
      }
    }
  }
}
