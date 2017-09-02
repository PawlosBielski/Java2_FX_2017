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
    int IMG_SIZE;
    int IMG_PAD = 5;

    //Funkcja wykonywana na początku tworzenia sceny
    public void initialize() {
        files = new ArrayList<>();
        files.add("Veigar-icon.png");
        files.add("Yasuo-icon.png");
        files.add("LoL_Screenshot.png");
        System.out.println("initializing...");
        System.out.println("canvas height:" + mycanvas.getHeight());
        IMG_SIZE = Math.min(300, (int)Math.min(mycanvas.getHeight(), mycanvas.getWidth()));
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
        int offsetX = ((int)mycanvas.getWidth() - IMG_SIZE) / 2;
        int offsetY = ((int)mycanvas.getHeight() - IMG_SIZE) / 2;

        //rysowanie prostokąta
        gc.setLineWidth(1);
        gc.strokeRect(offsetX - IMG_PAD, offsetY - IMG_PAD , IMG_SIZE + 2 * IMG_PAD, IMG_SIZE + 2 * IMG_PAD);

        //rysowanie obrazka
        int w = (int)currentlySelected.getWidth();
        int h = (int)currentlySelected.getHeight();
        int rescaledHeight = 0, rescaledWidth = 0;
        int deltaX = 0, deltaY = 0;
        if (w>=h) {
            //obrazek szerszy niż wyższy
            rescaledWidth = IMG_SIZE;
            double f = 1.0 * IMG_SIZE / w;
            rescaledHeight = (int)(h * f);
            deltaY = (IMG_SIZE - rescaledHeight) / 2;
        } else {
            //wyższy niż szerszy
            rescaledHeight = IMG_SIZE;
            double f = 1.0 * IMG_SIZE / h;
            rescaledWidth = (int) (w * f);
            deltaX = (IMG_SIZE - rescaledWidth) / 2;
        }
        gc.drawImage(currentlySelected, offsetX + deltaX, offsetY + deltaY, rescaledWidth, rescaledHeight);

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
