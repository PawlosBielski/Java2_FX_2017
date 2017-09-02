package fx_rysowanie;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class Controller {
    @FXML
    Canvas mycanvas;

    @FXML
    Stage stage;

    public void drawOnCanvas() {
        //Użycie canvasu:
        GraphicsContext gc = mycanvas.getGraphicsContext2D();
        gc.setStroke(Color.YELLOW);
        gc.strokeRoundRect(0, 0, 200, 300, 10, 10);
        gc.strokeLine(0, 0, 300, 310);
        gc.setLineWidth(3.5);
        gc.setLineDashes(2,1,5);
        for (int i = 0; i < 30; i++) {
            gc.strokeLine(0,0,300+i*10, 300);
        }
        gc.clearRect(50,50,200,210);
    }

}
