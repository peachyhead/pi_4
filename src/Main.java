import UI.CooktopViewPanel;
import UI.MainFrame;
import cooktop.CooktopModel;

public class Main {
    public static void main(String[] args) {
        var cookTop = new CooktopModel();
        var cookView = new CooktopViewPanel();
        cookView.initialize(cookTop);
        
        var mainFrame = new MainFrame();
        mainFrame.setupPanels(cookView);
    }
}