package turbowin;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.DoubleUnaryOperator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/** Independent, bidirectional unit conversions. */
final class CalculatorView {
  private record Conversion(String id, String from, String to, DoubleUnaryOperator convert) {}

  private record Row(Conversion conversion, TextField input, TextField output) {}

  private static final List<Conversion> CONVERSIONS =
      List.of(
          new Conversion("feet_to_metres", "feet", "metres", x -> x * 0.3048),
          new Conversion("metres_to_feet", "metres", "feet", x -> x * 3.2808399),
          new Conversion("fahrenheit_to_celsius", "°F", "°C", x -> (x - 32.0) / 1.8),
          new Conversion("celsius_to_fahrenheit", "°C", "°F", x -> x * 1.8 + 32.0),
          new Conversion("mph_to_knots", "mph", "knots", x -> x * 0.868976242),
          new Conversion("knots_to_mph", "knots", "mph", x -> x * 1.15077945),
          new Conversion("inhg_to_hpa", "inHg", "hPa", x -> x * 33.8638816),
          new Conversion("hpa_to_inhg", "hPa", "inHg", x -> x * 0.02952998751));

  private CalculatorView() {}

  static Scene create(Runnable close) {
    List<Row> rows = new ArrayList<>();
    Label errors = new Label();
    errors.setId("calculator_errors");
    errors.setWrapText(true);
    errors.getStyleClass().add("calculator-error");
    VBox content = new VBox(8);
    content.setPadding(new Insets(12, 16, 12, 16));
    String[] titles = {"Distance", "Temperature", "Wind speed", "Pressure"};
    for (int group = 0; group < titles.length; group++) {
      Label title = new Label(titles[group]);
      title.getStyleClass().add("group-title");
      GridPane grid = new GridPane();
      grid.setHgap(10);
      grid.setVgap(6);
      for (int i = 0; i < 2; i++) {
        Conversion conversion = CONVERSIONS.get(group * 2 + i);
        TextField input = new TextField();
        input.setId(conversion.id() + "_input");
        input.setPrefColumnCount(8);
        input.setTextFormatter(
            new TextFormatter<String>(change -> {
              if (change.isContentChange()) {
                change.setText(change.getText().replace(',', '.'));
                if (!change.getControlNewText().matches("-?\\d*(?:\\.\\d*)?")) return null;
              }
              return change;
            }));
        TextField output = new TextField();
        output.setId(conversion.id() + "_output");
        output.setPrefColumnCount(8);
        output.setEditable(false);
        input
            .textProperty()
            .addListener((observable, before, after) -> {
              output.clear();
              errors.setText("");
            });
        Label from = new Label(conversion.from());
        from.setLabelFor(input);
        Label to = new Label(conversion.to());
        to.setLabelFor(output);
        grid.addRow(i, from, input, new Label("→"), to, output);
        GridPane.setHgrow(input, Priority.ALWAYS);
        GridPane.setHgrow(output, Priority.ALWAYS);
        rows.add(new Row(conversion, input, output));
      }
      VBox card = new VBox(2, title, grid);
      card.getStyleClass().add("choice-group");
      card.setPadding(new Insets(8, 12, 8, 12));
      content.getChildren().add(card);
    }
    content.getChildren().add(errors);
    ScrollPane scroll = new ScrollPane(content);
    scroll.setFitToWidth(true);
    scroll.getStyleClass().add("cloud-cover-scroll");
    VBox.setVgrow(scroll, Priority.ALWAYS);
    Button calculate = new Button("Calculate");
    calculate.setDefaultButton(true);
    calculate.setOnAction(event -> calculate(rows, errors));
    Button cancel = new Button("Cancel");
    cancel.setCancelButton(true);
    cancel.setOnAction(event -> close.run());
    HBox actions = new HBox(10, calculate, cancel);
    actions.setAlignment(Pos.CENTER);
    actions.getStyleClass().add("cloud-cover-actions");
    VBox root = new VBox(scroll, actions);
    root.getStyleClass().add("cloud-cover-root");
    Scene scene = new Scene(root);
    scene
        .getStylesheets()
        .add(
            Objects.requireNonNull(CalculatorView.class.getResource("cloud-cover.css"))
                .toExternalForm());
    return scene;
  }

  private static void calculate(List<Row> rows, Label errors) {
    DecimalFormat format = new DecimalFormat("0.0", DecimalFormatSymbols.getInstance(Locale.ROOT));
    List<String> invalid = new ArrayList<>();
    for (Row row : rows) {
      String value = row.input().getText().trim();
      if (value.isEmpty()) continue;
      try {
        row.output()
            .setText(
                format.format(row.conversion().convert().applyAsDouble(Double.parseDouble(value))));
      } catch (NumberFormatException ex) {
        row.output().clear();
        invalid.add(row.conversion().from() + " invalid input");
      }
    }
    errors.setText(String.join("; ", invalid));
  }
}
