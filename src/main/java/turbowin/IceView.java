package turbowin;

import java.util.List;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

/** Five independent sea-ice reporting choices. */
final class IceView {
  record Input(
      String concentration, String development, String land, String bearing, String situation) {}

  record State(Input input, boolean offline) {}

  private IceView() {}

  static Scene create(State state, Consumer<Input> accept, Runnable cancel, Runnable help) {
    ComboBox<IceCatalog.Choice> concentration =
        combo("ice_concentration", IceCatalog.CONCENTRATION, state.input().concentration());
    ComboBox<IceCatalog.Choice> development =
        combo("ice_development", IceCatalog.DEVELOPMENT, state.input().development());
    ComboBox<IceCatalog.Choice> land = combo("ice_land", IceCatalog.LAND, state.input().land());
    ComboBox<IceCatalog.Choice> bearing =
        combo("ice_bearing", IceCatalog.BEARING, state.input().bearing());
    ComboBox<IceCatalog.Choice> situation =
        combo("ice_situation", IceCatalog.SITUATION, state.input().situation());
    VBox fields =
        new VBox(
            12,
            entry("Concentration or arrangement of sea ice", concentration),
            entry("Stage of development", development),
            entry("Ice of land origin", land),
            entry("Bearing of principal ice edge", bearing),
            entry("Ice situation and trend over preceding three hours", situation));
    VBox card = new VBox(fields);
    card.getStyleClass().add("choice-group");
    card.setPadding(new Insets(16, 20, 16, 20));
    card.setMaxWidth(720);
    Label heading = new Label("Ice");
    heading.getStyleClass().add("heading");
    VBox content = new VBox(16, heading, card);
    content.setPadding(new Insets(20, 24, 20, 24));
    content.setAlignment(Pos.TOP_CENTER);
    ScrollPane scroll = new ScrollPane(content);
    scroll.setFitToWidth(true);
    scroll.getStyleClass().add("cloud-cover-scroll");
    VBox.setVgrow(scroll, Priority.ALWAYS);
    Button ok = new Button("OK");
    Button cancelButton = new Button("Cancel");
    Button helpButton = new Button(state.offline() ? "Help" : "Internet");
    ok.setDefaultButton(true);
    ok.setOnAction(e ->
        accept.accept(
            new Input(
                code(concentration),
                code(development),
                code(land),
                code(bearing),
                code(situation))));
    cancelButton.setOnAction(e -> cancel.run());
    helpButton.setOnAction(e -> help.run());
    HBox buttons = new HBox(10, ok, cancelButton, helpButton);
    buttons.setAlignment(Pos.CENTER);
    buttons.getStyleClass().add("cloud-cover-actions");
    VBox root = new VBox(scroll, buttons);
    root.getStyleClass().add("cloud-cover-root");
    Scene scene = new Scene(root);
    scene.getStylesheets().add(IceView.class.getResource("cloud-cover.css").toExternalForm());
    return scene;
  }

  private static ComboBox<IceCatalog.Choice> combo(
      String id, List<IceCatalog.Choice> choices, String saved) {
    ComboBox<IceCatalog.Choice> box = new ComboBox<>();
    box.setId(id);
    box.setMaxWidth(Double.MAX_VALUE);
    box.setVisibleRowCount(5);
    box.setPromptText("Not specified");
    box.getItems().setAll(choices);
    box.setCellFactory(list -> new ChoiceCell(box));
    box.setConverter(
        new StringConverter<>() {
          @Override
          public String toString(IceCatalog.Choice item) {
            return item == null
                ? ""
                : item.description().isEmpty() ? "Not specified" : item.description();
          }

          @Override
          public IceCatalog.Choice fromString(String value) {
            return null;
          }
        });
    String code = normalized(saved);
    if (code != null)
      choices.stream()
          .filter(choice -> choice.code().equals(code))
          .findFirst()
          .ifPresent(choice -> box.getSelectionModel().select(choice));
    return box;
  }

  private static String normalized(String value) {
    if (value.trim().equals("u")) return "u";
    try {
      int code = Integer.parseInt(value.trim());
      return code >= 0 && code <= 9 ? Integer.toString(code) : null;
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private static String code(ComboBox<IceCatalog.Choice> box) {
    IceCatalog.Choice selected = box.getValue();
    return selected == null ? "" : selected.code();
  }

  private static VBox entry(String title, ComboBox<IceCatalog.Choice> box) {
    Label label = new Label(title);
    label.getStyleClass().add("group-title");
    label.setLabelFor(box);
    Label detail = new Label();
    detail.setWrapText(true);
    detail.getStyleClass().add("hint");
    detail.setId(box.getId() + "_description");
    Runnable update =
        () -> {
          IceCatalog.Choice selected = box.getValue();
          String text = selected == null ? "" : selected.description();
          detail.setText(text);
          detail.setVisible(text.length() > 75);
          detail.setManaged(detail.isVisible());
        };
    box.valueProperty().addListener((obs, old, value) -> update.run());
    update.run();
    return new VBox(3, label, box, detail);
  }

  private static final class ChoiceCell extends ListCell<IceCatalog.Choice> {
    private final Label label = new Label();

    ChoiceCell(ComboBox<IceCatalog.Choice> box) {
      label.setWrapText(true);
      label.setStyle("-fx-text-fill: #243746;");
      selectedProperty()
          .addListener((obs, old, selected) ->
              label.setStyle(selected ? "-fx-text-fill: white;" : "-fx-text-fill: #243746;"));
      label.prefWidthProperty().bind(box.widthProperty().subtract(45));
      prefWidthProperty().bind(box.widthProperty().subtract(5));
      setMinHeight(USE_PREF_SIZE);
    }

    @Override
    protected void updateItem(IceCatalog.Choice item, boolean empty) {
      super.updateItem(item, empty);
      if (empty || item == null) {
        setText(null);
        setGraphic(null);
      } else {
        label.setText(item.description().isEmpty() ? "Not specified" : item.description());
        setText(null);
        setGraphic(label);
      }
    }
  }
}
