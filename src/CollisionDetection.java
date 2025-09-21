import java.awt.image.BufferedImage;

public class CollisionDetection {
    
    public static boolean isCollision(
        BufferedImage img1, int x1, int y1,
        BufferedImage img2, int x2, int y2){

        int scale = 4;

        int top = Math.max(y1, y2);
        int bottom = Math.min(y1 + img1.getHeight()*scale, y2 + img2.getHeight()*scale);
        int left = Math.max(x1, x2);
        int right = Math.min(x1 + img1.getWidth()*scale, x2 + img2.getWidth()*scale);

        if (right <= left || bottom <= top) return false;

        for (int y = top; y < bottom; y++) {
            for (int x = left; x < right; x++) {
                int img1Pixel = img1.getRGB((x - x1)/4, (y - y1)/4);
                int img2Pixel = img2.getRGB((x - x2)/4, (y - y2)/4);

                if (((img1Pixel >> 24) & 0xff) != 0 && ((img2Pixel >> 24) & 0xff) != 0) {
                    return true;
                }
            }
        }
        return false;

    }
}
