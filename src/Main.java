import UI.CooktopViewPanel;
import UI.MainFrame;
import cooktop.CooktopModel;
import cooktop.EnvironmentProcessor;

public class Main {
    public static void main(String[] args) {
        EnvironmentProcessor.initialize();
        EnvironmentProcessor.setTargetTemperature(24f);
        var cookTop = new CooktopModel();
        var cookView = new CooktopViewPanel();
        cookView.initialize(cookTop);
        
        var mainFrame = new MainFrame();
        mainFrame.setupPanels(cookView);
    }
}