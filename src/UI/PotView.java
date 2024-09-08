package UI;

import cooktop.PotModel;

import javax.swing.*;
import java.awt.*;

public class PotView extends JPanel {
    
    private final static int strokeWidth = 10;
    private final PotModel potModel;

    public PotView(PotModel potModel) {
        this.potModel = potModel;
    }

    @Override
    public void paintComponent(Graphics g) {
        var g2d = (Graphics2D) g.create();
        drawGraphics(g2d);
        g2d.dispose();
    }

    protected void drawGraphics(Graphics g) {
        var viewRadius = potModel.getRadius() <= 25 
                ? potModel.getRadius() * 5
                : (int)(potModel.getRadius() * 3.5);
        var targetOffset = potModel.getRadius() <= 25
            ? (int)(potModel.getRadius() + potModel.getRadius() * 0.78)
               : (int)(potModel.getRadius() * 2.1);
        var offset = new Dimension(targetOffset, 
                targetOffset - (int)(targetOffset * 0.23));
        
        g.setColor(Color.gray);
        g.fillOval(offset.width, offset.height, viewRadius, viewRadius);

        var temperature = potModel.getContent().getCurrentTemperature().get();
        var lerp = (float) Math.clamp(temperature / 250f, 0f, 1f);
        var color = temperature == 0f
                ? Color.BLUE
                : new Color(lerp, lerp, 1f); 

        g.setColor(color);
        g.fillOval(offset.width + strokeWidth, offset.height + strokeWidth,
                viewRadius - strokeWidth * 2, viewRadius - strokeWidth * 2);
        
        if (potModel.isLidOpened()) return;
        g.setColor(new Color(0f, 0f, 0f, 0.5f));
        g.fillOval(offset.width, offset.height, viewRadius, viewRadius);
        
        g.fillOval(offset.width + viewRadius / 3, offset.height + viewRadius / 3, 
                50, 50);
    }
}
