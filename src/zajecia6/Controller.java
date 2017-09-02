package zajecia6;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.File;
import java.util.*;


public class Controller {
    @FXML
    TextArea oknoTekstowe;

    @FXML
    Menu toolsMenu;

    @FXML
    Canvas mycanvas;

    @FXML
    Stage stage;


    //////////////////////////////////////////
    // Dane dotyczące gry
    //////////////////////////////////////////


    List<Image> ikony;  //kolekcja obrazków
    List<Sprite> sprites;   //kolekcja obiektów odpowiadających postaciom gry

    Set<SmallSprite> fires;  //kolekcja małych obiektów animowanych

    //stałe w programie
    int ICON_SIZE = 120;
    int FRAMES_PER_SECOND = 30;
    double COLLISION_DISTANCE = 50;


    /**
     *  Główna klasa zbierająca informacje o pojedynczej postaci gry.
     *  Zawiera również informacje o pozycji postaci i prędkości jej ruchu, oraz metody
     *  do wykrywania kolizji, rysowania postaci, i ustalania nowego celu ruchu.
     */
    class Sprite {
        int iconNumber;
        boolean isSelected; //true jeśli tą postacią chcemy poruszać

        //Położenia
        double x, y;  //współrzędne centrum spritea
        double goalX, goalY; //współrzędne punktu do którego zmierzamy

        //Prędkości
        double v = 5;   //maksymalna prędkość
        double vx = 0;  //składowe aktualnej prędkości
        double vy = 0;


        //tzw. Konstruktor, czyli tworzenie konkretnej instancji tej klasy
        public Sprite(int iconNumber, double x, double y) {
            this.iconNumber = iconNumber;
            this.x = x;
            this.y = y;
            this.goalX = x;
            this.goalY = y;
        }

        //drukuje sprite'a na `gc` centrum w (x,y)
        void printSprite(GraphicsContext gc) {
            printIconWithRectangle(gc, ikony.get(iconNumber),
                    (int)x - ICON_SIZE/2, (int)y - ICON_SIZE/2, isSelected);
        }

        //sprawdza czy obecny sprite koliduje z punktem (px, py)
        boolean collidesWith(double px, double py) {
            return distance(x, y, px, py) < COLLISION_DISTANCE;
        }

        //Wykonywana jeśli chcemy by sprite od teraz poruszał się w kierunku punktu (newGoalX, newGoalY)
        public void setNewGoalPosition(double newGoalX, double newGoalY){
            goalX = newGoalX;
            goalY = newGoalY;
            double dx = goalX - x;
            double dy = goalY - y;
            if (dy==0) {
                if (dx>0) {
                    vx = v;
                } else {
                    vx = -v;
                }
                return;
            }

            double A = dx / dy;
            vy = v / Math.sqrt(1 + A * A);
            if (dy<0) vy = -vy;
            vx = A * vy;
        }

        //Wykonywana w każdej klatce animacji: wyliczenie nowego położenia sprite'a
        public void updatePosition() {
            //warunek dla osiągnięcia celu
            if (distance(x+vx, y+vy, goalX,goalY) > distance(x,y,goalX,goalY)) {
                x = goalX;
                y = goalY;
                return;
            }
            x += vx;
            y += vy;
        }

        private double distance(double x, double y, double x1, double y1) {
            return Math.sqrt((x - x1) * (x - x1) + (y - y1) * (y - y1));
        }

    }

    private void printIconWithRectangle(GraphicsContext gc, Image i, int x, int y, boolean isSelected) {
        gc.drawImage(i, x+5, y+5, ICON_SIZE-5, ICON_SIZE-5);
        if (isSelected) {
            gc.setLineWidth(3);
            gc.setStroke(Color.YELLOWGREEN);
        } else {
            gc.setLineWidth(1);
            gc.setStroke(Color.GRAY);
        }
        gc.strokeRoundRect(x, y, ICON_SIZE, ICON_SIZE, 10, 10);
        gc.setStroke(Color.BLACK);

    }

    private void repaintScene(GraphicsContext gc) {
        gc.clearRect(0, 0, 800, 800);
        gc.setFill(Color.color(0.1, 0.7, 0.5, 0.5));
        gc.fillArc(10, 110, 300, 300, 45, 240, ArcType.OPEN);
        for(Sprite s : sprites) {
            s.printSprite(gc);
        }
        for(SmallSprite s : fires) {
            s.printSprite(gc);
        }

    }


    //tworzenie postaci gry (sprite'ów)
    private void initializeSprites() {
        //Obrazki spriteów
        List<String> iconFiles = new ArrayList<>();
        iconFiles.add("Lulu-Dragon-Trainer-icon.png");
        iconFiles.add("Quinn-Valor-icon.png");
        iconFiles.add("Yasuo-icon.png");
        iconFiles.add("Veigar-icon.png");

        ikony = new ArrayList<>();
        for(String name : iconFiles) {
            ikony.add(loadImage(name));
        }

        sprites = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Sprite s = new Sprite(i, 150 + 135 * i, 100);  //ustawienie początkowych pozycji sprite'ów
            sprites.add(s);
        }
        sprites.get(0).isSelected = true;

        fires = new HashSet<>();     //narazie pusta lista małych spriteów

    }

    //Tworzenie periodycznej animacji
    private void initializeGameAnimation(GraphicsContext gc) {
        //Poniższy timelinie będzie odpalał "EventHandler" wybraną liczbę razy na sekunde
        Timeline mainTimeline = new Timeline(new KeyFrame(Duration.millis(1000 / FRAMES_PER_SECOND),
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        //Wykonywana w każdej klatce animacji gry:

                        //Przesuń sprite'y na nowe pozycje
                        for(Sprite s : sprites) {
                            s.updatePosition();
                        }
                        Set<SmallSprite> toErase = new HashSet<>();
                        for(SmallSprite s : fires) {
                            if (s.isDead()) {
                                toErase.add(s);
                            }
                        }
                        fires.removeAll(toErase);
                        for(SmallSprite s : fires) {
                            s.updatePosition();
                        }

                        //Narysuj całą planszę na nowo
                        repaintScene(gc);
                    }
                }));
        mainTimeline.setCycleCount(Timeline.INDEFINITE);
        mainTimeline.play();
    }

    private void initializeMouseEvents() {
        // Nieużwyana obecnie
        mycanvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent t) {
//                        Sprite lulu = sprites.get(selectedSprite);
//                        lulu.x = (int) (t.getX() + offsetX);
//                        lulu.y = (int) (t.getY() + offsetY);
                    }
                });

        // Ustala nową pozycję docelową dla aktualnie wybranego sprite'a
        mycanvas.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent t) {
//                        System.out.println(t.getButton());
                        //współrzędne kliknięcia
                        int xx = (int)t.getX();
                        int yy = (int)t.getY();

                        for(Sprite s : sprites) {
                            if (s.isSelected) {
                                if (t.getButton().equals(MouseButton.PRIMARY)) {
                                    s.setNewGoalPosition(xx, yy);
                                } else if (t.getButton().equals(MouseButton.SECONDARY)) {
                                   fires.add(new SmallSprite(
                                           s.x, s.y, xx, yy, 15, "fire", 7, 130, 5));
                                }
                            }
                        }

                        //nieużywana
                        if (t.getClickCount() >1) {
//                            reset(canvas, Color.BLUE);
                        }
                    }
                });

    }


    //Podłączenie eventów pod klawisze
    private void initializeKeyboardEvents() {
        //Wykorzystanie zdarzeń naciśnięcia klawiszy 1..4 do wyboru spriteów
        mycanvas.getScene().setOnKeyPressed(event -> {
            for(Sprite s : sprites) {
                s.isSelected = false;
            }
            if (event.getCode()== (KeyCode.DIGIT1)) {
                sprites.get(0).isSelected = true;
            }
            if (event.getCode()== (KeyCode.DIGIT2)) {
                sprites.get(1).isSelected = true;
            }
            if (event.getCode()== (KeyCode.DIGIT3)) {
                sprites.get(2).isSelected = true;
            }
            if (event.getCode()== (KeyCode.DIGIT4)) {
                sprites.get(3).isSelected = true;
            }
        });
    }

    public void startGame() {
        //Użycie canvasu:
        GraphicsContext gc = mycanvas.getGraphicsContext2D();
        initializeSprites();
        initializeMouseEvents();
        initializeKeyboardEvents();
        initializeGameAnimation(gc);
    }

    //////////////////////////////////////////////
    // Pozostałe metody (do testów JavaFX)
    // (nie związane z grą)
    //////////////////////////////////////////////

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
                gc.clearRect(0, 0, 500, 500);
                //show icon on canvas
                gc.drawImage(i, x.doubleValue(), y.doubleValue(), 300, 300);

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

    private Image rotateImage(Image i, DoubleProperty phi) {
        ImageView iv = new ImageView(i);
        iv.setRotate(phi.getValue());
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        Image rotatedImage = iv.snapshot(params, null);
        return rotatedImage;
    }
}
