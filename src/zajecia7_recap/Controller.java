package zajecia7_recap;


import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.min;

public class Controller {
    @FXML
    Canvas mycanvas;

    String path = "res/";
    List<String> files;
    int fileIndex = 0;
    int IMG_HEIGHT;
    int IMG_WIDTH;
    int IMG_PAD = 5;

    //Funkcja wykonywana na początku tworzenia sceny
    public void initialize() {
        files = new ArrayList<>();
        files.add("Veigar-icon.png");
        files.add("Yasuo-icon.png");
        files.add("LoL_Screenshot.png");
        System.out.println("initializing...");
        System.out.println("canvas height:" + mycanvas.getHeight());
        IMG_HEIGHT = Math.min(300, (int)Math.min(mycanvas.getHeight(), mycanvas.getWidth()));
        IMG_WIDTH = IMG_HEIGHT;
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
        int offsetX = ((int)mycanvas.getWidth() - IMG_WIDTH) / 2;
        int offsetY = ((int)mycanvas.getHeight() - IMG_HEIGHT) / 2;
        gc.setLineWidth(1);
        gc.strokeRect(offsetX - IMG_PAD, offsetY - IMG_PAD , IMG_WIDTH + 2 * IMG_PAD, IMG_HEIGHT + 2 * IMG_PAD);
        gc.drawImage(currentlySelected, offsetX, offsetY, IMG_WIDTH, IMG_HEIGHT);

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
