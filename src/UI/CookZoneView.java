package UI;

import cooktop.CookZoneModel;
import cooktop.PotModel;
import lombok.Getter;
import signal.SignalBus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CookZoneView extends JPanel {
    
    private final static int strokeWidth = 10;
    
    private PotView potView;
    @Getter private final CookZoneModel model;
    
    public CookZoneView(CookZoneModel model) {
        this.model = model;
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(200, 200));
        
        model.subscribeOnContentChange(evt -> {
            var heatProvider = (PotModel) evt.getNewValue();
            if (potView != null)
                remove(potView);
            if (heatProvider != null) {
                potView = new PotView(heatProvider);
                potView.setPreferredSize(new Dimension(model.getRadius() + 50, 
                        model.getRadius() + 50));
                add(potView);
            }
            revalidate();
        });
    }

    public void initialize() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                SignalBus.fire("zone_select", (String.valueOf(model.getId())));
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        var g2d = (Graphics2D) g.create();
        drawGraphics(g2d);
        g2d.dispose();
    }
    
    protected void drawGraphics(Graphics g) {
        var offset = model.getRadius() > 150 
                ? model.getRadius() / 6
                : model.getRadius() - model.getRadius() / 4;
        var clipBounds = g.getClipBounds();
        g.setColor(Color.RED);
        g.fillOval(offset + clipBounds.x, offset + clipBounds.y, 
                model.getRadius(), model.getRadius());
        
        var lerp = Math.min(model.getTemperature() / 250f, 1f);
        var color = model.getTemperature() == 0f
            ? Color.BLACK
            : new Color(lerp, 0f, 0f); 
                
        g.setColor(color);
        g.fillOval(offset + clipBounds.x + strokeWidth, offset + clipBounds.y + strokeWidth,
                model.getRadius() - strokeWidth * 2, model.getRadius() - strokeWidth * 2);
    }
}
