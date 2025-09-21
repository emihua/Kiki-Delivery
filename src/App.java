import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        //System.out.println("Game Started!");

        int frameWidth = 640*2;
        int frameHeight = 360*2;

        JFrame frame = new JFrame("Kiki's delivery");

        frame.setSize(frameWidth, frameHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        KikiDelivery kikiDelivery = new KikiDelivery();
        frame.add(kikiDelivery);
        frame.pack(); // Does not count title bar into dimensions
        kikiDelivery.requestFocus();
        frame.setVisible(true);

    }
}
