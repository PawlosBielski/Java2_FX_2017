package zajecia7_recap;


import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

public class Controller {
    @FXML
    Canvas mycanvas;

    String path = "res/";
    List<String> files;
    int fileIndex = 0;

    //Funkcja wykonywana na początku tworzenia sceny
    public void initialize() {
        files = new ArrayList<>();
        files.add("Veigar-icon.png");
        files.add("Yasuo-icon.png");
        System.out.println("initializing...");
    }


    public void sayIt() {
        System.out.println("sayit");
    }

    public void showDialog() {
        System.out.println("dialog");
    }

    public void alertujUsera() {
        System.out.println("alert!");
    }

    public void showPicture() {
        Image currentlySelected =
                new Image(getClass().getResourceAsStream(path + files.get(fileIndex)));
        GraphicsContext gc = mycanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, 400, 400);
        gc.drawImage(currentlySelected, 10, 50, 250, 250);

    }

    public void nextFile() {
        fileIndex += 1;
        if (fileIndex>=files.size()) {
            fileIndex -= files.size();
        }
        showPicture();
    }

    public void previousFile() {
        fileIndex -= 1;
        if (fileIndex<0) {
            fileIndex += files.size();
        }
        showPicture();
    }


}
