package org.example;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;

public class PrimaryController implements Initializable {

    private final BooleanProperty calculationInProgress = new SimpleBooleanProperty(false);
    private final TextField[] textFields = new TextField[81];

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private Button solveButton;

    @FXML
    private AnchorPane rootAnchorPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        progressIndicator.visibleProperty().bind(calculationInProgress);
        solveButton.visibleProperty().bind(calculationInProgress.not());

        GridPane gridPane = new GridPane();

        for (int i = 0; i < 81; i++) {
            if (i % 9 == 0) {
                RowConstraints row = new RowConstraints(41);
                gridPane.getRowConstraints().add(row);

                ColumnConstraints column = new ColumnConstraints(41);
                gridPane.getColumnConstraints().add(column);
            }

            TextField textField = new TextField();
            textField.getStyleClass().add("text-field");
            textField.textProperty().addListener((observableValue, old, n) -> {
                if (textField.getText().length() > 1) {
                    textField.setText(textField.getText().substring(0, 1));
                }
            });
            textFields[i] = textField;
            Pane pane = new Pane();
            pane.setPrefSize(41,41);
            textField.setPrefSize(39, 39);

            int y = i / 9;
            int x = i % 9;

            pane.getStyleClass().add("cell");
            if (x < 8 && (x - 2) % 3 == 0) {
                pane.getStyleClass().add("h-line");
            }
            if (y < 8 && (y - 2) % 3 == 0) {
                pane.getStyleClass().add("v-line");
            }

            pane.getChildren().add(textField);
            gridPane.getStyleClass().add("grid-pane");
            //GridPane.setMargin(pane, new Insets(1, 0, 0, 1));
            gridPane.add(pane, y, x);
        }

        AnchorPane.setTopAnchor(gridPane, 20.0);
        AnchorPane.setLeftAnchor(gridPane, 20.0);

        rootAnchorPane.getChildren().add(gridPane);
    }

    @FXML
    private void onSolve(ActionEvent actionEvent) {
        char[] board = new char[81];
        for (int i = 0; i < 81; i++) {
            String text = textFields[i].textProperty().get();
            if (text.length() > 0) {
                board[i] = text.charAt(0);
            } else {
                board[i] = '.';
            }
        }

        calculationInProgress.set(true);

        Thread backgroundThread = new Thread(() -> {
            SudokuSolver solver = new SudokuSolver();
            char[] solved = solver.solve(board);

            for (int i = 0; i < 81; i++) {
                textFields[i].textProperty().set(String.valueOf(solved[i]));
            }
            calculationInProgress.set(false);
        });
        // Terminate the running thread if the application exits
        backgroundThread.setDaemon(true);
        backgroundThread.start();
    }

    @FXML
    private void onClear(ActionEvent actionEvent) {
        for (int i = 0; i < 81; i++) {
            textFields[i].textProperty().set("");
        }
    }
}
