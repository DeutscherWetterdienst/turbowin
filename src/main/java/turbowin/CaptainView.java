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

/** Editable captains table, enabled after the captain log is loaded. */
final class CaptainView {
  record State(boolean german, boolean offline) {}

  static final class Row {
    private final StringProperty[] values = {
      new SimpleStringProperty(""),
      new SimpleStringProperty(""),
      new SimpleStringProperty(""),
      new SimpleStringProperty(""),
      new SimpleStringProperty("")
    };

    StringProperty value(int column) {
      return values[column];
    }
  }

  private final TableView<Row> table = new TableView<>();
  private final Button ok = new Button("OK");
  private final Scene scene;

  CaptainView(State state, Consumer<String[][]> accept, Runnable cancel, Runnable help) {
    table.setId("captain_table");
    table.setEditable(true);
    table.setDisable(true);
    table.setPlaceholder(new Label("Loading captain data…"));
    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    String[] headers = {
      "Surname",
      state.german() ? "Full christian name" : "Full initials",
      "Date of joining",
      "Date of leaving",
      "Discharge book"
    };
    for (int i = 0; i < headers.length; i++) {
      int index = i;
      TableColumn<Row, String> column = new TableColumn<>(headers[i]);
      column.setId("captain_column_" + i);
      column.setSortable(false);
      column.setReorderable(false);
      column.setCellValueFactory(cell -> cell.getValue().value(index));
      column.setCellFactory(ignored -> new EditingCell(index));
      table.getColumns().add(column);
    }
    VBox.setVgrow(table, Priority.ALWAYS);
    Label heading = new Label("Captains");
    heading.getStyleClass().add("heading");
    Label instructions = new Label("Double-click a cell to edit. Dates use DD/MM/YYYY.");
    instructions.setWrapText(true);
    instructions.getStyleClass().add("hint");
    Label note =
        new Label(
            (state.german() ? "Enter full christian names" : "Enter full initials, e.g. M.F.C.M.D.")
                + "; discharge book or seaman's card number if applicable.");
    note.setWrapText(true);
    note.getStyleClass().add("hint");
    Label clearing =
        new Label(
            "To clear records, use Maintenance → Move log files; do not delete individual cells.");
    clearing.setWrapText(true);
    clearing.getStyleClass().add("hint");
    VBox content = new VBox(12, heading, instructions, table, note, clearing);
    content.setPadding(new Insets(20, 24, 20, 24));
    content.setAlignment(Pos.TOP_CENTER);
    VBox.setVgrow(content, Priority.ALWAYS);
    Button cancelButton = new Button("Cancel");
    Button helpButton = new Button(state.offline() ? "Help" : "Internet");
    ok.setDisable(true);
    ok.setDefaultButton(true);
    ok.setOnAction(e -> accept.accept(snapshot()));
    cancelButton.setOnAction(e -> cancel.run());
    helpButton.setOnAction(e -> help.run());
    HBox buttons = new HBox(10, ok, cancelButton, helpButton);
    buttons.setAlignment(Pos.CENTER);
    buttons.getStyleClass().add("cloud-cover-actions");
    VBox root = new VBox(content, buttons);
    root.getStyleClass().add("cloud-cover-root");
    scene = new Scene(root);
    scene
        .getStylesheets()
        .add(
            Objects.requireNonNull(CaptainView.class.getResource("cloud-cover.css"))
                .toExternalForm());
  }

  Scene scene() {
    return scene;
  }

  void load(String[][] data) {
    var rows = FXCollections.<Row>observableArrayList();
    for (String[] values : data) {
      Row row = new Row();
      for (int i = 0; i < values.length; i++) row.value(i).set(values[i]);
      rows.add(row);
    }
    table.setItems(rows);
    table.setDisable(false);
    ok.setDisable(false);
  }

  private String[][] snapshot() {
    String[][] rows = new String[CaptainModel.CAPTAIN_ROWS][CaptainModel.CAPTAIN_COLUMNS];
    for (int row = 0; row < rows.length; row++)
      for (int column = 0; column < rows[row].length; column++)
        rows[row][column] = table.getItems().get(row).value(column).get();
    return rows;
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
