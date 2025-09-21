import java.awt.image.BufferedImage;


public class Obstacles {
    int x, y, width, height;
    BufferedImage bufferedImage;
    boolean passed;

    Obstacles(BufferedImage bufferedImage, int x, int y){
        this.bufferedImage = bufferedImage;
        this.x = x;
        this.y = y;
        this.width = bufferedImage.getWidth() * 4;
        this.height = bufferedImage.getHeight() * 4;
    }
}
