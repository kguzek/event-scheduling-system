package pl.papuda.ess.client.components;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import pl.papuda.ess.client.MainWindow;

public class AppPanel extends JPanel {

    private MainWindow mainWindow = null;
    
    protected MainWindow getMainWindow() {
        if (mainWindow != null) return mainWindow;
        mainWindow = (MainWindow) getTopLevelAncestor();
        return mainWindow;
    }
    
    protected void switchPage(String page) {
        getMainWindow().showLayoutCard(page);
    }

    protected void showErrorPopup(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    protected void showInfoPopup(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    protected boolean showOptionPopup(String message, String title) {
        int result = JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }
}
