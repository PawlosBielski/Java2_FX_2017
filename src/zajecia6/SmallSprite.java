package zajecia6;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class SmallSprite {
    Image[] icons;
    int iconSize;
    int phase;
    int maxPhase;
    int frameCounter = 0;
    double x, y, goalX, goalY;
    double vx, vy;

    public SmallSprite(double x, double y, double tgtX, double tgtY,
                       double speed, String filename, int iconCount, int iconSize, int maxPhase) {
        this.x = x;
        this.y = y;
        this.iconSize = iconSize;
        //wyliczyć goals
        //todo: obrócić ikony zgodnie z kierunkiem ruchu
        setGoals(tgtX, tgtY, maxPhase, speed);

        //wczytać ikony
        loadSpriteAnimation(filename, iconCount);
    }

    private void setGoals(double tgtX, double tgtY,
                          int maxPhase, double speed) {
        goalX = tgtX;
        goalY = tgtY;
        double dx = goalX - x;
        double dy = goalY - y;
        if (dy==0) {
            if (dx>0) {
                vx = speed;
            } else {
                vx = -speed;
            }
            return;
        }

        double A = dx / dy;
        vy = speed / Math.sqrt(1 + A * A);
        if (dy<0) vy = -vy;
        vx = A * vy;
        this.phase = 0;
        this.maxPhase = maxPhase;
    }

    public void printSprite(GraphicsContext gc) {
        gc.drawImage(icons[phase % icons.length], x-iconSize/2, y-iconSize/2, iconSize, iconSize);
    }

    public void updatePosition() {
        frameCounter++;
        phase = (frameCounter / 3);
        //warunek dla osiągnięcia celu
        if (distance(x+vx, y+vy, goalX,goalY) > distance(x,y,goalX,goalY)) {
            x = goalX;
            y = goalY;
            return;
        }
        x += vx;
        y += vy;
    }

    public boolean isDead() {
        return phase > maxPhase;
    }

    private double distance(double x, double y, double x1, double y1) {
        return Math.sqrt((x - x1) * (x - x1) + (y - y1) * (y - y1));
    }

    private void loadSpriteAnimation(String filename, int iconCount) {
        icons = new Image[iconCount];
        for (int i = 0; i < iconCount; i++) {
            icons[i] = new Image(
                    getClass().getResourceAsStream("res/" + filename + (i+1) + ".png"));
        }
    }
}
