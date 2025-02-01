package pl.papuda.ess.client.components;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import pl.papuda.ess.client.MainWindow;

public class AppPanel extends JPanel {

    protected MainWindow getMainWindow() {
        return (MainWindow) getTopLevelAncestor();
    }

    protected void switchPage(String page) {
        MainWindow mainWindow = getMainWindow();
        mainWindow.showLayoutCard(page);
    }

    protected void showErrorPopup(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    protected void showInfoPopup(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
