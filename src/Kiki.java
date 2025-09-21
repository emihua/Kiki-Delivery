import java.awt.image.BufferedImage;


public class Kiki{
    int x, y, height, width;
    BufferedImage img;

    Kiki(BufferedImage img, int x, int y, int width, int height){ // Constructor
        this.img = img;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
