package zajecia6;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ArcType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


public class Controller {
    @FXML
    TextArea oknoTekstowe;

    @FXML
    Menu toolsMenu;

    @FXML
    Canvas mycanvas;

    @FXML
    Stage stage;

    List<Image> ikony;


    public Controller() {
        List<String> iconFiles = new ArrayList<>();
        iconFiles.add("Lulu-Dragon-Trainer-icon.png");
        iconFiles.add("Quinn-Valor-icon.png");
        iconFiles.add("Yasuo-icon.png");
        iconFiles.add("Veigar-icon.png");

        ikony = new ArrayList<>();
        for(String name : iconFiles) {
            ikony.add(loadImage(name));
        }
    }

    public void sayIt() {
        System.out.println("It");
    }

    public void zmienNaDuze() {
        String tekst = oknoTekstowe.getText();
        tekst = tekst.toUpperCase();
        oknoTekstowe.setText(tekst);
    }

    public void disableToolsMenu() {
        boolean isDisabled = toolsMenu.isDisable();
        if (isDisabled) {
            toolsMenu.setDisable(false);
        } else {
            toolsMenu.setDisable(true);
        }
    }

    public void drawOnCanvas() {
        //Użycie canvasu:
        GraphicsContext gc = mycanvas.getGraphicsContext2D();

        //see eg. http://docs.oracle.com/javafx/2/canvas/jfxpub-canvas.htm
        gc.setFill(Color.color(0.1, 0.7, 0.5, 0.5));
        gc.fillArc(10, 110, 300, 300, 45, 240, ArcType.OPEN);
        int size = 128;
        for (int i = 0; i < ikony.size(); i++) {
            gc.drawImage(ikony.get(i), 50 + i * 135, 55, size, size);
            gc.strokeRoundRect(50 + i * 135, 50, size, size, 10, 10);
        }

        //todo: załadować część obrazka
        //todo: ustawić rodzaj pędzla
        //todo: obsługa myszy i "draggable"




    }

    public void animateLulu() {
        GraphicsContext gc = mycanvas.getGraphicsContext2D();
        DoubleProperty x  = new SimpleDoubleProperty();
        DoubleProperty y  = new SimpleDoubleProperty();
        DoubleProperty phi = new SimpleDoubleProperty();

        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(0),
                        new KeyValue(x, 0),
                        new KeyValue(y, 0),
                        new KeyValue(phi, 0)
                ),
                new KeyFrame(Duration.millis(2500),
                        new KeyValue(x, 200),
                        new KeyValue(y, 200),
                        new KeyValue(phi, 180)
                )
        );
        timeline.setAutoReverse(true);
        timeline.setCycleCount(12);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Image i = ikony.get(0);

                ImageView iv = new ImageView(i);
                iv.setRotate(phi.getValue());
                SnapshotParameters params = new SnapshotParameters();
                params.setFill(Color.TRANSPARENT);
                Image rotatedImage = iv.snapshot(params, null);

                gc.clearRect(0, 0, 500, 500);
                //show icon on canvas
                gc.drawImage(rotatedImage, x.doubleValue(), y.doubleValue(), 300, 300);

            }
        };
        timer.start();
        timeline.play();
    }

    public void alertujUsera() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog!");
        alert.setHeaderText("Some header");
        //todo: wyświetla narazie tylko jedną linijkę
        alert.setContentText("Lorem ipsum dolor sit amet enim. Etiam ullamcorper. ");
        alert.showAndWait();
        //more at: http://code.makery.ch/blog/javafx-dialogs-official/
    }

    public void myOpenFile() {
        File f = new FileChooser().showOpenDialog(stage);
        System.out.println(f.getAbsolutePath());
    }

    public void dodajUsera() {
        Dialog<String> dialog = new Dialog<>();
        //title...
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        //GridPane   ma mieć (3, 2)
        GridPane grid = new GridPane();
        //Dostawić elementy

        dialog.getDialogPane().setContent(grid);
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            System.out.println(result.get());
        }
    }

    public void loginDialog() {
        //Tworzenie obiektu dialogowego:
        // parametrem jest wynik który dostaniemy z dialogu (tutaj parę dwóch napisów)
        // (gdybyśmy chcieli pojedynczą np. liczbę, to byłoby Dialog<Integer>)
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Log in");
        dialog.setHeaderText("Logowanie do systemu");
        //Customizacja buttonów okna
        ButtonType naszeLogowanie = new ButtonType("Zaloguj", ButtonBar.ButtonData.APPLY);
//        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.PREVIOUS, ButtonType.YES.NEXT, ButtonType.FINISH, ButtonType.CANCEL);
        dialog.getDialogPane().getButtonTypes().addAll(naszeLogowanie, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField userField = new TextField();
        userField.setPromptText("User");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Hasło");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(userField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        /*
         * Wykonanie operacji przez wątek UI (JavaFX application thread)
         * lepsze niż samo userField.requestFocus();
         *
         * See also: https://stackoverflow.com/questions/13784333/platform-runlater-and-task-in-javafx
         */
        Platform.runLater(userField::requestFocus);

        //Te pola mają wiele "properties", które są "ObservableValue"; można zaczepiać listenery
//        userField.textProperty().addListener((observable, oldValue, newValue) -> {
//            if (newValue.equals("Abra Kadabra")) {
//                System.out.println("Powitał Kadabra!");
//            }
//        });

//        userField.borderProperty().addListener((observable, oldValue, newValue) -> {
//            Border xxx = oldValue;
//        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == naszeLogowanie) {
                return new Pair<>(userField.getText(), passField.getText());
            } else if (dialogButton==ButtonType.CANCEL) {
                System.out.println("Cancel button clicked");
            }
            return null;
        });

        //Ten kod pokazuje dialog na ekranie, i zmusza usera do reakcji
        Optional<Pair<String, String>> result = dialog.showAndWait();

        if (result.isPresent()) {
            Pair<String, String> ppp = result.get();
            System.out.println("Username:" + ppp.getKey());
            System.out.println("Password:" + ppp.getValue());
        }

        //todo: "new game dialog" or "num keyboard dialog"
        //todo: akcje pod klawiszami

    }

    //private
    Image loadImage(String name) {
        return new Image(getClass().getResourceAsStream("res/" + name));
    }
}
